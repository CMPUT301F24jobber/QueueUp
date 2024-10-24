package com.example.queueup.controllers;

import android.net.Uri;
import android.util.Log;

import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Image;
import com.example.queueup.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageController {
    private static ImageController instance = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private CollectionReference imageRef = db.collection("images");
    private CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    /**
     * Singleton instance getter for ImageController
     */
    public static ImageController getSingleton() {
        if (instance == null) {
            instance = new ImageController();
        }
        return instance;
    }

    /**
     * Private constructor for ImageController
     */
    private ImageController() {
    }

    /**
     * Get all images from a specific storage path
     * @param path The path to the storage folder
     * @return A Task containing the ListResult of all images
     */
    public Task<ListResult> getAllImages(String path) {
        StorageReference storageRef = storage.getReference(path);
        return storageRef.listAll();
    }

    /**
     * Get image metadata by its ID
     * @param id The ID of the image document
     * @return A Task containing the DocumentSnapshot with image details
     */
    public Task<DocumentSnapshot> getImageById(String id) {
        return imageRef.document(id).get();
    }

    /**
     * Add an image to storage and Firestore
     * @param storagePath The path where the image will be stored
     * @param imageUri The URI of the image to be uploaded
     * @return A Task containing the download URL of the uploaded image
     */
    public Task<String> addImage(String storagePath, Uri imageUri) {
        String uuid = UUID.randomUUID().toString();
        StorageReference storageRef = storage.getReference().child(storagePath + uuid);

        return storageRef.putFile(imageUri).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageRef.getDownloadUrl();
                })
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    String downloadUrl = task.getResult().toString();
                    Log.d("ImageController", downloadUrl);

                    // Create an Image object and add it to Firestore
                    Image image = new Image(downloadUrl, storagePath, uuid, uuid, 0, "image/jpeg", "");
                    return imageRef.document(uuid).set(image).continueWith(innerTask -> {
                        if (!innerTask.isSuccessful()) {
                            throw innerTask.getException();
                        }
                        return downloadUrl;
                    });
                });
    }

    /**
     * Remove image reference from Firestore
     * @param image The Image object to remove reference from
     * @return A Task representing the deletion operation
     */
    public Task<Void> removeReference(Image image) {
        if (image.getImageId() == null) {
            Log.d("ImageController", "Image ID is null");
            return null;
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("storageReferenceId", null);
        updates.put("filePath", null);
        return imageRef.document(image.getImageId()).update(updates);
    }

    /**
     * Delete an image from storage and Firestore
     * @param image The Image object to delete
     * @return A Task representing the deletion operation
     */
    public Task<Void> deleteImage(Image image) {
        StorageReference storageRef = storage.getReferenceFromUrl(image.getImageUrl());
        Task<Void> deleteImage = storageRef.delete();
        Task<Void> deletedocument = imageRef.document(image.getImageId()).delete();

        if(image.getStorageReferenceId() != null){
           removeReference(image);
        }

        // If its the user's own profile picture
        if (currentUserHandler.getCurrentUser().getValue().getProfileImageUrl().contains(image.getImageId())) {
            Log.d("ImageController", "Deleting user profile image.");
            User user = currentUserHandler.getCurrentUser().getValue();
            user.setProfileImageUrl(null);
            currentUserHandler.updateUser(user);
        }
        return Tasks.whenAll(deleteImage, deletedocument);
    }
}

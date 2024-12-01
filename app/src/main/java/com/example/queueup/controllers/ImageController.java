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

    public static ImageController getSingleton() {
        if (instance == null) {
            synchronized (ImageController.class) {
                if (instance == null) {
                    instance = new ImageController();
                }
            }
        }
        return instance;
    }


    private ImageController() {
    }

    /**
     * Get all images from a specific storage path
     * @param path
     * @return A Task containing the ListResult of all images in the path
     */
    public Task<ListResult> getAllImages(String path) {
        StorageReference storageRef = storage.getReference(path);
        return storageRef.listAll();
    }

    /**
     * Get image metadata by its ID
     * @param id
     * @return
     */
    public Task<DocumentSnapshot> getImageById(String id) {
        return imageRef.document(id).get();
    }

    /**
     * Add an image to storage and Firestore
     * @param storagePath
     * @param imageUri
     * @return A Task representing the addition operation
     */
    public Task<String> addImage(String storagePath, Uri imageUri) {
        String uuid = UUID.randomUUID().toString();
        StorageReference storageRef = storage.getReference().child(storagePath + "/" + uuid);

        return storageRef.putFile(imageUri)
                .continueWithTask(task -> {
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
                    Log.d("ImageController", "Download URL: " + downloadUrl);

                    // Create an Image object and add it to Firestore
                    Image image = new Image(downloadUrl, storagePath, uuid, uuid, 0, "image/jpeg", String.valueOf(System.currentTimeMillis()));
                    return imageRef.document(uuid).set(image)
                            .continueWith(innerTask -> {
                                if (!innerTask.isSuccessful()) {
                                    throw innerTask.getException();
                                }
                                return downloadUrl;
                            });
                });
    }

    /**
     * Remove image reference from Firestore
     * @param image
     * @return deletion image
     */
    public Task<Void> removeReference(Image image) {
        if (image.getImageId() == null) {
            Log.d("ImageController", "Image ID is null");
            return Tasks.forException(new IllegalArgumentException("Image ID is null"));
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("storageReferenceId", null);
        updates.put("filePath", null);
        return imageRef.document(image.getImageId()).update(updates);
    }

    /**
     * Delete an image from storage and Firestore
     * @param image
     * @return deletion image
     */
    public Task<Void> deleteImage(Image image) {
        if (image == null || image.getImageUrl() == null || image.getImageId() == null) {
            Log.e("ImageController", "Invalid image object");
            return Tasks.forException(new IllegalArgumentException("Invalid image object"));
        }

        StorageReference storageRef;
        storageRef = storage.getReferenceFromUrl(image.getImageUrl());


        Task<Void> deleteImageTask = storageRef.delete();

        return deleteImageTask;
    }
}

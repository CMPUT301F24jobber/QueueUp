package com.example.queueup.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ImageUploader {

    private static final String TAG = "ImageHandler";
    private static final String IMAGE_DIR_PATH = Environment.getDataDirectory() + "/QueueUp";
    private static final int MAX_IMAGE_SIZE_BYTES = 150 * 1000 * 1000;

    public interface UploadListener {
        void onUploadSuccess(String imageUrl);

        void onUploadFailure(Exception exception);
    }

    /**
     * Upload an image to Firebase Cloud Storage
     *
     * @param storagePath The path to store the image in Firebase Cloud Storage (e.g., user profile, event image, etc.)
     * @param imageUri    The URI of the image to upload
     * @param listener    The listener to handle the upload success or failure
     */
    public void uploadImage(String storagePath, Uri imageUri, UploadListener listener) {
        if (imageUri == null) {
            listener.onUploadFailure(new IllegalArgumentException("Image URI cannot be null"));
            return;
        }

        // Get a reference to Firebase Cloud Storage
        StorageReference firebaseStorageRef = FirebaseStorage.getInstance().getReference();

        // Create a unique reference for the image file
        StorageReference imageReference = firebaseStorageRef.child(storagePath + UUID.randomUUID().toString());

        // Upload the image
        imageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    taskSnapshot.getMetadata().getReference().getDownloadUrl()
                            .addOnCompleteListener(urlTask -> {
                                if (urlTask.isSuccessful()) {
                                    String imageUrl = urlTask.getResult().toString();
                                    listener.onUploadSuccess(imageUrl);
                                } else {
                                    listener.onUploadFailure(urlTask.getException());
                                }
                            });
                })
                .addOnFailureListener(listener::onUploadFailure);
    }

    /**
     * Save image to local storage
     *
     * @param bitmapImage The bitmap of the image to save
     * @param filename    The filename to save the image as
     */
    public static Uri saveImage(Bitmap bitmapImage, String filename) {
        // Create directory and file path
        String fullFilePath = IMAGE_DIR_PATH + "/" + filename;

        try (FileOutputStream fileOutputStream = new FileOutputStream(fullFilePath)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error saving image to local storage", e);
        }

        // Return URI to the saved image
        File savedImageFile = new File(fullFilePath);
        return Uri.fromFile(savedImageFile);
    }

    /**
     * Compress image
     *
     * @param originalImageUri The URI of the image to compress
     * @param compressionQuality  The quality (0 < quality <= 1) of the compressed image
     * @param context The context for accessing content resolver
     * @return The URI of the compressed image
     */
    public static Uri compressImage(Uri originalImageUri, double compressionQuality, Context context) throws IOException {
        // Validate compression quality
        if (compressionQuality <= 0 || compressionQuality > 1) {
            throw new IllegalArgumentException("Compression quality must be in the range: 0 < quality <= 1");
        }

        // Validate image URI
        if (originalImageUri == null) {
            throw new IllegalArgumentException("Image URI cannot be null");
        }

        // Convert URI to Bitmap
        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), originalImageUri);
        Log.d(TAG, String.format("Original image size: %d bytes", bitmapImage.getByteCount()));

        // Ensure the image size is not too large
        if (bitmapImage.getByteCount() >= MAX_IMAGE_SIZE_BYTES) {
            Toast.makeText(context, "Cannot upload as image size is too large.", Toast.LENGTH_SHORT).show();
        }
        assert (bitmapImage.getByteCount() < MAX_IMAGE_SIZE_BYTES); // Prevent crash for images greater than ~35MB

        // Create a temporary file to store the compressed image
        String compressedFilename = UUID.randomUUID().toString() + ".jpg";
        File compressedFile = new File(context.getCacheDir(), compressedFilename);

        // Compress the image
        try (FileOutputStream fileOutputStream = new FileOutputStream(compressedFile)) {
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, (int) (compressionQuality * 100), fileOutputStream);
            fileOutputStream.flush();
        }

        // Return URI to the compressed image
        return Uri.fromFile(compressedFile);
    }
}


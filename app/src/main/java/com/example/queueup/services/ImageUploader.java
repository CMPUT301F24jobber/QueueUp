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

    private static final String TAG = "ImageUploader";
    private static final String IMAGE_DIR_NAME = "QueueUpImages";
    private static final int MAX_IMAGE_SIZE_BYTES = 35 * 1024 * 1024; // 35 MB

    public interface UploadListener {
        void onUploadSuccess(String imageUrl);

        void onUploadFailure(Exception exception);
    }

    /**
     * Upload an image to Firebase Cloud Storage
     *
     * @param context      The context for accessing resources
     * @param storagePath  The path to store the image in Firebase Cloud Storage (e.g., user/profile, event/banner, etc.)
     * @param imageUri     The URI of the image to upload
     * @param listener     The listener to handle the upload success or failure
     */
    public void uploadImage(Context context, String storagePath, Uri imageUri, UploadListener listener) {
        if (imageUri == null) {
            listener.onUploadFailure(new IllegalArgumentException("Image URI cannot be null"));
            return;
        }

        // Get a reference to Firebase Cloud Storage
        StorageReference firebaseStorageRef = FirebaseStorage.getInstance().getReference();

        // Create a unique reference for the image file
        String uniqueImageName = UUID.randomUUID().toString();
        StorageReference imageReference = firebaseStorageRef.child(storagePath + "/" + uniqueImageName);

        // Upload the image
        imageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    taskSnapshot.getStorage().getDownloadUrl()
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
     * @param context      The context for accessing files directory
     * @param bitmapImage  The bitmap of the image to save
     * @param filename     The filename to save the image as
     * @return The URI of the saved image
     */
    public static Uri saveImage(Context context, Bitmap bitmapImage, String filename) {
        // Create directory and file path
        File imageDir = new File(context.getFilesDir(), IMAGE_DIR_NAME);
        if (!imageDir.exists()) {
            boolean dirCreated = imageDir.mkdirs();
            if (!dirCreated) {
                Log.e(TAG, "Failed to create image directory");
                return null;
            }
        }

        String fullFilePath = new File(imageDir, filename).getAbsolutePath();

        try (FileOutputStream fileOutputStream = new FileOutputStream(fullFilePath)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error saving image to local storage", e);
            return null;
        }

        // Return URI to the saved image
        File savedImageFile = new File(fullFilePath);
        return Uri.fromFile(savedImageFile);
    }

    /**
     * Compress image
     *
     * @param context             The context for accessing content resolver and files
     * @param originalImageUri    The URI of the image to compress
     * @param compressionQuality  The quality (0 < quality <= 1) of the compressed image
     * @return The URI of the compressed image
     * @throws IOException If an error occurs during compression
     */
    public static Uri compressImage(Context context, Uri originalImageUri, double compressionQuality) throws IOException {
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
            throw new IOException("Image size exceeds the maximum allowed limit.");
        }

        // Create a temporary file to store the compressed image
        String compressedFilename = "compressed_" + UUID.randomUUID().toString() + ".jpg";
        File compressedFile = new File(context.getCacheDir(), compressedFilename);

        // Compress the image
        try (FileOutputStream fileOutputStream = new FileOutputStream(compressedFile)) {
            int quality = (int) (compressionQuality * 100);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
            fileOutputStream.flush();
        }

        // Return URI to the compressed image
        return Uri.fromFile(compressedFile);
    }
}

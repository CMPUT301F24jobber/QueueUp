package com.example.queueup.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Represents an image stored in Firebase storage. It includes attributes such as
 * image URL, file path, storage reference ID, image ID, size, type, and creation date.
 * The class provides methods to retrieve images from Firebase as rounded bitmaps.
 */
public class Image {
    private String imageUrl;
    private String filePath;
    private String storageReferenceId;
    private String imageId;
    private long imageSize;
    private String imageType;
    private String creationDate;

    /**
     * Default constructor for creating an empty Image object.
     */
    public Image() {}

    /**
     * Constructs an Image object with specified attributes.
     *
     * @param imageUrl           The URL of the image.
     * @param filePath           The file path of the image.
     * @param storageReferenceId The storage reference ID for the image in Firebase.
     * @param imageId            The unique identifier for the image.
     * @param imageSize          The size of the image in bytes.
     * @param imageType          The type of the image (e.g., PNG, JPG).
     * @param creationDate       The creation date of the image.
     */
    public Image(String imageUrl, String filePath, String storageReferenceId, String imageId, long imageSize, String imageType, String creationDate) {
        this.imageUrl = imageUrl;
        this.filePath = filePath;
        this.storageReferenceId = storageReferenceId;
        this.imageId = imageId;
        this.imageSize = imageSize;
        this.imageType = imageType;
        this.creationDate = creationDate;
    }

    // Getter and Setter methods for the image attributes

    /**
     * Returns the image URL.
     *
     * @return The URL of the image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the image URL.
     *
     * @param imageUrl The new URL for the image.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Returns the file path of the image.
     *
     * @return The file path of the image.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the file path of the image.
     *
     * @param filePath The new file path for the image.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the storage reference ID for the image.
     *
     * @return The storage reference ID.
     */
    public String getStorageReferenceId() {
        return storageReferenceId;
    }

    /**
     * Sets the storage reference ID for the image.
     *
     * @param storageReferenceId The new storage reference ID.
     */
    public void setStorageReferenceId(String storageReferenceId) {
        this.storageReferenceId = storageReferenceId;
    }

    /**
     * Returns the unique identifier for the image.
     *
     * @return The image ID.
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * Sets the unique identifier for the image.
     *
     * @param imageId The new image ID.
     */
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    /**
     * Returns the size of the image in bytes.
     *
     * @return The image size in bytes.
     */
    public long getImageSize() {
        return imageSize;
    }

    /**
     * Sets the size of the image in bytes.
     *
     * @param imageSize The new image size in bytes.
     */
    public void setImageSize(long imageSize) {
        this.imageSize = imageSize;
    }

    /**
     * Returns the type of the image (e.g., PNG, JPG).
     *
     * @return The image type.
     */
    public String getImageType() {
        return imageType;
    }

    /**
     * Sets the type of the image (e.g., PNG, JPG).
     *
     * @param imageType The new image type.
     */
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    /**
     * Returns the creation date of the image.
     *
     * @return The creation date of the image.
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the image.
     *
     * @param creationDate The new creation date.
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Retrieves the image from Firebase Storage and returns it as a rounded thumbnail.
     *
     * @param context The context of the activity.
     * @return A Task containing the RoundedBitmapDrawable for the thumbnail image.
     */
    public Task<RoundedBitmapDrawable> getRoundedThumbnailImage(Context context) {
        if (imageUrl == null) {
            Log.d("Image", "Image URL is null");
            return Tasks.forException(new IllegalArgumentException("Image URL is null"));
        }

        StorageReference storageRef;
        try {
            storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        } catch (IllegalArgumentException e) {
            Log.e("Image", "Invalid image URL: " + imageUrl, e);
            return Tasks.forException(e);
        }

        final long ONE_MEGABYTE = 1024 * 1024;
        return storageRef.getBytes(ONE_MEGABYTE).continueWith(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            byte[] imageBytes = task.getResult();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
            RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), scaledBitmap);
            roundedDrawable.setCornerRadius(50f);
            return roundedDrawable;
        });
    }

    /**
     * Retrieves the full-size image from Firebase Storage and returns it as a rounded image.
     *
     * @param context The context of the activity.
     * @return A Task containing the RoundedBitmapDrawable for the full image.
     */
    public Task<RoundedBitmapDrawable> getFullRoundedImage(Context context) {
        if (imageUrl == null) {
            Log.d("Image", "Image URL is null");
            return Tasks.forException(new IllegalArgumentException("Image URL is null"));
        }

        StorageReference storageRef;
        try {
            storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        } catch (IllegalArgumentException e) {
            Log.e("Image", "Invalid image URL: " + imageUrl, e);
            return Tasks.forException(e);
        }

        final long ONE_MEGABYTE = 1024 * 1024;
        return storageRef.getBytes(ONE_MEGABYTE).continueWith(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            byte[] imageBytes = task.getResult();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
            roundedDrawable.setCornerRadius(50f);
            return roundedDrawable;
        });
    }
}

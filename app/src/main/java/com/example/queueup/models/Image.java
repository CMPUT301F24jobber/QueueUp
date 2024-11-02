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

public class Image {
    private String imageUrl;
    private String filePath;
    private String storageReferenceId;
    private String imageId;
    private long imageSize;
    private String imageType;
    private String creationDate;

    public Image() {
    }

    public Image(String imageUrl, String filePath, String storageReferenceId, String imageId, long imageSize, String imageType, String creationDate) {
        this.imageUrl = imageUrl;
        this.filePath = filePath;
        this.storageReferenceId = storageReferenceId;
        this.imageId = imageId;
        this.imageSize = imageSize;
        this.imageType = imageType;
        this.creationDate = creationDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStorageReferenceId() {
        return storageReferenceId;
    }

    public void setStorageReferenceId(String storageReferenceId) {
        this.storageReferenceId = storageReferenceId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public long getImageSize() {
        return imageSize;
    }

    public void setImageSize(long imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Get the image from the storage as a RoundedBitmapDrawable (Thumbnail)
     * @param context The context of the activity
     * @return A Task containing the RoundedBitmapDrawable
     */
    public Task<RoundedBitmapDrawable> getRoundedThumbnailImage(Context context){
        if(imageUrl == null) {
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
     * Get the image from the storage as a RoundedBitmapDrawable (Full Image)
     * @param context The context of the activity
     * @return A Task containing the RoundedBitmapDrawable
     */
    public Task<RoundedBitmapDrawable> getFullRoundedImage(Context context){
        if(imageUrl == null) {
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

package com.example.queueup.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.queueup.controllers.ImageController;
import com.example.queueup.models.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.Tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ImageViewModel extends ViewModel {
    public interface DeleteImageCallback {
        void onImageDeleted();
        void onImageDeleteFailed();
    }

    private final MutableLiveData<Image> selectedImageLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Image>> allImagesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final ImageController imageController = ImageController.getSingleton();

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public void clearErrorMessage() {
        errorMessageLiveData.setValue(null);
    }

    /**
     * Set the selected image
     *
     * @param
     */
    public void setSelectedImage(Image image) {
        selectedImageLiveData.setValue(image);
        Log.d("ImageViewModel", "Selected image URI: " + image.getImageUrl());
    }

    /**
     * Get the selected image
     *
     * @return LiveData<Image>
     */
    public LiveData<Image> getSelectedImage() {
        return selectedImageLiveData;
    }

    /**
     * Fetches all images from the storage
     */
    public void fetchAllImages() {
        List<String> imagePaths = List.of("event/banner", "user/profile");
        List<Task<ListResult>> tasks = new ArrayList<>();

        for (String path : imagePaths) {
            tasks.add(imageController.getAllImages(path));
        }

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            List<Image> fetchedImages = new ArrayList<>();
            for (Object result : results) {
                if (result instanceof ListResult) {
                    ListResult listResult = (ListResult) result;
                    fetchedImages.addAll(processImageListResult(listResult));
                }
            }
            allImagesLiveData.setValue(fetchedImages);
        }).addOnFailureListener(e -> {
            Log.e("ImageViewModel", "Failed to fetch all images", e);
            errorMessageLiveData.setValue("Failed to fetch images: " + e.getMessage());
        });
    }

    /**
     * Processes the list result and returns a list of Image objects
     *
     * @param listResult
     * @return
     */
    private List<Image> processImageListResult(ListResult listResult) {
        List<Image> images = new ArrayList<>();
        List<Task<Image>> imageTasks = new ArrayList<>();

        for (StorageReference fileReference : listResult.getItems()) {
            Task<Image> imageTask = fileReference.getDownloadUrl().continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                Uri uri = task.getResult();
                return fileReference.getMetadata().continueWith(metaTask -> {
                    if (!metaTask.isSuccessful()) {
                        throw metaTask.getException();
                    }
                    StorageMetadata metadata = metaTask.getResult();
                    return createImageFromMetadata(uri, metadata);
                });
            });
            imageTasks.add(imageTask);
        }

        try {
            List<Object> completedTasks = Tasks.await(Tasks.whenAllSuccess(imageTasks));
            for (Object obj : completedTasks) {
                if (obj instanceof Image) {
                    images.add((Image) obj);
                }
            }
        } catch (Exception e) {
            Log.e("ImageViewModel", "Error processing image tasks", e);
            errorMessageLiveData.setValue("Error processing images: " + e.getMessage());
        }

        return images;
    }

    /**
     * Creates an Image object from the provided metadata
     *
     * @param uri
     * @param storageMetadata
     * @return
     */
    private Image createImageFromMetadata(Uri uri, StorageMetadata storageMetadata) {
        Image image = new Image();
        image.setImageUrl(uri.toString());
        image.setFilePath(storageMetadata.getPath());
        image.setImageSize(storageMetadata.getSizeBytes());
        image.setImageType(storageMetadata.getContentType());
        image.setImageId(storageMetadata.getName());

        long creationTimeMillis = storageMetadata.getCreationTimeMillis();
        Date creationDate = new Date(creationTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        image.setCreationDate(dateFormat.format(creationDate));

        return image;
    }

    /**
     * Returns all images
     *
     * @return LiveData<List<Image>>
     */
    public LiveData<List<Image>> getAllImages() {
        return allImagesLiveData;
    }

    /**
     * Deletes the provided image
     *
     * @param image
     * @param callback
     */
    public void deleteImage(Image image, DeleteImageCallback callback) {
        imageController.deleteImage(image).addOnSuccessListener(aVoid -> {
            if (callback != null) {
                callback.onImageDeleted();
            }
        }).addOnFailureListener(e -> {
            if (callback != null) {
                callback.onImageDeleteFailed();
            }
        });
    }
}
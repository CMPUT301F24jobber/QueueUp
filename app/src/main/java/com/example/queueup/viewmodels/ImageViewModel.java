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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ImageViewModel extends ViewModel {
    /**
     * Callback interface for deleting images
     */
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
     * Set the selected image to be stored in the ViewModel state
     *
     * @param image the image object to be stored
     */
    public void setSelectedImage(Image image) {
        selectedImageLiveData.setValue(image);
        Log.d("ImageViewModel", "Selected image URI: " + image.getImageUrl());
    }

    /**
     * Get the selected image stored in the ViewModel state
     *
     * @return LiveData<Image>
     */
    public LiveData<Image> getSelectedImage() {
        return selectedImageLiveData;
    }

    /**
     * Fetch all images from the specified paths
     */
    public void fetchAllImages() {
        List<String> imagePaths = Arrays.asList("event/banner", "user/profile");
        List<Image> fetchedImages = new ArrayList<>();

        for (String path : imagePaths) {
            imageController.getAllImages(path).addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    Log.d("ImageViewModel", "Fetched images from path: " + path);
                    processImageListResult(listResult, fetchedImages);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    errorMessageLiveData.setValue("Failed to fetch images from path: " + path);
                }
            });
        }
    }

    /**
     * Processes the ListResult returned by Firebase and updates the LiveData with the fetched images
     *
     * @param listResult the list of storage references
     * @param fetchedImages the list to store the fetched images
     */
    private void processImageListResult(ListResult listResult, List<Image> fetchedImages) {
        for (StorageReference fileReference : listResult.getItems()) {
            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    fetchImageMetadata(uri, fetchedImages);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    errorMessageLiveData.setValue("Failed to get image download URL");
                }
            });
        }
    }

    /**
     * Fetches metadata for a given image URI and adds the image to the list
     *
     * @param uri the download URI of the image
     * @param fetchedImages the list to store the fetched images
     */
    private void fetchImageMetadata(Uri uri, List<Image> fetchedImages) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
        storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                Image image = createImageFromMetadata(uri, storageMetadata);
                fetchedImages.add(image);
                allImagesLiveData.setValue(fetchedImages);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Failed to get image metadata");
            }
        });
    }

    /**
     * Creates an Image object from the given metadata and URI
     *
     * @param uri the download URI of the image
     * @param storageMetadata the metadata of the image
     * @return an Image object
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        image.setCreationDate(dateFormat.format(creationDate));

        return image;
    }

    /**
     * Returns the LiveData object containing all images
     *
     * @return LiveData<List<Image>>
     */
    public LiveData<List<Image>> getAllImages() {
        return allImagesLiveData;
    }

    /**
     * Deletes an image and triggers the provided callback
     *
     * @param image the image to delete
     * @param callback the callback to trigger upon success or failure
     */
    public void deleteImage(Image image, DeleteImageCallback callback) {
        imageController.deleteImage(image).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (callback != null) {
                    callback.onImageDeleted();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorMessageLiveData.setValue("Image deletion failed");
                if (callback != null) {
                    callback.onImageDeleteFailed();
                }
            }
        });
    }
}

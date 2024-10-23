package com.example.queueup.services;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ImageUploader {

    private static ImageUploader imageUploader = null;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference imagesRef = storage.getReference("images");


    private ImageUploader() {}

    public static ImageUploader getImageUploader() {
        if (imageUploader == null) {
            imageUploader = new ImageUploader();
        }
        return imageUploader;
    }

    public UploadTask uploadImage(byte[] image) {
        return imagesRef.putBytes(image);
    }
    public UploadTask uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] image = baos.toByteArray();
        return imagesRef.putBytes(image);
    }

    public UploadTask uploadImage(Uri file) {
        return imagesRef.putFile(file);
    }

}

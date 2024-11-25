package com.example.queueup.handlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.example.queueup.controllers.EventController;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.Event;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageViewHandler {
    private static ImageViewHandler instance = null;
    private static final EventController eventController = EventController.getInstance();
    private static final UserController userController = UserController.getInstance();

    private ImageViewHandler() {
    }

    public static ImageViewHandler getInstance() {
        if (instance == null) {
            synchronized (ImageViewHandler.class) {
                if (instance == null) {
                    instance = new ImageViewHandler();
                }
            }
        }
        return instance;
    }

    /**
     * Set the event image for the event
     *
     * @param event
     * @param eventImageView
     * @param activity
     */
    public void setEventImage(Event event, ImageView eventImageView, AppCompatActivity activity) {
        // Fetch the event banner URL from the event object
        String uriEventBanner = event.getEventBannerImageUrl();

        // Initially hide the ImageView until we successfully load the image
        eventImageView.setVisibility(View.INVISIBLE);


        if (uriEventBanner != null && !uriEventBanner.isEmpty()) {
            StorageReference storageRef;
            try {
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uriEventBanner);
            } catch (IllegalArgumentException e) {
                Log.e("ImageViewHandler", "Invalid event banner URL: " + uriEventBanner, e);
                eventImageView.setVisibility(View.VISIBLE);
                eventImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                return;
            }

            final long ONE_MEGABYTE = 1024 * 1024;

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                // Decode the bytes into a Bitmap
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Create a rounded drawable from the Bitmap
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), bmp);
                roundedDrawable.setCornerRadius(20.0f);

                // Set the image drawable and adjust ImageView settings
                eventImageView.setImageDrawable(roundedDrawable);
                eventImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                eventImageView.setVisibility(View.VISIBLE);
            }).addOnFailureListener(exception -> {

                // Remove the event image reference if the fetch fails
                eventController.setEventBannerImage(event.getEventId(), null)
                        .addOnSuccessListener(aVoid -> Log.d("Event Image", "Event image reference successfully removed."))
                        .addOnFailureListener(e -> Log.e("Event Image", "Failed to remove event image reference.", e));

                // Make sure the ImageView is visible, but indicate no image
                eventImageView.setVisibility(View.VISIBLE);
                eventImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            });
        } else {
            eventImageView.setVisibility(View.VISIBLE);
            eventImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }
}

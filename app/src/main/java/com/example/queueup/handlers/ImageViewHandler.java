package com.example.queueup.handlers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.example.queueup.controllers.EventController;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.Event;
import com.example.queueup.models.ImageDimension;
import com.example.queueup.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageViewHandler {
    private static int deviceScreenWidth;
    private static int deviceScreenHeight;
    private static ImageViewHandler instance = null;
    private static AppCompatActivity activityOwner;
    private static final EventController eventController = EventController.getEventController();
    private static final UserController userController = UserController.getInstance();

    private ImageViewHandler() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activityOwner.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceScreenWidth = displayMetrics.widthPixels;
        deviceScreenHeight = displayMetrics.heightPixels;
    }

    public static void initializeInstance() {
        if (activityOwner == null) {
            throw new IllegalStateException("Activity owner must be set before initializing ImageViewHandler.");
        }
        instance = new ImageViewHandler();
    }

    public static ImageViewHandler getInstance() {
        if (instance == null) {
            initializeInstance();
        }
        return instance;
    }

    public static void setActivityOwner(AppCompatActivity activity) {
        activityOwner = activity;
    }

    /**
     * Set the event image for the event
     *
     * @param event The event for which the image is to be set
     * @param eventImageView The ImageView where the event image will be displayed
     */
    public void setEventImage(Event event, ImageView eventImageView) {
        // Fetch the event banner URL from the event object
        String uriEventBanner = event.getImageUrl();

        // Initially hide the ImageView until we successfully load the image
        eventImageView.setVisibility(View.INVISIBLE);

        // If the event banner URL is not null, proceed to fetch it from Firebase Storage
        if (uriEventBanner != null && !uriEventBanner.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uriEventBanner);
            final long ONE_MEGABYTE = 1024 * 1024;

            // Attempt to fetch the image bytes from Firebase
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                // Decode the bytes into a Bitmap
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Create a rounded drawable from the Bitmap
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(activityOwner.getResources(), bmp);
                roundedDrawable.setCornerRadius(20.0f); // Set corner radius for rounded effect (adjust as needed)

                // Set the image drawable and adjust ImageView settings
                eventImageView.setImageDrawable(roundedDrawable);
                eventImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                eventImageView.setVisibility(View.VISIBLE);
            }).addOnFailureListener(exception -> {
                // Log the error if fetching image fails
                Log.w("Event Image", "Error getting event image, removing reference", exception);

                // Remove the event image reference if the fetch fails
                eventController.updateEventbyId(event.getId(), "eventBannerImgUrl", null)
                        .addOnSuccessListener(aVoid -> Log.d("Event Image", "Event image reference successfully removed."))
                        .addOnFailureListener(e -> Log.e("Event Image", "Failed to remove event image reference.", e));

                // Make sure the ImageView is visible, but indicate no image
                eventImageView.setVisibility(View.VISIBLE);
                eventImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            });
        } else {
            // If there is no image URL, keep the ImageView visible but with default settings
            eventImageView.setVisibility(View.VISIBLE);
            eventImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }


    /**
     * Set user profile image
     *
     * @param user The user whose profile image is to be set
     * @param profileImageView The ImageView where the profile image will be displayed
     * @param resources The application resources
     * @param imageDimension The dimensions for the profile image (optional)
     */
    public void setUserProfileImage(User user, ImageView profileImageView, Resources resources, @Nullable ImageDimension imageDimension) {
        int targetWidth = (imageDimension != null) ? imageDimension.getWidth() : 72;
        int targetHeight = (imageDimension != null) ? imageDimension.getHeight() : 72;

        if (user.getProfileImageUrl() != null) {
            String profileImageUrl = user.getProfileImageUrl();

            // Fetch image from Firebase storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profileImageUrl);
            final long ONE_MEGABYTE = 1024 * 1024;

            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(imageBytes -> {
                Bitmap originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, false);
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(resources, scaledBitmap);
                roundedDrawable.setCornerRadius(Math.min(targetWidth, targetHeight) / 2.0f);
                profileImageView.setImageDrawable(roundedDrawable);
            }).addOnFailureListener(exception -> {
                Log.w("ImageViewHandler", "Failed to load profile image for user: " + user.getUsername(), exception);
            });
        }
    }
}

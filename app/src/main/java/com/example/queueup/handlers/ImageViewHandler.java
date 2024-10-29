package com.example.queueup.handlers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private static ImageViewHandler instance = null;
    private static final EventController eventController = EventController.getInstance();
    private static final UserController userController = UserController.getInstance();

    private ImageViewHandler() {
    }

    /**
     * Singleton instance getter for ImageViewHandler
     */
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
     * @param event           The event for which the image is to be set
     * @param eventImageView  The ImageView where the event image will be displayed
     * @param activity        The activity context for resource access
     */
    public void setEventImage(Event event, ImageView eventImageView, AppCompatActivity activity) {
        // Fetch the event banner URL from the event object
        String uriEventBanner = event.getEventBannerImageUrl();

        // Initially hide the ImageView until we successfully load the image
        eventImageView.setVisibility(View.INVISIBLE);

        // If the event banner URL is not null, proceed to fetch it from Firebase Storage
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

            // Attempt to fetch the image bytes from Firebase
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                // Decode the bytes into a Bitmap
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                // Create a rounded drawable from the Bitmap
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), bmp);
                roundedDrawable.setCornerRadius(20.0f); // Set corner radius for rounded effect (adjust as needed)

                // Set the image drawable and adjust ImageView settings
                eventImageView.setImageDrawable(roundedDrawable);
                eventImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                eventImageView.setVisibility(View.VISIBLE);
            }).addOnFailureListener(exception -> {
                // Log the error if fetching image fails
                Log.w("Event Image", "Error getting event image, removing reference", exception);

                // Remove the event image reference if the fetch fails
                eventController.setEventBannerImage(event.getEventId(), null)
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
     * @param user              The user whose profile image is to be set
     * @param profileImageView  The ImageView where the profile image will be displayed
     * @param resources         The application resources
     * @param imageDimension    The dimensions for the profile image (optional)
     */
    public void setUserProfileImage(User user, ImageView profileImageView, Resources resources, @Nullable ImageDimension imageDimension) {
        int targetWidth = (imageDimension != null) ? imageDimension.getWidth() : 72;
        int targetHeight = (imageDimension != null) ? imageDimension.getHeight() : 72;

        if (user.getProfileImageUrl() != null) {
            String profileImageUrl = user.getProfileImageUrl();

            // Fetch image from Firebase storage
            StorageReference storageReference;
            try {
                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(profileImageUrl);
            } catch (IllegalArgumentException e) {
                Log.w("ImageViewHandler", "Invalid profile image URL: " + profileImageUrl, e);
                // Optionally set a default image
                profileImageView.setImageResource(android.R.drawable.ic_menu_camera); // Replace with your default image
                return;
            }

            final long ONE_MEGABYTE = 1024 * 1024;

            storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(imageBytes -> {
                Bitmap originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, false);
                RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(resources, scaledBitmap);
                roundedDrawable.setCornerRadius(Math.min(targetWidth, targetHeight) / 2.0f);
                profileImageView.setImageDrawable(roundedDrawable);
            }).addOnFailureListener(exception -> {
                Log.w("ImageViewHandler", "Failed to load profile image for user: " + user.getUsername(), exception);
                // Optionally set a default image
                profileImageView.setImageResource(android.R.drawable.ic_menu_camera); // Replace with your default image
            });
        } else {
            // Optionally set a default image or handle absence of profile image
            profileImageView.setImageResource(android.R.drawable.ic_menu_camera); // Replace with your default image
        }
    }
}

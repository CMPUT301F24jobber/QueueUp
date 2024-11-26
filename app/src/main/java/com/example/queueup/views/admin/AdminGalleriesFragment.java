package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.models.Image;
import com.example.queueup.viewmodels.EventViewModel;
import com.example.queueup.viewmodels.ImageViewModel;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminGalleriesFragment extends Fragment {
	private ImageView imageView;
	private TextView imageTitle;
	private MaterialButton deleteButton;
	private EventViewModel eventViewModel;
	private ImageViewModel imageViewModel;
	private eventRemovedListener listener;
	private Event currentEvent;

	public interface eventRemovedListener {
		void onEventRemoved(Event event);
	}

	public void setEventRemovedListener(eventRemovedListener listener) {
		this.listener = listener;
	}

	public AdminGalleriesFragment() {
		super(R.layout.admin_galleries_fragment);
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
		imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);

		imageView = view.findViewById(R.id.image_view);
		deleteButton = view.findViewById(R.id.delete_button);
		imageTitle = view.findViewById(R.id.image_title);

		Bundle args = getArguments();
		if (args != null) {
			currentEvent = args.getSerializable("event", Event.class);
			if (currentEvent != null) {
				imageTitle.setText(currentEvent.getEventName());
				String imageUrl = currentEvent.getEventBannerImageUrl();
				if (imageUrl != null && !imageUrl.isEmpty()) {
					Glide.with(this).load(imageUrl).into(imageView);
				}
			}
		}

		deleteButton.setOnClickListener((v -> {
			if (currentEvent != null) {
				deleteImage(currentEvent);
				if (listener != null) {
					listener.onEventRemoved(currentEvent);
				}
			}
		}));
	}

	private void deleteImage(Event event) {
		String imageUrl = event.getEventBannerImageUrl();

		if (imageUrl != null && !imageUrl.isEmpty()) {
			Image imageToDelete = new Image();
			imageToDelete.setImageUrl(imageUrl);
			String filePath = imageUrl;

			// Extract the path from Firebase Storage URL
			if (imageUrl.contains("firebase")) {
				String[] parts = imageUrl.split("\\?");
				if (parts.length > 0) {
					String pathPart = parts[0];
					// Remove the base URL part
					String[] pathSegments = pathPart.split("/o/");
					if (pathSegments.length > 1) {
						filePath = pathSegments[1].replace("%2F", "/");
					}
				}
			}

			imageToDelete.setFilePath(filePath);
			imageToDelete.setImageId(event.getEventId());
			imageToDelete.setImageType("image/jpeg"); // Set appropriate type
			imageToDelete.setCreationDate(new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
					.format(new Date()));

			imageViewModel.deleteImage(imageToDelete, new ImageViewModel.DeleteImageCallback() {
				@Override
				public void onImageDeleted() {
					Glide.with(requireContext()).clear(imageView);
					updateEventImageUrl(event);
					if (listener != null) {
						listener.onEventRemoved(event);
					}
					Toast.makeText(getContext(), "Successfully deleted the image", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onImageDeleteFailed() {
					Toast.makeText(getContext(), "Failed to delete the image", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	private void updateEventImageUrl(Event event) {
		if (event != null) {
			event.setEventBannerImageUrl(null);
			eventViewModel.updateEvent(event);
		}
	}
}
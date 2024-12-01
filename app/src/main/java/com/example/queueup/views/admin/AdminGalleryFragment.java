package com.example.queueup.views.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.controllers.EventController;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.Event;
import com.example.queueup.models.Image;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.EventViewModel;
import com.example.queueup.viewmodels.ImagesArrayAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminGalleryFragment extends Fragment {
    private ArrayList<Image> imagesList;
    private ListView imageListView;
    private ImagesArrayAdapter imagesAdapter;
    private EventController eventController = EventController.getInstance();
    private UserController userController = UserController.getInstance();

    public AdminGalleryFragment() {
        super(R.layout.admin_gallery_fragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize list and adapter
        imagesList = new ArrayList<>();
        imageListView = view.findViewById(R.id.admin_images_list);
        imagesAdapter = new ImagesArrayAdapter(view.getContext(), imagesList);
        imageListView.setAdapter(imagesAdapter);



        // Set up observers and fetch events

        getImages();
    }

    private void getImages() {
        List<Image> eventImages = new ArrayList<>();
        List<Image> userImages = new ArrayList<>();

        eventController.getAllEvents().addOnSuccessListener( queryDocumentSnapshots -> {

            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    Event event = doc.toObject(Event.class);
                    if (event != null && event.getEventBannerImageUrl() != null && !event.getEventBannerImageUrl().isEmpty()) {
                        Image eventImage = new Image("event", event.getEventName(), event.getEventBannerImageUrl(), event.getEventId());
                        eventImages.add(eventImage);
                    }
                }
            }
        }).continueWith(task -> {
            return userController.getAllUsers().addOnSuccessListener( queryDocumentSnapshots -> {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        User user = doc.toObject(User.class);
                        if (user != null && user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                            Image eventImage = new Image("user", user.getUsername(), user.getProfileImageUrl(), user.getUuid());
                            userImages.add(eventImage);
                        }
                    }
                }
            }).continueWith(lastTask -> {
                imagesList.clear();
                imagesList.addAll(eventImages);
                imagesList.addAll(userImages);
                imagesAdapter.notifyDataSetChanged();

                imageListView.setOnItemClickListener( (parent, itemView, position, id) -> {
                    Log.d("hello", imagesList.get(position).getImageType());
                    nagvigateToImage(imagesList.get(position));
                });
                return null;

            });
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        getImages();

    }

    private void nagvigateToImage(Image image) {
        Bundle args = new Bundle();
        args.putSerializable("image", image);

        AdminImageFragment fragment = new AdminImageFragment();
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.admin_activity_fragment, fragment)
                .addToBackStack("Event")
                .commit();
    }
}
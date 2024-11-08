package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.ImagesArrayAdapter;

import java.util.ArrayList;

/**
 * Fragment that displays a gallery of users in a ListView, allowing the admin to view
 * users and their associated images.
 * This fragment currently uses placeholder user data but can be expanded to fetch actual
 * user data from a database or other source.
 */
public class AdminGalleryFragment extends Fragment {
    public AdminGalleryFragment() {
        super(R.layout.admin_gallery_fragment);
    }
    private ArrayList<User> dataList;
    private ListView eventList;
    private ImagesArrayAdapter eventAdapter;

    /**
     * Initializes the views and binds the data for the gallery of users.
     * It sets up a ListView with an adapter to display user data.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        User e = new User("e","e","e","e","e","e", true);
        dataList = new ArrayList<User>();
        dataList.add(e);

        eventList = getView().findViewById(R.id.admin_images_list);
        eventAdapter = new ImagesArrayAdapter(view.getContext(), dataList);
        eventList.setAdapter(eventAdapter);
    }

}

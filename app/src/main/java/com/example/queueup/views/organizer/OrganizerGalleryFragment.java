package com.example.queueup.views.organizer;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.ImagesArrayAdapter;

import java.util.ArrayList;

public class OrganizerGalleryFragment extends Fragment {
    public OrganizerGalleryFragment() {
        super(R.layout.organizer_gallery_fragment);
    }
    private ArrayList<User> dataList;
    private ListView eventList;
    private ImagesArrayAdapter eventAdapter;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        User e = new User("e","e","e","e","e","e");
        dataList = new ArrayList<User>();
        dataList.add(e);

        eventList = getView().findViewById(R.id.organizer_images_list);
        eventAdapter = new ImagesArrayAdapter(view.getContext(), dataList);
        eventList.setAdapter(eventAdapter);
    }

}
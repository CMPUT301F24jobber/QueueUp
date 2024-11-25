package com.example.queueup.views.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.AdminEventImagesAdapter;
import com.example.queueup.viewmodels.EventViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class AdminGalleryFragment extends Fragment {
    public AdminGalleryFragment() {
        super(R.layout.admin_gallery_fragment);
    }

    private ArrayList<Event> eventList;
    private ListView eventListView;
    private AdminEventImagesAdapter eventAdapter;
    private FirebaseFirestore firestore;
    private EventViewModel eventViewModel;

    /**
     * Called when the view for the fragment has been created. 
     * @param view
     * @param savedInstanceState 
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventViewModel = new EventViewModel();

        firestore = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        eventListView = view.findViewById(R.id.admin_images_list);
        eventAdapter = new AdminEventImagesAdapter(view.getContext(), eventList);
        eventListView.setAdapter(eventAdapter);
        fetchEventsFromFirestore();
    }

    /**
     * Fetches all events
     */
    private void fetchEventsFromFirestore() {
        eventViewModel.fetchAllEvents();
        eventViewModel.getAllEventsLiveData().observe(getViewLifecycleOwner(), events -> {
            eventList.clear();
            eventList.addAll(events);
            eventAdapter.notifyDataSetChanged();
        });
    }
}

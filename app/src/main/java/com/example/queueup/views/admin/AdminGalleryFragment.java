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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Fragment that displays a gallery of events in a ListView, allowing the admin to view
 * all events fetched from Firestore.
 */
public class AdminGalleryFragment extends Fragment {
    public AdminGalleryFragment() {
        super(R.layout.admin_gallery_fragment);
    }

    private ArrayList<Event> eventList;
    private ListView eventListView;
    private AdminEventImagesAdapter eventAdapter;
    private FirebaseFirestore firestore;

    /**
     * Initializes the views and binds the data for the gallery of events.
     * It sets up a ListView with an adapter to display event data.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        eventListView = view.findViewById(R.id.admin_images_list);
        eventAdapter = new AdminEventImagesAdapter(view.getContext(), eventList);
        eventListView.setAdapter(eventAdapter);
        fetchEventsFromFirestore();
    }

    /**
     * Fetches events from the Firestore database and updates the event list.
     */
    private void fetchEventsFromFirestore() {
        CollectionReference eventsRef = firestore.collection("events");
        eventsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    eventList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            eventList.add(event);
                        }
                    }
                    eventAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No events found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to fetch events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

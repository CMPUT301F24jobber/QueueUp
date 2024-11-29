package com.example.queueup.views.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.R;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventViewModel;
import com.example.queueup.viewmodels.QRCodeEventAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Fragment responsible for displaying a list of events fetched from Firestore for the organizer.
 */
public class OrganizerQRCodesFragment extends Fragment {
    private static final String TAG = "OrganizerQRCodesFragment";

    public OrganizerQRCodesFragment() {
        super(R.layout.organizer_qrcodes_fragment);
    }

    private ArrayList<Event> eventList;
    private QRCodeEventAdapter eventsAdapter;
    private EventViewModel eventViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        eventList = new ArrayList<>();
        ListView eventsListView = view.findViewById(R.id.organizer_qrcodes_list);
        eventsAdapter = new QRCodeEventAdapter(view.getContext(), eventList);


        eventsListView.setAdapter(eventsAdapter);

        observeViewModel();

        // Fetch events for the current organizer
        String organizerId = CurrentUserHandler.getSingleton().getDeviceId();
        eventViewModel.fetchEventsByOrganizer(organizerId);
    }

    /**
     * Fetches events from the Firestore database and updates the event list.
     */
    private void observeViewModel() {
        eventViewModel.getEventsByOrganizerLiveData().observe(getViewLifecycleOwner(), events -> {
            eventList.clear();
            eventList.addAll(events);
            eventsAdapter.notifyDataSetChanged();
        });
    }






}
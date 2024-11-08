package com.example.queueup.views.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.QRCodeEventAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

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
    private FirebaseFirestore firestore;
    private Button downloadButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        ListView eventsListView = view.findViewById(R.id.organizer_qrcodes_list);
        downloadButton = view.findViewById(R.id.qr_download_button);
        eventsAdapter = new QRCodeEventAdapter(view.getContext(), eventList);
        eventsListView.setAdapter(eventsAdapter);

        //downloadButton.setOnClickListener(v -> downloadQrCode());

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
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            eventList.add(event);
                        } else {
                            Log.w(TAG, "Null event found in Firestore data.");
                        }
                    }
                    eventsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No events found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to fetch events", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching events from Firestore", e);
            }
        });
    }

}
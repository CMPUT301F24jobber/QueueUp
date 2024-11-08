package com.example.queueup.views.organizer;

import android.content.pm.PackageManager;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        ListView eventsListView = view.findViewById(R.id.organizer_qrcodes_list);
        eventsAdapter = new QRCodeEventAdapter(view.getContext(), eventList);


        eventsListView.setAdapter(eventsAdapter);

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

    /**
     * Generates a QR code for the given event and saves it to the device storage.
     *
     * @param event The event for which the QR code is generated.
     */
    private void downloadQrCode(Event event) {
        if (event == null || event.getCheckInQrCodeId() == null || event.getCheckInQrCodeId().isEmpty()) {
            Toast.makeText(getContext(), "QR code ID is missing for this event", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Generate QR code bitmap
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(
                    event.getCheckInQrCodeId(),
                    BarcodeFormat.QR_CODE,
                    800, 800
            );

            // Save bitmap to device storage
            saveBitmapToStorage(bitmap, "QR_Code_" + event.getEventName() + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves a bitmap to device storage.
     *
     * @param bitmap The bitmap to save.
     * @param fileName The name of the file to save.
     */
    private void saveBitmapToStorage(Bitmap bitmap, String fileName) {
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Toast.makeText(getContext(), "QR code saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save QR code", Toast.LENGTH_SHORT).show();
        }
    }


}
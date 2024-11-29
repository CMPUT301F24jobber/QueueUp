package com.example.queueup.views.attendee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;


public class AttendeeQRscanFragment extends Fragment {
    public AttendeeQRscanFragment() {
        super(R.layout.attendee_qr_fragment);
    }
    MaterialButton scanButton;
    EventController eventController;
    PreviewView previewView;
    AttendeeController attendeeController;

    /**
     * Called when the fragment is created.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        scanButton = view.findViewById(R.id.scan_button);
        attendeeController = AttendeeController.getInstance();
        eventController = EventController.getInstance();
        previewView = view.findViewById(R.id.cameraView);
        previewView.setVisibility(View.INVISIBLE);
        ActivityResultLauncher<ScanOptions> barCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            Intent originalIntent = result.getOriginalIntent();

            eventController.getEventById(result.getContents().toString())
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Event event = documentSnapshot.toObject(Event.class);
                                if (event != null) {
                                    Intent intent;
                                    intent = new Intent(getActivity(), AttendeeEvent.class);
                                    intent.putExtra("event", event);
                                    startActivity(intent);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Intent intent = new Intent(getActivity(), AttendeeHome.class);
                            startActivity(intent);
                        }
                    });
        });

        scanButton.setOnClickListener((v) -> {
            ScanOptions scanOptions = new ScanOptions();
            scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            scanOptions.setCaptureActivity(AttendeeQRscanActivity.class);
            scanOptions.setPrompt("Scan Event QR Code");
            barCodeLauncher.launch(scanOptions);
        });
    }

}

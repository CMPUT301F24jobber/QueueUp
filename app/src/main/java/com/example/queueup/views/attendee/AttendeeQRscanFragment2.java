package com.example.queueup.views.attendee;

import static androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getMainExecutor;


import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Range;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.mlkit.vision.MlKitAnalyzer;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.camera.view.video.AudioConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AttendeeQRscanFragment2 extends Fragment {
    public AttendeeQRscanFragment2() {
        super(R.layout.attendee_qr_fragment);
    }
    private MaterialButton scanButton;
    private EventController eventController;
    private AttendeeController attendeeController;
    private PreviewView previewView;
    private Executor executor;
    private BarcodeScanner barcodeScanner;
    private BarcodeScannerOptions options;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
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

        if (checkSelfPermission(getActivity(), permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission.CAMERA}, 1);
        }

        options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);
        executor = Executors.newSingleThreadExecutor();

        cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(getContext()));


    }
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER) // Keep only the latest frame
                .build();

        imageAnalysis.setAnalyzer(executor,
                new MlKitAnalyzer(List.of(barcodeScanner), COORDINATE_SYSTEM_VIEW_REFERENCED,
                        executor, result -> {
                    Toast.makeText(getContext(), "found", Toast.LENGTH_SHORT).show();

                    eventController.getEventById(result.toString())
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
                }));
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (barcodeScanner != null) {
            barcodeScanner.close();
        }

    }


}

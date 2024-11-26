// https://reintech.io/blog/implementing-android-app-qr-code-scanner
// https://www.digitalocean.com/community/tutorials/qr-code-barcode-scanner-android
package com.example.queueup.views.attendee;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.MainActivity;
import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;
import com.example.queueup.viewmodels.EventViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AttendeeQRscanActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private EventViewModel eventViewModel;
    private EventController eventController;

    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        eventController = EventController.getInstance();

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            initQRCodeScanner();
        }
    }

    /**
     * Called when the activity is resumed.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        }
    }

    /**
     * Initializes the QR code scanner.
     */
    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    /**
     * Called when the activity is resumed.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        eventController.getEventById(result.getContents() )
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Event event = documentSnapshot.toObject(Event.class);
                            if (event != null) {
                                Intent intent;
                                intent = new Intent(AttendeeQRscanActivity.this, AttendeeEvent.class);
                                intent.putExtra("event", event);

                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AttendeeQRscanActivity.this, "Event not found", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AttendeeQRscanActivity.this, AttendeeHome.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }


}

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

/**
 * AttendeeQRscanFragment is an activity that allows the user to scan QR codes using the device's camera.
 * It requests the camera permission if it hasn't been granted, and initializes the QR code scanner if permission is granted.
 */
public class AttendeeQRscanActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private EventViewModel eventViewModel;
    private EventController eventController;

    /**
     * Called when the activity is created. This method checks if the app has camera permission, and if not,
     * it requests the permission. Once granted, the QR code scanner is initialized.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state, if any.
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
     * Handles the result of the camera permission request. If permission is granted, the QR code scanner is initialized.
     * Otherwise, a message is shown and the activity is finished.
     *
     * @param requestCode The request code passed in requestPermissions().
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
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
     * Initializes the QR code scanner. This method sets up the scanner to scan QR codes, locks the orientation,
     * and sets a prompt message. It then starts the scan activity.
     */
    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    /**
     * Handles the result of the QR code scan. If the scan is successful, the scanned content is displayed.
     * If the scan is cancelled, a message is shown.
     *
     * @param requestCode The request code passed in initiateScan().
     * @param resultCode The result code returned by the scanning activity.
     * @param data The Intent data returned by the scanning activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("scan", "hi");
            } else {
                Log.d("scan", result.getContents());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
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

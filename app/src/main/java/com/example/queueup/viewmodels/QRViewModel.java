package com.example.queueup.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.queueup.controllers.QRController;
import com.example.queueup.models.QRCode;

/**
 * ViewModel responsible for handling QR code data in the application.
 * It interacts with the QRController to fetch QR code information from a data source
 * and provides the data to the UI layer via LiveData.
 */
public class QRViewModel extends ViewModel {
    private static final String TAG = QRViewModel.class.getSimpleName();

    // LiveData for storing the QR code object
    private final MutableLiveData<QRCode> qrCodeLiveData = new MutableLiveData<>();
    // LiveData for storing error messages related to QR code operations
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    // QRController instance for handling QR code-related logic
    private final QRController qrController = QRController.getInstance();


    /**
     * Gets the LiveData object that holds the QRCode data.
     *
     * @return LiveData<QRCode> the LiveData object containing the QRCode data
     */
    public LiveData<QRCode> getQrCodeLiveData() {
        return qrCodeLiveData;
    }

    /**
     * Gets the LiveData object that holds error messages.
     *
     * @return LiveData<String> the LiveData object containing error messages
     */
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    /**
     * Clears the error message LiveData.
     */
    public void clearError() {
        errorLiveData.setValue(null);
    }

    /**
     * Clears the QR code LiveData.
     */
    public void clearQrCode() {
        qrCodeLiveData.setValue(null);
    }

    /**
     * Fetches a QR code from the QRController using the provided QR code ID.
     * On success, the QR code data is updated in the LiveData. On failure, an error message is set.
     *
     * @param qrCodeId the ID of the QR code to be fetched
     */
    public void fetchQrCode(String qrCodeId) {
        qrController.getQR(qrCodeId).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                QRCode qrCode = documentSnapshot.toObject(QRCode.class);
                if (qrCode != null) {
                    qrCodeLiveData.setValue(qrCode);
                } else {
                    errorLiveData.setValue("Invalid QR code data");
                }
            } else {
                errorLiveData.setValue("QR code not found");
            }
        }).addOnFailureListener(e -> {
            errorLiveData.setValue("Failed to fetch QR code");
            Log.e(TAG, "Failed to fetch QR code", e);
        });
    }
}

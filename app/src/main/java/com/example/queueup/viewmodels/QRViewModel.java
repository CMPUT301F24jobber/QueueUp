package com.example.queueup.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.queueup.controllers.QRController;
import com.example.queueup.models.QRCode;

import kotlin.contracts.Returns;

public class QRViewModel extends ViewModel {
    private static final String TAG = QRViewModel.class.getSimpleName();

    private final MutableLiveData<QRCode> qrCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final QRController qrController = QRController.getInstance();


    /**
     * Returns the LiveData object for the QR code.
     * @return LiveData<QRCode>
     */
    public LiveData<QRCode> getQrCodeLiveData() {
        return qrCodeLiveData;
    }

    /**
     * Returns the LiveData object for the error message.
     * @return LiveData<String>
     * /
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
     * Fetches the QR code from the database.
     * @param qrCodeId
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

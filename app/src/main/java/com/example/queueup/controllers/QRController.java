package com.example.queueup.controllers;

import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.QRCode;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class QRController {
    private static QRController instance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference qrCollection = db.collection("QR");
    private final CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    private QRController() {}

    public static QRController getInstance() {
        if (instance == null) {
            instance = new QRController();
        }
        return instance;
    }

    /**
     * Create a QR code for an event
     *
     * @param qrCode
     */
    public void createQR(QRCode qrCode) {
        qrCollection.document(qrCode.getId()).set(qrCode);
    }

    /**
     * Delete a QR code
     *
     * @param qrCode
     */
    public void deleteQR(QRCode qrCode) {
        qrCollection.document(qrCode.getId()).delete();
    }

    /**
     * Get all QR codes
     *
     * @return A qr code
     */
    public Task<QuerySnapshot> getQRs() {
        return qrCollection.get();
    }

    /**
     * Get a QR code by ID
     *
     * @param qrId
     * @return A qr code
     */
    public Task<DocumentSnapshot> getQR(String qrId) {
        return qrCollection.document(qrId).get();
    }
}

package com.example.queueup.controllers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FacilityController {
    private static FacilityController singleInstance = null;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference facilityCollectionReference = db.collection("facilities");
    private FacilityController() {}

    public static synchronized FacilityController getInstance() {
        if (singleInstance == null) {
            singleInstance = new FacilityController();
        }
        return singleInstance;
    }

    public Task<DocumentSnapshot> getFacilitybyId(String userId) {
        return facilityCollectionReference.document(userId).get();
    }
}

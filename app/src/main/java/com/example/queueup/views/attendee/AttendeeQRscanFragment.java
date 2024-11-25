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
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;
import com.google.android.material.button.MaterialButton;


public class AttendeeQRscanFragment extends Fragment {
    public AttendeeQRscanFragment() {
        super(R.layout.attendee_qr_fragment);
    }
    MaterialButton scanButton;
    EventController eventController;
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
        ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                        }
                    }
                });;

        scanButton.setOnClickListener((v) -> {
            Intent intent = new Intent(view.getContext(), AttendeeQRscanActivity.class);
            mStartForResult.launch(intent);
        });
    }

}

package com.example.queueup.views.organizer;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.example.queueup.views.SignUp;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class OrganizerCreateFacility extends DialogFragment {

    private Button uploadButton, submitButton;
    private ImageButton backButton;
    private TextInputLayout facilityNameInputLayout;
    private User user;
    private UserController userController = UserController.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.organizer_create_facility, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = this.getArguments().getParcelable("user", User.class);

        backButton = view.findViewById(R.id.back_button);
        submitButton = view.findViewById(R.id.facility_submit_button);
        facilityNameInputLayout = view.findViewById(R.id.facilityNameInputLayout);

        backButton.setOnClickListener(v -> {
            dismiss();
        });
        submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener( v -> {
            String name = facilityNameInputLayout.getEditText().getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Facility Name can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            user.setFacility(name);
            CurrentUserHandler.getSingleton().updateUser(user);
            userController.updateUser(user);
            Intent intent = new Intent(getActivity(), OrganizerHome.class); // Navigate to OrganizerHome
            startActivity(intent);
        });
    }




    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

}

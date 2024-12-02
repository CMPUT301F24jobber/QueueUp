package com.example.queueup.views.organizer;

import static com.example.queueup.handlers.CurrentUserHandler.userViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.queueup.MainActivity;
import com.example.queueup.R;
import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.EventViewModel;
import com.google.android.material.button.MaterialButton;


public class OrganizerFacilityFragment extends Fragment {
    public OrganizerFacilityFragment() {
        super(R.layout.facility_page);
    }

    private TextView facilityName;
    private Button editButton;
    private MaterialButton switchRoleButton;
    private EventViewModel eventViewModel;
    private User currentUser;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        facilityName = view.findViewById(R.id.facility_name);
        editButton = view.findViewById(R.id.editButton);
        switchRoleButton = view.findViewById(R.id.switch_role);
        currentUser = CurrentUserHandler.getSingleton().getCurrentUser().getValue();
        facilityName.setText(currentUser.getFacility());
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);


        editButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", currentUser);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            OrganizerCreateFacility facilityDialog = new OrganizerCreateFacility();
            facilityDialog.setArguments(bundle);
            transaction.add(android.R.id.content, facilityDialog)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        });
        // Set up Switch Role Button Click Listener
        switchRoleButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OrganizerFacilityFragment.class);
            startActivity(intent);
            getActivity().finish();
        });
        observeViewModel();
        fetchUserData();

    }
    private void observeViewModel() {
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                facilityName.setText(currentUser.getFacility());
            }
        });
    }
    private void fetchUserData() {
        userViewModel.loadUserByDeviceId(userViewModel.getDeviceId());
    }

}

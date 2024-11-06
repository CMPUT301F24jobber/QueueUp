package com.example.queueup.views.attendee;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UsersArrayAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AttendeeWaitlistFragment extends Fragment {
    public AttendeeWaitlistFragment() {
        super(R.layout.attendee_join_waitlist_fragment);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

    }

}

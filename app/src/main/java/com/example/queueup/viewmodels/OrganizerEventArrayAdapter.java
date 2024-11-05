package com.example.queueup.viewmodels;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.views.admin.AdminEventFragment;
import com.example.queueup.views.attendee.AttendeeEventFragment;

import java.util.ArrayList;

public class OrganizerEventArrayAdapter extends EventArrayAdapter {
    public OrganizerEventArrayAdapter(Context context, ArrayList<Event> event) {
        super(context, event);
    }

    protected View.OnClickListener onClickListener(View view) {
        return (v) -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.organizer_activity_fragment, AdminEventFragment.class, null)
                    .addToBackStack(null)
                    .commit();
        };
    }

}

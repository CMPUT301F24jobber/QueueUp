package com.example.queueup.viewmodels;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.views.admin.AdminEventFragment;
import com.example.queueup.views.attendee.AttendeeEventFragment;
import com.example.queueup.views.attendee.AttendeeHomeFragment;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AttendeeEventArrayAdapter extends EventArrayAdapter {
    public AttendeeEventArrayAdapter(Context context, ArrayList<Event> event) {
        super(context, event);
    }

    protected View.OnClickListener onClickListener(View view) {
        return (v) -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.attendee_activity_fragment, AttendeeEventFragment.class, null)
                    .addToBackStack(null)
                    .commit();
        };
    }

}

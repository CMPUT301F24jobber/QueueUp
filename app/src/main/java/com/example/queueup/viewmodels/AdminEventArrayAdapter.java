package com.example.queueup.viewmodels;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.views.admin.AdminEventFragment;
import com.example.queueup.views.attendee.AttendeeEventFragment;

import java.util.ArrayList;

public class AdminEventArrayAdapter extends EventArrayAdapter {
    public AdminEventArrayAdapter(Context context, ArrayList<Event> event) {
        super(context, event);
    }

    protected View.OnClickListener onClickListener(View view, int position) {
        return (v) -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", getItem(position));
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_activity_fragment, AdminEventFragment.class, bundle)
                    .addToBackStack(null)
                    .commit();
            this.notifyDataSetChanged();

        };
    }

}

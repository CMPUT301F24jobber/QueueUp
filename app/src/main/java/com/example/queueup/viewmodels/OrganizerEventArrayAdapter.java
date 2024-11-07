package com.example.queueup.viewmodels;


import android.content.Context;
import android.content.Intent;
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

import com.example.queueup.MainActivity;
import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.views.admin.AdminEventFragment;

import com.example.queueup.views.organizer.OrganizerCreateEvent;
import com.example.queueup.views.organizer.OrganizerEvent;
import com.example.queueup.views.organizer.OrganizerHome;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class OrganizerEventArrayAdapter extends EventArrayAdapter {
    public OrganizerEventArrayAdapter(Context context, ArrayList<Event> event) {
        super(context, event);
    }


    protected View.OnClickListener onClickListener(View view, int position) {
        return (v) -> {
            Intent intent = new Intent(view.getContext(), OrganizerEvent.class);
            intent.putExtra("event", getItem(position));
            view.getContext().startActivity(intent);
            this.notifyDataSetChanged();

        };
    }

}

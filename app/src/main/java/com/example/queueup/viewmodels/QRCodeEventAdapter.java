package com.example.queueup.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.example.queueup.models.Event;
import com.example.queueup.views.organizer.OrganizerQr;

import java.util.ArrayList;

public class QRCodeEventAdapter extends EventArrayAdapter {

    public QRCodeEventAdapter(Context context, ArrayList<Event> events) {
        super(context, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Event event = getItem(position);

        return view;
    }

    @Override
    protected View.OnClickListener onClickListener(View view, int position) {
        // redirect to QR code download page
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrganizerQr.class);
                intent.putExtra("event", getItem(position));
                getContext().startActivity(intent);
            }
        };
    }

}
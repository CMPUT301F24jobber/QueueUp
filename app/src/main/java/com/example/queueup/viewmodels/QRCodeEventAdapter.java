package com.example.queueup.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.queueup.models.Event;
import com.example.queueup.views.organizer.OrganizerQr;

import java.util.ArrayList;

public class QRCodeEventAdapter extends EventArrayAdapter {

    public QRCodeEventAdapter(Context context, ArrayList<Event> events) {
        super(context, events);
    }

    /**
     * Returns the view for the QRCodeEventAdapter
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent); // get the view from the parent class
        Event event = getItem(position);
        return view;
    }

    /**
     * Returns the onClickListener for the QRCodeEventAdapter
     * @param view
     * @param position
     * @return
     */
    @Override
    protected View.OnClickListener onClickListener(View view, int position) {
        // redirect to QR code download page
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(position).getCheckInQrCodeId() == null) {
                    Toast.makeText(getContext(), "No QR code available", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), OrganizerQr.class);
                intent.putExtra("event", getItem(position));
                getContext().startActivity(intent);
            }
        };
    }

}
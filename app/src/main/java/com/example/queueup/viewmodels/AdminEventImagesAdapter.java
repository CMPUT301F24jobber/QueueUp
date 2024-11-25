package com.example.queueup.viewmodels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.models.Event;

import java.util.ArrayList;

public class AdminEventImagesAdapter extends ArrayAdapter<Event> {

    /**
     * Constructor for the AdminEventImagesAdapter
     * @param context
     * @param events
     */
    public AdminEventImagesAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    /**
     * Returns the view for the AdminEventImagesAdapter
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_event_image, parent, false); // Change to your layout file name if different
            viewHolder = new ViewHolder();
            viewHolder.eventImage = convertView.findViewById(R.id.event_image);
            viewHolder.eventName = convertView.findViewById(R.id.event_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Event event = getItem(position);

        if (event != null) {
            viewHolder.eventName.setText(event.getEventName());

            Glide.with(getContext())
                    .load(event.getEventBannerImageUrl())
                    .centerCrop()
                    .circleCrop()
                    .into(viewHolder.eventImage);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView eventImage;
        TextView eventName;
    }
}

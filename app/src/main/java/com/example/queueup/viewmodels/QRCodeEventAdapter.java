package com.example.queueup.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.models.Image;
import com.example.queueup.services.QRCodeImage;
import com.example.queueup.views.organizer.OrganizerQr;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class QRCodeEventAdapter extends ArrayAdapter<Event> {
    public QRCodeEventAdapter(Context context, ArrayList<Event> event) {
        super(context, 0, event);
    }
    TextView eventTitle;
    TextView eventDate;
    TextView eventLocation;
    ImageView eventImage;
    DateTimeFormatter formatter;

    /**
     * Returns the view for the EventArrayAdapter
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.admin_image_content,
                    parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);

        TextView fileName = view.findViewById(R.id.file_name);
        ImageView qrCodeImage = view.findViewById(R.id.file_image);
        fileName.setText(event.getEventName());
        QRCodeImage qrCodeGenerator = new QRCodeImage();
        Bitmap qrBitmap = qrCodeGenerator.generateQrCodeImage(event.getEventId());

        qrCodeImage.setImageBitmap(qrBitmap);
        view.setOnClickListener(onClickListener(view, position));

        return view;
    }

    /**
     * Returns the onClickListener for the EventArrayAdapter
     * @param view
     * @param position
     * @return
     */
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

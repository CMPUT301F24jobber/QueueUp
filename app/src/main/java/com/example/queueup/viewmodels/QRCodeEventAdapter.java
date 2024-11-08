package com.example.queueup.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.queueup.models.Event;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.example.queueup.R;

import java.util.ArrayList;

public class QRCodeEventAdapter extends EventArrayAdapter {

    public QRCodeEventAdapter(Context context, ArrayList<Event> events) {
        super(context, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        ImageView eventImageView = view.findViewById(R.id.event_image);

        View downloadButton = view.findViewById(R.id.qr_download_button);
        if (downloadButton != null) {
            downloadButton.setVisibility(View.VISIBLE); // making download button visible
        }


        // generating QR code for each event
        Event event = getItem(position);
        if (event != null && event.getCheckInQrCodeId() != null) {
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(
                        event.getCheckInQrCodeId(),
                        BarcodeFormat.QR_CODE,
                        450, 450
                );
                eventImageView.setImageBitmap(bitmap); // set qr code in image view
            } catch (WriterException e) {
                e.printStackTrace(); // handling qr code generation error

            }
        }

        return view;
    }

    @Override
    protected View.OnClickListener onClickListener(View view, int position) {
        return null;
    }

}
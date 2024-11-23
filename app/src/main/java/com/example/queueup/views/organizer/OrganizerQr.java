package com.example.queueup.views.organizer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueup.R;
import com.example.queueup.models.Event;
import com.example.queueup.services.QRCodeImage;

public class OrganizerQr extends AppCompatActivity {
    private static final String EXTRA_EVENT = "event";
    private ImageView qrCodeImage;
    private QRCodeImage qrCodeGenerator;
    private Event event;
    private ImageButton backButton;

    public static Intent newIntent(Context context, Event event) {
        Intent intent = new Intent(context, OrganizerQr.class);
        intent.putExtra(EXTRA_EVENT, event);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_fragment);
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        // Get event from intent
        event = (Event) getIntent().getSerializableExtra(EXTRA_EVENT);

        // Initialize views
        qrCodeImage = findViewById(R.id.qrCodeImage);
        qrCodeGenerator = new QRCodeImage();

        if (event != null) {
            // Generate QR code with event ID
            String qrContent = event.getEventId();
            Bitmap qrBitmap = qrCodeGenerator.generateQrCodeImage(qrContent);
            qrCodeImage.setImageBitmap(qrBitmap);
        }
    }
}
package com.example.queueup.services;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Send the token to the server
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent intent = new Intent("Notification");
        intent.putExtra("message", remoteMessage.getNotification().getBody());
        intent.putExtra("title", remoteMessage.getNotification().getTitle());

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

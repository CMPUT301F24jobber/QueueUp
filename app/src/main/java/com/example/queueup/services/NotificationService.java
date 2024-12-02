package com.example.queueup.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.example.queueup.R;
import com.example.queueup.controllers.UserController;
import com.example.queueup.models.User;
import com.example.queueup.viewmodels.UserViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

// wanna migrate to JobIntentService POSSIBLY LAGGY
public class NotificationService extends Service {
    private static final String TAG = "Notification Service";
    private static final String CHANNEL_ID = "default_channel";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private NotificationManager notificationManager;
    private NotificationChannel notificationChannel;
    private UserController userController = UserController.getInstance();

    private String deviceId;
    private Thread thread;

    private void showNotification(String title) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.circle_shape);

        notificationManager.notify(title.hashCode(), notificationBuilder.build());
    }
    private void showNotificationDesc(String title, String description) {
        // this chunk should be in the bind I think, not sure completely, it's fine here but eh

        // end chunk
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.circle_shape);

        notificationManager.notify(title.hashCode(), notificationBuilder.build());
    }
    private void showAllNotifications() {
        userController.getUserByDeviceId(deviceId).addOnSuccessListener(querySnapshot -> {
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                user = document.toObject(User.class);
            } else {
                return;
            }
            if (user == null) {
                return;
            }
            ArrayList<String> notifications = (ArrayList<String>) user.getNotifications();

            if (notifications.isEmpty()) return;
            if (notifications.size() % 2 == 1) return;

            for (int i = 0, n = notifications.size(); i < n; i +=2 ) {
                if (notificationPerm(notifications.get(i))) {
                    showNotification(notifications.get(i+1));
                }
            }

            userController.clearNotifications(user.getUuid());
        });


    }

    private Boolean notificationPerm(String status) {
        return (notificationAll() || notificationChosen(status) || notificationNotChosen(status));
    }

    private Boolean notificationAll() {
        return user.isReceiveAllNotifications();
    }
    private Boolean notificationChosen(String status) {
        return (user.isReceiveChosenNotifications() && (status == "selected"));
    }

    private Boolean notificationNotChosen(String status) {
        return (user.isReceiveNotChosenNotifications() && (status == "not_selected"));
    }


    private void observeNotification() {
        db.collection("users").whereEqualTo("deviceId", deviceId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        userController.getUserByDeviceId(deviceId).addOnSuccessListener(querySnapshot -> {
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                user = document.toObject(User.class);
                            }
                            showAllNotifications();
                        });

                    }
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand (Intent intent,
                               int flags,
                               int startId) {
        super.onStartCommand(intent, flags, startId);

        deviceId = userController.getDeviceId(getApplicationContext());

        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        CharSequence channelName = "Default Channel";
        notificationChannel = new NotificationChannel(
                CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(notificationChannel);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                observeNotification();
            }
        });
        thread.start();

        Toast.makeText(this, "notification service started", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
    }

}

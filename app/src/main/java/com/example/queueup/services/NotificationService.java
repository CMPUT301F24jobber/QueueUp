package com.example.queueup.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.queueup.handlers.CurrentUserHandler;
import com.example.queueup.models.User;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

// wanna migrate to JobIntentService
public class NotificationService extends Service {
    private static final String TAG = "Notification Service";
    private static final String CHANNEL_ID = "default_channel";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private CurrentUserHandler currentUserHandler = CurrentUserHandler.getSingleton();

    private void showNotification(String title) {
        // this chunk should be in the bind I think, not sure completely, it's fine here but eh
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Default Channel";
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        // end chunk
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
        Toast.makeText(this, "show notification reached", Toast.LENGTH_SHORT).show();

    }
    private void showAllNotifications() {
        Toast.makeText(this, "all notification reached", Toast.LENGTH_SHORT).show();

        if (user == null) {         Toast.makeText(this, "USER NULL!", Toast.LENGTH_SHORT).show();
            ; return; }
        ArrayList<String> notifications = user.getNotifications();
        Toast.makeText(this, Integer.toString(notifications.size()), Toast.LENGTH_SHORT).show();

        if (notifications.isEmpty()) return;
        for (String notification : notifications) {
            showNotification(notification);
        }
        user.clearNotifications();
        currentUserHandler.updateUser(user);
    }

    private void observeNotification() {
        db.collection("users").whereEqualTo("deviceId", currentUserHandler.getDeviceId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
//                            DocumentSnapshot document = snapshot.getDocuments().get(0);
//                            user = document.toObject(User.class);
                        showAllNotifications();
                    }
                });
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        observeNotification();
    }
    @Override
    public int onStartCommand (Intent intent,
                               int flags,
                               int startId) {
        super.onStartCommand(intent, flags, startId);
        observeNotification();
        showAllNotifications();
        Toast.makeText(this, "notification service started", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
    }
}

package com.example.queueup.handlers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.queueup.controllers.AttendeeController;
import com.example.queueup.controllers.EventController;
import com.example.queueup.models.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import okhttp3.Response;

public class PushNotificationHandler {
    private static PushNotificationHandler singleInstance = null;
    private static final AttendeeController attendanceController = AttendeeController.getInstance();
    private static final EventController eventController = EventController.getInstance();
    private static final String TAG = "PushNotificationHandler";

    // Add your Server Key here (from Firebase Console)
    private static final String SERVER_KEY = "YOUR_SERVER_KEY_HERE";

    public static PushNotificationHandler getSingleton() {
        if (singleInstance == null) {
            singleInstance = new PushNotificationHandler();
        }
        return singleInstance;
    }

    public PushNotificationHandler() {}

    public void handleNotificationPermissions(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    public void sendNotification(Event event, String message) {
        String title = event.getEventName();
        eventController.getAttendeesFCMTokens(event.getEventId()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> tokens = task.getResult();
                for (String token : tokens) {
                    sendNotificationToToken(token, title, message);
                }
            } else {
                Log.d(TAG, "Failed to get FCM tokens for event " + event.getEventId());
            }
        });
    }

    private void sendNotificationToToken(String token, String title, String body) {
        try {
            JSONObject root = new JSONObject();
            JSONObject notification = new JSONObject();
            JSONObject data = new JSONObject();

            notification.put("title", title);
            notification.put("body", body);

            // Optional: Add data payload
            data.put("message", body);

            root.put("to", token);
            root.put("notification", notification);
            root.put("data", data);

            sendPushNotification(root);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to create JSON: " + e.getMessage());
        }
    }

    private void sendPushNotification(JSONObject root) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(root.toString(), mediaType);

        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Authorization", "key=" + SERVER_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "Notification send failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "Notification sent: " + response.body().string());
            }
        });
    }
}

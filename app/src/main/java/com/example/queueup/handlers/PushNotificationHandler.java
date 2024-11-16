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
import com.example.queueup.controllers.UserController;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

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
    private static final AttendeeController attendeeController = AttendeeController.getInstance();
    private static final EventController eventController = EventController.getInstance();
    private static final UserController userController = UserController.getInstance();
    private static final String TAG = "PushNotificationHandler";

    private static final String SERVER_KEY = "BLZ2jFq-dnKm43m9o3i__kWIZ19HH7Fr1B6WQ2DKw_tYQ6--3MW1rUvVRYd-zUDTxKdGHZxLGMPH65_y3RjOXhE";
    private final OkHttpClient client = new OkHttpClient();

    public static PushNotificationHandler getSingleton() {
        if (singleInstance == null) {
            singleInstance = new PushNotificationHandler();
        }
        return singleInstance;
    }

    private PushNotificationHandler() {}

    /**
     * Handles notification permissions for Android Tiramisu and above.
     *
     * @param context The application context.
     * @param activity The activity from which to request permissions.
     */
    public void handleNotificationPermissions(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    // Enums for Notification Types
    public enum NotificationType {
        GENERAL,
        ORGANIZER,
        ADMIN
    }

    // Send notification to a single user
    public void sendNotificationToUser(String userId, String title, String body, NotificationType type) {
        userController.isNotificationEnabled(userId, type).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult()) {
                userController.getFCMToken(userId).addOnCompleteListener(tokenTask -> {
                    if (tokenTask.isSuccessful()) {
                        String token = tokenTask.getResult();
                        if (token != null && !token.isEmpty()) {
                            sendNotificationToToken(token, title, body);
                        } else {
                            Log.d(TAG, "FCM token is null or empty for user " + userId);
                        }
                    } else {
                        Log.d(TAG, "Failed to get FCM token for user " + userId);
                    }
                });
            } else {
                Log.d(TAG, "Notifications are disabled for user " + userId + " or failed to check.");
            }
        });
    }

    // Send notification to multiple users
    private void sendNotificationToUsers(List<String> userIds, String title, String body, NotificationType type) {
        for (String userId : userIds) {
            sendNotificationToUser(userId, title, body, type);
        }
    }

    // Entrant Notifications

    // Notification when chosen from the waiting list
    public Task<Void> sendLotteryWinNotification(String eventId, String userId) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        eventController.getEventName(eventId).addOnCompleteListener(eventNameTask -> {
            if (eventNameTask.isSuccessful() && eventNameTask.getResult() != null) {
                String eventName = eventNameTask.getResult();
                String title = "Congratulations!";
                String body = "You have been selected for " + eventName;
                sendNotificationToUser(userId, title, body, NotificationType.GENERAL);
                taskCompletionSource.setResult(null);
            } else {
                taskCompletionSource.setException(new Exception("Failed to retrieve event name."));
            }
        });

        return taskCompletionSource.getTask();
    }

    // Notification when not chosen
    public Task<Void> sendLotteryLoseNotification(String eventId, String userId) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        eventController.getEventName(eventId).addOnCompleteListener(eventNameTask -> {
            if (eventNameTask.isSuccessful() && eventNameTask.getResult() != null) {
                String eventName = eventNameTask.getResult();
                String title = "Lottery Result";
                String body = "You were not selected for " + eventName;
                sendNotificationToUser(userId, title, body, NotificationType.GENERAL);
                taskCompletionSource.setResult(null);
            } else {
                taskCompletionSource.setException(new Exception("Failed to retrieve event name."));
            }
        });

        return taskCompletionSource.getTask();
    }

    // Notification for another chance
    public Task<Void> sendReplacementNotification(String eventId, String userId, NotificationType general) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        eventController.getEventName(eventId).addOnCompleteListener(eventNameTask -> {
            if (eventNameTask.isSuccessful() && eventNameTask.getResult() != null) {
                String eventName = eventNameTask.getResult();
                String title = "Good News!";
                String body = "A spot opened up for " + eventName + ". You're invited to sign up.";
                sendNotificationToUser(userId, title, body, NotificationType.GENERAL);
                taskCompletionSource.setResult(null);
            } else {
                taskCompletionSource.setException(new Exception("Failed to retrieve event name."));
            }
        });

        return taskCompletionSource.getTask();
    }

    // Send invitation to chosen entrants
    public Task<Void> sendInvitationNotification(String eventId, String userId) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        eventController.getEventName(eventId).addOnCompleteListener(eventNameTask -> {
            if (eventNameTask.isSuccessful() && eventNameTask.getResult() != null) {
                String eventName = eventNameTask.getResult();
                String title = "Invitation to Sign Up";
                String body = "You are invited to sign up for " + eventName;
                sendNotificationToUser(userId, title, body, NotificationType.ORGANIZER);
                taskCompletionSource.setResult(null);
            } else {
                taskCompletionSource.setException(new Exception("Failed to retrieve event name."));
            }
        });

        return taskCompletionSource.getTask();
    }

    // Send notification to all entrants on the waiting list
    public Task<Void> sendNotificationToWaitingList(String eventId, String message) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        eventController.getWaitingListUserIds(eventId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> userIds = task.getResult();
                sendNotificationToUsers(userIds, "Message from Organizer", message, NotificationType.ORGANIZER);
                taskCompletionSource.setResult(null);
            } else {
                Log.d(TAG, "Failed to get waiting list user IDs for event " + eventId);
                taskCompletionSource.setException(task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }

    // Send notification to all selected entrants
    public Task<Void> sendNotificationToSelectedEntrants(String eventId, String message) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        eventController.getSelectedUserIds(eventId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> userIds = task.getResult();
                sendNotificationToUsers(userIds, "Message from Organizer", message, NotificationType.ORGANIZER);
                taskCompletionSource.setResult(null);
            } else {
                Log.d(TAG, "Failed to get selected user IDs for event " + eventId);
                taskCompletionSource.setException(task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }

    // Send notification to all cancelled entrants
    public Task<Void> sendNotificationToCancelledEntrants(String eventId, String message) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        eventController.getCancelledUserIds(eventId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> userIds = task.getResult();
                sendNotificationToUsers(userIds, "Message from Organizer", message, NotificationType.ORGANIZER);
                taskCompletionSource.setResult(null);
            } else {
                Log.d(TAG, "Failed to get cancelled user IDs for event " + eventId);
                taskCompletionSource.setException(task.getException());
            }
        });

        return taskCompletionSource.getTask();
    }

    // Helper method to send notifications asynchronously
    private Task<Void> sendNotificationAsync(String userId, String title, String body, NotificationType type) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
        sendNotificationToUser(userId, title, body, type);
        // Assume notification is sent successfully as it's asynchronous
        taskCompletionSource.setResult(null);
        return taskCompletionSource.getTask();
    }

    /**
     * Sends a push notification using Firebase Cloud Messaging (FCM) HTTP API.
     *
     * @param root The JSON payload containing notification details.
     */
    private void sendPushNotification(JSONObject root) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
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

    /**
     * Sends a push notification to a specific FCM token.
     *
     * @param token The recipient's FCM token.
     * @param title The title of the notification.
     * @param body The body content of the notification.
     */
    private void sendNotificationToToken(String token, String title, String body) {
        try {
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);

            JSONObject root = new JSONObject();
            root.put("to", token);
            root.put("notification", notification);

            sendPushNotification(root);
        } catch (Exception e) {
            Log.e(TAG, "Failed to build notification JSON.", e);
        }
    }
}

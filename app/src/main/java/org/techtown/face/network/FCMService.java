package org.techtown.face.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.techtown.face.R;
import org.techtown.face.activities.MainActivity;
import org.techtown.face.activities.SettingActivity;

public class FCMService extends FirebaseMessagingService {

    String CHANNEL_ID = "FCMTest";
    String TAG = "FCMService";
    String DESCRIPTION = "Test Push Alarm For FCM";

    public FCMService() {
        super();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (!message.getData().isEmpty()) {
            showNotification(message.getData().get("title"), message.getData().get("body"));
            Log.w(TAG, message.getData().get("title") + "-" + message.getData().get("body"));
        }
        if (message.getNotification() != null) {
            showNotification(message.getNotification().getTitle(), message.getNotification().getBody());
            Log.w(TAG, message.getNotification().getTitle() + "-" + message.getNotification().getBody());
        }
    }

    private void showNotification(String title, String body) {
        Intent nIntent = new Intent(this, MainActivity.class);
        nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nIntent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.user_icon)
                .setContentTitle(title)
                .setContentText(body).setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "FCMTest",
                    NotificationManager.IMPORTANCE_DEFAULT);
        }
        createNotificationChannel(channel, DESCRIPTION);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(101, mBuilder.build());
    }

    private void createNotificationChannel(NotificationChannel channel, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
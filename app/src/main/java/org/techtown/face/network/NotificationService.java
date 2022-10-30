package org.techtown.face.network;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import org.techtown.face.R;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.Objects;

public class NotificationService extends Service {
    public NotificationService() {
    }

    String CHANNEL_ID = "test";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent==null){
            return NotificationService.START_STICKY;
        }else{
            notificationRead();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void notificationRead(){
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                .addSnapshotListener((value, error) -> {
                   for(DocumentChange change : value.getDocumentChanges()){
                           if(Objects.equals(change.getDocument().getString(Constants.KEY_NOTIFICATION), Constants.KEY_MEET)){
                               String name = change.getDocument().getString(Constants.KEY_NAME);
                               NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                       .setSmallIcon(R.drawable.user_icon)
                                       .setContentTitle("FACE")
                                       .setContentText(name+"님의 대면 만남 신청이 왔습니다.")
                                       .setDefaults(Notification.DEFAULT_VIBRATE)
                                       .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                       .setAutoCancel(true)
                                       .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
                               NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                               notificationManager.notify(101, mBuilder.build());
                           }else if(Objects.equals(change.getDocument().getString(Constants.KEY_NOTIFICATION),Constants.KEY_COLLECTION_CHAT)){
                               String name = change.getDocument().getString(Constants.KEY_NAME);
                               String message = change.getDocument().getString(Constants.KEY_MESSAGE);
                               NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                       .setSmallIcon(R.drawable.user_icon)
                                       .setContentTitle("FACE")
                                       .setContentText(name+"  "+message)
                                       .setDefaults(Notification.DEFAULT_VIBRATE)
                                       .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                       .setAutoCancel(true)
                                       .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
                               NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                               notificationManager.notify(101, mBuilder.build());
                           }else if(Objects.equals(change.getDocument().getString(Constants.KEY_NOTIFICATION),Constants.KEY_FAMILY_REQUEST)){
                               String name = change.getDocument().getString(Constants.KEY_NAME);
                               NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                       .setSmallIcon(R.drawable.user_icon)
                                       .setContentTitle("FACE")
                                       .setContentText(name+"님의 가족 요청이 들어왔어요!")
                                       .setDefaults(Notification.DEFAULT_VIBRATE)
                                       .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                       .setAutoCancel(true)
                                       .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
                               NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                               notificationManager.notify(101, mBuilder.build());
                           }else{
                               Log.e("This", "Is problem");
                           }
                       }
                   });
    }
}
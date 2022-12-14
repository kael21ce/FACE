package org.techtown.face.utilites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    FirebaseFirestore db;
    PreferenceManager preferenceManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        long now = System.currentTimeMillis();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(context);
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.w(TAG, "onReceive() is called");

            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = parseSmsMessage(bundle);

            if (messages != null && messages.length > 0) {
                String sender = messages[0].getOriginatingAddress();
                Log.w(TAG, "SMS sender : " + sender);

                String contents = messages[0].getMessageBody();
                Log.w(TAG, "SMS contents : " + contents);

                Long receivedTime = messages[0].getTimestampMillis();
                Log.w(TAG, "SMS received timestamp : " + receivedTime);

                //Sms 발신 시 발신자 쪽에서 표정 업데이트
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .whereEqualTo(Constants.KEY_MOBILE, sender)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //상대방 데이터베이스에 내 expression 업데이트
                                    Log.w(TAG, "here1: " + document.getId());
                                    String senderId = document.getId();
                                    db.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(userId)
                                            .collection(Constants.KEY_COLLECTION_USERS)
                                            .whereEqualTo(Constants.KEY_USER, senderId)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                                .document(senderId)
                                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                                .whereEqualTo(Constants.KEY_USER, userId)
                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                                        if (task2.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot snapshot : task2.getResult()) {
                                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                                        .document(senderId)
                                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                                        .document(snapshot.getId()).update(Constants.KEY_EXPRESSION, 5);
                                                                                Log.w(TAG, senderId + ": 내 expression 업데이트 됨");
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                    //상대방 데이터베이스에 내 window 업데이트
                                    db.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(userId)
                                            .collection(Constants.KEY_COLLECTION_USERS)
                                            .whereEqualTo(Constants.KEY_USER, senderId)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                                .document(senderId)
                                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                                .whereEqualTo(Constants.KEY_USER, userId)
                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                                        if (task2.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot snapshot : task2.getResult()) {
                                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                                        .document(senderId)
                                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                                        .document(snapshot.getId()).update(Constants.KEY_WINDOW, now);
                                                                                Log.w(TAG, senderId + ": 내 window 업데이트 됨");
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                //For ScaleInfo: 데이터베이스에 정보 올리기
                Map<String, Object> sms = new HashMap<>();
                sms.put(Constants.KEY_SENDER, sender);
                sms.put(Constants.KEY_RECEIVER_ID, userId);
                sms.put(Constants.KEY_RECEIVED_TIME, receivedTime);
                sms.put(Constants.KEY_RECEIVED_MOBILE, preferenceManager.getString(Constants.KEY_MOBILE));
                db.collection(Constants.KEY_COLLECTION_SMS)
                        .add(sms)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Log.w(TAG, "Sender: " + sender);
                                Log.w(TAG, "Receiver_id: " + userId);
                                Log.w(TAG, "Received time: " + receivedTime);
                                Log.w(TAG, "Received mobile: " + preferenceManager.getString(Constants.KEY_MOBILE));
                            }
                        });
            }
        }
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle) {
        Object[] objects = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objects.length];

        int smsCount = objects.length;
        for (int i = 0; i < smsCount; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) objects[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) objects[i]);
            }
        }
        return messages;
    }
}
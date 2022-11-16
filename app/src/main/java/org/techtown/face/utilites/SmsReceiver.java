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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    FirebaseFirestore db;
    PreferenceManager preferenceManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        long now = System.currentTimeMillis();
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
                db = FirebaseFirestore.getInstance();
                preferenceManager = new PreferenceManager(context);
                String userId = preferenceManager.getString(Constants.KEY_USER_ID);
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(userId)
                        .collection(Constants.KEY_COLLECTION_USERS)
                        .whereEqualTo(Constants.KEY_MOBILE, sender)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //상대방 데이터베이스에 내 expression 업데이트
                                    db.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(document.getId())
                                            .collection(Constants.KEY_COLLECTION_USERS)
                                            .whereEqualTo(Constants.KEY_USER, userId)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                                .document(document.getId())
                                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                                .document(documentSnapshot.getId())
                                                                .update(Constants.KEY_EXPRESSION, 5);
                                                        Log.w(TAG, document.getId() + "내 expression 업데이트 됨");
                                                    }
                                                }
                                            });
                                    //상대방 데이터베이스에 내 window 업데이트
                                    db.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(document.getId())
                                            .collection(Constants.KEY_COLLECTION_USERS)
                                            .whereEqualTo(Constants.KEY_USER, userId)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                                .document(document.getId())
                                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                                .document(documentSnapshot.getId())
                                                                .update(Constants.KEY_WINDOW, now);
                                                        Log.w(TAG, document.getId() + "내 window 업데이트 됨");
                                                    }
                                                }
                                            });
                                }
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
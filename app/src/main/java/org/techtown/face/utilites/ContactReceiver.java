package org.techtown.face.utilites;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class ContactReceiver extends BroadcastReceiver {

    String RTAG = "ContactReceiver";
    PreferenceManager preferenceManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FACEdatabase";
    String phonestate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        preferenceManager = new PreferenceManager(context.getApplicationContext());
        long now = System.currentTimeMillis();
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            Log.i(RTAG, "onReceive() is called.");
            TelecomManager telephonyManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String state = extras.getString(TelephonyManager.EXTRA_STATE);
                if (state.equals(phonestate)) {
                    return;
                } else {
                    phonestate = state;
                }
                if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    String callingMobile = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.i(TAG, "Calling mobile: " + callingMobile);
                    preferenceManager = new PreferenceManager(context);
                    db.collection(Constants.KEY_COLLECTION_USERS)
                            .get()
                            .addOnCompleteListener(task -> {
                                String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (Objects.equals(document.get(Constants.KEY_MOBILE), callingMobile)) {
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(document.getId())
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_EXPRESSION, 5);
                                                                Log.w(RTAG, document.getId() + "내 expression 업데이트 됨");
                                                            }
                                                        }
                                                    });
                                            //상대방 데이터베이스에 내 window 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(document.getId())
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_WINDOW, now);
                                                                Log.w(RTAG, document.getId() + "내 window 업데이트 됨");
                                                            }
                                                        }
                                                    });
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, document.getId())
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(currentUserId)
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_EXPRESSION, 5);
                                                                Log.w(RTAG, currentUserId + "상대방 expression 업데이트 됨");
                                                            }
                                                        }
                                                    });
                                            //나의 데이터베이스에 상대방 window 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, document.getId())
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(currentUserId)
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_WINDOW, now);
                                                                Log.w(RTAG, currentUserId + "상대방 window 업데이트 됨");
                                                            }
                                                        }
                                                    });
                                            Log.i(TAG, "Update expression and window by " + RTAG);
                                        } else {
                                            Log.i(RTAG, "Not update " + TAG);
                                        }
                                    }
                                }
                            });
                }
            }
        }

    }
}
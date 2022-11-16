package org.techtown.face.utilites;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//의사소통의 양 측정을 위한 메소드를 포함하고 있는 클래스
public class ScaleInfo extends ContentProvider {

    String[] callSet= new String[] { CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION };
    long now = System.currentTimeMillis();
    //데이터 수집 윈도우: 일주일
    public long weekago = now - 604800000;
    //FACEdatabase
    PreferenceManager preferenceManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FACEdatabase";

    public Handler handler = new Handler();


    @Override
    public boolean onCreate() {
        return true;
    }

    //입력된 연락처로부터 수신된 횟수 가져오기
    public int getIncomingNum(Context context, String mobile) {
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                callSet, null, null, null);
        int numIncoming = 0;
        if (cursor == null) {
            return 0;
        }

        int recordCount = cursor.getCount();
        cursor.moveToFirst();

        for (int i=0; i < recordCount; i++) {
            String shiftMobile = cursor.getString(2);
            if (shiftMobile.equals(mobile)) {
                if (cursor.getLong(0)>=weekago) {
                    //통화 시간이 20초 이상인 연락만 인정
                    if (Integer.parseInt(cursor.getString(3))>=20) {
                        if (cursor.getInt(1) == CallLog.Calls.INCOMING_TYPE) {
                            numIncoming += 1;
                        }
                        cursor.moveToNext();
                    } else {
                        cursor.moveToNext();
                    }
                } else {
                    cursor.moveToNext();
                }
            } else {
                cursor.moveToNext();
            }
        }
        cursor.close();
        return numIncoming;
    }

    //입력된 연락처에게 발신한 횟수 가져오기
    public int getOutgoingNum(Context context, String mobile) {
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                callSet, null, null, null);
        int numOutgoing = 0;
        if (cursor == null) {
            return 0;
        }

        int recordCount = cursor.getCount();
        cursor.moveToFirst();

        for (int i=0; i < recordCount; i++) {
            String shiftMobile = cursor.getString(2);
            if (shiftMobile.equals(mobile)) {
                if (cursor.getLong(0)>=weekago) {
                    //통화 시간이 20초 이상인 연락만 인정
                    if (Integer.parseInt(cursor.getString(3))>=20) {
                        if (cursor.getInt(1) == CallLog.Calls.OUTGOING_TYPE) {
                            numOutgoing += 1;
                        }
                        cursor.moveToNext();
                    } else {
                        cursor.moveToNext();
                    }
                } else {
                    cursor.moveToNext();
                }
            } else {
                cursor.moveToNext();
            }
        }
        cursor.close();
        return numOutgoing;
    }

    //입력한 연락처에서 채팅 받은 횟수 가져오기
    public void getInboxNum(Context context, String mobile) {
        preferenceManager = new PreferenceManager(context);
        preferenceManager.putInt("in" + mobile, 0);
        //db에서 상대방 document ID로 필터링
        db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            if (mobile.equals(document.get(Constants.KEY_MOBILE))) {
                                String senderId = document.getId();
                                //chat 컬렉션에서 senderId와 일치하는 대화 가져오기
                                db.collection(Constants.KEY_COLLECTION_CHAT)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            int num = 0;
                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                if (senderId.equals(documentSnapshot.get(Constants.KEY_SENDER_ID))
                                                && currentUserId.equals(documentSnapshot.get(Constants.KEY_RECEIVER_ID))) {
                                                    num+=1;
                                                    Log.w(TAG, "Successfully counted: " + mobile);
                                                } else {
                                                    Log.w(TAG, "no chat");
                                                }
                                            }
                                            Log.w(TAG, "Result for " + mobile + ": " + num);
                                            preferenceManager.putInt("in" + mobile , num);
                                        });
                            }
                        }
                    }
                });
        handler.postDelayed(() -> Log.w("FACEdatabase", "Inbox-" + mobile + ": " + preferenceManager.getInt("in" + mobile)), 1000);
    }

    //입력한 연락처에게 채팅 보낸 횟수 가져오기
    public void getSentNum(Context context, String mobile) {
        preferenceManager = new PreferenceManager(context);
        preferenceManager.putInt("out" + mobile, 0);
        //db에서 상대방 document ID로 필터링
        db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (task.isSuccessful()) {
                            if (mobile.equals(document.get(Constants.KEY_MOBILE))) {
                                String receiverId = document.getId();
                                //chat 컬렉션에서 senderId와 일치하는 대화 가져오기
                                db.collection(Constants.KEY_COLLECTION_CHAT)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            int num = 0;
                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                if (receiverId.equals(documentSnapshot.get(Constants.KEY_RECEIVER_ID))
                                                        && currentUserId.equals(documentSnapshot.get(Constants.KEY_SENDER_ID))) {
                                                    num+=1;
                                                    Log.w(TAG, "Successfully counted: " + mobile);
                                                }else {
                                                    Log.w(TAG, "no chat");
                                                }
                                            }
                                            Log.w(TAG, "Result for " + mobile + ": " + num);
                                            preferenceManager.putInt("out" + mobile , num);
                                        });
                            }
                        }
                    }
                });
        handler.postDelayed(() -> Log.w("FACEdatabase", "Sent-" + mobile + ": " + preferenceManager.getInt("out" + mobile)), 1000);
    }

    //입력한 연락처로부터 받은 sms 가져오기
    public void getReceivedNum(Context context, String mobile) {
        preferenceManager = new PreferenceManager(context);
        preferenceManager.putInt("received" + mobile, 0);
        db.collection(Constants.KEY_COLLECTION_SMS)
                .whereEqualTo(Constants.KEY_SENDER, mobile)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int num = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            num += 1;
                        }
                        preferenceManager.putInt("received" + mobile, num);
                    }
                });
        handler.postDelayed(() -> Log.w("FACEdatabase", "Received-" + mobile + ": " + preferenceManager.getInt("received" + mobile)), 1000);
    }

    //입력한 연락처에 보낸 sms 가져오기
    public void getSendNum(Context context, String mobile, String myMobile) {
        preferenceManager = new PreferenceManager(context);
        //String myId = preferenceManager.getString(Constants.KEY_USER_ID);
        preferenceManager.putInt("send" + mobile, 0);
        db.collection(Constants.KEY_COLLECTION_SMS)
                .whereEqualTo(Constants.KEY_SENDER, myMobile)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int num = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.get(Constants.KEY_RECEIVED_MOBILE).toString().equals(mobile)) {
                                num += 1;
                            }
                        }
                        preferenceManager.putInt("send" + mobile, num);
                    }
                });
        handler.postDelayed(() -> Log.w("FACEdatabase", "Send-" + mobile + ": " + preferenceManager.getInt("send" + mobile)), 1000);
    }

    //각도 산출하기
    public void getAngle(Context context, String mobile,String myMobile) {
        final float[] angle = new float[1];
        final float[] y = new float[1];
        //통화
        int numCall = getIncomingNum(context, mobile)-getOutgoingNum(context, mobile);
        //채팅 & SMS
        final int[] numChat = {0};
        final int[] numSMS = {0};
        preferenceManager = new PreferenceManager(context);
        getInboxNum(context, mobile);
        getSentNum(context, mobile);
        getReceivedNum(context, mobile);
        getSendNum(context, mobile, myMobile);
        handler.postDelayed(() -> {
            numChat[0] = preferenceManager.getInt("in" + mobile)
                    -preferenceManager.getInt("out" + mobile);
            numSMS[0] = preferenceManager.getInt("received" + mobile) - preferenceManager.getInt("send" + mobile);
            Log.w("FACEdatabase", "call: " + numCall + " & " + "chat: " + numChat[0]);
            //연락 수 차이
            y[0] = 1.0f * numCall + 1.0f * numChat[0] + 1.0f * numSMS[0];
            //각도 계산
            if (Float.compare(y[0], 10)==1 || Float.compare(y[0], -10)==1) {
                angle[0] = 4* y[0];
            } else {
                if (Float.compare(y[0], 0)==1) {
                    angle[0] = 45.0f;
                } else {
                    angle[0] = -45.0f;
                }
            }
            Log.w("ScaleInfo", "Angle resulted: " + angle[0]);
            preferenceManager.putFloat("angle" + mobile, angle[0]);
        }, 1000);
    }

   @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

package org.techtown.face.utilites;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.atomic.AtomicInteger;

//의사소통의 양 측정을 위한 메소드를 포함하고 있는 클래스
public class ScaleInfo extends ContentProvider {

    String[] callSet;
    long now = System.currentTimeMillis();
    //데이터 수집 윈도우: 일주일
    public long weekago = now - 604800000;
    //FACEdatabase
    PreferenceManager preferenceManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public boolean onCreate() {
        return true;
    }

    //getIncomingNum: 입력된 연락처로부터 수신된 횟수 가져오기
    public int getIncomingNum(Context context, String mobile) {
        callSet = new String[] { CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.NUMBER,
                CallLog.Calls.DURATION };
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
        callSet = new String[] { CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.NUMBER,
                CallLog.Calls.DURATION };
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
                        if (cursor.getInt(1) == CallLog.Calls.INCOMING_TYPE) {
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

    //입력된 연락처와의 통화 수 차이 가져오기
    public int differenceCall(Context context, String mobile) {
        int numI = getIncomingNum(context, mobile);
        int numO = getOutgoingNum(context, mobile);

        return numI-numO;
    }

    //입력한 연락처에서 채팅 받은 횟수 가져오기
    public int getInboxNum(String mobile) {
        AtomicInteger numInbox = new AtomicInteger();
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
                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                if (senderId.equals(documentSnapshot.get(Constants.KEY_SENDER_ID))
                                                && currentUserId.equals(documentSnapshot.get(Constants.KEY_RECEIVER_ID))) {
                                                    numInbox.addAndGet(1);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
        return numInbox.get();
    }

    //입력한 연락처에게 채팅 보낸 횟수 가져오기
    public int getSentNum(String mobile) {
        AtomicInteger numSent = new AtomicInteger();
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
                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                if (receiverId.equals(documentSnapshot.get(Constants.KEY_RECEIVER_ID))
                                                        && currentUserId.equals(documentSnapshot.get(Constants.KEY_SENDER_ID))) {
                                                    numSent.addAndGet(1);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
        return numSent.get();
    }

    //입력된 연락처와의 채팅 수 차이 가져오기
    public int differenceChat(String mobile) {
        int numI = getInboxNum(mobile);
        int numS = getSentNum(mobile);

        return numI-numS;
    }

    //입력된 연락처와의 연락 수 차이 가져오기
    public float differenceContact(Context context, String mobile) {
        int numCall = differenceCall(context, mobile);
        int numChat = differenceChat(mobile);
        return 1.0f * numCall + 1.0f * numChat;
    }

    //각도 산출하기
    public float getAngle(Context context, String mobile) {
        float angle;
        float y = differenceContact(context, mobile);
        if (Float.compare(y, 10)==1 || Float.compare(y, -10)==1) {
            angle = 4*y;
        } else {
            if (Float.compare(y, 0)==1) {
                angle = 45.0f;
            } else {
                angle = -45.0f;
            }
        }

        return angle;
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

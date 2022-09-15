package org.techtown.face.utilites;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;

//의사소통의 양 측정을 위한 메소드를 포함하고 있는 클래스
public class ScaleInfo extends ContentProvider {

    SimpleDateFormat simpleDateFormat;
    String[] callSet;
    long now = System.currentTimeMillis();
    //데이터 수집 윈도우: 일주일
    long weekago = now - 604800000;

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
                //통화 시간이 5초 이상인 연락만 인정
                if (Integer.parseInt(cursor.getString(3))>=5) {
                    if (cursor.getInt(1) == CallLog.Calls.INCOMING_TYPE) {
                        numIncoming += 1;
                    } else {
                        numIncoming = numIncoming;
                    }
                    cursor.moveToNext();
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
                //통화 시간이 5초 이상인 연락만 인정
                if (Integer.parseInt(cursor.getString(3))>=5) {
                    if (cursor.getInt(1) == CallLog.Calls.OUTGOING_TYPE) {
                        numOutgoing += 1;
                    } else {
                        numOutgoing = numOutgoing;
                    }
                    cursor.moveToNext();
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

    //입력된 연락처와의 연락 수 차이 가져오기
    public float differenceContact(Context context, String mobile) {
        int numCall = differenceCall(context, mobile);
        float y = 1.0f* numCall;
        return y;
    }

    //각도 산출하기
    public float getAngle(Context context, String mobile) {
        float angle = 0;
        float y = differenceContact(context, mobile);
        if (y<10 || y>-10) {
            angle = 4*y;
        } else {
            if (y>0) {
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

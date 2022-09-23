package org.techtown.face.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.techtown.face.utilites.ScaleInfo;

public class ContactService extends Service {

    String TAG = "ContactService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate() is called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_STICKY;
        } else {
            //전화번호 가져오기
            String mobile = intent.getStringExtra("mobile");
            Log.i(TAG, "Given Mobile: " + mobile);
            //전송할 인텐트 생성
            Intent contactIntent = new Intent(getApplicationContext(), ScaleInfo.class);
            contactIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            contactIntent.putExtra("newMobile", mobile);
            startActivity(contactIntent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public ContactService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
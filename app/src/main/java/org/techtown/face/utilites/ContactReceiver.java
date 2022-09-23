package org.techtown.face.utilites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class ContactReceiver extends BroadcastReceiver {

    String RTAG = "ContactReceiver";
    String STAG = "ContactService";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FACEdatabase";


    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Log.i(RTAG, "onReceive() is called.");
        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            String callingMobile = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i(TAG, "Calling mobile: " + callingMobile);
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (Objects.equals(document.get(Constants.KEY_MOBILE), callingMobile)) {
                                    //데이터베이스에 전화번호 있을 때만 ContactService로 인텐트 전송
                                    Intent serviceIntent = new Intent(context, ContactService.class);
                                    serviceIntent.putExtra("mobile", callingMobile);
                                    context.startService(serviceIntent);
                                    Log.i(STAG, "Start Service");
                                } else {
                                    Log.i(RTAG, "Not send to service");
                                }
                            }
                        }
                    });
        }

    }
}
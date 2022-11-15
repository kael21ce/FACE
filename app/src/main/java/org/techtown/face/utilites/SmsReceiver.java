package org.techtown.face.utilites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

//import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.w(TAG, "onReceive() is called");

            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = parseSmsMessage(bundle);

            if (messages != null && messages.length > 0) {
                String sender = messages[0].getOriginatingAddress();
                Log.w(TAG, "SMS sender : " + sender);

                String contents = messages[0].getMessageBody();
                Log.w(TAG, "SMS contents : " + contents);

                //Date receivedDate = new Date(messages[0].getTimestampMillis());
                Long receivedTime = messages[0].getTimestampMillis();
                Log.w(TAG, "SMS received timestamp : " + receivedTime);
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
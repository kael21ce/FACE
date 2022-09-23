package org.techtown.face.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ContactService extends Service {
    public ContactService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
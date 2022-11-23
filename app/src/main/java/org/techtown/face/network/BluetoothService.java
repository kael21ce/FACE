package org.techtown.face.network;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.techtown.face.utilites.ConnectedThread;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothService extends Service {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PreferenceManager preferenceManager;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    ConnectedThread connectedThread;
    BluetoothSocket btSocket;
    BluetoothDevice device;
    final static String TAG = "FACEBluetooth";
    Handler mHandler = new Handler();

    IBinder btBinder = new BtBinder();

    public class BtBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "onBind() 호출됨.");
        preferenceManager = new PreferenceManager(getApplicationContext());
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "블루투스 권한 확인 필요", Toast.LENGTH_SHORT).show();
        }
        db.collection(Constants.KEY_COLLECTION_GARDEN).whereEqualTo(Constants.KEY_USER, myId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String registeredId = document.getString(Constants.KEY_REGISTERED);
                            final String[] expresison = new String[1];
                            final String[] userName = new String[1];
                            final String[] inString = new String[1];
                            //표정 가져오기
                            db.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(myId)
                                    .collection(Constants.KEY_COLLECTION_USERS)
                                    .whereEqualTo(Constants.KEY_USER, registeredId)
                                    .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                expresison[0] = documentSnapshot.get(Constants.KEY_EXPRESSION).toString();
                                                userName[0] = documentSnapshot.get(Constants.KEY_NAME).toString();
                                                inString[0] = expresison[0] + "," + userName[0];
                                            }
                                        }
                                    });
                            mHandler.postDelayed(() -> {
                                device = btAdapter.getRemoteDevice(document.getString(Constants.KEY_ADDRESS));
                                Log.w(TAG, "isConnected: " + isConnected(device));
                                if (!isConnected(device)) {
                                    @NonNull ConnectedThread forConnect = writeToDevice(document.getString(Constants.KEY_ADDRESS), inString[0]);
                                    writeForDevice(forConnect, inString[0]);
                                    Log.w(TAG, document.getString(Constants.KEY_NAME) + "에 전해진 표정: " + expresison[0]);
                                    final String[] userInId = new String[1];
                                    db.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(myId)
                                            .collection(Constants.KEY_COLLECTION_USERS)
                                            .whereEqualTo(Constants.KEY_USER, registeredId)
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task15) {
                                                    if (task15.isSuccessful()) {
                                                        for (DocumentSnapshot snapshot : task15.getResult()) {
                                                            userInId[0] = snapshot.getId();
                                                        }
                                                    }
                                                }
                                            });
                                    mHandler.postDelayed(() -> {
                                        DocumentReference ref = db.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(myId)
                                                .collection(Constants.KEY_COLLECTION_USERS).document(userInId[0]);
                                        ref.addSnapshotListener((documentSnapshot, error) -> {
                                            Log.w("BluetoothService", "onEvent 호출됨.");
                                            String darkness = documentSnapshot.get(Constants.KEY_EXPRESSION).toString();
                                            String name = documentSnapshot.getString(Constants.KEY_NAME);
                                            String sentOne = darkness + "," + name;
                                            if (!inString[0].equals(sentOne)) {
                                                writeForDevice(forConnect ,sentOne);
                                            }
                                        });
                                    }, 1000);
                                }
                            }, 1000);
                        }
                    }
                });
        return btBinder;
    }

    //createBluetoothSocket 메서드
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass()
                    .getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, uuid);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "블루투스 권한 허용이 필요합니다.",
                    Toast.LENGTH_LONG).show();
        }
        return device.createRfcommSocketToServiceRecord(uuid);
    }

    //기기의 연결 상태 확인
    public boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressLint("MissingPermission")
    public ConnectedThread writeToDevice(String deviceAddress, String input) {
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
        boolean flag = true;
        try {
            btSocket = createBluetoothSocket(device);
            try {
                btSocket.connect();
            } catch (IOException closeException) {
                flag = false;
                Log.e(TAG, "Could not close the client socket", closeException);
            }
        } catch (IOException e) {
            flag = false;
            Log.e(TAG, "Not in location: " + device.getName(), e);
            Toast.makeText(getApplicationContext(), "기기가 주변에 없습니다."
                    , Toast.LENGTH_LONG).show();
        }

        if (flag) {
            connectedThread = new ConnectedThread(btSocket);
            connectedThread.start();
            //connectedThread.write(input);
        }
        return connectedThread;
    }

    public void writeForDevice(ConnectedThread connectedThread, String input) {
        connectedThread.write(input);
    }
}
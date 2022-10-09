package org.techtown.face.network;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.utilites.ConnectedThread;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PreferenceManager preferenceManager;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    ConnectedThread connectedThread;
    BluetoothSocket btSocket;
    final static String TAG = "FACEBluetooth";

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public BluetoothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        preferenceManager = new PreferenceManager(getApplicationContext());
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "블루투스 권한 확인 필요", Toast.LENGTH_SHORT).show();
        }
        Set<BluetoothDevice> bluetoothDevices = btAdapter.getBondedDevices();
        for (BluetoothDevice btDevice : bluetoothDevices) {
            db.collection(Constants.KEY_COLLECTION_GARDEN)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString(Constants.KEY_USER).equals(myId)) {
                                    String address = document.getString(Constants.KEY_ADDRESS);
                                    if (address.equals(btDevice.getAddress())) {
                                        boolean flag = true;
                                        if (isConnected(btDevice)) {
                                            try {
                                                btSocket = createBluetoothSocket(btDevice);
                                                btSocket.connect();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                flag = false;
                                            }
                                            if (flag) {
                                                //expression 가져오기
                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                        .document(myId)
                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                        .get()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                    if (Objects.equals(documentSnapshot.get(Constants.KEY_USER), document.get(Constants.KEY_REGISTERED))) {
                                                                        if (btSocket != null) {
                                                                            connectedThread = new ConnectedThread(btSocket);
                                                                            connectedThread.start();
                                                                        }
                                                                        connectedThread.write(documentSnapshot
                                                                                .get(Constants.KEY_EXPRESSION).toString());
                                                                        Log.w(TAG, "Expression " + documentSnapshot
                                                                                .get(Constants.KEY_EXPRESSION).toString()
                                                                                + " is sent to " + btDevice.getName());
                                                                    } else {
                                                                        Toast.makeText(getApplicationContext(),
                                                                                "가족 정원이 등록되지 않았습니다.",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getApplicationContext(),
                                                        "가족 정원이 주변에 있지 않습니다.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            try {
                                                btSocket = createBluetoothSocket(btDevice);
                                                btSocket.connect();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                flag = false;
                                            }
                                            if (flag) {
                                                connectedThread = new ConnectedThread(btSocket);
                                                connectedThread.start();
                                            } else {
                                                Toast.makeText(getApplicationContext(),
                                                        "가족 정원이 주변에 있지 않습니다.",
                                                        Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                        }
                                    }

                                }
                            }
                    });
        }
        return new BtBinder();
    }

    class BtBinder extends Binder {
        public void showBound() {
            Log.w(TAG, TAG + " 성공적으로 바인딩 됨.");
        }

        public void loseBinding() {
            Log.w(TAG, TAG + " 바인딩 해제 됨.");
        }
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
}
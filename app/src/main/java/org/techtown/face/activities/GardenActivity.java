package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.adapters.PairedAdapter;
import org.techtown.face.adapters.SurroundAdapter;
import org.techtown.face.models.Bluetooth;
import org.techtown.face.utilites.ConnectedThread;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class GardenActivity extends AppCompatActivity {

    TextView connectedExist;
    TextView toConnectExist;
    Button searchButton;
    RecyclerView connectedRecycler;
    RecyclerView toConnectRecycler;
    BluetoothAdapter btAdapter;
    BluetoothSocket btSocket;
    BluetoothDevice device;
    private final static int REQUEST_ENABLED_BT = 101;
    String garden = "GARDEN";
    String TAG = "Bluetooth";
    String trying = "TRY";
    String success = "SUCCESS";
    String failed = "FAILED";
    ConnectedThread connectedThread;

    //페어링된 기기 관련
    ArrayList<String> devicePairedArrayList;
    ArrayList<String> devicePairedNameList;
    Set<BluetoothDevice> pairedDevices;
    PairedAdapter pairedAdapter = new PairedAdapter();

    //주변 기기 관련
    ArrayList<String> deviceLocalArrayList;
    ArrayList<String> deviceLocalNameList;
    SurroundAdapter surroundAdapter = new SurroundAdapter();

    //데이터베이스
    PreferenceManager preferenceManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String fTAG = "FACEdatabase";

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden);

        preferenceManager = new PreferenceManager(GardenActivity.this);
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);

        connectedExist = findViewById(R.id.connectedExist);
        connectedExist.setVisibility(View.GONE);
        toConnectExist = findViewById(R.id.toConnectExist);
        searchButton = findViewById(R.id.searchButton);
        connectedRecycler = findViewById(R.id.connectedRecycler);
        toConnectRecycler = findViewById(R.id.toConnectRecycler);

        LinearLayoutManager layoutManagerC = new LinearLayoutManager(GardenActivity.this,
                LinearLayoutManager.VERTICAL, false);
        connectedRecycler.setLayoutManager(layoutManagerC);
        LinearLayoutManager layoutManagerT = new LinearLayoutManager(GardenActivity.this,
                LinearLayoutManager.VERTICAL, false);
        toConnectRecycler.setLayoutManager(layoutManagerT);

        //블루투스 연결 상태 확인
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "블루투스 권한 허용이 필요합니다.", Toast.LENGTH_LONG).show();
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLED_BT);
        }

        //페어링된 기기 목록
        devicePairedArrayList = new ArrayList<>();
        devicePairedNameList = new ArrayList<>();
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                if (deviceName.contains(garden)) {
                    devicePairedArrayList.add(deviceHardwareAddress);
                    devicePairedNameList.add(deviceName);
                }
            }
        } else {
            connectedExist.setVisibility(View.VISIBLE);
        }

        int pairedLength = devicePairedArrayList.size();
        for (int i = 0; i < pairedLength; i++) {
            pairedAdapter.addItem(new Bluetooth(devicePairedNameList.get(i)
                    , devicePairedArrayList.get(i), false));
        }
        connectedRecycler.setAdapter(pairedAdapter);

        //주변 기기 목록
        deviceLocalArrayList = new ArrayList<>();
        deviceLocalNameList = new ArrayList<>();
        //기기 검색
        if (ActivityCompat.checkSelfPermission(GardenActivity.this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(GardenActivity.this, "블루투스 권한 허용이 필요합니다."
                    , Toast.LENGTH_LONG).show();
            return;
        }
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
            } else {
                Toast.makeText(GardenActivity.this, "블루투스 어댑터 연결에 실패했습니다."
                        , Toast.LENGTH_LONG).show();
            }
        }

        //검색 버튼 클릭 시
        searchButton.setOnClickListener(view -> {
            toConnectExist.setVisibility(View.GONE);
            deviceLocalArrayList = new ArrayList<>();
            deviceLocalNameList = new ArrayList<>();
            //기기 검색
            if (ActivityCompat.checkSelfPermission(GardenActivity.this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(GardenActivity.this, "블루투스 권한 허용이 필요합니다."
                        , Toast.LENGTH_LONG).show();
                return;
            }
            if (btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery();
            } else {
                if (btAdapter.isEnabled()) {
                    btAdapter.startDiscovery();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver, filter);
                } else {
                    Toast.makeText(GardenActivity.this, "블루투스 어댑터 연결에 실패했습니다."
                            , Toast.LENGTH_LONG).show();
                }
            }
        });

        //터치 이벤트 추가 - 기기 연결 상태 바꾸는 것 필요
        pairedAdapter.setOnItemClickListener(((position, address, flag) -> {
            device = btAdapter.getRemoteDevice(address);
            flag = true;
            Log.d(TAG, "Clicked device: " + device.getName() + " / " + address);
            try {
                if (deviceLocalArrayList.contains(device.getAddress())) {
                    btSocket = createBluetoothSocket(device);
                    try {
                        btSocket.connect();
                    } catch (IOException closeException) {
                        flag = false;
                        Log.e(TAG, "Could not close the client socket", closeException);
                    }
                } else {
                    flag = false;
                    Log.w(TAG, "Not in location-1: " + device.getName());
                    Toast.makeText(GardenActivity.this, "기기가 주변에 없습니다."
                            , Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                flag = false;
                Log.e(TAG, "Not in location-2: " + device.getName(), e);
                Toast.makeText(GardenActivity.this, "기기가 주변에 없습니다."
                        , Toast.LENGTH_LONG).show();
            }

            if (flag) {
                if (btSocket != null) {
                    connectedThread = new ConnectedThread(btSocket);
                    connectedThread.start();
                } else {
                    Log.w(TAG, "Not in location-3: " + device.getName());
                    Toast.makeText(GardenActivity.this, "기기가 주변에 없습니다."
                            , Toast.LENGTH_LONG).show();
                }
            }
            if (connectedThread != null) {
                db.collection(Constants.KEY_COLLECTION_GARDEN)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //데이터베이스에 등록된 가족 정원이 있는 경우
                                    if (document.get(Constants.KEY_ADDRESS).equals(device.getAddress())) {
                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(myId)
                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                .get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                            if (documentSnapshot.get(Constants.KEY_USER)
                                                                    .equals(document.get(Constants.KEY_USER))) {
                                                                connectedThread.write(documentSnapshot
                                                                        .get(Constants.KEY_EXPRESSION).toString());
                                                                Log.w(fTAG, "Expression " + documentSnapshot
                                                                        .get(Constants.KEY_EXPRESSION).toString()
                                                                        + " is sent to " + device.getName());
                                                            } else {
                                                                Toast.makeText(GardenActivity.this,
                                                                        "가족 정원이 등록되지 않았습니다.",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });
                                    } else {
                                        //데이터베이스에 가족정원이 등록되지 않은 경우: 등록하기
                                        HashMap<String, Object> garden = new HashMap<>();
                                        garden.put(Constants.KEY_ADDRESS, device.getAddress().toString());
                                        garden.put(Constants.KEY_NAME, device.getName().toString());
                                        //등록할 유저 id 불러오기

                                    }
                                }
                            }
                        });
            }
        }));
    }

    //ACTION_FOUND 인텐트를 위한 브로드캐스트 리시버
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (device != null) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                    Log.w(TAG, "Address of " + deviceName + ": " + deviceHardwareAddress);
                    if (deviceName != null && deviceName.contains(garden)
                            && !deviceLocalNameList.contains(deviceName)) {
                        deviceLocalArrayList.add(deviceHardwareAddress);
                        deviceLocalNameList.add(deviceName);
                        //리사이클러뷰에 보여주기
                        int localLength = deviceLocalArrayList.size();
                        for (int i = 0; i < localLength; i++) {
                            surroundAdapter.addItem(new Bluetooth(deviceLocalNameList.get(i),
                                    deviceLocalArrayList.get(i), false));
                            Log.w(TAG, "Item is added: " + deviceLocalNameList.get(i));
                        }
                        toConnectRecycler.setAdapter(surroundAdapter);
                        if (localLength == 0) {
                            toConnectExist.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(GardenActivity.this, "블루투스 권한 허용이 필요합니다.",
                    Toast.LENGTH_LONG).show();
        }
        return device.createRfcommSocketToServiceRecord(uuid);
    }
}
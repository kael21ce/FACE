package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.techtown.face.R;
import org.techtown.face.adapters.PairedAdapter;
import org.techtown.face.adapters.SurroundAdapter;
import org.techtown.face.models.Bluetooth;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class GardenActivity extends AppCompatActivity {

    Button searchButton;
    RecyclerView connectedRecycler;
    RecyclerView toConnectRecycler;
    BluetoothAdapter btAdapter;
    private final static int REQUEST_ENABLED_BT = 101;
    String garden = "GARDEN";

    //페어링된 기기 관련
    ArrayList<String> devicePairedArrayList;
    ArrayList<String> devicePairedRight;
    Set<BluetoothDevice> pairedDevices;

    //주변 기기 관련
    ArrayList<String> deviceLocalArrayList;
    ArrayList<String> deviceLocalRight;

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden);

        LinearLayoutManager layoutManager = new LinearLayoutManager(GardenActivity.this,
                LinearLayoutManager.VERTICAL, false);

        searchButton = findViewById(R.id.searchButton);
        connectedRecycler = findViewById(R.id.connectedRecycler);
        toConnectRecycler = findViewById(R.id.toConnectRecycler);
        PairedAdapter pairedAdapter = new PairedAdapter();
        SurroundAdapter surroundAdapter = new SurroundAdapter();

        //블루투스 연결 상태 확인
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "블루투스 활성화가 필요합니다.", Toast.LENGTH_LONG).show();
                return;
            }
            startActivityForResult(btIntent, REQUEST_ENABLED_BT);
        }

        //페어링된 기기 목록
        devicePairedArrayList = new ArrayList<>();
        devicePairedRight = new ArrayList<>();
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                if (deviceName.contains(garden)) {
                    devicePairedArrayList.add(deviceHardwareAddress);
                    devicePairedRight.add(deviceName);
                }
            }
        }
        String faceDeviceAddress = "";
        int pairedLength = devicePairedArrayList.size();
        for (int i = 0; i < pairedLength; i++) {
            Bluetooth bt = new Bluetooth();
            bt.setDevice(devicePairedRight.get(i));
            bt.setAddress(devicePairedArrayList.get(i));
            pairedAdapter.addItem(bt);
        }
        connectedRecycler.setAdapter(pairedAdapter);

        //주변 기기 목록
        deviceLocalArrayList = new ArrayList<>();
        deviceLocalRight = new ArrayList<>();
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
            } else {
                Toast.makeText(GardenActivity.this, "주변에 기기가 없습니다." ,Toast.LENGTH_LONG).show();
            }
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int localLength = deviceLocalArrayList.size();
                for (int i = 0; i < localLength; i++) {
                    Bluetooth bt = new Bluetooth();
                    bt.setDevice(deviceLocalRight.get(i));
                    bt.setAddress(deviceLocalArrayList.get(i));
                    surroundAdapter.addItem(bt);
                    }
            }
        });




        //터치 이벤트 추가
        pairedAdapter.setOnItemClickListener(((position, item) -> {
            String deviceName = item.getDevice();

        }));
    }

    //ACTION_FOUND를 위한 브로드캐스트 리시버
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                deviceLocalArrayList.add(deviceHardwareAddress);
                deviceLocalRight.add(deviceName);
            }
        }
    };
}
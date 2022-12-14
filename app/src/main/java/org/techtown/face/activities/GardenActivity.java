package org.techtown.face.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.adapters.PairedAdapter;
import org.techtown.face.adapters.RegisterAdapter;
import org.techtown.face.adapters.SurroundAdapter;
import org.techtown.face.models.Bluetooth;
import org.techtown.face.models.Family;
import org.techtown.face.models.User;
import org.techtown.face.network.BluetoothService;
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

    TextView locationExist;
    Button searchLocation;
    RecyclerView gardenRecycler;
    RecyclerView locationRecycler;
    LinearLayout containerExist;
    BluetoothAdapter btAdapter;
    BluetoothSocket btSocket;
    BluetoothDevice device;
    BluetoothService btService;
    private final static int REQUEST_ENABLED_BT = 101;
    String garden = "GARDEN";
    String TAG = "FACEBluetooth";
    String userIdToRegister;
    ConnectedThread connectedThread;
    Dialog registerDialog;
    private SwipeRefreshLayout gardenSwipeRefresh;
    boolean isBtService = false;

    //???????????? ?????? ??????
    ArrayList<String> devicePairedArrayList;
    ArrayList<String> devicePairedNameList;
    Set<BluetoothDevice> pairedDevices;
    PairedAdapter pairedAdapter;

    //?????? ?????? ??????
    ArrayList<String> deviceLocalArrayList;
    ArrayList<String> deviceLocalNameList;
    SurroundAdapter surroundAdapter;

    //??????????????????
    PreferenceManager preferenceManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Handler mHandler = new Handler();

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //???????????? ??????????????? ??? ???????????? ?????????
            Log.w(TAG, iBinder + "?????????.");
            BluetoothService.BtBinder bb = (BluetoothService.BtBinder) iBinder;
            btService = bb.getService();
            isBtService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //???????????? ????????? ???????????? ??????????????? ??? ???????????? ?????????
            Log.w(TAG, componentName + "?????????.");
            isBtService = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garden);
        Log.d("GardenActivity", "onCreateView() ?????????.");

        //BluetoothService
        Intent btIntent = new Intent(GardenActivity.this, BluetoothService.class);
        bindService(btIntent, connection, Context.BIND_AUTO_CREATE);

        preferenceManager = new PreferenceManager(GardenActivity.this);
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);

        locationExist = findViewById(R.id.locationExist);
        searchLocation = findViewById(R.id.searchLocation);
        gardenRecycler = findViewById(R.id.gardenRecycler);
        locationRecycler = findViewById(R.id.locationRecycler);
        containerExist = findViewById(R.id.containerExist);

        pairedAdapter = new PairedAdapter();
        surroundAdapter = new SurroundAdapter();

        LinearLayoutManager layoutManagerG = new LinearLayoutManager(GardenActivity.this);
        layoutManagerG.setOrientation(LinearLayoutManager.HORIZONTAL);
        gardenRecycler.setLayoutManager(layoutManagerG);
        LinearLayoutManager layoutManagerT = new LinearLayoutManager(GardenActivity.this,
                LinearLayoutManager.VERTICAL, false);
        locationRecycler.setLayoutManager(layoutManagerT);

        //???????????? ?????? ?????? ??????
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(GardenActivity.this
                    , Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(GardenActivity.this, "???????????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLED_BT);
        } else {
            Toast.makeText(GardenActivity.this, "??????????????? ??????????????? ????????????.", Toast.LENGTH_SHORT).show();
        }

        //???????????? ?????? ??????
        devicePairedArrayList = new ArrayList<>();
        devicePairedNameList = new ArrayList<>();
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                if (deviceName!=null && deviceName.contains(garden)) {
                    devicePairedArrayList.add(deviceHardwareAddress);
                    devicePairedNameList.add(deviceName);
                }
            }
        } else {
            containerExist.setVisibility(View.VISIBLE);
        }

        int pairedLength = devicePairedArrayList.size();
        //???????????????????????? user??? myId??? ???????????? ?????? ????????????
        db.collection(Constants.KEY_COLLECTION_GARDEN).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    for (int i = 0; i < pairedLength; i++) {
                        if (devicePairedArrayList.get(i).equals(document.getString(Constants.KEY_ADDRESS))
                                && document.getString(Constants.KEY_USER).equals(myId)) {
                            pairedAdapter.addItem(new Bluetooth(document.getString(Constants.KEY_NAME),
                                    document.getString(Constants.KEY_ADDRESS), false));
                            Log.w(TAG, "????????? ????????? ?????????: " + document.getString(Constants.KEY_NAME));
                        }
                    }
                }
            }
            gardenRecycler.setAdapter(pairedAdapter);
            Log.w(TAG, "Adapter is set");
        });

        //?????? ?????? ??????
        deviceLocalArrayList = new ArrayList<>();
        deviceLocalNameList = new ArrayList<>();

        //?????? ?????? ?????? ???
        searchLocation.setOnClickListener(view -> {
            locationExist.setVisibility(View.GONE);
            deviceLocalArrayList = new ArrayList<>();
            deviceLocalNameList = new ArrayList<>();
            //?????? ??????
            if (ActivityCompat.checkSelfPermission(GardenActivity.this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(GardenActivity.this, "???????????? ?????? ????????? ???????????????."
                        , Toast.LENGTH_LONG).show();
                return;
            }
            if (btAdapter.isEnabled()) {
                if (btAdapter.isDiscovering()) {
                    btAdapter.cancelDiscovery();
                    Toast.makeText(GardenActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    surroundAdapter.clear();
                    btAdapter.startDiscovery();
                    Toast.makeText(GardenActivity.this, "?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver, filter);
                }
            } else {
                Toast.makeText(GardenActivity.this, "??????????????? ?????????????????? ????????????.", Toast.LENGTH_SHORT).show();
            }
        });

        //?????? ????????? ?????? - ?????? ?????? ?????? ????????? ??? ??????
        surroundAdapter.setOnItemClickListener((position, address) -> {
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            Log.d(TAG, "Clicked device: " + device.getName() + " / " + address);
            final String[] userId = {""};
            db.collection(Constants.KEY_COLLECTION_GARDEN).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (address.equals(document.getString(Constants.KEY_ADDRESS))) {
                            userId[0] = document.getString(Constants.KEY_USER);
                            Log.w(TAG, "?????? ?????????: " + userId[0]);
                        }
                    }
                }
            });
            mHandler.postDelayed(() -> {
                if (userId[0].equals(myId)) {
                    Toast.makeText(GardenActivity.this, "????????? ?????? ???????????? ????????????.", Toast.LENGTH_SHORT);
                } else {
                    connectedThread = registerDevice(GardenActivity.this, address);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //??????
                            getExpressionFromDevice(device);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.w(TAG, "inString: " + preferenceManager.getString(device.getAddress() + myId));
                                    connectedThread.write(preferenceManager.getString(device.getAddress() + myId));
                                }
                            },1000);
                        }
                    },9000);
                }
            }, 1000);
        });

        //???????????? ????????????
        gardenSwipeRefresh = findViewById(R.id.gardenSwipeLayout);
        gardenSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshActivity();
                        gardenSwipeRefresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private void refreshActivity() {
        finish();
        overridePendingTransition(0,0);
        Intent refreshIntent = getIntent();
        startActivity(refreshIntent);
        overridePendingTransition(0,0);
    }

    private void calculateExpression() {
        Intent refreshIntent = new Intent(GardenActivity.this, BluetoothService.class);
        unbindService(connection);
        mHandler.postDelayed(() -> bindService(refreshIntent, connection, BIND_AUTO_CREATE),500);
    }

    //ACTION_FOUND ???????????? ?????? ?????????????????? ?????????
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (device != null) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();
                    Log.w(TAG, "Address of " + deviceName + ": " + deviceHardwareAddress);
                    if (deviceName != null && deviceName.contains(garden)) {
                        deviceLocalArrayList.add(deviceHardwareAddress);
                        deviceLocalNameList.add(deviceName);
                        //????????????????????? ????????????
                        int localLength = deviceLocalArrayList.size();
                        for (int i = 0; i < localLength; i++) {
                            surroundAdapter.addItem(new Bluetooth(deviceLocalNameList.get(i),
                                    deviceLocalArrayList.get(i), false));
                            Log.w(TAG, "Item is added in locationRecycler: " + deviceLocalNameList.get(i));
                        }
                        locationRecycler.setAdapter(surroundAdapter);
                        if (localLength == 0) {
                            locationExist.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("GardenFragment", "onDestroyView() ?????????.");
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    //createBluetoothSocket ?????????
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
            Toast.makeText(getApplicationContext(), "???????????? ?????? ????????? ???????????????.",
                    Toast.LENGTH_LONG).show();
        }
        return device.createRfcommSocketToServiceRecord(uuid);
    }

    public void showRegisterDialog(BluetoothDevice device) {
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);
        final String[] registedId = {null};
        registerDialog.show();
        //????????????????????? ??????
        RecyclerView registerRecycler = registerDialog.findViewById(R.id.registerRecycler);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(registerDialog.getContext(), LinearLayoutManager.VERTICAL, false);
        registerRecycler.setLayoutManager(layoutManager);
        RegisterAdapter registerAdapter = new RegisterAdapter();

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(myId)
                .collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = new User();
                            user.id = document.get(Constants.KEY_USER).toString();
                            final String[] name = new String[1];
                            db.collection(Constants.KEY_COLLECTION_USERS)
                                    .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                if (documentSnapshot.getId().equals(user.id)) {
                                                    name[0] = documentSnapshot.get(Constants.KEY_NAME).toString();
                                                    preferenceManager.putString("register" + user.id
                                                            , name[0]);
                                                }
                                            }
                                        }
                                    });
                            //????????????????????? ???????????? ????????? ??????
                            mHandler.postDelayed(() -> {
                                user.name = preferenceManager.getString("register" + user.id);
                                registerAdapter.addItem(new Family(user));
                            }, 1000);
                            Log.w(TAG, "Item is added: name-"
                                    + preferenceManager.getString("register" + user.id)
                                    + " / address-" + user.id);
                        }
                        mHandler.postDelayed(() -> registerRecycler.setAdapter(registerAdapter), 1000);
                    }

                });
        //????????? ?????? ?????????
        registerAdapter.setOnItemClickListener(((position, userId) -> {
            registedId[0] = userId;
            Log.w(TAG, "Touched: " + registedId[0]);
        }));

        //?????? ??????
        Button rejectButton = registerDialog.findViewById(R.id.rejectButton);
        rejectButton.setOnClickListener(view -> registerDialog.dismiss());

        //?????? ??????
        Button selectButton = registerDialog.findViewById(R.id.selectButton);
        selectButton.setOnClickListener(view -> {
            if (registedId[0] == null) {
                Toast.makeText(getApplicationContext(), "????????? ?????? ????????????.",
                        Toast.LENGTH_SHORT).show();
                registerDialog.dismiss();
            } else {
                userIdToRegister = registedId[0];
                HashMap<String, Object> garden = new HashMap<>();
                garden.put(Constants.KEY_ADDRESS, device.getAddress());
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "???????????? ?????? ????????? ???????????????.",
                            Toast.LENGTH_LONG).show();
                }
                garden.put(Constants.KEY_NAME, device.getName());
                garden.put(Constants.KEY_REGISTERED, userIdToRegister);
                garden.put(Constants.KEY_USER, myId);
                db.collection(Constants.KEY_COLLECTION_GARDEN).add(garden);
                Log.w(TAG, "?????? ????????? ?????????????????????.");
                Log.w(TAG, "?????? id: " + userIdToRegister);
                Toast.makeText(getApplicationContext(), "?????????????????????!", Toast.LENGTH_SHORT).show();
                registerDialog.dismiss();
                refreshActivity();
            }
        });
    }
    @SuppressLint("MissingPermission")
    private ConnectedThread registerDevice(Context context, String deviceAddress) {
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        device = btAdapter.getRemoteDevice(deviceAddress);
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
            Log.e(TAG, "Not in location-2: " + device.getName(), e);
            Toast.makeText(context, "????????? ????????? ????????????."
                    , Toast.LENGTH_LONG).show();
        }

        if (flag) {
            connectedThread = new ConnectedThread(btSocket);
            connectedThread.start();
            //????????????
            registerDialog = new Dialog(context);
            registerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            registerDialog.setContentView(R.layout.dialog_btregister);
            showRegisterDialog(device);
        }
        return connectedThread;
    }

    private void getExpressionFromDevice(BluetoothDevice device) {
        final String[] string = new String[1];
        preferenceManager = new PreferenceManager(getApplicationContext());
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "???????????? ?????? ?????? ??????", Toast.LENGTH_SHORT).show();
        }
        db.collection(Constants.KEY_COLLECTION_GARDEN).whereEqualTo(Constants.KEY_USER, myId)
                .whereEqualTo(Constants.KEY_ADDRESS, device.getAddress())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String registeredId = document.getString(Constants.KEY_REGISTERED);
                            final String[] expresison = new String[1];
                            final String[] userName = new String[1];
                            //?????? ????????????
                            db.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(myId)
                                    .collection(Constants.KEY_COLLECTION_USERS)
                                    .whereEqualTo(Constants.KEY_USER, registeredId)
                                    .get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                expresison[0] = documentSnapshot.get(Constants.KEY_EXPRESSION).toString();
                                                userName[0] = documentSnapshot.get(Constants.KEY_NAME).toString();
                                                string[0] = expresison[0] + "," + userName[0];
                                                String key = device.getAddress() + myId;
                                                preferenceManager.putString(key, string[0]);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}

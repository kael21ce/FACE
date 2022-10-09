package org.techtown.face.fragments;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class GardenFragment extends Fragment {
    TextView locationExist;
    Button searchLocation;
    RecyclerView gardenRecycler;
    RecyclerView locationRecycler;
    LinearLayout containerExist;
    BluetoothAdapter btAdapter;
    BluetoothSocket btSocket;
    BluetoothDevice device;
    private final static int REQUEST_ENABLED_BT = 101;
    String garden = "GARDEN";
    String TAG = "FACEBluetooth";
    String userIdToRegister;
    ConnectedThread connectedThread;
    Dialog registerDialog;

    //페어링된 기기 관련
    ArrayList<String> devicePairedArrayList;
    ArrayList<String> devicePairedNameList;
    Set<BluetoothDevice> pairedDevices;
    PairedAdapter pairedAdapter;

    //주변 기기 관련
    ArrayList<String> deviceLocalArrayList;
    ArrayList<String> deviceLocalNameList;
    SurroundAdapter surroundAdapter;

    //데이터베이스
    PreferenceManager preferenceManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Handler mHandler = new Handler();

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.w(TAG, iBinder + "호출됨.");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w(TAG, componentName + "끊어짐.");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("GardenFragment", "onCreateView() 호출됨.");

        View v = inflater.inflate(R.layout.fragment_garden, container, false);

        //BluetoothService
        Intent btIntent = new Intent(v.getContext(), BluetoothService.class);
        getActivity().bindService(btIntent, connection, Context.BIND_AUTO_CREATE);

        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);

        locationExist = v.findViewById(R.id.locationExist);
        searchLocation = v.findViewById(R.id.searchLocation);
        gardenRecycler = v.findViewById(R.id.gardenRecycler);
        locationRecycler = v.findViewById(R.id.locationRecycler);
        containerExist = v.findViewById(R.id.containerExist);

        pairedAdapter = new PairedAdapter();
        surroundAdapter = new SurroundAdapter();

        LinearLayoutManager layoutManagerG = new LinearLayoutManager(v.getContext());
        layoutManagerG.setOrientation(LinearLayoutManager.HORIZONTAL);
        gardenRecycler.setLayoutManager(layoutManagerG);
        LinearLayoutManager layoutManagerT = new LinearLayoutManager(v.getContext(),
                LinearLayoutManager.VERTICAL, false);
        locationRecycler.setLayoutManager(layoutManagerT);

        //블루투스 연결 상태 확인
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext()
                    , Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(v.getContext(), "블루투스 권한 허용이 필요합니다.", Toast.LENGTH_LONG).show();
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
                if (deviceName!=null && deviceName.contains(garden)) {
                    devicePairedArrayList.add(deviceHardwareAddress);
                    devicePairedNameList.add(deviceName);
                }
            }
        } else {
            containerExist.setVisibility(View.VISIBLE);
        }

        int pairedLength = devicePairedArrayList.size();
        for (int i = 0; i < pairedLength; i++) {
            //데이터베이스에서 user와 myId가 일치하는 것만 가져오기
            int finalI = i;
            db.collection(Constants.KEY_COLLECTION_GARDEN).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (devicePairedNameList.get(finalI).equals(document.getString(Constants.KEY_ADDRESS))
                                && document.getString(Constants.KEY_USER).equals(myId)) {
                            pairedAdapter.addItem(new Bluetooth(document.getString(Constants.KEY_NAME),
                                    document.getString(Constants.KEY_ADDRESS), false));
                            Log.w(TAG, "페어링 아이템 추가됨: " + document.getString(Constants.KEY_NAME));
                        }
                    }
                }
            });
        }
        gardenRecycler.setAdapter(pairedAdapter);

        //주변 기기 목록
        deviceLocalArrayList = new ArrayList<>();
        deviceLocalNameList = new ArrayList<>();
        //기기 검색
        if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(v.getContext(), "블루투스 권한 허용이 필요합니다."
                    , Toast.LENGTH_LONG).show();
        }
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                getActivity().registerReceiver(receiver, filter);
            } else {
                Toast.makeText(v.getContext(), "블루투스 어댑터 연결에 실패했습니다."
                        , Toast.LENGTH_LONG).show();
            }
        }

        //검색 버튼 클릭 시
        searchLocation.setOnClickListener(view -> {
            locationExist.setVisibility(View.GONE);
            deviceLocalArrayList = new ArrayList<>();
            deviceLocalNameList = new ArrayList<>();
            //기기 검색
            if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(v.getContext(), "블루투스 권한 허용이 필요합니다."
                        , Toast.LENGTH_LONG).show();
                return;
            }
            if (btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery();
            } else {
                if (btAdapter.isEnabled()) {
                    btAdapter.startDiscovery();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    getActivity().registerReceiver(receiver, filter);
                } else {
                    Toast.makeText(v.getContext(), "블루투스 어댑터 연결에 실패했습니다."
                            , Toast.LENGTH_LONG).show();
                }
            }
            if (deviceLocalArrayList.size()==0) {
                locationExist.setVisibility(View.VISIBLE);
            }
        });

        //터치 이벤트 추가 - 기기 연결 상태 바꾸는 것 필요
        surroundAdapter.setOnItemClickListener((position, address) -> {
            device = btAdapter.getRemoteDevice(address);
            boolean flag = true;
            Log.d(TAG, "Clicked device: " + device.getName() + " / " + address);
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
                Toast.makeText(v.getContext(), "기기가 주변에 없습니다."
                        , Toast.LENGTH_LONG).show();
            }

            if (flag) {
                connectedThread = new ConnectedThread(btSocket);
                connectedThread.start();
                // 데이터베이스에 등록하기
                registerDialog = new Dialog(v.getContext());
                registerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                registerDialog.setContentView(R.layout.dialog_btregister);
                showRegisterDialog(device);
            }
        });

        return v;
    }

    //ACTION_FOUND 인텐트를 위한 브로드캐스트 리시버
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
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
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("GardenFragment", "onDestroyView() 호출됨.");
        requireActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("GardenFragment", "onDestroy() 호출됨.");
        getActivity().unbindService(connection);
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
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity().getApplicationContext(), "블루투스 권한 허용이 필요합니다.",
                    Toast.LENGTH_LONG).show();
        }
        return device.createRfcommSocketToServiceRecord(uuid);
    }

    public void showRegisterDialog(BluetoothDevice device) {
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);
        final String[] registedId = {null};
        registerDialog.show();
        //리사이클러뷰에 배치
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
                            //데이터베이스에 추가되는 딜레이 고려
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
        //아이템 터치 이벤트
        registerAdapter.setOnItemClickListener(((position, userId) -> {
            registedId[0] = userId;
            Log.w(TAG, "Touched: " + registedId[0]);
        }));

        //취소 버튼
        Button rejectButton = registerDialog.findViewById(R.id.rejectButton);
        rejectButton.setOnClickListener(view -> registerDialog.dismiss());

        //선택 버튼
        Button selectButton = registerDialog.findViewById(R.id.selectButton);
        selectButton.setOnClickListener(view -> {
            if (registedId[0] == null) {
                Toast.makeText(getActivity().getApplicationContext(), "선택을 하지 않습니다.",
                        Toast.LENGTH_SHORT).show();
                registerDialog.dismiss();
            } else {
                userIdToRegister = registedId[0];
                HashMap<String, Object> garden = new HashMap<>();
                garden.put(Constants.KEY_ADDRESS, device.getAddress());
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity().getApplicationContext(), "블루투스 권한 허용이 필요합니다.",
                            Toast.LENGTH_LONG).show();
                }
                garden.put(Constants.KEY_NAME, device.getName());
                garden.put(Constants.KEY_USER, userIdToRegister);
                db.collection(Constants.KEY_COLLECTION_GARDEN).add(garden);
                Log.w(TAG, "가족 정원이 등록되었습니다.");
                Log.w(TAG, "등록 id: " + userIdToRegister);
                registerDialog.dismiss();
            }
        });
    }
}

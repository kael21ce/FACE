package org.techtown.face.adapters;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import org.techtown.face.R;
import org.techtown.face.models.Bluetooth;
import org.techtown.face.models.User;
import org.techtown.face.utilites.ConnectedThread;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

public class SurroundAdapter extends RecyclerView.Adapter<SurroundAdapter.ViewHolder> {
    ArrayList<Bluetooth> items = new ArrayList<>();
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final String TAG = "FACEBluetooth";

    public interface OnItemClickListener {
        void onItemClicked(int position, User user);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(SurroundAdapter.OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.surround_item, viewGroup, false);

        SurroundAdapter.ViewHolder viewHolder = new SurroundAdapter.ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Bluetooth item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView surroundName;
        TextView surroundAddress;
        Button connectButton;
        BluetoothSocket btSocket;
        BluetoothDevice device;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        ConnectedThread connectedThread;
        boolean flag;


        public ViewHolder(View itemVIew) {
            super(itemVIew);

            surroundName = itemVIew.findViewById(R.id.surroundName);
            surroundAddress = itemVIew.findViewById(R.id.surroundAddress);
            connectButton = itemVIew.findViewById(R.id.connectButton);
        }

        public void setItem(Bluetooth item) {
            surroundName.setText(item.getDevice());
            surroundAddress.setText(item.getAddress());
            device = btAdapter.getRemoteDevice(item.getAddress());
            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final Method m = device.getClass()
                                .getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
                        btSocket = (BluetoothSocket) m.invoke(device, uuid);
                    } catch (Exception e) {
                        Log.e(TAG, "Could not create Insecure RFComm Connection", e);
                    }
                    flag = true;
                    try {
                        if (ActivityCompat.checkSelfPermission(itemView.getContext(),
                                Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(itemView.getContext(), "블루투스 권한 허용이 필요합니다.",
                                    Toast.LENGTH_LONG).show();
                        }
                        btSocket.connect();
                    } catch (IOException closeException) {
                        flag = false;
                        Log.e(TAG, "Could not close the client socket", closeException);
                    }
                    if (flag) {
                        if (btSocket != null) {
                            connectedThread = new ConnectedThread(btSocket);
                            connectedThread.start();
                            Toast.makeText(itemView.getContext(), "연결되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "Not in location-3: " + device.getName());
                            Toast.makeText(itemView.getContext(), "기기가 주변에 없습니다."
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }
    public void addItem(Bluetooth item) {
        items.add(item);
    }

    public void setItems(ArrayList<Bluetooth> items) {
        this.items = items;
    }

    public Bluetooth getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Bluetooth item) {
        items.set(position, item);
    }
}

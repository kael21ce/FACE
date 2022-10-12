package org.techtown.face.adapters;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.face.R;
import org.techtown.face.models.Bluetooth;

import java.util.ArrayList;

public class SurroundAdapter extends RecyclerView.Adapter<SurroundAdapter.ViewHolder> {
    ArrayList<Bluetooth> items = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClicked(int position, String address);
    }

    private SurroundAdapter.OnItemClickListener itemClickListener;

    public void setOnItemClickListener(SurroundAdapter.OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.surround_item, viewGroup, false);

        SurroundAdapter.ViewHolder viewHolder = new SurroundAdapter.ViewHolder(itemView);

        itemView.setOnClickListener(view -> {
            int position = viewHolder.getAdapterPosition();
            String address = null;
            if (position != RecyclerView.NO_POSITION) {
                address = items.get(position).getAddress();
            }
            itemClickListener.onItemClicked(position, address);
        });

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
        BluetoothDevice device;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();


        public ViewHolder(View itemVIew) {
            super(itemVIew);

            surroundName = itemVIew.findViewById(R.id.surroundName);
            surroundAddress = itemVIew.findViewById(R.id.surroundAddress);
        }

        public void setItem(Bluetooth item) {
            surroundName.setText(item.getDevice());
            surroundAddress.setText(item.getAddress());
            device = btAdapter.getRemoteDevice(item.getAddress());
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

    public void clear() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }
}

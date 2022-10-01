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

public class PairedAdapter extends RecyclerView.Adapter<PairedAdapter.ViewHolder> {
    ArrayList<Bluetooth> items = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClicked(int position, String address, boolean flag);
    }

    private PairedAdapter.OnItemClickListener itemClickListener;

    public void setOnItemClickListener (PairedAdapter.OnItemClickListener listener) {
        itemClickListener=listener;
    }

    @NonNull
    @Override
    public PairedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.paired_item, viewGroup, false);

        PairedAdapter.ViewHolder viewHolder = new PairedAdapter.ViewHolder(itemView);

        itemView.setOnClickListener(view -> {
            int position = viewHolder.getAdapterPosition();
            boolean flag = false;
            String address = null;
            if (position != RecyclerView.NO_POSITION) {
                address = items.get(position).getAddress();
                flag = true;
            }
            itemClickListener.onItemClicked(position, address, flag);
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
        TextView pairedName;
        TextView statusText;


        public ViewHolder(View itemVIew) {
            super(itemVIew);

            pairedName = itemVIew.findViewById(R.id.pairedName);
            statusText = itemVIew.findViewById(R.id.statusText);
        }

        public void setItem(Bluetooth item) {
            pairedName.setText(item.getDevice());
            statusText.setText("페어링됨.");
            if (item.isFlag() == true) {
                statusText.setText("연결됨.");
            }
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


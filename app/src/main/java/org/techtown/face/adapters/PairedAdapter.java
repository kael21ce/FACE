package org.techtown.face.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.face.R;
import org.techtown.face.models.Bluetooth;
import org.techtown.face.models.Family;
import org.techtown.face.models.User;

import java.util.ArrayList;

public class PairedAdapter extends RecyclerView.Adapter<PairedAdapter.ViewHolder> {
    ArrayList<Bluetooth> items = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClicked(int position, Bluetooth item);
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

        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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


package org.techtown.face.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.face.R;
import org.techtown.face.models.Bluetooth;
import org.techtown.face.models.Family;
import org.techtown.face.models.User;

import java.util.ArrayList;

public class SurroundAdapter extends RecyclerView.Adapter<SurroundAdapter.ViewHolder> {
    ArrayList<Bluetooth> items = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClicked(int position, User user);
    }

    private PairedAdapter.OnItemClickListener itemClickListener;

    public void setOnItemClickListener (PairedAdapter.OnItemClickListener listener) {
        itemClickListener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.paired_item, viewGroup, false);

        SurroundAdapter.ViewHolder viewHolder = new SurroundAdapter.ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pairedName;
        TextView statusText;
        Button connectButton;


        public ViewHolder(View itemVIew) {
            super(itemVIew);

            pairedName = itemVIew.findViewById(R.id.pairedName);
            statusText = itemVIew.findViewById(R.id.statusText);
            connectButton = itemVIew.findViewById(R.id.connectButton);
        }

        public void setItem(Family item) {

        }
    }
    public void addItem(Family item) {
        itemFamily.add(item);
    }

    public void setItems(ArrayList<Family> items) {
        this.items = items;
    }

    public Family getItem(int position) {
        return itemFamily.get(position);
    }

    public void setItem(int position, Family item) {
        itemFamily.set(position, item);
    }
}

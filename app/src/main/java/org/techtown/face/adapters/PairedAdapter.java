package org.techtown.face.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.models.Bluetooth;
import org.techtown.face.utilites.Constants;

import java.util.ArrayList;

public class PairedAdapter extends RecyclerView.Adapter<PairedAdapter.ViewHolder> {
    ArrayList<Bluetooth> items = new ArrayList<>();

    @NonNull
    @Override
    public PairedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.garden_item, viewGroup, false);

        PairedAdapter.ViewHolder viewHolder = new PairedAdapter.ViewHolder(itemView);

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
        TextView gardenName;
        TextView deviceText;
        TextView gardenStatus;
        Button setGarden;
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        public ViewHolder(View itemVIew) {
            super(itemVIew);
            gardenName =itemVIew.findViewById(R.id.gardenName);
            deviceText = itemVIew.findViewById(R.id.deviceText);
            gardenStatus = itemVIew.findViewById(R.id.gardenStatus);
            setGarden = itemVIew.findViewById(R.id.setGarden);
        }

        public void setItem(Bluetooth item) {
            db.collection(Constants.KEY_COLLECTION_GARDEN).get()
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString(Constants.KEY_ADDRESS).equals(item.getAddress())) {
                            db.collection(Constants.KEY_COLLECTION_USERS).get()
                                    .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                        if (documentSnapshot.getId()
                                                .equals(document.getString("registered"))) {
                                            gardenName.setText(documentSnapshot.getString(Constants.KEY_NAME) + "의 가족정원");
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
            deviceText.setText(item.getDevice());
            gardenStatus.setText("페어링되었습니다.");
            if (item.isFlag() == true) {
                gardenStatus.setText("연결되었습니다.");
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


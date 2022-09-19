package org.techtown.face.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

import org.techtown.face.models.User;
import org.techtown.face.utilites.Family;
import org.techtown.face.R;

import java.util.ArrayList;

public class FamilyAdapter extends RecyclerView.Adapter<FamilyAdapter.ViewHolder> {
    ArrayList<Family> items = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClicked(int position, User user);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener (OnItemClickListener listener) {
        itemClickListener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.frame_item, viewGroup, false);

        FamilyAdapter.ViewHolder viewHolder = new FamilyAdapter.ViewHolder(itemView);

        itemView.setOnClickListener(view -> {
            User user = new User();
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                user = items.get(position).getUserContact();
            }
            itemClickListener.onItemClicked(position, user);
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Family item = items.get(position);
        viewHolder.setItem(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        ImageView familyImg;


        public ViewHolder(View itemVIew) {
            super(itemVIew);

            nameTxt = itemVIew.findViewById(R.id.nameTxt);
            familyImg = itemVIew.findViewById(R.id.familyImg);
        }

        public void setItem(Family item) {
            nameTxt.setText(item.getUserContact().name);
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(item.getUserContact().path);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(itemView).load(uri.toString()).into(familyImg);
            });
        }
    }
    public void addItem(Family item) {
        items.add(item);
    }

    public void setItems(ArrayList<Family> items) {
        this.items = items;
    }

    public Family getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Family item) {
        items.set(position, item);
    }
}


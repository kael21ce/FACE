package org.techtown.face.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.face.R;
import org.techtown.face.models.MeetItem;
import org.techtown.face.utilites.Constants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class MeetAdapter extends RecyclerView.Adapter<MeetAdapter.ViewHolder> {
    ArrayList<MeetItem> items = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTxt;
        TextView mobileTxt;
        Button delete;
        Button addMeet;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.meetImage);
            nameTxt = itemView.findViewById(R.id.meetName);
            mobileTxt = itemView.findViewById(R.id.meetMobile);
            delete = itemView.findViewById(R.id.delete);
            addMeet = itemView.findViewById(R.id.addMeetButton);
        }

        public void setItem(MeetItem item){
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(item.getPath());
            imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> Glide.with(itemView)
                    .load(downloadUrl.toString())
                    .into(imageView));
            nameTxt.setText(item.getName());
            mobileTxt.setText(item.getMobile());
            delete.setOnClickListener (view -> {
                delete.setVisibility(View.INVISIBLE);
                addMeet.setVisibility(View.INVISIBLE);
                Toast.makeText(itemView.getContext(), "삭제되었습니다",Toast.LENGTH_SHORT).show();
            });

            long now = System.currentTimeMillis();
            HashMap<String, Object> meet = new HashMap<>();
            meet.put(Constants.KEY_WINDOW, now);
            meet.put(Constants.KEY_EXPRESSION, 5);

            addMeet.setOnClickListener(v -> {
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(item.getMyId())
                        .collection(Constants.KEY_COLLECTION_USERS)
                        .document(item.getUserId())
                        .update(meet);
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(item.getUserId())
                        .collection(Constants.KEY_COLLECTION_USERS)
                        .document(item.getMyId())
                        .update(meet);
                delete.setVisibility(View.INVISIBLE);
                addMeet.setVisibility(View.INVISIBLE);
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(item.getMyId())
                        .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                        .document(item.getDocId())
                        .delete()
                        .addOnCompleteListener(task -> Toast.makeText(itemView.getContext(), "가족으로 추가되었습니다",Toast.LENGTH_SHORT).show());
            });

        }
    }

    @NonNull
    @Override
    public MeetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.meet_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetAdapter.ViewHolder holder, int position) {
        MeetItem item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(MeetItem item) {
        items.add(item);
    }

    public void setItems(ArrayList<MeetItem> items) {
        this.items = items;
    }

    public MeetItem getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, MeetItem item) {
        items.set(position, item);
    }


}

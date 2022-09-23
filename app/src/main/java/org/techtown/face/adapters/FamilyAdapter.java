package org.techtown.face.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.face.models.User;
import org.techtown.face.models.Family;
import org.techtown.face.R;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.ArrayList;
import java.util.Objects;

public class FamilyAdapter extends RecyclerView.Adapter<FamilyAdapter.ViewHolder> {
    ArrayList<Family> items = new ArrayList<>();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static PreferenceManager preferenceManager;

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
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(itemView).load(uri.toString()).into(familyImg));
            long now = System.currentTimeMillis();
            //현재 표정 계산하기
            final long[] lastNow = {0};
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (item.getUserContact().mobile.equals(document.get("mobile"))) {
                                    //나의 데이터베이스에서 상대방의 window 가져오기
                                    db.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(currentUserId)
                                            .collection(Constants.KEY_COLLECTION_USERS)
                                            .document(document.getId())
                                            .get()
                                            .addOnCompleteListener(task1 -> lastNow[0] = (long) document.get("window"));
                                    //
                                    int idealContact = Integer.parseInt(Objects.requireNonNull(document.get("ideal_contact")).toString())*86400000;
                                    int minContact = Integer.parseInt(Objects.requireNonNull(document.get("min_contact")).toString())*86400000;
                                    long term = (minContact-idealContact) / 3;
                                    if (now - lastNow[0] < idealContact) {
                                        //나의 데이터베이스에 상대방 expression 업데이트
                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(currentUserId)
                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                .document(document.getId())
                                                .update("expression", 5);
                                        //상대방 데이터베이스에 내 expression 업데이트
                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(document.getId())
                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                .document(currentUserId)
                                                .update("expression", 5);
                                    }
                                    if (now - lastNow[0] >= idealContact) {
                                        if (now - lastNow[0] < idealContact + term) {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update("expression", 4);
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .update("expression", 4);
                                        } else if (now - lastNow[0] < idealContact + term*2) {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update("expression", 3);
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .update("expression", 3);
                                        } else if (now - lastNow[0] < idealContact + term*3) {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update("expression", 2);
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .update("expression", 2);
                                        } else {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update("expression", 1);
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .update("expression", 1);
                                        }
                                    }
                                }
                            }
                        }
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


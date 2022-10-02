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
import org.techtown.face.models.AddItem;
import org.techtown.face.utilites.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class AddAdapter extends RecyclerView.Adapter<AddAdapter.ViewHolder> {
    ArrayList<AddItem> items = new ArrayList<>();


    @NonNull
    @Override
    public AddAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.add_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddItem item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    interface OnItemClickListener{
        void onItemClick(View v, int position);
        void onDeleteClick(View v, int position);
    }

    private static OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        TextView mobileTxt;
        ImageView image;
        Button delete;
        Button add;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.addedName);
            mobileTxt = itemView.findViewById(R.id.addedMobile);
            image = itemView.findViewById(R.id.addedImage);
            delete = itemView.findViewById(R.id.deleteButton);
            add = itemView.findViewById(R.id.addButton);

        }

        public void setItem(AddItem item) {
            nameTxt.setText(item.getName());
            mobileTxt.setText(item.getMobile());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageReference.child(item.getPath());
            imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> Glide.with(itemView)
                    .load(downloadUrl.toString())
                    .into(image));
            delete.setOnClickListener (view -> {
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    if (mListener!=null) {
                        mListener.onDeleteClick(view, position);
                    }
                }
                Toast.makeText(itemView.getContext(), "삭제되었습니다",Toast.LENGTH_SHORT).show();
            });

            //가족 추가 버튼을 누르면 나와 상대방의 users 데이터베이스에 서로의 정보가 추가된다.
            add.setOnClickListener(v -> {
                long now = System.currentTimeMillis();
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    if (mListener!=null) {
                        mListener.onDeleteClick(v, position);
                    }
                }
                //문서 id와 추가되는 시간 데이터베이스에 입력
                HashMap<String, Object> user = new HashMap<>();
                user.put(Constants.KEY_USER, item.getUserId());
                user.put(Constants.KEY_WINDOW, now);
                user.put(Constants.KEY_EXPRESSION, 5);
                HashMap<String, Object> myUser = new HashMap<>();
                myUser.put(Constants.KEY_USER, item.getMyId());
                myUser.put(Constants.KEY_WINDOW, now);
                myUser.put(Constants.KEY_EXPRESSION, 5);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(item.getMyId())
                        .collection(Constants.KEY_COLLECTION_USERS)
                        .add(user);
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(item.getUserId())
                        .collection(Constants.KEY_COLLECTION_USERS)
                        .add(myUser);
                add.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(item.getMyId())
                        .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                        .document(item.getDocId())
                        .delete()
                        .addOnCompleteListener(task -> Toast.makeText(itemView.getContext(), "가족으로 추가되었습니다",Toast.LENGTH_SHORT).show());
            });
        }
    }


    public void addItem(AddItem item) {
        items.add(item);
    }

    public void setItems(ArrayList<AddItem> items) {
        this.items = items;
    }

    public AddItem getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, AddItem item) {
        items.set(position, item);
    }


}

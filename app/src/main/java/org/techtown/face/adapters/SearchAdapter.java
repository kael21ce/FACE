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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.face.R;
import org.techtown.face.utilites.Constants;
import org.techtown.face.models.SearchItem;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    ArrayList<SearchItem> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.search_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchItem item = items.get(position);
        holder.setItem(item);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        TextView mobileTxt;
        ImageView image;
        Button delete;
        Button add;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.searchName);
            mobileTxt = itemView.findViewById(R.id.searchMobile);
            image = itemView.findViewById(R.id.searchImage);
            add = itemView.findViewById(R.id.addFamilyButton);

        }

        public void setItem(SearchItem item) {
            nameTxt.setText(item.getName());
            mobileTxt.setText(item.getMobile());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageReference.child(item.getPath());
            imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> Glide.with(itemView)
                    .load(downloadUrl.toString())
                    .into(image));
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS).document(item.getMyId()).collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                   for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                       String userId = documentSnapshot.getId();
                       if(item.getUserId()==userId){
                           add.setVisibility(View.INVISIBLE);
                       }
                   }
               }
            });
            add.setOnClickListener(v -> {
                add.setVisibility(View.INVISIBLE);
                HashMap<String, String> notification = new HashMap<>();
                notification.put(Constants.KEY_TYPE, Constants.KEY_FAMILY_REQUEST);
                notification.put(Constants.KEY_NOTIFICATION, item.getMyId());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(item.getUserId())
                        .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                        .add(notification)
                        .addOnCompleteListener(task -> {
                           Toast.makeText(itemView.getContext(),"요청 성공했습니다.",Toast.LENGTH_SHORT).show();
                        });
            });
        }
    }


    public void addItem(SearchItem item) {
        items.add(item);
    }

    public void setItems(ArrayList<SearchItem> items) {
        this.items = items;
    }

    public SearchItem getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, SearchItem item) {
        items.set(position, item);
    }

    public void deleteItem(int position){
        this.items.remove(position);
    }
}
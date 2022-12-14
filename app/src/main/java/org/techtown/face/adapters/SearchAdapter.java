package org.techtown.face.adapters;

import android.content.Intent;
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
import org.techtown.face.activities.FamilyAddActivity;
import org.techtown.face.activities.SearchActivity;
import org.techtown.face.utilites.Constants;
import org.techtown.face.models.SearchItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(item.getMyId())
                    .collection(Constants.KEY_COLLECTION_USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                       if(task.isSuccessful()){
                           for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                               if(Objects.equals(item.getUserId(), documentSnapshot.getString(Constants.KEY_USER))){
                                   add.setVisibility(View.INVISIBLE);
                               }
                           }
                       }
                    });

            add.setOnClickListener(v -> {
                add.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(itemView.getContext(), FamilyAddActivity.class);
                intent.putExtra(Constants.KEY_NAME, item.getName());
                intent.putExtra(Constants.KEY_PATH, item.getPath());
                intent.putExtra(Constants.KEY_MOBILE, item.getMobile());
                intent.putExtra(Constants.KEY_USER_ID, item.getUserId());
                itemView.getContext().startActivity(intent);
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
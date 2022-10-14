package org.techtown.face.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.face.R;
import org.techtown.face.activities.FamilyViewActivity;
import org.techtown.face.activities.MainActivity;
import org.techtown.face.models.Family;
import org.techtown.face.models.SearchItem;
import org.techtown.face.utilites.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FamilySettingAdapter extends RecyclerView.Adapter<FamilySettingAdapter.ViewHolder> {
        ArrayList<Family> items = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = inflater.inflate(R.layout.search_item, viewGroup, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Family item = items.get(position);
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

            public void setItem(Family item) {
                nameTxt.setText(item.getUserContact().name);
                mobileTxt.setText(item.getUserContact().mobile);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storageReference.child(item.getUserContact().path);
                imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> Glide.with(itemView)
                        .load(downloadUrl.toString())
                        .into(image));

                add.setText("보기");
                add.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), FamilyViewActivity.class);//여기 새로 만들어서 채워
                    intent.putExtra(Constants.KEY_USER, item.getUserContact());
                    itemView.getContext().startActivity(intent);
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

        public void deleteItem(int position){
            this.items.remove(position);
        }

}

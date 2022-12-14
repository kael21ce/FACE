package org.techtown.face.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.techtown.face.R;
import org.techtown.face.utilites.Constants;
import org.techtown.face.models.MomentCheckItem;

import java.util.ArrayList;

public class MomentCheckAdapter extends RecyclerView.Adapter<MomentCheckAdapter.ViewHolder> {
    ArrayList<MomentCheckItem> items = new ArrayList<>();

    @NonNull
    @Override
    public MomentCheckAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.moment_check_item, viewGroup, false);
        return new MomentCheckAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MomentCheckAdapter.ViewHolder viewHolder, int position) {
        MomentCheckItem item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt;
        TextView momentDate;
        Button button;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView4);
            nameTxt = itemView.findViewById(R.id.textView4);
            momentDate = itemView.findViewById(R.id.textView6);
            button = itemView.findViewById(R.id.delete);
        }

        public void setItem(MomentCheckItem item) {

            nameTxt.setText(item.getName()+"??? ??????");
            momentDate.setText("| "+item.getDates()+" |");
            StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(item.getImages());
            imgRef.getDownloadUrl().addOnSuccessListener(command -> Glide.with(itemView).load(command.toString()).into(image));
            button.setOnClickListener(v -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(item.getUserId())
                        .collection(Constants.KEY_COLLECTION_IMAGES)
                        .document(item.getImageId())
                        .delete()
                        .addOnCompleteListener(task -> {
                            Log.e("HaHa", "I'm the best");
                        });
            });

        }
    }


    public void addItem(MomentCheckItem item) {
        items.add(item);
    }

    public void setItems(ArrayList<MomentCheckItem> items) {
        this.items = items;
    }

    public MomentCheckItem getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, MomentCheckItem item) {
        items.set(position, item);
    }

}

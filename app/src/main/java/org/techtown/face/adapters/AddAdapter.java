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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.face.R;
import org.techtown.face.utilites.SearchItem;

import java.util.ArrayList;

public class AddAdapter extends RecyclerView.Adapter<AddAdapter.ViewHolder> {
    ArrayList<SearchItem> items = new ArrayList<>();


    @NonNull
    @Override
    public AddAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.add_item, parent, false);
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

        public void setItem(SearchItem item) {
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
            add.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION){
                    if (mListener!=null) {
                        mListener.onDeleteClick(v, position);
                    }
                }
                Toast.makeText(itemView.getContext(), "추가되었습니다",Toast.LENGTH_SHORT).show();
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


}

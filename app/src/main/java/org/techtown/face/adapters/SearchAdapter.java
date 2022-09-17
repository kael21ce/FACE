package org.techtown.face.adapters;

import android.app.Activity;
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

import org.techtown.face.R;
import org.techtown.face.utilites.Family;
import org.techtown.face.utilites.SearchItem;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    ArrayList<SearchItem> searchItemArrayList;
    Activity activity;


    public SearchAdapter(ArrayList<SearchItem> searchItemArrayList, Activity activity) {
        this.searchItemArrayList = searchItemArrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_item,null);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.userName.setText(searchItemArrayList.get(position).getUserName());
        holder.userMobile.setText(searchItemArrayList.get(position).getUserMobile());
        holder.userImage.setImageBitmap(getUserImage(searchItemArrayList.get(position).getUserImage()));
    }

    @Override
    public int getItemCount() {
        return searchItemArrayList.size();
    }

    public void addItem(SearchItem searchItem) {
        searchItemArrayList.add(searchItem);
    }

    public void  filterList(ArrayList<SearchItem> filteredList) {
        searchItemArrayList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        TextView userMobile;
        ImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.userName=itemView.findViewById(R.id.userName);
            this.userMobile=itemView.findViewById(R.id.userMobile);
            this.userImage=itemView.findViewById(R.id.userImage);
        }
    }

    private static Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(String.valueOf(encodedImage),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}

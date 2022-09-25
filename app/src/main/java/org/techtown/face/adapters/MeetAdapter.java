package org.techtown.face.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.face.R;
import org.techtown.face.models.MeetItem;

import java.util.ArrayList;

public class MeetAdapter extends RecyclerView.Adapter<MeetAdapter.ViewHolder> {
    ArrayList<MeetItem> items = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTxt;
        TextView mobileTxt;
        Button delete;
        Button addMeet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.meetImage);
            nameTxt = itemView.findViewById(R.id.meetName);
            mobileTxt = itemView.findViewById(R.id.meetMobile);
            delete = itemView.findViewById(R.id.delete);
            addMeet = itemView.findViewById(R.id.addMeetButton);
        }

        public void setItem(MeetItem item){

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

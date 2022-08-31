package org.techtown.face;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScaleAdapter extends RecyclerView.Adapter<ScaleAdapter.ViewHolder>{
    ArrayList<Family.FamilyScale> items = new ArrayList<Family.FamilyScale>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.scale_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Family.FamilyScale item = items.get(position);
        viewHolder.setItem(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Family.FamilyScale item) {
        items.add(item);
    }

    public void setItems(ArrayList<Family.FamilyScale> items) {
        this.items = items;
    }

    public Family.FamilyScale getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Family.FamilyScale item) {
        items.set(position, item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        ImageView scaleHead;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.scaleName);
            scaleHead = itemView.findViewById(R.id.scale_head);

        }

        public void setItem(Family.FamilyScale item) {
            nameTxt.setText(item.getScaleName());
            scaleHead.setRotation(item.getScaleAngle());
        }
    }
}

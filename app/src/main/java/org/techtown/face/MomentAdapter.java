package org.techtown.face;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.face.Family.FamilyScale;

import java.util.ArrayList;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.ViewHolder> {
    ArrayList<FamilyScale> items = new ArrayList<FamilyScale>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.moment_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FamilyScale item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.momentName);
        }

        public void setItem(FamilyScale item) {
            nameTxt.setText(item.getScaleName());
        }
    }

    public void addItem(FamilyScale item) {
        items.add(item);
    }

    public void setItems(ArrayList<FamilyScale> items) {
        this.items = items;
    }

    public FamilyScale getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, FamilyScale item) {
        items.set(position, item);
    }
}

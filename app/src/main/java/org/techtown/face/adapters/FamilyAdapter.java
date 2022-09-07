package org.techtown.face.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.face.utilites.Family;
import org.techtown.face.R;

import java.util.ArrayList;

public class FamilyAdapter extends RecyclerView.Adapter<FamilyAdapter.ViewHolder> {
    ArrayList<Family> items = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClicked(int position, String name, String mobile, Integer minContact, Integer idealContact);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener (OnItemClickListener listener) {
        itemClickListener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.frame_item, viewGroup, false);



        FamilyAdapter.ViewHolder viewHolder = new FamilyAdapter.ViewHolder(itemView);

        itemView.setOnClickListener(view -> {
            String name="";
            String mobile="";
            Integer minContact=0;
            Integer idealContact=0;
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                name=items.get(position).getName();
                mobile=items.get(position).getPhoneNumber();
                minContact=items.get(position).getMinContact();
                idealContact=items.get(position).getIdealContact();
            }
            itemClickListener.onItemClicked(position, name, mobile, minContact, idealContact);
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Family item = items.get(position);
        viewHolder.setItem(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        ImageView familyImg;

        public ViewHolder(View itemVIew) {
            super(itemVIew);

            nameTxt = itemVIew.findViewById(R.id.nameTxt);
            familyImg = itemVIew.findViewById(R.id.familyImg);
        }

        public void setItem(Family item) {
            nameTxt.setText(item.getName());
            familyImg.setImageResource(item.getFace());
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


}

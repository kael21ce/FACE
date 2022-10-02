package org.techtown.face.adapters;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.face.R;
import org.techtown.face.models.Family;

import java.util.ArrayList;

public class RegisterAdapter extends RecyclerView.Adapter<RegisterAdapter.ViewHolder> {
    ArrayList<Family> items = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClicked(int position, String userId);
    }

    private RegisterAdapter.OnItemClickListener itemClickListener;

    public void setOnItemClickListener(RegisterAdapter.OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.register_item, viewGroup, false);

        RegisterAdapter.ViewHolder viewHolder = new RegisterAdapter.ViewHolder(itemView);

        itemView.setOnClickListener(view -> {
            int position = viewHolder.getAdapterPosition();
            String userId = items.get(position).getUserContact().id;
            TextView registerContainer = itemView.findViewById(R.id.nameRegisted);
            registerContainer.setBackgroundColor(Color.parseColor("3F51B5"));
            registerContainer.setTextColor(Color.WHITE);
            itemClickListener.onItemClicked(position, userId);
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
        TextView nameRegisted;

        public ViewHolder(View itemView) {
            super(itemView);

            nameRegisted = itemView.findViewById(R.id.nameRegisted);
        }

        public void setItem(Family item) {
            nameRegisted.setText(item.getUserContact().name);
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

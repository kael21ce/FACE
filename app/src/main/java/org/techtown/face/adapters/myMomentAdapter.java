package org.techtown.face.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import org.techtown.face.R;
import org.techtown.face.models.Moment;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class myMomentAdapter extends RecyclerView.Adapter<myMomentAdapter.ViewHolder> {
    ArrayList<Moment> items = new ArrayList<>();

    public interface OnItemClickListener{
        void onAddClick(View v, int position); //순간 추가
        void onDeleteClick(View v, int position);//순간 삭제
    }
    private static myMomentAdapter.OnItemClickListener itemClickListener;

    public void setOnItemClickListener (myMomentAdapter.OnItemClickListener listener) {
        itemClickListener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.my_moment_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Moment item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 imageViewPager;
        TextView my_momentDate;
        CircleIndicator3 imgIndicator;
        ImageButton my_plusMoment;
        ImageButton my_deleteMoment;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewPager = itemView.findViewById(R.id.my_imageViewPager);
            my_momentDate = itemView.findViewById(R.id.my_momentDate);
            imgIndicator = itemView.findViewById(R.id.my_imgIndicator);
            my_deleteMoment = itemView.findViewById(R.id.my_deleteMoment);
        }

        public void setItem(Moment item) {
            String[] dates = item.getDates();

            imageViewPager.setOffscreenPageLimit(1);
            imageViewPager.setAdapter(new ImageSliderAdapter(itemView.getContext(), item.getImages()));
            imgIndicator.setViewPager(imageViewPager);

            imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    my_momentDate.setText("| " + dates[position] + " |");
                }
            });

            my_plusMoment.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION) {
                    if (itemClickListener!=null) {
                        itemClickListener.onAddClick(v, position);
                    }
                }
            });
            my_deleteMoment.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position!=RecyclerView.NO_POSITION) {
                    if (itemClickListener!=null) {
                        itemClickListener.onDeleteClick(v, position);
                    }
                }
            });
        }
    }


    public void addItem(Moment item) {
        items.add(item);
    }

    public void setItems(ArrayList<Moment> items) {
        this.items = items;
    }

    public Moment getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Moment item) {
        items.set(position, item);
    }
}

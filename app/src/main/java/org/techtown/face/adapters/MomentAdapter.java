package org.techtown.face.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import org.techtown.face.utilites.Moment;
import org.techtown.face.R;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.ViewHolder> {
    ArrayList<Moment> items = new ArrayList<Moment>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.moment_item, viewGroup, false);
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
        TextView nameTxt;
        ViewPager2 imageViewPager;
        TextView momentDate;
        CircleIndicator3 imgIndicator;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.momentName);
            imageViewPager = itemView.findViewById(R.id.imageViewPager);
            momentDate = itemView.findViewById(R.id.momentDate);
            imgIndicator = (CircleIndicator3) itemView.findViewById(R.id.imgIndicator);
        }

        public void setItem(Moment item) {
            nameTxt.setText(item.getName()+"의 순간");
            String[] dates = item.getDates();

            imageViewPager.setOffscreenPageLimit(1);
            imageViewPager.setAdapter(new ImageSliderAdapter(itemView.getContext(), item.getImages()));
            imgIndicator.setViewPager(imageViewPager);

            imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    momentDate.setText("| " + dates[position] + " |");
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

package org.techtown.face.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import org.techtown.face.models.Moment;
import org.techtown.face.R;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.ViewHolder> {
    ArrayList<Moment> items = new ArrayList<>();
    Context context;
    PreferenceManager preferenceManager;
    String TAG = "MomentAdapter";

    public interface OnItemClickListener{
        void onDeleteClick(View v, int position);//순간 삭제
    }
    private static MomentAdapter.OnItemClickListener itemClickListener;

    public void setOnItemClickListener (MomentAdapter.OnItemClickListener listener) {
        itemClickListener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.w(TAG, "onCreateViewHolder 호출됨.");
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(getViewSrc(viewType), viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(itemView, viewType);
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
        private int viewType;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
        }

        //나의 순간
        private void setMyMoment(Moment item) {
            ViewPager2 my_imageViewPager = itemView.findViewById(R.id.my_imageViewPager);
            TextView my_momentDate = itemView.findViewById(R.id.my_momentDate);
            CircleIndicator3 imgIndicator = itemView.findViewById(R.id.my_imgIndicator);
            ImageButton my_deleteMoment = itemView.findViewById(R.id.my_deleteMoment);

            String[] dates = item.getDates();

            my_imageViewPager.setOffscreenPageLimit(1);
            my_imageViewPager.setAdapter(new ImageSliderAdapter(itemView.getContext(), item.getImages()));
            imgIndicator.setViewPager(my_imageViewPager);

            my_imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    my_momentDate.setText("| " + dates[position] + " |");
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

        //가족의 순간
        private void setFamilyMoment(Moment item) {
            TextView nameTxt = itemView.findViewById(R.id.momentName);
            ViewPager2 imageViewPager = itemView.findViewById(R.id.imageViewPager);
            TextView momentDate = itemView.findViewById(R.id.momentDate);
            CircleIndicator3 imgIndicator = itemView.findViewById(R.id.imgIndicator);

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

        public void setItem(Moment item) {
            if (viewType == TYPE_MY_MOMENT) {
                setMyMoment(item);
            } else if (viewType == TYPE_FAMILY_MOMENT) {
                setFamilyMoment(item);
            }
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

    private static final int TYPE_MY_MOMENT = 1001;
    private static final int TYPE_FAMILY_MOMENT = 1002;

    private int getViewSrc(int viewType) {
        if (viewType == TYPE_MY_MOMENT) {
            return R.layout.my_moment_item;
        } else {
            return R.layout.moment_item;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.w(TAG, "getItemViewType 호출됨.");
        if (position==0) {
            return TYPE_MY_MOMENT;
        } else {
            return TYPE_FAMILY_MOMENT;
        }
    }
}

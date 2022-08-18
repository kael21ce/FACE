package org.techtown.face;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import me.relex.circleindicator.CircleIndicator3;

public class MomentFragment extends Fragment {

    private ViewPager2 myImageViewPager;
    private CircleIndicator3 myImgIndicator;

    public int[] images = new int[] {
            R.drawable.pasta,
            R.drawable.cat,
            R.drawable.pork
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moment, container, false);

        //나의 순간 ViewPager2 다루기
        myImageViewPager = v.findViewById(R.id.myViewPager);
        myImgIndicator = (CircleIndicator3) v.findViewById(R.id.imgIndicator);

        myImageViewPager.setOffscreenPageLimit(1);
        myImageViewPager.setAdapter(new ImageSliderAdapter(v.getContext(), images));

        myImgIndicator.setViewPager(myImageViewPager);



        return v;
    }
}

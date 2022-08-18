package org.techtown.face;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import me.relex.circleindicator.CircleIndicator3;

public class MomentFragment extends Fragment {

    private ViewPager2 myImageViewPager;
    private CircleIndicator3 myImgIndicator;
    private TextView myDate;

    //추후에 데이터베이스 활용하기
    private int[] images = new int[] {
            R.drawable.pasta,
            R.drawable.cat,
            R.drawable.pork
            };

    private String[] dates = new String[] {
            "2022-08-16", "2022-08-17", "2022-08-18"
    };

    private int[] himages = new int[] {
            R.drawable.beer
    };

    private String[] hdates = new String[] {
            "2022-08-16"
    };

    private int[] iimages = new int[] {
            R.drawable.holds, R.drawable.pasta
    };

    private String[] idates = new String[] {
            "2022-08-17", "2022-08-18"
    };
    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moment, container, false);

        //리사이클러뷰에 순간 아이템 배열
        RecyclerView momentRecyclerView = v.findViewById(R.id.momentRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(),
                LinearLayoutManager.VERTICAL, false);
        momentRecyclerView.setLayoutManager(layoutManager);
        MomentAdapter momentAdapter = new MomentAdapter();

        momentAdapter.addItem(new Moment("나", images, dates));
        momentAdapter.addItem(new Moment("홍길동", himages, hdates));
        momentAdapter.addItem(new Moment("임꺽정", iimages, idates));

        momentRecyclerView.setAdapter(momentAdapter);
        //


        return v;
    }
}

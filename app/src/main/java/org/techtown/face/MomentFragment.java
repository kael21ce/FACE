package org.techtown.face;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class MomentFragment extends Fragment {

    public ViewPager2 myImageViewPager;
    public LinearLayout layoutIndicator;
    public LinearLayout myMomentLayout;

    private int[] images = new int[] {
            R.drawable.pasta,
            R.drawable.cat,
            R.drawable.gallery
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moment, container, false);

        //나의 순간 ViewPager2 다루기
        myImageViewPager = v.findViewById(R.id.imgSlider);
        layoutIndicator = v.findViewById(R.id.myImgIndicator);
        myMomentLayout = v.findViewById(R.id.mMomentContainer);

        try {
            myImageViewPager.setOffscreenPageLimit(1);
            myImageViewPager.setAdapter(new ImageSliderAdapter(v.getContext(), images));

            myImageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    setCurrentIndicator(position);
                }
            });

            setupIndicator(images.length);
            //
        } catch (Exception e) {
            e.printStackTrace();

            //추후에 순간을 추가해보세요라고 창 나오도록 수정해야 함!!
        }


        return v;
    }

    //Indicator 설정
    public void setupIndicator(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i<indicators.length; i++) {
            indicators[i] =new ImageView(getContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    //현재 Indicator의 상태 설정
    public void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i<childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i==position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getContext(),
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getContext(),
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }
}

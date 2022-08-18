package org.techtown.face;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FrameFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frame, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);

        GridLayoutManager layoutManager = new GridLayoutManager(v.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        FamilyAdapter adapter = new FamilyAdapter();
        
        //어탭터로 가족 추가
        //데이터베이스 추가 시 변경하기
        adapter.addItem(new Family("나", "01046076705", R.drawable.user_icon));
        adapter.addItem(new Family("홍길동", "01000000000", R.drawable.user_icon));
        adapter.addItem(new Family("임꺽정", "01012345678", R.drawable.user_icon));
        //
        adapter.setOnItemClickListener(new FamilyAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, String name, String mobile) {
                Intent contactIntent = new Intent(v.getContext(), FamilyActivity.class);
                contactIntent.putExtra("name", name);
                contactIntent.putExtra("mobile", mobile);
                startActivity(contactIntent);
            }
        });

        recyclerView.setAdapter(adapter);

        return v;
    }
}

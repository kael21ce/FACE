package org.techtown.face;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.face.Family.FamilyScale;

public class ScaleFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scale, container, false);

        //리사이클러뷰 객체
        RecyclerView recyclerView = v.findViewById(R.id.scaleRecyclerView);
        //

        //리사이클러뷰 레이아웃 매니저 생성
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //ScaleAdapter 생성 및 아이템 추가
        ScaleAdapter adapter = new ScaleAdapter();

        adapter.addItem(new FamilyScale("홍길동", "01000000000"));
        adapter.addItem(new FamilyScale("임꺽정", "01012345678"));

        //어댑터 설정
        recyclerView.setAdapter(adapter);

        return v;
    }
}

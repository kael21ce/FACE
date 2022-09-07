package org.techtown.face.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.activities.FamilyActivity;
import org.techtown.face.adapters.FamilyAdapter;
import org.techtown.face.utilites.Family;

public class FrameFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FACEdatabase";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frame, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);

        GridLayoutManager layoutManager = new GridLayoutManager(v.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        FamilyAdapter adapter = new FamilyAdapter();
        
        //어탭터로 가족 추가
        //나에 대한 정보 추가
        adapter.addItem(new Family("나", "01046076705", R.drawable.user_icon, 3, 7));
        //터치 이벤트 추가
        adapter.setOnItemClickListener((position, name, phone_number, min_contact, ideal_contact) -> {
            Intent contactIntent = new Intent(v.getContext(), FamilyActivity.class);
            contactIntent.putExtra("name", name);
            contactIntent.putExtra("phone_number", phone_number);
            contactIntent.putExtra("min_contact", min_contact);
            contactIntent.putExtra("ideal_contact", ideal_contact);
            startActivity(contactIntent);
        });
        //db에서 데이터 가져오기
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.get("name").toString();
                            String phone_number = document.get("phone_number").toString();
                            int min_contact = Integer.parseInt(document.get("min_contact").toString());
                            int ideal_contact = Integer.parseInt(document.get("ideal_contact").toString());
                            int expression = Integer.parseInt(document.get("expression").toString());
                            boolean meet = (Boolean) document.get("meet");


                            //대면 만남 여부, 표정 변화 단계에 따라 이미지 달리하여 추가하기
                            if (meet) {
                                adapter.addItem(new Family(name, phone_number, R.drawable.five, min_contact, ideal_contact));
                            } else {
                                if (expression==5) {
                                    adapter.addItem(new Family(name, phone_number, R.drawable.five, min_contact, ideal_contact));
                                } else if (expression==4) {
                                    adapter.addItem(new Family(name, phone_number, R.drawable.four, min_contact, ideal_contact));
                                } else if (expression==3) {
                                    adapter.addItem(new Family(name, phone_number, R.drawable.three, min_contact, ideal_contact));
                                } else if (expression==2) {
                                    adapter.addItem(new Family(name, phone_number, R.drawable.two, min_contact, ideal_contact));
                                } else if (expression==1) {
                                    adapter.addItem(new Family(name, phone_number, R.drawable.one, min_contact, ideal_contact));
                                } else {
                                    adapter.addItem(new Family(name, phone_number, R.drawable.user_icon, min_contact, ideal_contact));
                                }
                            }
                            Log.w(TAG, "Successfully loaded");
                        }
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });

        return v;
    }
}

package org.techtown.face;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        adapter.setOnItemClickListener(new FamilyAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, String name, String mobile, Integer minContact, Integer idealContact) {
                Intent contactIntent = new Intent(v.getContext(), FamilyActivity.class);
                contactIntent.putExtra("name", name);
                contactIntent.putExtra("mobile", mobile);
                contactIntent.putExtra("minContact", minContact);
                contactIntent.putExtra("idealContact", idealContact);
                startActivity(contactIntent);
            }
        });
        //db에서 데이터 가져오기
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.get("name").toString();
                                String mobile = document.get("mobile").toString();
                                int minContact = Integer.parseInt(document.get("minContact").toString());
                                int idealContact = Integer.parseInt(document.get("idealContact").toString());
                                adapter.addItem(new Family(name, mobile, R.drawable.user_icon, minContact, idealContact));
                                Log.w(TAG, "Successfully loaded");
                            }
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return v;
    }
}

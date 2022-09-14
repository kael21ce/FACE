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
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.Family;
import org.techtown.face.utilites.PreferenceManager;

public class FrameFragment extends Fragment {

    PreferenceManager preferenceManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FACEdatabase";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_frame, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(v.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        FamilyAdapter adapter = new FamilyAdapter();
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        //어탭터로 가족 추가

        //터치 이벤트 추가
        adapter.setOnItemClickListener((position, user) -> {
            Intent contactIntent = new Intent(v.getContext(), FamilyActivity.class);
            contactIntent.putExtra(Constants.KEY_USER,user);
            startActivity(contactIntent);
        });
        //db에서 데이터 가져오기
        //나에 대한 정보 추가

        db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (currentUserId.equals(document.getId())){
                                User user = new User();
                                user.name = "나";
                                user.email = document.get(Constants.KEY_EMAIL).toString();
                                user.image= document.get(Constants.KEY_IMAGE).toString();
                                user.token = document.get(Constants.KEY_FCM_TOKEN).toString();
                                user.id = document.getId();
                                user.mobile = document.get(Constants.KEY_MOBILE).toString();
                                user.min_contact = Integer.parseInt(document.get(Constants.KEY_MIN_CONTACT).toString());
                                user.ideal_contact = Integer.parseInt(document.get(Constants.KEY_IDEAL_CONTACT).toString());
                                user.like = document.get(Constants.KEY_THEME_LIKE).toString();
                                user.dislike = document.get(Constants.KEY_THEME_DISLIKE).toString();
                                adapter.addItem(new Family(user));
                                recyclerView.setAdapter(adapter);
                            }
                            Log.w(TAG, "Successfully loaded");
                        }
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
        //다른 사람 정보 추가
       db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (currentUserId.equals(document.getId())){
                                continue;

                            }
                            User user = new User();
                            user.name = document.get(Constants.KEY_NAME).toString();
                            user.email = document.get(Constants.KEY_EMAIL).toString();
                            user.image= document.get(Constants.KEY_IMAGE).toString();
                            user.id = document.getId();
                            user.mobile = document.get(Constants.KEY_MOBILE).toString();
                            user.min_contact = Integer.parseInt(document.get(Constants.KEY_MIN_CONTACT).toString());
                            user.ideal_contact = Integer.parseInt(document.get(Constants.KEY_IDEAL_CONTACT).toString());
                            user.like = document.get(Constants.KEY_THEME_LIKE).toString();
                            user.dislike = document.get(Constants.KEY_THEME_DISLIKE).toString();
                            int expression = Integer.parseInt(document.get(Constants.KEY_EXPRESSION).toString());
                            boolean meet = (Boolean) document.get(Constants.KEY_MEET);

                            //대면 만남 여부, 표정 변화 단계에 따라 이미지 달리하여 추가하기
                            if (meet) {
                                adapter.addItem(new Family(user));
                            } else {
                                if (expression==5) {
                                    adapter.addItem(new Family(user));
                                } else if (expression==4) {
                                    adapter.addItem(new Family(user));
                                } else if (expression==3) {
                                    adapter.addItem(new Family(user));
                                } else if (expression==2) {
                                    adapter.addItem(new Family(user));
                                } else if (expression==1) {
                                    adapter.addItem(new Family(user));
                                } else {
                                    adapter.addItem(new Family(user));
                                }
                            }
                        }
                        Log.w(TAG, "Successfully loaded");

                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });

        return v;
    }
}

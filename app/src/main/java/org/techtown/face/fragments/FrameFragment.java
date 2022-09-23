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
import org.techtown.face.models.Family;
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

        String myId = preferenceManager.getString(Constants.KEY_USER_ID);

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
                                user.token = document.get(Constants.KEY_FCM_TOKEN).toString();
                                user.id = document.getId();
                                user.mobile = document.get(Constants.KEY_MOBILE).toString();
                                user.min_contact = Integer.parseInt(document.get(Constants.KEY_MIN_CONTACT).toString());
                                user.ideal_contact = Integer.parseInt(document.get(Constants.KEY_IDEAL_CONTACT).toString());
                                user.like = document.get(Constants.KEY_THEME_LIKE).toString();
                                user.dislike = document.get(Constants.KEY_THEME_DISLIKE).toString();
                                user.path = document.get(Constants.KEY_PATH).toString();
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
                .document(myId)
                .collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getString(Constants.KEY_USER);
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    for(QueryDocumentSnapshot queryDocumentSnapshot : task1.getResult()){
                                        String thisId = queryDocumentSnapshot.getId();
                                        if(userId.equals(thisId)){
                                            User user = new User();
                                            user.name = queryDocumentSnapshot.get(Constants.KEY_NAME).toString();
                                            user.image= queryDocumentSnapshot.get(Constants.KEY_IMAGE).toString();
                                            user.id = queryDocumentSnapshot.getId();
                                            user.mobile = queryDocumentSnapshot.get(Constants.KEY_MOBILE).toString();
                                            user.min_contact = Integer.parseInt(queryDocumentSnapshot.get(Constants.KEY_MIN_CONTACT).toString());
                                            user.ideal_contact = Integer.parseInt(queryDocumentSnapshot.get(Constants.KEY_IDEAL_CONTACT).toString());
                                            user.like = queryDocumentSnapshot.get(Constants.KEY_THEME_LIKE).toString();
                                            user.dislike = queryDocumentSnapshot.get(Constants.KEY_THEME_DISLIKE).toString();
                                            user.path = queryDocumentSnapshot.get(Constants.KEY_PATH).toString();
                                            int expression = Integer.parseInt(queryDocumentSnapshot.get(Constants.KEY_EXPRESSION).toString());
                                            boolean meet = (Boolean) queryDocumentSnapshot.get(Constants.KEY_MEET);

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
                                            recyclerView.setAdapter(adapter);
                                        }
                                    }
                                }
                            });

                            //대면 만남 여부, 표정 변화 단계에 따라 이미지 달리하여 추가하기

                        }
                        Log.w(TAG, "Successfully loaded");}
                });
        return v;
    }
}

package org.techtown.face.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.techtown.face.utilites.Constants;
import org.techtown.face.models.Family.FamilyScale;
import org.techtown.face.R;
import org.techtown.face.adapters.ScaleAdapter;
import org.techtown.face.utilites.PreferenceManager;

public class ScaleFragment extends Fragment {

    PreferenceManager preferenceManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FACEdatabase";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scale, container, false);
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());

        //리사이클러뷰 객체
        RecyclerView recyclerView = v.findViewById(R.id.scaleRecyclerView);

        //리사이클러뷰 레이아웃 매니저 생성
        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //ScaleAdapter 생성
        ScaleAdapter adapter = new ScaleAdapter();

        String myId = preferenceManager.getString(Constants.KEY_USER_ID);

        //db에서 데이터 가져오기
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(myId)
                .collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getString(Constants.KEY_USER);
                            db.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                        String thisId = documentSnapshot.getId();
                                        if (userId.equals(thisId)) {
                                            String name = documentSnapshot.get(Constants.KEY_NAME).toString();
                                            String mobile = documentSnapshot.get(Constants.KEY_MOBILE).toString();
                                            adapter.addItem(new FamilyScale(name, mobile, userId));
                                            Log.w(TAG, "Successfully loaded");
                                        }
                                    }
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            });
                        }
                    }
                });

        return v;
    }
}

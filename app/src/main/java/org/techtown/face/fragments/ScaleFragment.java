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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.Family.FamilyScale;
import org.techtown.face.R;
import org.techtown.face.adapters.ScaleAdapter;
import org.techtown.face.utilites.PreferenceManager;
import org.techtown.face.utilites.ScaleInfo;

public class ScaleFragment extends Fragment {

    PreferenceManager preferenceManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FACEdatabase";
    ScaleInfo scaleInfo = new ScaleInfo();

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

        //db에서 데이터 가져오기
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!currentUserId.equals(document.getId())) {
                                    String name = document.get("name").toString();
                                    String mobile = document.get("mobile").toString();
                                    adapter.addItem(new FamilyScale(name, mobile));
                                    Log.w(TAG, "Successfully loaded");
                                }
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

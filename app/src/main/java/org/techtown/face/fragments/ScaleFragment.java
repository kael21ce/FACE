package org.techtown.face.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private SwipeRefreshLayout scaleSwipeRefresh;
    Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scale, container, false);
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        mHandler = new Handler();

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

        scaleSwipeRefresh = v.findViewById(R.id.scaleSwipeRefresh);
        scaleSwipeRefresh.setOnRefreshListener(() -> {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshFragment();
                    scaleSwipeRefresh.setRefreshing(false);
                }
            },1000);
        });

        return v;
    }

    private void refreshActivity() {
        Fragment fg;
        fg = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        getActivity().finish();
        getActivity().overridePendingTransition(0,0);
        Intent refreshIntent = getActivity().getIntent();
        startActivity(refreshIntent);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.attach(fg).commit();
    }

    private void refreshFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ScaleFragment.class, null)
                .commit();
    }
}

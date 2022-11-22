package org.techtown.face.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.activities.MomentCheckActivity;
import org.techtown.face.adapters.MomentAdapter;
import org.techtown.face.models.Moment;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

public class MomentFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PreferenceManager preferenceManager;
    SwipeRefreshLayout momentSwipeRefresh;
    Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moment, container, false);
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        RecyclerView momentRecyclerView = v.findViewById(R.id.momentRecyclerView);
        mHandler = new Handler();

        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        momentRecyclerView.setLayoutManager(layoutManager);
        MomentAdapter momentAdapter = new MomentAdapter();
        momentAdapter.setOnItemClickListener((v1, position) -> {
            Intent deleteIntent = new Intent(v1.getContext(), MomentCheckActivity.class);
            startActivity(deleteIntent);
        });

        //리사이클러뷰에 순간 아이템 배열
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_IMAGES)
                .orderBy(Constants.KEY_TIMESTAMP,Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult().size()>0){
                        int size=task.getResult().size();
                        String name="";
                        String[] image = new String[size];
                        String[] date = new String[size];
                        int i=0;
                        for(QueryDocumentSnapshot document : task.getResult()){
                            if(document.exists()){
                                name = document.get(Constants.KEY_NAME).toString();
                                image[i] = document.get(Constants.KEY_IMAGE).toString();
                                date[i] = document.get(Constants.KEY_TIMESTAMP).toString();
                                i++;
                            } else{
                                Log.e("document doesn't exist","FUCK YOU");
                            }
                        }
                        momentAdapter.addItem(new Moment(name, "m", image, date));
                        momentRecyclerView.setAdapter(momentAdapter);
                    }
                });

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.e("ee","->"+task.getResult().size());
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            String userId = queryDocumentSnapshot.getString(Constants.KEY_USER);
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            database.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(userId)
                                    .collection(Constants.KEY_COLLECTION_IMAGES)
                                    .orderBy(Constants.KEY_TIMESTAMP,Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()&&task1.getResult().size()>0){
                                            int size=task1.getResult().size();
                                            String name="";
                                            String[] image = new String[size];
                                            String[] date = new String[size];
                                            int i=0;
                                            for(QueryDocumentSnapshot document : task1.getResult()){
                                                if(document.exists()){
                                                    name = document.get(Constants.KEY_NAME).toString();
                                                    image[i] = document.get(Constants.KEY_IMAGE).toString();
                                                    date[i] = document.get(Constants.KEY_TIMESTAMP).toString();
                                                    i++;
                                                } else{
                                                    Log.e("oh","hell");
                                                }
                                            }
                                            momentAdapter.addItem(new Moment(name, "y", image, date));
                                            momentRecyclerView.setAdapter(momentAdapter);
                                        } else {
                                            Log.e("what", "the hell inside here?");
                                        }
                                    });
                        }
                    } else {
                        Log.e("what", "the fuck");
                    }

        });

        momentSwipeRefresh = v.findViewById(R.id.momentSwipeRefresh);
        momentSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshFragment();
                        momentSwipeRefresh.setRefreshing(false);
                    }
                },1000);
            }
        });

        return v;
    }

    private void refreshFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, MomentFragment.class, null)
                .commit();
    }
}

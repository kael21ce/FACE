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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.techtown.face.R;
import org.techtown.face.activities.FamilyActivity;
import org.techtown.face.adapters.FamilyAdapter;
import org.techtown.face.models.Family;
import org.techtown.face.models.User;
import org.techtown.face.models.ViewData;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.ArrayList;

public class FrameFragment extends Fragment {

    PreferenceManager preferenceManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FACEdatabase";
    private SwipeRefreshLayout frameSwipeRefresh;
    Handler mHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_frame, container, false);
        FamilyAdapter adapter = new FamilyAdapter();
        mHandler = new Handler();

        adapter.setOnItemClickListener((position, user) -> {
            Intent contactIntent = new Intent(v.getContext(), FamilyActivity.class);
            contactIntent.putExtra(Constants.KEY_USER, user);
            startActivity(contactIntent);
        });

        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(v.getContext(), 2);


        //db에서 데이터 가져오기
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);
        ArrayList<ViewData> viewDataArrayList = new ArrayList<ViewData>();
        //다른 사람 정보 추가
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(myId)
                .collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();

                        int cnt = 0;
                        int familyNum = result.size();
                        Log.w("Firebase Family Number", String.valueOf(familyNum));

                        /***sky 추가***/
                        int skyNum = skyNum(familyNum);
                        //Log.w("skyNum", String.valueOf(skyNum));
                        for (int i = 0; i < skyNum; i++) {
                            adapter.addItem(new ViewData(0));
                        }

                        /***roof 추가***/
                        adapter.addItem(new ViewData(1));

                        /***layout 설정***/
                        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                            @Override
                            public int getSpanSize(int position) {
                                if (position < skyNum + 1) return 2;//roof
                                if (((familyNum % 2) == 1) && (position == skyNum + familyNum))
                                    return 2;
                                if (position > skyNum + familyNum) return 2;
                                return 1;
                            }
                        });

                        recyclerView.setLayoutManager(layoutManager);

                        for (QueryDocumentSnapshot document : result) {
                            Log.w("cnt 증가", String.valueOf(++cnt));
                            User user = new User();
                            user.name = document.getString(Constants.KEY_NAME);
                            //Log.w(TAG, user.name);
                            user.min_contact = Integer.parseInt(document.get(Constants.KEY_MIN_CONTACT).toString());
                            user.ideal_contact = Integer.parseInt(document.get(Constants.KEY_IDEAL_CONTACT).toString());
                            user.like = document.get(Constants.KEY_THEME_LIKE).toString();
                            user.dislike = document.get(Constants.KEY_THEME_DISLIKE).toString();
                            user.id = document.getString(Constants.KEY_USER);
                            user.expression = Integer.parseInt(document.get(Constants.KEY_EXPRESSION).toString());
                            int finalCnt1 = cnt;
                            db.collection(Constants.KEY_COLLECTION_USERS).document(user.id).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot snapshot = task1.getResult();
                                    user.image = snapshot.get(Constants.KEY_IMAGE).toString();
                                    user.mobile = snapshot.get(Constants.KEY_MOBILE).toString();
                                    user.path = snapshot.get(Constants.KEY_PATH).toString();

                                    Family family = new Family(user);
                                    ViewData face = new ViewData(2, family);
                                    face.setType(2);
                                    face.setFamily(family);
                                    adapter.addItem(face);
                                    Log.w("ADD ITEM", String.valueOf(adapter.getItemCount()-skyNum-1));

                                    //가족 추가 완료
                                    if (adapter.getItemCount()-skyNum-1 == familyNum) {
                                        Log.w("SET ADAPTER", String.valueOf(familyNum));
                                        //잔디 추가
                                        adapter.addItem(new ViewData(3));
                                    }
                                    //adapter 설정
                                    recyclerView.setAdapter(adapter);
                                }
                            });
                        }
                        Log.w(TAG, "Successfully loaded");
                    }
                });
        frameSwipeRefresh = v.findViewById(R.id.frameSwipeRefresh);
        frameSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshFragment();
                        frameSwipeRefresh.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        return v;
    }

    int skyNum(int familyNum) {
        int sky;
        switch (familyNum) {
            case 0:
            case 1:
            case 2:
                sky = 2;
                break;
            default:
                sky = 1;
                break;
        }
        return sky;
    }

    String change_filter(int expression) {
        String color;
        switch (expression) {
            case 1:
                color = "#111111";
                break;
            case 2:
                color = "#333333";
                break;
            case 3:
                color = "#666666";
                break;
            case 4:
                color = "#AAAAAA";
                break;
            case 5:
                color = "#FFFFFF";
                break;
            default:
                color = "0000000";
                break;
        }
        return color;
    }


    private void refreshFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, FrameFragment.class, null)
                .commit();

        }
}

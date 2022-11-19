package org.techtown.face.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
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
    String FTAG = "FrameFragment";
    private SwipeRefreshLayout frameSwipeRefresh;
    Handler mHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<Family> userArrayList = new ArrayList<Family>();
        View v = inflater.inflate(R.layout.fragment_frame, container, false);
        mHandler = new Handler();
        Log.w(FTAG, "onCreateView 호출됨.");

        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(v.getContext(), 2);
        FamilyAdapter adapter = new FamilyAdapter();

        //터치 이벤트 추가
        adapter.setOnItemClickListener((position, user) -> {
            Intent contactIntent = new Intent(v.getContext(), FamilyActivity.class);
            contactIntent.putExtra(Constants.KEY_USER,user);
            startActivity(contactIntent);
        });

        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());

        //db에서 데이터 가져오기
        String myId = preferenceManager.getString(Constants.KEY_USER_ID);
        ArrayList<ViewData> viewDataArrayList= new ArrayList<ViewData>();
        //다른 사람 정보 추가
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(myId)
                .collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        int cnt=0;
                        int familyNum = result.size();
                        Log.w("familyNum", String.valueOf(familyNum));

                        //sky 추가
                        int skyNum = skyNum(familyNum);
                        //Log.w("skyNum", String.valueOf(skyNum));
                        for(int i=0; i<skyNum; i++){
                            adapter.addItem(new ViewData(0));
                        }

                        //roof 추가
                        adapter.addItem(new ViewData(1));

                        //layout 설정
                        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){

                            @Override
                            public int getSpanSize(int position) {
                                if(position<skyNum+1) return 2;
                                if((familyNum%2==1) && (position==skyNum+familyNum)) return 2;
                                return 1;
                            }
                        });

                        recyclerView.setLayoutManager(layoutManager);

                        for (QueryDocumentSnapshot document : result) {
                            cnt++;
                            User user = new User();
                            user.name = document.getString(Constants.KEY_NAME);
                            Log.w(TAG, user.name);
                            user.min_contact = Integer.parseInt(document.get(Constants.KEY_MIN_CONTACT).toString());
                            user.ideal_contact = Integer.parseInt(document.get(Constants.KEY_IDEAL_CONTACT).toString());
                            user.like = document.get(Constants.KEY_THEME_LIKE).toString();
                            user.dislike = document.get(Constants.KEY_THEME_DISLIKE).toString();
                            user.id = document.getString(Constants.KEY_USER);
                            user.expression = Integer.parseInt(document.get(Constants.KEY_EXPRESSION).toString());
                            int finalCnt = cnt;
                            db.collection(Constants.KEY_COLLECTION_USERS).document(user.id).get().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    DocumentSnapshot snapshot = task1.getResult();
                                    user.image= snapshot.get(Constants.KEY_IMAGE).toString();
                                    user.mobile = snapshot.get(Constants.KEY_MOBILE).toString();
                                    user.path = snapshot.get(Constants.KEY_PATH).toString();

                                    Family family = new Family(user);
                                    ViewData face = new ViewData(2, family);
                                    face.setType(2);
                                    face.setFamily(family);
                                    adapter.addItem(face);
                                    //가족 추가 완료
                                    if(finalCnt == familyNum){
                                        Log.w("SET ADAPTER", "adapter setting");
                                        //잔디 추가

                                        //adapter 설정
                                        recyclerView.setAdapter(adapter);
                                    }
                                    //userArrayList.add(new Family(user));
                                    //Log.w("userarray", String.valueOf(userArrayList.size()));
                                    //adapter.addItem((new Family(user)));
                                    //recyclerView.setAdapter(adapter);
                                }
                            });
                        }
                        Log.w(TAG, "Successfully loaded");}
                });

        //viewDataArrayList를 채움

/*
        for(int i=0; i<userArrayList.size(); i++){
            Family family = userArrayList.get(i);
            ViewData face = new ViewData(2, family);
            viewDataArrayList.add(face);
        }

 */


/*
        FamilyAdapter adapter = new FamilyAdapter(viewDataArrayList);
        recyclerView.setAdapter(adapter);

 */


        //어탭터로 가족 추가
/*
        //터치 이벤트 추가
        adapter.setOnItemClickListener((position, user) -> {
            Intent contactIntent = new Intent(v.getContext(), FamilyActivity.class);
            contactIntent.putExtra(Constants.KEY_USER,user);
            startActivity(contactIntent);
        });

 */
        //스와이프하여 새로고침
        frameSwipeRefresh = v.findViewById(R.id.frameSwipeRefresh);
        frameSwipeRefresh.setOnRefreshListener(() -> {
            Log.w(FTAG, "onFresh 호출됨.");
            mHandler.postDelayed(() -> {
                refreshActivity();
                frameSwipeRefresh.setRefreshing(false);
            }, 1000);
        });
        return v;
    }

    int skyNum(int familyNum){
        int skyNum;
        switch (familyNum){
            case 0:
            case 1:
            case 2:
                skyNum = 2;
            default:
                skyNum = 1;
        }
        return skyNum;
    }

    String change_filter(int expression){
        String color;
        switch(expression){
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
}

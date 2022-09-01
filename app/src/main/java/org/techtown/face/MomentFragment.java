package org.techtown.face;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import me.relex.circleindicator.CircleIndicator3;

public class MomentFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FACEdatabase";

    private ViewPager2 myImageViewPager;
    private CircleIndicator3 myImgIndicator;
    private TextView myDate;

    //추후에 데이터베이스 활용하기
    private int[] images = new int[] {
            R.drawable.pasta,
            R.drawable.cat,
            R.drawable.pork
            };

    private String[] dates = new String[] {
            "2022-08-16", "2022-08-17", "2022-08-18"
    };

    private int[] himages = new int[] {
            R.drawable.beer
    };

    private String[] hdates = new String[] {
            "2022-08-16"
    };

    private int[] iimages = new int[] {
            R.drawable.holds, R.drawable.pasta
    };

    private String[] idates = new String[] {
            "2022-08-17", "2022-08-18"
    };
    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moment, container, false);

        //리사이클러뷰에 순간 아이템 배열
        RecyclerView momentRecyclerView = v.findViewById(R.id.momentRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(),
                LinearLayoutManager.VERTICAL, false);
        momentRecyclerView.setLayoutManager(layoutManager);
        MomentAdapter momentAdapter = new MomentAdapter();

        momentAdapter.addItem(new Moment("나", images, dates));

        //db에서 데이터 가져오기
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.get("name").toString();
                                if (name.equals("홍길동")) {
                                    momentAdapter.addItem(new Moment(name, himages, hdates));
                                } else {
                                    momentAdapter.addItem(new Moment(name, iimages, idates));
                                }
                                Log.w(TAG, "Successfully loaded");
                            }
                            momentRecyclerView.setAdapter(momentAdapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        //


        return v;
    }
}

package org.techtown.face.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.adapters.MomentAdapter;
import org.techtown.face.utilites.Constants;
import org.techtown.face.models.Moment;

public class MomentFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "FACEdatabase";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moment, container, false);
        //리사이클러뷰에 순간 아이템 배열
        RecyclerView momentRecyclerView = v.findViewById(R.id.momentRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        momentRecyclerView.setLayoutManager(layoutManager);
        MomentAdapter momentAdapter = new MomentAdapter();

        db.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String userId;
                for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                    userId = queryDocumentSnapshot.getId();
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    database.collection(Constants.KEY_COLLECTION_USERS)
                            .document(userId)
                            .collection(Constants.KEY_COLLECTION_IMAGES)
                            .orderBy(Constants.KEY_TIMESTAMP,Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()&&task1.getResult().size()>0){
                                    int j=0;
                                    for(QueryDocumentSnapshot document : task1.getResult()){
                                        if(document.exists()){
                                            j++;
                                        } else{
                                            Log.e("oh","hell");
                                        }
                                    }
                                    String name="";
                                    String[] image = new String[j];
                                    String[] date = new String[j];
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
                                    momentAdapter.addItem(new Moment(name, image, date));
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

        //db에서 데이터 가져오기

        return v;
    }
}

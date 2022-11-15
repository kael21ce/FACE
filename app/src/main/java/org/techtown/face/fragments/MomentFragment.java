package org.techtown.face.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;
import org.techtown.face.R;
import org.techtown.face.activities.MomentCheckActivity;
import org.techtown.face.adapters.MomentAdapter;
import org.techtown.face.adapters.myMomentAdapter;
import org.techtown.face.models.Moment;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MomentFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PreferenceManager preferenceManager;
    private final String TAG = "MomentFragment";
    UploadTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moment, container, false);
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        RecyclerView momentRecyclerView = v.findViewById(R.id.momentRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        momentRecyclerView.setLayoutManager(layoutManager);
        MomentAdapter momentAdapter = new MomentAdapter();
        momentAdapter.setOnItemClickListener(new MomentAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(View v, int position) {
                Intent deleteIntent = new Intent(v.getContext(), MomentCheckActivity.class);
                startActivity(deleteIntent);
            }
        });

        //리사이클러뷰에 순간 아이템 배열
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_IMAGES)
                .orderBy(Constants.KEY_TIMESTAMP,Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult().size()>0){
                        int j=0;
                        for(QueryDocumentSnapshot document : task.getResult()){
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
                        for(QueryDocumentSnapshot document : task.getResult()){
                            if(document.exists()){
                                name = document.get(Constants.KEY_NAME).toString();
                                image[i] = document.get(Constants.KEY_IMAGE).toString();
                                date[i] = document.get(Constants.KEY_TIMESTAMP).toString();
                                i++;
                            } else{
                                Log.e("oh","hell");
                            }
                        }
                        momentAdapter.addItem(new Moment(name,
                                preferenceManager.getString(Constants.KEY_USER_ID), image, date));
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
                                            momentAdapter.addItem(new Moment(name, userId, image, date));
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
        return v;
    }

    //RESULT_OK = -1
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == -1){
                    if (result.getData()!= null){
                        Log.e(TAG,result.getData().toString());
                        Uri imageUri = result.getData().getData();
                        String path = preferenceManager.getString(Constants.KEY_USER_ID)+"/"+imageUri.getLastPathSegment();

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference imageRef = storageReference.child(path);
                        uploadTask = imageRef.putFile(imageUri);
                        uploadTask.addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                showToast("We did it!");
                            } else {
                                showToast("Failed");
                            }
                        });

                        HashMap<String,Object> images = new HashMap<>();

                        String name = preferenceManager.getString(Constants.KEY_NAME);
                        String image = path;
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String dates = sdf.format(date);

                        images.put(Constants.KEY_NAME,name);
                        images.put(Constants.KEY_IMAGE,image);
                        images.put(Constants.KEY_TIMESTAMP,dates);

                        db.collection(Constants.KEY_COLLECTION_USERS)
                                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                .collection(Constants.KEY_COLLECTION_IMAGES)
                                .add(images)
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        showToast("horray");
                                    } else {
                                        showToast("fuck");
                                    }
                                });
                    }
                }
            });

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

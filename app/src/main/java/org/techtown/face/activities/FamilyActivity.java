package org.techtown.face.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.techtown.face.R;
import org.techtown.face.adapters.ImageSliderAdapter;
import org.techtown.face.adapters.MomentAdapter;
import org.techtown.face.databinding.ActivityFamilyBinding;
import org.techtown.face.fragments.MomentFragment;
import org.techtown.face.models.Moment;
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.HashMap;

import me.relex.circleindicator.CircleIndicator3;

public class FamilyActivity extends BaseActivity {

    PreferenceManager preferenceManager;
    ActivityFamilyBinding binding;
    User user;
    Dialog momentDialog;
    Handler mHandler;
    private final String TAG = "FamilyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFamilyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        user = new User();

        //이름, 전화번호, 최소, 이상적인 연락 횟수 인텐트에서 가져오기
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constants.KEY_USER);

        binding.exit.setOnClickListener(v -> finish());
        binding.hope.setText(user.name + "의 마음을 전합니다");
        binding.minContact.setText("아무리 적어도 " + user.min_contact + "일에 1번씩은 연락하면 좋겠어...");
        binding.idealContact.setText(user.ideal_contact + "일에 1번씩 연락하면 정말 좋을 것 같아!");
        binding.callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.mobile));
            startActivity(callIntent);
        });
        binding.momentButton.setText(user.name + "의 순간 보러 가기");
        //순간으로 이동하는 것 추가하기
        binding.momentButton.setOnClickListener(v -> showMomentDialog(FamilyActivity.this, user));
        binding.chatButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), ChatActivity.class);
            intent1.putExtra(Constants.KEY_USER, user);
            startActivity(intent1);
            finish();
        });
        binding.contactButton.setOnClickListener(v -> sendContact());
        binding.themeLike.setText(user.like + " 이야기를 함께한다면 정말 즐거울 것 같아!");
        binding.themeDislike.setText(user.dislike + " 이야기는 조금 줄여주면 좋겠어...");
    }

    private void sendContact(){
        HashMap<String, Object> notification = new HashMap<>();
        notification.put(Constants.KEY_NOTIFICATION, Constants.KEY_MEET);
        notification.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
        notification.put(Constants.KEY_USER, preferenceManager.getString(Constants.KEY_USER_ID));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(user.id)
                .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                .add(notification)
                .addOnCompleteListener(task -> Toast.makeText(this, "요청이 성공하였습니다.",Toast.LENGTH_SHORT).show());

    }

    private void showMomentDialog(Context context, User user) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(context);
        mHandler = new Handler();

        momentDialog = new Dialog(context);
        momentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        momentDialog.setContentView(R.layout.user_moment);
        ImageButton user_closeMoment = momentDialog.findViewById(R.id.user_closeMoment);
        user_closeMoment.setOnClickListener(v -> momentDialog.dismiss());
        RecyclerView user_momentRecycler = momentDialog.findViewById(R.id.user_momentRecycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        user_momentRecycler.setLayoutManager(layoutManager);
        MomentAdapter momentAdapter = new MomentAdapter();

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_NAME, user.name)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            String userId = queryDocumentSnapshot.getString(Constants.KEY_USER);
                            db.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(userId)
                                    .collection(Constants.KEY_COLLECTION_IMAGES)
                                    .orderBy(Constants.KEY_TIMESTAMP, Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()&&task1.getResult().size()>0){
                                            int j=0;
                                            for(QueryDocumentSnapshot document : task1.getResult()){
                                                if(document.exists()){
                                                    j++;
                                                } else{
                                                    Log.e(TAG,"No document exists");
                                                }
                                            }
                                            String name="";
                                            String[] image = new String[j];
                                            String[] date = new String[j];
                                            int i=0;
                                            for(QueryDocumentSnapshot document : task1.getResult()) {
                                                if(document.exists()){
                                                    name = document.get(Constants.KEY_NAME).toString();
                                                    image[i] = document.get(Constants.KEY_IMAGE).toString();
                                                    date[i] = document.get(Constants.KEY_TIMESTAMP).toString();
                                                    i++;
                                                } else{
                                                    Log.e(TAG,"No moment exists");
                                                }
                                            }
                                            momentAdapter.addItem(new Moment(name, "y", image, date));
                                            user_momentRecycler.setAdapter(momentAdapter);
                                        } else {
                                            Log.e(TAG, "Task is not processed successfully");
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "Task is not processed successfully");
                    }

                });
        momentDialog.show();
    }
}

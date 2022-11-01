package org.techtown.face.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.techtown.face.databinding.ActivityFamilyBinding;
import org.techtown.face.fragments.MomentFragment;
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.HashMap;

public class FamilyActivity extends BaseActivity {

    PreferenceManager preferenceManager;
    ActivityFamilyBinding binding;
    User user;

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
        binding.familyName.setText(user.name);
        binding.hope.setText(user.name + "의 마음을 전합니다");
        binding.minContact.setText("아무리 적어도 " + user.min_contact + "일에 1번씩은 연락하면 좋겠어...");
        binding.idealContact.setText(user.ideal_contact + "일에 1번씩 연락하면 정말 좋을 것 같아!");
        binding.callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.mobile));
            startActivity(callIntent);
        });
        binding.momentButton.setText(user.name + "의 순간 보러 가기");
        //순간으로 이동하는 것 추가하기
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
        HashMap<String, Object> meetRequest = new HashMap<>();
        meetRequest.put(Constants.KEY_TYPE,Constants.KEY_MEET_REQUEST);
        meetRequest.put(Constants.KEY_NOTIFICATION, preferenceManager.getString(Constants.KEY_USER_ID));

        HashMap<String, Object> notification = new HashMap<>();
        notification.put(Constants.KEY_NOTIFICATION, Constants.KEY_MEET);
        notification.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(user.id)
                .collection(Constants.KEY_COLLECTION_CLOUD)
                .add(meetRequest)
                .addOnCompleteListener(task -> {if(task.isSuccessful()){Toast.makeText(this, "요청이 성공하였습니다.",Toast.LENGTH_SHORT).show();}});

        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(user.id)
                .collection(Constants.KEY_COLLECTION_NOTIFICATION)
                .add(notification)
                .addOnCompleteListener(task -> Log.e("su","ccess"));
    }
}

package org.techtown.face.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivityFamilyBinding;
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

public class FamilyActivity extends BaseActivity {

    TextView fName;
    //바라는 연락 횟수; 데이터베이스 구현 후 적용
    TextView minContact;
    TextView idealContact;
    TextView themeLike;
    TextView themeDislike;
    PreferenceManager preferenceManager;
    ActivityFamilyBinding binding;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFamilyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        user = new User();

        fName = findViewById(R.id.familyName);
        minContact = findViewById(R.id.minContact);
        idealContact = findViewById(R.id.idealContact);
        themeLike = findViewById(R.id.themeLike);
        themeDislike = findViewById(R.id.themeDislike);




        Button exitButton = findViewById(R.id.exit);
        exitButton.setOnClickListener(view -> finish());

        //이름, 전화번호, 최소, 이상적인 연락 횟수 인텐트에서 가져오기
        Intent intent = getIntent();

        fName.setText(intent.getStringExtra(Constants.KEY_NAME));
        String phone_number = "tel:" + intent.getStringExtra(Constants.KEY_PHONE_NUMBER);
        Integer mContact = intent.getIntExtra(Constants.KEY_MIN_CONTACT,0);
        Integer iContact = intent.getIntExtra(Constants.KEY_IDEAL_CONTACT,0);
        minContact.setText("적어도 " + mContact + "일에 1번");
        idealContact.setText(iContact + "일에 1번");

        fName.setText(intent.getStringExtra("name"));



        //
        FloatingActionButton callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phone_number));
            startActivity(callIntent);
        });



        user = (User) intent.getSerializableExtra(Constants.KEY_USER);


        binding.chatButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), ChatActivity.class);
            intent1.putExtra(Constants.KEY_USER, user);
            startActivity(intent1);
            finish();
        });

    }


}

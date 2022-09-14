package org.techtown.face.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivityFamilyBinding;
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

public class FamilyActivity extends BaseActivity {

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
        preferenceManager = new PreferenceManager(getApplicationContext());
        user = new User();

        themeLike = findViewById(R.id.themeLike);
        themeDislike = findViewById(R.id.themeDislike);

<<<<<<< HEAD
=======



>>>>>>> main
        Button exitButton = findViewById(R.id.exit);
        exitButton.setOnClickListener(view -> finish());

        //이름, 전화번호, 최소, 이상적인 연락 횟수 인텐트에서 가져오기
        Intent intent = getIntent();
<<<<<<< HEAD
        user = (User) intent.getSerializableExtra(Constants.KEY_USER);

        binding.familyName.setText(user.name);
        binding.minContact.setText("적어도 " + user.min_contact + "일에 1번");
        binding.idealContact.setText(user.ideal_contact + "일에 1번");
=======

        fName.setText(intent.getStringExtra(Constants.KEY_NAME));
        String phone_number = "tel:" + intent.getStringExtra(Constants.KEY_PHONE_NUMBER);
        Integer mContact = intent.getIntExtra(Constants.KEY_MIN_CONTACT,0);
        Integer iContact = intent.getIntExtra(Constants.KEY_IDEAL_CONTACT,0);
        minContact.setText("적어도 " + mContact + "일에 1번");
        idealContact.setText(iContact + "일에 1번");

        fName.setText(intent.getStringExtra("name"));


>>>>>>> main

        binding.callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.mobile));
            startActivity(callIntent);
        });
<<<<<<< HEAD
=======



        user = (User) intent.getSerializableExtra(Constants.KEY_USER);


>>>>>>> main
        binding.chatButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), ChatActivity.class);
            intent1.putExtra(Constants.KEY_USER, user);
            startActivity(intent1);
            finish();
        });

        binding.themeLike.setText(user.like);
        binding.themeDislike.setText(user.dislike);

    }


}

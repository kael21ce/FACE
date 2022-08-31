package org.techtown.face;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FamilyActivity extends AppCompatActivity {

    TextView fName;
    //바라는 연락 횟수; 데이터베이스 구현 후 적용
    TextView maxContact;
    TextView minContact;
    TextView idealContact;
    TextView themeLike;
    TextView themeDislike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);

        fName=findViewById(R.id.familyName);
        minContact=findViewById(R.id.minContact);
        idealContact = findViewById(R.id.idealContact);
        themeLike = findViewById(R.id.themeLike);
        themeDislike = findViewById(R.id.themeDislike);

        Button exitButton = findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //이름, 전화번호 인텐트에서 가져오기
        Intent intent = getIntent();
        fName.setText(intent.getStringExtra("name"));
        String mobile = "tel:" + intent.getStringExtra("mobile");
        //

        FloatingActionButton callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(mobile));
                startActivity(callIntent);
            }
        });
    }
}

package org.techtown.face;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountActivity extends AppCompatActivity {

    TextView tex1, tex2;
    Button bnt_logout, btn_member_delete;
    String user_id = null;
    private FirebaseAuth user_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        tex1 = findViewById(R.id.te1);
        tex2 = findViewById(R.id.te2);
        bnt_logout = findViewById(R.id.btn1);
        btn_member_delete = findViewById(R.id.btn2);
        user_auth = FirebaseAuth.getInstance(); //로그아웃 위해 필요함

        FirebaseUser user = user_auth.getCurrentUser(); //현재 사용자 받아오기

        //firebase 정의
        final FirebaseFirestore user_table = FirebaseFirestore.getInstance();

        if (user != null) {
            String email = user.getEmail();
            tex1.setText(email);

            String uid = user.getUid();
            user_id = uid; // 아이디 변수에 넣기
            tex2.setText(uid);
        }
        else {
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //로그아웃
        bnt_logout.setOnClickListener(v -> {
            user_auth.signOut();
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        //회원탈퇴
        btn_member_delete.setOnClickListener(v -> {
            Log.d("delete-member-onclick", "버튼을 누르긴 했어,,");
            assert user != null;
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AccountActivity.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                            filed_Delete(user_table, user_id);
                            Log.d("delete-member", "성공");
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                        else {
                            Log.d("delete-member-err", "탈퇴 실패");
                        }
                    });
        });
    }

    void filed_Delete(FirebaseFirestore user_table, String user_id) { // 해당 user id 필드값 삭제

        user_table.collection("User").document(user_id)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("User-delete", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w("User-delete", "Error deleting document", e));
    }
}

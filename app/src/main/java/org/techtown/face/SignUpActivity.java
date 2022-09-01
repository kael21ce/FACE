package org.techtown.face;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail, editTextPassword;
    private TextView password;

    //비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_passWord);
        password = findViewById(R.id.pwd);
        Button buttonJoin = findViewById(R.id.btn_join);

        buttonJoin.setOnClickListener(v -> {
            if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                // 이메일과 비밀번호가 공백이 아닌 경우
                createUser(editTextEmail.getText().toString(), editTextPassword.getText().toString(), password);
            }
            else {
                // 이메일과 비밀번호가 공백인 경우
                Toast.makeText(SignUpActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void createUser(String email, String password, TextView check_pwd) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 회원가입 성공시
                        Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        check_pwd.setText("비밀번호가 유효합니다.");
                        finish();
                    }
                    else {
                        // 계정이 중복된 경우
                        if (!isValidPasswd())
                            check_pwd.setText("비밀번호는 4-16자와 특수문자를 포함해야합니다.");
                    }
                });
    }
    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        String check_password = editTextPassword.toString();
        // 비밀번호 형식 불일치
        return PASSWORD_PATTERN.matcher(check_password).matches();
    }

}

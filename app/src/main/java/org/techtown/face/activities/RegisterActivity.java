package org.techtown.face.activities;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.face.R;

public class RegisterActivity extends AppCompatActivity {

    Button rejectButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_btregister);

        rejectButton.findViewById(R.id.rejectButton);
        rejectButton.setOnClickListener(view -> finish());
    }
}

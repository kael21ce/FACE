package org.techtown.face.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivityChangeInfoBinding;

public class ChangeInfoActivity extends AppCompatActivity {

    ActivityChangeInfoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_info);
    }
}
package org.techtown.face.activities;

import static android.content.ContentValues.TAG;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.techtown.face.R;
import org.techtown.face.databinding.ActivityFamilyBinding;
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

public class FamilyActivity extends BaseActivity {

    PreferenceManager preferenceManager;
    ActivityFamilyBinding binding;
    User user;
    HashMap<String,Object> imageMap = new HashMap<>();


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
        binding.minContact.setText("적어도 " + user.min_contact + "일에 1번");
        binding.idealContact.setText(user.ideal_contact + "일에 1번");
        binding.callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.mobile));
            startActivity(callIntent);
        });
        binding.chatButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), ChatActivity.class);
            intent1.putExtra(Constants.KEY_USER, user);
            startActivity(intent1);
            finish();
        });
        binding.themeLike.setText(user.like);
        binding.themeDislike.setText(user.dislike);
        binding.button.setOnClickListener(v -> {
            Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent2);
        });
        binding.button2.setOnClickListener(v -> {
            Intent intent3 = new Intent(FamilyActivity.this,MomentCheckActivity.class);
            startActivity(intent3);
        });
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData()!= null){
                        Log.e(TAG,result.getData().toString());
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            long now = System.currentTimeMillis();
                            Date date = new Date(now);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String getTime = sdf.format(date);
                            imageMap.put(Constants.KEY_NAME,preferenceManager.getString(Constants.KEY_NAME));
                            imageMap.put(Constants.KEY_TIMESTAMP, getTime);
                            imageMap.put(Constants.KEY_IMAGE,encodeImage(bitmap));
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection(Constants.KEY_COLLECTION_USERS)
                                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                    .collection(Constants.KEY_COLLECTION_IMAGES)
                                    .add(imageMap)
                                    .addOnCompleteListener(task -> showToast("upload success"));
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte [] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
}

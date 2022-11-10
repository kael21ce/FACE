package org.techtown.face.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.face.databinding.ActivityChangeInfoBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ChangeInfoActivity extends AppCompatActivity {

    ActivityChangeInfoBinding binding;
    PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(preferenceManager.getString(Constants.KEY_PATH));
        reference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(this).load(uri).into(binding.imageView4));
        binding.imageView3.setImageBitmap(getUserImage(preferenceManager.getString(Constants.KEY_IMAGE)));
        binding.phoneNumber.setText(preferenceManager.getString(Constants.KEY_MOBILE));
        binding.buttonChange.setOnClickListener(view -> changeInfo());
        binding.layoutImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.layoutImage2.setOnClickListener(v -> {
            Intent intent = new Intent(ChangeInfoActivity.this, CameraActivity.class);
            startActivity(intent);
        });
    }



    private void changeInfo(){
        if(encodedImage!=null){preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);}
        preferenceManager.putString(Constants.KEY_MOBILE,binding.phoneNumber.getText().toString());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(
                        Constants.KEY_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE),
                        Constants.KEY_MOBILE, preferenceManager.getString(Constants.KEY_MOBILE)
                ).addOnSuccessListener(unused -> showToast("Update Success"))
                .addOnFailureListener(runnable -> showToast("Update Failed"));

        Intent intent = new Intent(ChangeInfoActivity.this, AccountActivity.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(String.valueOf(encodedImage),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
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

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData()!= null){
                        Log.e(TAG,result.getData().toString());
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageView3.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
}
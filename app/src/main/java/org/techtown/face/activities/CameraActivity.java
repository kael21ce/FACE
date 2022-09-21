package org.techtown.face.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.techtown.face.databinding.ActivityCameraBinding;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.io.ByteArrayOutputStream;

public class CameraActivity extends AppCompatActivity {
    ActivityCameraBinding binding;
    Bitmap bitmap;
    PreferenceManager preferenceManager;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        binding.imageUpload.setVisibility(View.INVISIBLE);
        binding.next.setVisibility(View.INVISIBLE);

        binding.cameraSet.setOnClickListener(v -> setCamera());
        binding.imageUpload.setOnClickListener(v -> upload());
        binding.next.setOnClickListener(v -> {
            Intent intent = new Intent(CameraActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

    private void setCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pickImage.launch(takePictureIntent);
    }

    private void upload(){

        if(binding.cameraImage.getDrawable()!=null){
            String path = preferenceManager.getString(Constants.KEY_USER_ID)+"/"+"profile"+"5";
            preferenceManager.putString(Constants.KEY_PATH,path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(path);
            uploadTask = imageRef.putBytes(data);
            uploadTask.addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(this, "Uploaded Succeed",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Uploaded Failed",Toast.LENGTH_SHORT).show();
                }
            });
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID))
                    .update(Constants.KEY_PATH,path).addOnSuccessListener(unused -> {
                        binding.next.setVisibility(View.VISIBLE);
                        Log.e("Camera", "Succeed");
                    });
        }

    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData()!= null){
                        Bundle extras = result.getData().getExtras();
                        bitmap = (Bitmap) extras.get("data");
                        binding.cameraImage.setImageBitmap(bitmap);
                        binding.imageUpload.setVisibility(View.VISIBLE);
                    }
                }
            });
}
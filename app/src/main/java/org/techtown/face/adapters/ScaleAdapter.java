package org.techtown.face.adapters;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.activities.FamilyActivity;
import org.techtown.face.models.User;
import org.techtown.face.utilites.Constants;
import org.techtown.face.models.Family;
import org.techtown.face.R;
import org.techtown.face.utilites.PreferenceManager;
import org.techtown.face.utilites.ScaleInfo;

import java.util.ArrayList;

public class ScaleAdapter extends RecyclerView.Adapter<ScaleAdapter.ViewHolder>{
    ArrayList<Family.FamilyScale> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.scale_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Family.FamilyScale item = items.get(position);
        viewHolder.setItem(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Family.FamilyScale item) {
        items.add(item);
    }

    public void setItems(ArrayList<Family.FamilyScale> items) {
        this.items = items;
    }

    public Family.FamilyScale getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Family.FamilyScale item) {
        items.set(position, item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String TAG = "FACEdatabase";
        String STAG = "ScaleAdapter";
        PreferenceManager preferenceManager;

        TextView nameTxt;
        TextView incomingNum;
        TextView outgoingNum;
        ImageView scaleHead;
        FloatingActionButton interactButton;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.scaleName);
            incomingNum = itemView.findViewById(R.id.incomingNum);
            outgoingNum = itemView.findViewById(R.id.outgoingNum);
            scaleHead = itemView.findViewById(R.id.scale_head);
            interactButton = itemView.findViewById(R.id.interactButton);

        }

        @SuppressLint("SetTextI18n")
        public void setItem(Family.FamilyScale item) {
            ScaleInfo scaleInfo = new ScaleInfo();
            preferenceManager = new PreferenceManager(itemView.getContext());
            Handler mHandler = new Handler();
            final int[] numI = {0};
            final int[] numO = {0};
            float y = 0;
            final float[] angle = {0};

            nameTxt.setText(item.getScaleName());
            String mobileForScale = item.getScaleMoible();
            scaleInfo.getInboxNum(itemView.getContext(), mobileForScale);
            scaleInfo.getSentNum(itemView.getContext(), mobileForScale);
            scaleInfo.getAngle(itemView.getContext(), mobileForScale);
            mHandler.postDelayed(() -> {
                numI[0] = scaleInfo.getIncomingNum(itemView.getContext(), mobileForScale)
                        + preferenceManager.getInt("in" + mobileForScale);
                numO[0] = scaleInfo.getOutgoingNum(itemView.getContext(), mobileForScale)
                        + preferenceManager.getInt("out" + mobileForScale);
                Log.w(STAG, "Inbox in adapter-" + preferenceManager.getInt("in" + mobileForScale));
                Log.w(STAG, "Sent in adapter-" + preferenceManager.getInt("out" + mobileForScale));
                incomingNum.setText("수신: " + numI[0]);
                outgoingNum.setText("발신: " + numO[0]);
                //각도
                angle[0] = preferenceManager.getFloat("angle" + mobileForScale);
                Log.w(STAG, "Angle in adapter-" + angle[0]);
                scaleHead.setRotation(angle[0]);
            }, 2000);


            //interactButton 클릭 시 FamilyActivity로 넘어가기
            interactButton.setOnClickListener(v -> {
                User user = new User();
                //db에서 데이터 가져오기
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.get(Constants.KEY_MOBILE).equals(mobileForScale)) {
                                        user.name = document.get(Constants.KEY_NAME).toString();
                                        user.email = document.get(Constants.KEY_EMAIL).toString();
                                        user.image= document.get(Constants.KEY_IMAGE).toString();
                                        user.token = document.get(Constants.KEY_FCM_TOKEN).toString();
                                        user.id = document.getId();
                                        user.mobile = document.get(Constants.KEY_MOBILE).toString();
                                        user.min_contact = Integer.parseInt(document.get(Constants.KEY_MIN_CONTACT).toString());
                                        user.ideal_contact = Integer.parseInt(document.get(Constants.KEY_IDEAL_CONTACT).toString());
                                        user.like = document.get(Constants.KEY_THEME_LIKE).toString();
                                        user.dislike = document.get(Constants.KEY_THEME_DISLIKE).toString();
                                        //Intent 전송
                                        Intent contactIntent = new Intent(v.getContext(), FamilyActivity.class);
                                        contactIntent.putExtra(Constants.KEY_USER, user);
                                        v.getContext().startActivity(contactIntent);
                                        Log.w(TAG, "Successfully loaded from ScaleAdapter");
                                    }
                                }
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        });

            });


        }
    }
}

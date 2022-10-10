package org.techtown.face.network;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ContactService extends Service {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    PreferenceManager preferenceManager;
    String TAG = "ContactService";
    Handler mHandler = new Handler();

    public ContactService() {
    }

    public void calculateExpression(long now) {
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getString(Constants.KEY_USER_ID) != null) {
            final long[] lastNow = {0};
            String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .document(currentUserId)
                    .collection(Constants.KEY_COLLECTION_USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        AtomicInteger idealContact = new AtomicInteger();
                        AtomicInteger minContact = new AtomicInteger();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //상대방의 window 가져오기
                                lastNow[0] = (long) document.get(Constants.KEY_WINDOW);
                                String userId = Objects.requireNonNull(document.get(Constants.KEY_USER)).toString();
                                preferenceManager.putInt("ideal" + userId, 0);
                                preferenceManager.putInt("min" + userId, 0);
                                //
                                idealContact.set(Integer.parseInt(Objects.requireNonNull(document.get("ideal_contact"))
                                        .toString()) * 86400000);
                                preferenceManager.putInt("ideal" + userId, idealContact.get());
                                minContact.set(Integer.parseInt(Objects.requireNonNull(document.get("min_contact"))
                                        .toString()) * 86400000);
                                preferenceManager.putInt("min" + userId, minContact.get());
                                mHandler.postDelayed(() -> {
                                    Log.d(TAG, userId + "-" + "idealContact: "
                                            + preferenceManager.getInt("ideal" + userId) +
                                            " | " + "minContact: "
                                            + preferenceManager.getInt("min" + userId));
                                    long term = (minContact.get() - idealContact.get()) / 3;
                                    long diff = now - lastNow[0];
                                    Log.e(TAG, "연락 간격 차이: " + diff);
                                    if (now - lastNow[0] < idealContact.get()) {
                                        //나의 데이터베이스에 상대방 expression 업데이트
                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(currentUserId)
                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                .document(document.getId())
                                                .update(Constants.KEY_EXPRESSION, 5);
                                        Log.w(TAG, currentUserId + "표정 5로 업데이트 됨.");
                                        //상대방 데이터베이스에 내 expression 업데이트
                                        db.collection(Constants.KEY_COLLECTION_USERS)
                                                .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                .collection(Constants.KEY_COLLECTION_USERS)
                                                .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                .get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                                    .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                                    .document(documentSnapshot.getId())
                                                                    .update(Constants.KEY_EXPRESSION, 5);
                                                            Log.w(TAG, document.get(Constants.KEY_USER) + "표정 5로 업데이트 됨.");
                                                        }
                                                    }
                                                });
                                    }
                                    if (now - lastNow[0] >= idealContact.get()) {
                                        if (now - lastNow[0] < idealContact.get() + term) {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update(Constants.KEY_EXPRESSION, 4);
                                            Log.w(TAG, currentUserId + "표정 4로 업데이트 됨.");
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_EXPRESSION, 4);
                                                                Log.w(TAG, document.get(Constants.KEY_USER) + "표정 4로 업데이트 됨.");
                                                            }
                                                        }
                                                    });
                                        } else if (now - lastNow[0] < idealContact.get() + term*2) {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update(Constants.KEY_EXPRESSION, 3);
                                            Log.w(TAG, currentUserId + "표정 3으로 업데이트 됨.");
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_EXPRESSION, 3);
                                                                Log.w(TAG, document.get(Constants.KEY_USER) + "표정 3으로 업데이트 됨.");
                                                            }
                                                        }
                                                    });
                                        } else if (now - lastNow[0] < idealContact.get() + term*3) {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update(Constants.KEY_EXPRESSION, 2);
                                            Log.w(TAG, currentUserId + "표정 2로 업데이트 됨.");
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_EXPRESSION, 2);
                                                                Log.w(TAG, document.get(Constants.KEY_USER) + "표정 2로 업데이트 됨.");
                                                            }
                                                        }
                                                    });
                                        } else {
                                            //나의 데이터베이스에 상대방 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(currentUserId)
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(document.getId())
                                                    .update(Constants.KEY_EXPRESSION, 1);
                                            Log.w(TAG, currentUserId + "표정 1로 업데이트 됨.");
                                            //상대방 데이터베이스에 내 expression 업데이트
                                            db.collection(Constants.KEY_COLLECTION_USERS)
                                                    .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                    .collection(Constants.KEY_COLLECTION_USERS)
                                                    .whereEqualTo(Constants.KEY_USER, currentUserId)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                                                db.collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(Objects.requireNonNull(document.get(Constants.KEY_USER)).toString())
                                                                        .collection(Constants.KEY_COLLECTION_USERS)
                                                                        .document(documentSnapshot.getId())
                                                                        .update(Constants.KEY_EXPRESSION, 1);
                                                                Log.w(TAG, document.get(Constants.KEY_USER) + "표정 1로 업데이트 됨.");
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }, 1000);
                            }
                        }
                    });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        long now = System.currentTimeMillis();
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_CALCULATE_EXPRESSION)) {
                    calculateExpression(now);
                    Log.d(TAG, "ContactService 작동 중");
                }
            }
        }
        return new ContactBinder();
    }

    class ContactBinder extends Binder {
        public void showBound() {
            Log.w(TAG, TAG + " 성공적으로 바인딩 됨.");
        }

        public void loseBinding() {
            Log.w(TAG, TAG + " 바인딩 해제 됨.");
        }
    }
}
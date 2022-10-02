package org.techtown.face.network;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.face.R;
import org.techtown.face.utilites.Constants;
import org.techtown.face.utilites.PreferenceManager;

import java.util.HashMap;

public class GeoService extends Service {



    //위도 경도 게산식이 있는 코드
    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_MULTI_PROCESS);
            String myId = preferences.getString(Constants.KEY_USER_ID, null);

            if (locationResult != null && locationResult.getLastLocation() != null) {
                //위도와 경도 받아서 로그로 출력하기
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                HashMap<String,Object> location = new HashMap<>();
                location.put(Constants.KEY_LATITUDE, latitude);
                location.put(Constants.KEY_LONGITUDE, longitude);
                Log.e("LOCATION_UPDATE", latitude + ", " + longitude);


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                //위도와 경도 데이터 유저 문서에 업데이트 하기
                db.collection(Constants.KEY_COLLECTION_USERS).document(myId).update(location);

                //위도와 경도로 거리 계산하고 업데이트 하기
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .document(myId)
                        .collection(Constants.KEY_COLLECTION_USERS)
                        .get()
                        .addOnCompleteListener(task -> {
                           if(task.isSuccessful()){
                               for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                   String userId = documentSnapshot.getString(Constants.KEY_USER);
                                   //상대방의 위도와 경도 데이터 가져오기기
                                   db.collection(Constants.KEY_COLLECTION_USERS)
                                           .document(userId)
                                           .get()
                                           .addOnCompleteListener(task1 -> {
                                               if(task1.isSuccessful()) {
                                                   DocumentSnapshot documentSnapshot1 = task1.getResult();
                                                   Double latitude1 = documentSnapshot1.getDouble(Constants.KEY_LATITUDE);
                                                   Double longitude1 = documentSnapshot1.getDouble(Constants.KEY_LONGITUDE);
                                                   Log.e("FACEGeo", "위치를 출력합니다-"
                                                           + documentSnapshot1.getString(Constants.KEY_NAME) +": " + "Latitude: " + latitude1
                                                           + " & " + "Longitude: " + longitude1);
                                                   if(latitude1 != null && longitude1 !=null){
                                                       double lat1 = latitude1;
                                                       double lon1 = longitude1;
                                                       double dist = distance(latitude,longitude,lat1,lon1,"meter");
                                                       Log.e("This","Is->"+dist);
                                                       Log.e("FACEGeo", "거리를 출력합니다-" + documentSnapshot1.getString(Constants.KEY_NAME) + ": "
                                                               + distance(latitude,longitude,lat1,lon1,"meter"));

                                                       //계산한 거리로 window 업데이트 하기
                                                       if(dist<50){
                                                           HashMap<String,Object> now = new HashMap<>();
                                                           now.put(Constants.KEY_WINDOW, System.currentTimeMillis());
                                                           db.collection(Constants.KEY_COLLECTION_USERS)
                                                                   .document(myId)
                                                                   .collection(Constants.KEY_COLLECTION_USERS)
                                                                   .document(documentSnapshot.getId())
                                                                   .update(now);
                                                           Log.e("FACEGeo", "업데이트된 윈도우-"
                                                                   + documentSnapshot1.getString(Constants.KEY_NAME) +": " + System.currentTimeMillis());
                                                       }
                                                   }else{
                                                       Log.e("Hey", "상대방이 GPS를 사용하지 않고 있습니다.");
                                                   }
                                               }else{
                                                   Log.e("SHit","fuck");
                                               }
                                           });
                               }
                           } else {
                               Log.e("Oh","Fuck");
                           }
                        });
            }
        }
    };

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
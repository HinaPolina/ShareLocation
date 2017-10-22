package hinapolina.com.sharelocation.services;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.ui.Application;
import hinapolina.com.sharelocation.ui.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static hinapolina.com.sharelocation.activities.SearchResult.FCM_MESSAGE_URL;

/**
 * Created by polina on 10/17/17.
 */

public class JobScheduler extends JobService {
    @Override

    public boolean onStartJob(final JobParameters jobParameters) {
        System.err.println("!!!! onStartJob");
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationServices.getFusedLocationProviderClient(getApplicationContext()).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        System.err.println("!!!! onStartJob: update DB");
                        Bundle parameters = jobParameters.getExtras();
                        String id = parameters.getString(Utils.USER_ID, "");
                        DatabaseReference mDatabase = Application.getmDatabase();
                        mDatabase.child("users").child(id).child("lat").setValue(location.getLatitude());
                        mDatabase.child("users").child(id).child("lng").setValue(location.getLongitude());
                        mDatabase.child("users").child(id).child("battery").setValue((int) Utils.getBatteryLevel(getApplicationContext()));
                        mDatabase.child("users").child(id).child("date").setValue(Calendar.getInstance().getTime().toString());
                    }
                });

            }
        }).start();
        return false;
    }

    private void sendNotificationFriendsNearBy(Location current, User user, String name) {
        float [] dist = new float[1];

        Location.distanceBetween(current.getLatitude(),current.getLongitude(),user.getLat(),user.getLng(),dist);

        if(dist[0]/1000 > 1){
            OkHttpClient mClient = new OkHttpClient();
            JSONObject root = new JSONObject();
            JSONObject notification = new JSONObject();
//        notification.put("body", body);
            try {
                notification.put("title", name + " is near you");

                //        notification.put("icon", icon);

                JSONObject data = new JSONObject();
                data.put("message", "I am message");
                root.put("notification", notification);
                root.put("data", data);
                root.put("registration_ids", new JSONArray(Arrays.asList(user.getToken())));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), root.toString());
            Request request = new Request.Builder()
                    .url(FCM_MESSAGE_URL)
                    .post(body)
                    .addHeader("Authorization", "key=" + getResources().getString(R.string.servrt_id))
                    .addHeader("Content-Type", "application/json")
                    .build();
            mClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.err.println("Ooops!! ");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.err.println("RESPONSE CODE: " + response.code());
                    System.err.println("RESPONSE: " + response.body().string());
                }
            });

        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}

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

import hinapolina.com.sharelocation.ui.Application;
import hinapolina.com.sharelocation.ui.Utils;

/**
 * Created by polina on 10/17/17.
 */

public class JobScheduler extends JobService {
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationServices.getFusedLocationProviderClient(getApplicationContext()).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Bundle parameters = jobParameters.getExtras();
                        String id = parameters.getString(Utils.USER_ID, "");
                        DatabaseReference mDatabase = Application.getmDatabase();
                        mDatabase.child("users").child(id).child("lat").setValue(location.getLatitude());
                        mDatabase.child("users").child(id).child("lng").setValue(location.getLongitude());
                        mDatabase.child("users").child(id).child("battery").setValue(Utils.getBatteryLevel(getApplicationContext()));
                    }
                });

            }
        }).start();

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.ui.Application;
import hinapolina.com.sharelocation.ui.Utils;

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
                        if (location != null) {
                            Bundle parameters = jobParameters.getExtras();
                            String id = parameters.getString(Utils.USER_ID, "");
                            String name = parameters.getString(Utils.USER_NAME);
                            DatabaseReference mDatabase = Application.getmDatabase();
                            Map<String, Object> params = new HashMap<>();
                            params.put(Utils.LAT, location.getLatitude());
                            params.put(Utils.LNG, location.getLongitude());
                            params.put(Utils.BATTERY, (int) Utils.getBatteryLevel(getApplicationContext()));
                            params.put(Utils.DATE, Calendar.getInstance().getTime().toString());
                            mDatabase.child(Utils.USERS).child(id).updateChildren(params);
                            sendNotificationFriendsNearBy(location, name, mDatabase, id);
                        }
                    }
                });

            }
        }).start();

        return false;
    }

    private void sendNotificationFriendsNearBy(final Location current, final String name, final DatabaseReference mDatabase, final String currentID) {
        mDatabase.child(Utils.NEAR).child(currentID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot friends) {
                String res = friends.getValue(String.class);
                if(res==null) return;
                String [] arr = res.split(";");
                final Set<String> friendsNear =  new HashSet<>(Arrays.asList(arr));
                System.err.println("Friends of  user " + res + " into DB" );

                mDatabase.child(Utils.FRIENDS).child(currentID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot friends) {
                        String res = friends.getValue(String.class);
                        String [] arr = res.split(";");
                        final Set<String> friendsIdList =  new HashSet<>(Arrays.asList(arr));
                        System.err.println("Friends of  user " + res + " into DB" );


                        mDatabase.child(Utils.USERS).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Set<String> newNearFriends = new HashSet<String>();
                                for (DataSnapshot userRes: dataSnapshot.getChildren()) {
                                    String userId = userRes.getKey();
                                    User user = userRes.getValue(User.class);
                                    user.setId(userId);

                                    if (friendsIdList.contains(userId)){
                                        float [] dist = new float[1];
                                        Location.distanceBetween(current.getLatitude(),current.getLongitude(),user.getLat(),user.getLng(),dist);
                                        boolean isNear = dist[0] < 1000;
                                        if(isNear && !friendsNear.contains(user.getId())){
                                            Utils.sendPush(user, name + " "+ getResources().getString(R.string.nearby), getResources());
                                        }
                                        if (isNear)
                                            newNearFriends.add(userId);
                                    }
                                }
                                mDatabase.child(Utils.NEAR).child(currentID).setValue(StringUtils.join(newNearFriends, ';'));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.err.println("The read failed: " + databaseError.getMessage());
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("The read failed: " + databaseError.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("The read failed: " + databaseError.getMessage());
            }
        });

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
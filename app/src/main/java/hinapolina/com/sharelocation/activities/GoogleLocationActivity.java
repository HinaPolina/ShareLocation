package hinapolina.com.sharelocation.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import hinapolina.com.sharelocation.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.R.attr.start;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by hinaikhan on 10/12/17.
 */
@RuntimePermissions
public class GoogleLocationActivity extends AppCompatActivity {

    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    //use for user's Last Known Location
    private FusedLocationProviderClient mFusedLocationClient;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private long UPDATE_INTERVAL = 10 * 1000; //10sec
    private long FASTEST_INTERVAL = 2000; //2sec

    private final static String KEY_LOCATION = "location";
    private final static String TAG = GoogleLocationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        startLocationUpdates();
        getUsersLastKnowLocation();

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mSupportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

    }

    protected void loadMap(GoogleMap googleMap){

        mGoogleMap = googleMap;
        if (mGoogleMap != null){
            //Map is ready
            Toast.makeText(this, "Map is Loading " , Toast.LENGTH_LONG).show();
            GoogleLocationActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
//            GoogleLocationActivityPermissionsDispatcher.getMyLocationWithCheck(this);

        }else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        GoogleLocationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void startLocationUpdates() {

        //create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        //create LocationSettingRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        //check whether location settings r satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }


    

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * Called when the Activity is no longer visible.
	 */
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display the connection status
        if(mCurrentLocation != null){
            Toast.makeText(this, "GPS Found your location" , Toast.LENGTH_LONG).show();
            //create lat & lng objt for users with maps
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            mGoogleMap.animateCamera(cameraUpdate);
        }else{
            Toast.makeText(this, "GPS not able to find your location " , Toast.LENGTH_LONG).show();
        }
        GoogleLocationActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
    }

    public void onLocationChanged(Location location) {
        //new location
        String updateNewLocation = "Your New Location Update " +
                Double.toString(location.getLatitude()) + ", " +
                Double.toString(location.getLongitude());

        Toast.makeText(this, updateNewLocation, Toast.LENGTH_LONG).show();


    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    //get user's last know location
    private void getUsersLastKnowLocation() {

        mFusedLocationClient = getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    onLocationChanged(location);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("GoogleLocationActivity" , "Trying to get last GPS location");
                e.printStackTrace();
            }
        });

        }
    }















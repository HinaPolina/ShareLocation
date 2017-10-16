package hinapolina.com.sharelocation.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import hinapolina.com.sharelocation.Application;
import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.Utils;
import hinapolina.com.sharelocation.listener.UserUpdateListener;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.retrofit.FirebaseHelper;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


/**
 * Created by hinaikhan on 10/12/17.
 */
@RuntimePermissions
public class GoogleLocationFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener ,UserUpdateListener {

    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    public User mUser;
    private Context mContext;
    String currentUserId;

    GoogleApiClient mGoogleApiClient;
    List<Marker> mMarkers;
    private FirebaseAuth mAuth;

    FirebaseHelper fbHelper;
    SharedPreferences sharedPref;
    private DatabaseReference mDatabase;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private final static String KEY_LOCATION = "location";
    private final static String TAG = GoogleLocationFragment.class.getSimpleName();

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mContext = container.getContext();
        mDatabase = Application.getmDatabase();
        sharedPref = mContext.getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPref.getString(Utils.USER_ID, "") ;
        fbHelper = new FirebaseHelper(this);

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);

        mUser = new User();

        mMarkers = new ArrayList<Marker>();
        getPermissionToReadUserContacts();


        return view;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }


    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        if (mMarkers != null) {
            //Remove existing markers
            for (Marker marker : mMarkers) {
                marker.remove();
            }

            //Reset markers list
            mMarkers.clear();
        }

        double latitiude = 0;
        double longitude = 0;

        //Add current user location marker on map
        mMarkers.add(addMarker(location.getLatitude(), location.getLongitude()));

        //Display friends locations as markers on map
        fbHelper.getUsersFromFirebaseByID(currentUserId);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 8));

        sendLocationToServer(location);
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(mContext)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private Marker addMarker(double latitude, double longitude) {
        Log.d(TAG, "Latitude: " + latitude + ", longitude: " + longitude);
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        return mGoogleMap.addMarker(markerOptions);
    }


    public void getPermissionToReadUserContacts() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {


        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(mContext, "permission denied", Toast.LENGTH_LONG).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // send new Location to the server
    private void sendLocationToServer(Location location){
        sharedPref = mContext.getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        mDatabase.child("users").child(sharedPref.getString(Utils.USER_ID, "")).child("lat").setValue(location.getLatitude());
        mDatabase.child("users").child(sharedPref.getString(Utils.USER_ID, "")).child("lng").setValue(location.getLongitude());
        User user = new User();
        user.setName(sharedPref.getString(Utils.USER_NAME, ""));
        user.setEmail(sharedPref.getString(Utils.EMAIL, ""));
        user.setImageURI(sharedPref.getString(Utils.IMAGE, ""));
        user.setLat(location.getLatitude());
        user.setLng(location.getLongitude());
        user.setBattery((int) Utils.getBatteryLevel(mContext));
        mDatabase.child("users").child(currentUserId).setValue(user);
    }

    public static Bitmap getProfilePicture(String image){
        Bitmap bitmap = null;
        Bitmap newBitmap = null;
        try {
        URL imageURL = new URL(image);
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

            float ratio = Math.min(
                    (float) 80 / bitmap.getWidth(),
                    (float) 80 / bitmap.getHeight());
            int width = Math.round((float) ratio * bitmap.getWidth());
            int height = Math.round((float) ratio * bitmap.getHeight());

            newBitmap = Bitmap.createScaledBitmap(bitmap, width,
                    height, false);


        return newBitmap;
    }

    @Override
    public void updateUserMarker(User user) {
        addMarker(user.getLat(), user.getLng());
    }
}








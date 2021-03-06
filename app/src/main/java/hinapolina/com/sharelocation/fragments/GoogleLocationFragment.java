package hinapolina.com.sharelocation.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.TalkListener;
import hinapolina.com.sharelocation.activities.HomeActivity;
import hinapolina.com.sharelocation.activities.LoginActivity;
import hinapolina.com.sharelocation.adapters.MarkerAdapter;
import hinapolina.com.sharelocation.adapters.UsersRecyclerViewAdapter;
import hinapolina.com.sharelocation.listener.UserUpdateListener;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.FirebaseHelper;
import hinapolina.com.sharelocation.ui.Application;
import hinapolina.com.sharelocation.ui.Utils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static hinapolina.com.sharelocation.R.id.map;


/**
 * Created by hinaikhan on 10/12/17.
 */
@RuntimePermissions
public class GoogleLocationFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener ,UserUpdateListener, TalkListener {

    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private BottomSheetBehavior mBottomSheetBehavior;

    private Context mContext;
    String currentUserId;
    GoogleApiClient mGoogleApiClient;
    Marker myUser;

    FirebaseHelper fbHelper;
    SharedPreferences sharedPref;
    private DatabaseReference mDatabase;
    User user = new User();
    private List<User> mUsers = new ArrayList<>();
    private LoginActivity loginActivity;
    private Set<Integer> currentUnCheckedItems;

    private UsersRecyclerViewAdapter mUsersRecyclerView;

    private LinearLayoutManager layoutManager;
    private RecyclerView mRecyclerView;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private final static String KEY_LOCATION = "location";
    private final static String TAG = GoogleLocationFragment.class.getSimpleName();
   Map <String, Marker> markers = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mContext = container.getContext();
        mUsersRecyclerView =
                new UsersRecyclerViewAdapter(mContext, this, mUsers);
        layoutManager = new LinearLayoutManager(mContext,  LinearLayoutManager.VERTICAL, false);
        mDatabase = Application.getmDatabase();
        sharedPref = mContext.getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPref.getString(Utils.USER_ID, "") ;
        fbHelper = new FirebaseHelper(this);
        loginActivity = new LoginActivity();

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);

        }

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(map);
        mSupportMapFragment.getMapAsync(this);

        getPermissionToReadUserContacts();

        mRecyclerView = (RecyclerView)  view.findViewById(R.id.profile_details_recycler) ;
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        Log.d(TAG, "initiating recycler");
        mUsersRecyclerView = new UsersRecyclerViewAdapter(mContext, this, mUsers);
        mUsersRecyclerView.setFragmentManager(getFragmentManager());
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mUsersRecyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mGoogleApiClient!=null) mSupportMapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setInfoWindowAdapter(new MarkerAdapter(getLayoutInflater()));

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
        LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 8));

            }
        });
        //Display friends locations as markers on map
        fbHelper.getUsersFromFirebaseByID(currentUserId);
//
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUnCheckedItems = new HashSet<Integer>();
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null&& mGoogleApiClient.isConnected()) {
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
        if (myUser != null)
            myUser.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        //Add current user location marker on map
        //sendLocationToServer(location);
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

    private Marker addMarker(final User user) {


        if(markers.containsKey(user.getId())) {
          markers.get(user.getId()).remove();
            markers.remove(user.getId());

           Marker m = creatMarker(user);

           markers.put(user.getId(), m);
            return m;
        }  else {

           Marker marker = creatMarker(user);
            markers.put(user.getId(),marker);
            return marker;

        }
    }

    private Marker creatMarker(final User user) {
        final MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(user.getLat(), user.getLng());
        String date = "";
        if(user.getDate()!=null) date = Utils.getLastUpdate(user.getDate());
        Log.d(TAG, "Latitude: " + user.getLat()+ ", longitude: " + user.getLng());
        markerOptions.position(latLng);
        markerOptions.title(user.getName());
        markerOptions.snippet("Battery level: " + user.getBattery() + "\n" + date);
        final Marker m= mGoogleMap.addMarker(markerOptions);
        Picasso.with(getContext())
                .load(user.getImageURI().replaceAll("large", "small"))
                .centerCrop()
                .resize(80, 80)
                .transform(new RoundTransformation())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        System.err.println("BITMAP: "+ bitmap);
                        m.remove();
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        Marker m = mGoogleMap.addMarker(markerOptions);
                        markers.put(user.getId(), m);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        System.err.println("BITMAP Failed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        return m;
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


    @Override
    public void updateUserMarker(User user) {
        Marker marker = addMarker(user);
        if (TextUtils.equals(user.getId(), currentUserId)) {
            myUser = marker;
        }
    }

    static public class RoundTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    public void addUserToAdapter(User user) {
        boolean isInList = false;
      for(int i = 0; i<mUsers.size(); i++){
          if(mUsers.get(i).getId().equals(user.getId())){
              mUsers.remove(i);
              mUsers.add(i, user);
              isInList = true;
              return;
          }
      }
      if(!isInList) mUsers.add(user);
        mUsersRecyclerView.notifyDataSetChanged();
    }



    @Override
    public void addUsersToAdapter(List<User> users) {

    }

    public void callUser(String userId) {
        FragmentActivity activity = getActivity();
        if (activity instanceof HomeActivity) {
            ((HomeActivity)activity).callUser(userId);
        }
    }

}








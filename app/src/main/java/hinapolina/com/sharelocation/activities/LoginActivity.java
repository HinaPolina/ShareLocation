package hinapolina.com.sharelocation.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.retrofit.FirebaseHelper;
import hinapolina.com.sharelocation.ui.Application;
import hinapolina.com.sharelocation.ui.DataHolder;
import hinapolina.com.sharelocation.ui.Utils;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 111;
    private static final int RC_FB_SIGN_IN = 64206;

    private LoginButton loginButton;
    CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private SignInButton signInButton;
    private DatabaseReference mDatabase;
    private final static int MY_PERMISSIONS_REQUEST_LOCATION = 1001;
    FirebaseHelper firebaseHelper;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    Location location;


    @Override
    public void onStart() {
        super.onStart();


        firebaseHelper = new FirebaseHelper(null);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            gotoHome();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCurrentLocation();
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = Application.getmDatabase();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        googleSignIn();
        facebookSingIn();

    }

    private void googleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);
    }

    private void facebookSingIn() {
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_friends"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FB Login Result", loginResult.toString());

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());
                                Log.v("Friends list response", response.toString());
                                // Application code
                                try {
                                    String email = object.getString("email");
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    User user = new User();
                                    String userToken = FirebaseInstanceId.getInstance().getToken();
                                    user.setToken(userToken);
                                    user.setName(name);
                                    user.setImageURI("https://graph.facebook.com/" + id + "/picture?type=small");
                                    user.setEmail(email);
                                    JSONObject object_friends = object.optJSONObject("friends");
                                    JSONArray array = object_friends.getJSONArray("data");
                                    // saved your friends from facebook to local DB
                                    firebaseHelper.saveFriendsToBD(array, id);
                                    // Save current user to Firebase
                                    DataHolder.getInstance().put("userId", id);
                                    saveUserToServer(id, user);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,friends ");
                request.setParameters(parameters);
                request.executeAsync();
                System.err.println("Starting load fiends:");
                GraphRequest.newMyFriendsRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray array, GraphResponse response) {
                            }
                        }).executeAsync();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("", "Cancelled Facebook login");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("", "Error logging in via facebook");
            }
        });

    }




    private void saveUserToServer(String id, User user) {
            if (location != null) {
            user.setLat(location.getLatitude());
            user.setLng(location.getLongitude());
        }
        int battery = (int)Utils.getBatteryLevel(this);
        user.setBattery(battery);
        user.setId(id);
        mDatabase.child("users").child(id).setValue(user);
        SharedPreferences sharedPref = getSharedPreferences(Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Utils.USER_ID, id);
        editor.putString(Utils.USER_NAME, user.getName());
        editor.putString(Utils.EMAIL, user.getEmail());
        editor.putString(Utils.IMAGE, user.getImageURI());
        editor.putString(Utils.TOKEN, user.getToken());
        editor.apply();
        gotoHome();

    }



    private void getCurrentLocation() {
        checkLocationPermission();
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            System.err.println("Location " + location.getLatitude() + " " +location.getLongitude());
                            setLocation(location);
                        }
                    }
                });

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(LoginActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }



    // authentication with firebase
    private void handleFacebookAccessToken(AccessToken accessToken) {
        System.err.println("handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.err.println("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();



                        } else {
                            // If sign in fails, display a message to the user.
                            System.err.println("signInWithCredential:failure" + task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }



    private void handleSignInwithGoogleResult(GoogleSignInResult result) {
        Log.d("log", "handleSignInwithGoogleResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //create new user
            User user = new User();
            String userToken = FirebaseInstanceId.getInstance().getToken();
            user.setToken(userToken);
            user.setImageURI(acct.getPhotoUrl().toString());
            user.setEmail(acct.getEmail());
            user.setName(acct.getDisplayName());
            // save user in DB
            saveUserToServer(acct.getId(), user);
            // authentication with firebase
            firebaseAuthWithGoogle(acct);
        } else {

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        System.err.println("firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.err.println("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            System.err.println("signInWithCredential:failure"+task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("log", "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInwithGoogleResult(result);
        } else if (requestCode == RC_FB_SIGN_IN) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            throw new Error("Unsupported Login Type. Request Code - " + requestCode);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

        }
    }


    private void gotoHome(){
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }
}

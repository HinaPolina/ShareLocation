package hinapolina.com.sharelocation.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.Utils;
import hinapolina.com.sharelocation.fragments.BatteryFragment;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.ui.DataHolder;

import static hinapolina.com.sharelocation.Utils.REQUEST_CODE;

/**
 * Created by hinaikhan on 10/14/17.
 */

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    public int navItemIndex = 0;
    public static final String TAG_BATTERY = "battery";
    private static final String CLOSE_BUTTON = "mCloseButton";
    public static final String TAG_GOOGLE_MAP = "map";
    public static String CURRENT_TAG = TAG_GOOGLE_MAP;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;

    //user's profile
    private ImageView imgUserProfile;
    private TextView tvUserName;
    public User mUser;
    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        toolbar.setTitle("Share Location");
        mAuth = FirebaseAuth.getInstance();
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            bundle = new Bundle();
        }


        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        setUpNav();

        loadHomeFragment();
        setUserProfileData();
    }

    private void initUI(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerLayout = navigationView.getHeaderView(0);
        tvUserName =(TextView) headerLayout.findViewById(R.id.tv_profile_details_name);
        imgUserProfile = (ImageView) headerLayout.findViewById(R.id.img_profile_details);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSubmitButtonEnabled(true);
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        searchView.requestFocusFromTouch();
        searchView.setFocusable(true);
        hideCloseButton(searchView);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(android.R.drawable.ic_menu_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(HomeActivity.this, SearchResult.class);
                intent.putExtra(Utils.USER_NAME, query);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return true;
    }

    private void hideCloseButton(SearchView mSearchView) {
        try {
            Field searchField = SearchView.class.getDeclaredField(CLOSE_BUTTON);
            searchField.setAccessible(true);
            ImageView mSearchCloseButton = (ImageView) searchField.get(mSearchView);
            if (mSearchCloseButton != null) {
                mSearchCloseButton.setEnabled(false);
                mSearchCloseButton.setImageDrawable(getResources().getDrawable(android.R.color.transparent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
           // todo update new friends list
        }

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_battery) {

        } else if (id == R.id.nav_log_out) {

        } else if (id == R.id.nav_sharing_location) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//     if user select the current navigation menu again, don't do anything
//     just close the navigation drawer
     private void loadHomeFragment(){
        drawer.closeDrawers();
        Fragment fragment = getFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }



    private Fragment getFragment() {
        switch (navItemIndex) {

            case 0:
                GoogleLocationFragment locationFragment = new GoogleLocationFragment();
                return locationFragment;

            case 1:
                BatteryFragment batteryFragment = new BatteryFragment();
                return batteryFragment;

            default:
                return new GoogleLocationFragment();


        }
    }

    private void setUpNav() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_sharing_location: {
                        navItemIndex = 0;
                        loadHomeFragment();
                        break;
                    }
                    case R.id.nav_log_out: {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Do you want to quit the app?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DataHolder.getInstance().clear();
                                        FirebaseAuth.getInstance().signOut();
                                        LoginManager.getInstance().logOut();
                                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        HomeActivity.this.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.create().show();
                        break;
                    }
                }

                drawer.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    private void setUserProfileData() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            tvUserName.setText(currentUser.getDisplayName());
            Picasso.with(this).load(currentUser.getPhotoUrl()).into(imgUserProfile);
        }

            }

}


package hinapolina.com.sharelocation.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import hinapolina.com.sharelocation.R;
import static hinapolina.com.sharelocation.R.id.linear_rider_login;


public class UberLoginActivity extends AppCompatActivity {

    private LinearLayout mLinearRiderSignIn;
    private LinearLayout mLinearDriverSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setTitle("Uber Login");
        initUI();
        onClickItems();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI(){
        mLinearRiderSignIn = (LinearLayout) findViewById(linear_rider_login);
        mLinearDriverSignIn = (LinearLayout) findViewById(R.id.linear_driver_login);
    }

    private void onClickItems(){
        mLinearRiderSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://auth.uber.com/login/?next_url=https%3A%2F%2Friders.uber.com%2F&state=1Tr08Eva0CK1NPQKQjXjG1QNlPH1xTP_9Tvn7w5xo_k%3D");
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setToolbarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLight));
                intentBuilder.setExitAnimations(getBaseContext(), android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                CustomTabsIntent customTabsIntent = intentBuilder.build();
                customTabsIntent.launchUrl(getBaseContext(), uri);


            }
        });

        mLinearDriverSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://auth.uber.com/login/?next_url=https%3A%2F%2Fpartners.uber.com");
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                intentBuilder.setToolbarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLight));
                intentBuilder.setExitAnimations(getBaseContext(), android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                CustomTabsIntent customTabsIntent = intentBuilder.build();
                customTabsIntent.launchUrl(getBaseContext(), uri);

            }
        });
    }

}

package hinapolina.com.sharelocation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.crash.FirebaseCrash;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    }
}

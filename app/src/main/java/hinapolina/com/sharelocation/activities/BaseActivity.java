package hinapolina.com.sharelocation.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;

import hinapolina.com.sharelocation.SinchService;
import hinapolina.com.sharelocation.services.CallingService;

/**
 * Created by hinaikhan on 10/29/17.
 */

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection, SinchService.StartFailedListener  {

    private SinchService.SinchServiceInterface mSinchServiceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);
        //Log.d("", "");
        //getApplicationContext().bindService(new Intent(this, CallingService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
        getSinchServiceInterface().setStartListener(this);
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    protected void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
        startActivity(mainActivity);
    }



}
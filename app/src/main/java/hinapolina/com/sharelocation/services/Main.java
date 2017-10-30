package hinapolina.com.sharelocation.services;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

public abstract class Main extends AppCompatActivity implements ServiceConnection {

    private CallingService.CallingFunctions callingFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().bindService(new Intent(this, CallingService.class), this,
                BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (CallingService.class.getName().equals(componentName.getClassName())) {
            callingFunctions = (CallingService.CallingFunctions) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (CallingService.class.getName().equals(componentName.getClassName())) {
            callingFunctions = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected CallingService.CallingFunctions getCallingFunctions() {
        return callingFunctions;
    }



}

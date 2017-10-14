package hinapolina.com.sharelocation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by polina on 10/12/17.
 */

public class Utils {

    public static final String USER_ID =  "user_id";
    public static final String USER_NAME = "user_name";

    public static float getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);


        System.err.println("battery leval" +level);
        return ((float)level / (float)scale) * 100.0f;
    }



}

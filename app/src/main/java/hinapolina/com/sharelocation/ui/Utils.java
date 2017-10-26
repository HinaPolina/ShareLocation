package hinapolina.com.sharelocation.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.text.format.DateUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by polina on 10/12/17.
 */

public class Utils{

    private static final String TAG = Utils.class.getSimpleName();

    public static final String USER_ID =  "user_id";
    public static final String USER_NAME = "user_name";
    public static final String MY_PREFS_NAME = "pref" ;
    public static final String EMAIL = "email" ;
    public static final String IMAGE = "image";
    public static final int REQUEST_CODE = 123;
    public static final String TOKEN = "token" ;
    public static final String UPDATE = "send";
    public static final String LAT = "lat" ;
    public static final String LNG = "lng";
    public static final String BATTERY =  "bat";
    public static final String FRIEND_NEARBY = "near" ;
    public static final String USER =  "user";

    public static float getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);


        System.err.println("battery leval" +level);
        return ((float)level / (float)scale) * 100.0f;
    }

    public static BatteryStatus getBatteryStatus(Context context) {
        BatteryStatus batteryStatus = new BatteryStatus();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = context.registerReceiver(null, ifilter);

        // Are we charging / charged?
        int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // How are we charging?
        int chargePlug = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        batteryStatus.setCharging(isCharging);
        batteryStatus.setAcCharge(acCharge);
        batteryStatus.setUsbCharge(usbCharge);

        return batteryStatus;
    }



    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return exitValue == 0;
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getLastUpdate(String date) {

            String twitterFormat = "MMM d, h:mm a";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            String relativeDate = "";
            try {
                long dateMillis = sf.parse(date).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.err.println("Time " + relativeDate);
            return relativeDate;

    }

    public static String printableDate(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
        return sdf.format(timeInMillis);
    }

    public static String printableTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        return sdf.format(timeInMillis);
    }

    public static String printableDateTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d 'at' h:mm a");
        return sdf.format(timeInMillis);
    }
}

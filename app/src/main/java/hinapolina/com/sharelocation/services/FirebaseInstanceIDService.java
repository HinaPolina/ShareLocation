package hinapolina.com.sharelocation.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import hinapolina.com.sharelocation.ui.Utils;

/**
 * Created by polina on 10/17/17.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.err.println("GENERATED TOKEN: " + refreshedToken);
        SharedPreferences sharedPref = getSharedPreferences(Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Utils.TOKEN, refreshedToken);
        editor.commit();
    }
}

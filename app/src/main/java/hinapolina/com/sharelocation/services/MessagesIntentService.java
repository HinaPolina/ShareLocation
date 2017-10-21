package hinapolina.com.sharelocation.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by hinaikhan on 10/19/17.
 */

public class MessagesIntentService extends IntentService {

    private static final String TAG = MessagesIntentService.class.getSimpleName();

    public MessagesIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        FirebaseInstanceIDService instanceIDService = new FirebaseInstanceIDService();
        

    }
}

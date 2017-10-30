package hinapolina.com.sharelocation.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.activities.HomeActivity;
import hinapolina.com.sharelocation.listener.UserUpdateListener;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.FirebaseHelper;
import hinapolina.com.sharelocation.ui.Utils;

import static hinapolina.com.sharelocation.R.id.userName;


public class CallingService extends Service implements UserUpdateListener {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseHelper firebaseHelper;
    private List<User> users;

    private static final String APP_KEY = "f0e983e8-ecc8-4de7-aff2-7101417ecdc5";
    private static final String APP_SECRET = "bEpWHW07tkqi8/D0rURPRw==";
    private static final String ENVIRONMENT = "sclientapi.sinch.com";
    public static final String CALL_ID = "CALL_ID";

    SinchClient sinchClient;
    private StartFailedListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseHelper = new FirebaseHelper(this);
        final SharedPreferences sharedPreferences = getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        final String currentId =sharedPreferences.getString(Utils.USER_ID, "");
        firebaseHelper.findUserByName("", currentId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null)
                {
                    createSinchClient();
                }


            }
        };

        auth.addAuthStateListener(authStateListener);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void showNotification()
    {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean getNotificationValue = sharedPreferences.getBoolean("notification", true);
        if(getNotificationValue)
        {
            Intent intent = new Intent(this, HomeActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
            Notification notification = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("You have a new message").setSmallIcon(R.drawable.share_icon)
                    .setContentIntent(pIntent).getNotification();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationManager.notify(0, notification);
        }

        String getMessageToneValue = sharedPreferences.getString("mtone", "default");
        Uri uri = Uri.parse(getMessageToneValue);
        Ringtone messageTone = RingtoneManager.getRingtone(this, uri);
        messageTone.play();
    }


    private SinchClient createSinchClient() {
        android.content.Context context = this.getApplicationContext();
        SinchClient sinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .userId("<user id>")
                .build();

        sinchClient.setSupportManagedPush(true);
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.setSupportActiveConnectionInBackground(true);

        sinchClient.addSinchClientListener(new SinchClientListener() {
            @Override
            public void onClientStarted(SinchClient sinchClient) {

            }

            @Override
            public void onClientStopped(SinchClient sinchClient) {

            }

            @Override
            public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {

            }

            @Override
            public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {

            }

            @Override
            public void onLogMessage(int i, String s, String s1) {

            }
        });

        sinchClient.start();

        return sinchClient;
    }



    private void stop() {
        sinchClient.stopListeningOnActiveConnection();
        sinchClient.terminate();
    }

    public void stopClient() {
        stop();
    }

    public AudioController audioController ()
    {
        return sinchClient.getAudioController();
    }

    public VideoController videoController ()
    {
        return sinchClient.getVideoController();
    }

    public Call getCall(String callId) {
        return sinchClient.getCallClient().getCall(callId);
    }




    @Override
    public void updateUserMarker(User user) {

    }

    @Override
    public void addUserToAdapter(User user) {

    }

    @Override
    public void addUsersToAdapter(List<User> users) {

    }

    private boolean isStarted() {
        return (sinchClient != null && sinchClient.isStarted());
    }

    public interface StartFailedListener {
        void onStartFailed(SinchError error);

        void onStarted();
    }


    public class CallingFunctions extends Binder {

        public Call makeVoiceCall(String userId) {
            if (sinchClient == null) {
                return null;
            }
            return sinchClient.getCallClient().callUser(userId);
        }

        public Call makeVideoCall(String userId)
        {
            if (sinchClient == null) {
                return null;
            }
            return sinchClient.getCallClient().callUserVideo(userId);
        }

        public void sendMessage(String recipientUserId, String textBody)
        {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            sinchClient.getMessageClient().send(message);
        }

        public boolean isStarted() {
            return CallingService.this.isStarted();
        }

        public void startClient() {
            createSinchClient();
        }

        public void stopClient() {
            stop();
        }

        public AudioController audioController ()
        {
            return sinchClient.getAudioController();
        }

        public VideoController videoController ()
        {
            return sinchClient.getVideoController();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public Call getCall(String callId) {
            return sinchClient.getCallClient().getCall(callId);
        }
    }
}
package hinapolina.com.sharelocation.activities.videotalk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import hinapolina.com.sharelocation.Manifest;
import hinapolina.com.sharelocation.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by hinaikhan on 10/20/17.
 */

public class VideoTalkActivity extends AppCompatActivity implements Session.SessionListener {

    private static String API_KEY = "45985662";
    private static String SESSION_ID = "1_MX40NTk4NTY2Mn5-MTUwODU1ODE3ODE3OH5XNEVCMElBaEFXN0Y5MUJtZHl0N1Z3cTR-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NTk4NTY2MiZzaWc9NjllM2VkMGRlY2IzZDUyNDc2MTNiNGM1YzE2ZDdkNzNiNjVhMjFmMDpzZXNzaW9uX2lkPTFfTVg0ME5UazROVFkyTW41LU1UVXdPRFUxT0RFM09ERTNPSDVYTkVWQ01FbEJhRUZYTjBZNU1VSnRaSGwwTjFaM2NUUi1mZyZjcmVhdGVfdGltZT0xNTA4NTU4MjAyJm5vbmNlPTAuMjQzMjA1NDMzNzExNDA5MzUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTUxMTE1MzgwMSZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";
    private static final String LOG_TAG = VideoTalkActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_talk);

        // initialize view objects from your layout
        mPublisherViewContainer = (FrameLayout)findViewById(R.id.publisher_container);
        mSubscriberViewContainer = (FrameLayout)findViewById(R.id.subscriber_container);


    }

    /* Activity lifecycle methods */

    @Override
    protected void onPause() {

        Log.d(LOG_TAG, "onPause");

        super.onPause();

        if (mSession != null) {
            mSession.onPause();
        }

    }

    @Override
    protected void onResume() {

        Log.d(LOG_TAG, "onResume");

        super.onResume();

        if (mSession != null) {
            mSession.onResume();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onConnected(Session session) {

    }

    @Override
    public void onDisconnected(Session session) {

    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }
}

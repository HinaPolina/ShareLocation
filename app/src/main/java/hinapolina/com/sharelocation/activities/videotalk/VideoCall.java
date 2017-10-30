package hinapolina.com.sharelocation.activities.videotalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.model.Message;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.services.Main;
import hinapolina.com.sharelocation.ui.Utils;

import static hinapolina.com.sharelocation.R.id.userName;
import static hinapolina.com.sharelocation.activities.message.ChatActivity.TO_USER;

/**
 * Created by hinaikhan on 10/29/17.
 */

public class VideoCall extends Main {


    ImageView receiverPic;
    ImageButton mic, speaker, endCall;
    TextView receiverName, callStatus;
    String userProfile,userName, userUID, callID, myCallID;

    boolean micClicked = false;
    boolean videoOfClicked = false;
    boolean speakerClicked = true;

    DatabaseReference removeData = FirebaseDatabase.getInstance().getReference();
    DatabaseReference addData = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String myID = user.getUid();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_talk);

        endCall = (ImageButton)findViewById(R.id.video_end_call);
        receiverPic = (ImageView)findViewById(R.id.video_receiver_pic);
        receiverName = (TextView)findViewById(R.id.video_receiver_name);
        callStatus = (TextView)findViewById(R.id.video_ringing);

        mic = (ImageButton)findViewById(R.id.video_mic_off);
        speaker = (ImageButton)findViewById(R.id.video_speaker);


        Message message = new Message();

        SharedPreferences sharedPref = getBaseContext().getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        //Set message attributes
        message.setReceiver(sharedPref.getString(Utils.USER_NAME, " "));

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted. Make Free Calls Now", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please Grant Permission To Make Free Calls.", Toast
                    .LENGTH_LONG).show();
        }
    }
}

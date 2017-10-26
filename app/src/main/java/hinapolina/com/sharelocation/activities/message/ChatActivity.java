package hinapolina.com.sharelocation.activities.message;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.Calendar;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.activities.HomeActivity;
import hinapolina.com.sharelocation.adapters.MessageRecyclerAdapter;
import hinapolina.com.sharelocation.fragments.SharePlacesDialog;
import hinapolina.com.sharelocation.listener.OnPlaceListener;
import hinapolina.com.sharelocation.model.Message;
import hinapolina.com.sharelocation.model.Place;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.services.FirebaseTopicNotificationService;
import hinapolina.com.sharelocation.ui.Application;
import hinapolina.com.sharelocation.ui.Utils;


/**
 * Created by hinaikhan on 10/22/17.
 */

public class ChatActivity extends AppCompatActivity  implements OnPlaceListener{

    public static final String TO_USER = "toUser";
    public static final String TO_USER_TOKEN = "toUserToken";

    private RecyclerView mRecyclerViewMessage;
    private MessageRecyclerAdapter mMessageAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebasetorageReference;
    private ChildEventListener mChildEventListener;

    private ProgressBar mProgressBar;
    private ImageButton imgPhotoButton;
    private TextView tvUserName;
    private EditText etMessage;
    private Button btnSendMessage;
    private Toolbar mToolbar;
    private LinearLayout mLinearLayoutBack;


    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int MESSAGE_LENGTH_LIMIT = 150;
    private static final int RC_PHOTO_PICKER = 2;

    private User  toUser;
    private String currentUserName;
    Boolean isGroup = false;
    private Location location;

    public static final String URI_STATIC = "https://maps.googleapis.com/maps/api/staticmap?center=";
    private String PARAMETERS = "&zoom=15&size=600x300&maptype=roadmap&markers=color%3Ared%7C";
    private String downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_recycler_view_adapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            toUser = (User)bundle.get(TO_USER);
        }

        mDatabaseReference = Application.getmDatabase().child(tableName());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location loc) {
                if (loc != null)
                    location = loc;
            }
        });

        initUI();

        //initialize fb references
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //mDatabaseReference = Application.getmDatabase().child("messages");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebasetorageReference = mFirebaseStorage.getReference().child("chat_photos");


        mMessageAdapter = new MessageRecyclerAdapter(this);
        mRecyclerViewMessage.setLayoutManager(new LinearLayoutManager(this,  LinearLayoutManager.VERTICAL, false));
        mRecyclerViewMessage.setAdapter(mMessageAdapter);

        tvUserName.setText(toUser.getName());

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        onClickListeners();
    }

    private void initUI() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPhotoButton = (ImageButton) findViewById(R.id.photoPickerButton);
        etMessage = (EditText) findViewById(R.id.messageEditText);
        btnSendMessage = (Button) findViewById(R.id.sendButton);
        mRecyclerViewMessage = (RecyclerView) findViewById(R.id.messageRecyclerView);
        tvUserName = (TextView) findViewById(R.id.tv_profile_name);
        mLinearLayoutBack = (LinearLayout) findViewById(R.id.back);


    }


    public void onSharePlace (View v){
        DialogFragment dialog = SharePlacesDialog.newInstance(location);
        dialog.show(getSupportFragmentManager(), getResources().getString(R.string.placeFragment));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    Message message = (Message) dataSnapshot.getValue(Message.class);
                    Log.d("ChatActivity", "Message: " + message.toString());
                    mMessageAdapter.addMessage(message, dataSnapshot.getKey());
                    //mMessageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }

    private void onClickListeners(){

        //enable send button when there's text to send
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.toString().trim().length() > 0){
                    btnSendMessage.setEnabled(true);
                }else{
                    btnSendMessage.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etMessage.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MESSAGE_LENGTH_LIMIT)});

        //send button send messgae & clears the Edit text
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(null, downloadUrl);
            }
        });

        //ImagePickerButton shows an image picker to upload a image for a message
        imgPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                intentPhoto.setType("image/jpeg");
                intentPhoto.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intentPhoto, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        mLinearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });



    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mFirebasetorageReference.child(selectedImageUri.getLastPathSegment());
            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            downloadUrl = taskSnapshot.getDownloadUrl().toString();

                         //   sendMessage(null, downloadUrl);
                        }
                    });
        }
    }

    private void sendMessage(Place place, String imgUrl) {
        Message message = new Message();

        SharedPreferences sharedPref = getBaseContext().getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        //Set message attributes
        message.setSender(sharedPref.getString(Utils.USER_NAME, " "));
        message.setMessage(etMessage.getText().toString());
        message.setUserProfileImg(sharedPref.getString(Utils.IMAGE, ""));
        message.setTimeInMillis(Calendar.getInstance().getTimeInMillis());

        if (!TextUtils.isEmpty(imgUrl)) {
            message.setImgUrl(imgUrl);
        }

        if(place!=null){

            message.setPlaceName(place.getName());
            message.setLat(place.getLat());
            message.setLng(place.getLng());
            message.setMessage(getString(R.string.share) +" " +place.getName());
            message.setImgUrl(place.getUrl());
        }



        //Push message to firebase
        mDatabaseReference.push().setValue(message);

        //clear the input box
        etMessage.setText(" ");

        //Send push notification to all users
        FirebaseTopicNotificationService service = new FirebaseTopicNotificationService();
        service.notifyAllUsers(ChatActivity.this, Arrays.asList(toUser.getToken()), message.getMessage());
    }

    private String tableName() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("privatemsg_");

        SharedPreferences sharedPref = getSharedPreferences(Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        String fromUserId = sharedPref.getString(Utils.USER_ID, "") ;

        if (fromUserId.compareTo(toUser.getId()) > 0) {
            sbuf.append(toUser.getId()).append("-").append(fromUserId);
        } else {
            sbuf.append(fromUserId).append("-").append(toUser.getId());
        }

        return sbuf.toString();
    }

    @Override
    public void onPlace(final Place place) {
        System.err.println("SELECTED PLACE: " + place);
        String uri = URI_STATIC + place.getLat() +"," +place.getLng()+
                PARAMETERS + place.getLat()+"%2C"+ place.getLng()+"&key="
                +getResources().getString(R.string.google_maps_static_api_key);
        place.setUrl(uri);
        sendMessage(place, "");

    }
}

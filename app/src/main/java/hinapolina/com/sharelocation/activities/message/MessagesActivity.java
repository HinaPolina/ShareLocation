package hinapolina.com.sharelocation.activities.message;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.adapters.GroupChatAdapter;
import hinapolina.com.sharelocation.fragments.SharePlacesDialog;
import hinapolina.com.sharelocation.listener.OnPlaceListener;
import hinapolina.com.sharelocation.listener.UserUpdateListener;
import hinapolina.com.sharelocation.model.Message;
import hinapolina.com.sharelocation.model.Place;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.FirebaseHelper;
import hinapolina.com.sharelocation.services.FirebaseTopicNotificationService;
import hinapolina.com.sharelocation.ui.Application;
import hinapolina.com.sharelocation.ui.Utils;
import hinapolina.com.sharelocation.utils.ImageUtils;

public class MessagesActivity extends AppCompatActivity implements UserUpdateListener, OnPlaceListener {

    private static final String TAG = MessagesActivity.class.getSimpleName();
    private static final int MESSAGE_LENGTH_LIMIT = 150;
    private static final int RC_PHOTO_PICKER = 2;

    private RecyclerView mRecyclerViewMessage;
    private ImageButton imgPhotoButton;
    private EditText etMessage;
    private ImageButton btnSendMessage;
    private GroupChatAdapter mMessageAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebasetorageReference;
    private ChildEventListener mChildEventListener;
    private String mUserName;
    private Toolbar mToolbar;
    public ImageView backIcon;
    Boolean isGroup = true;
    String downloadUrl;
    public static final String URI_STATIC = "https://maps.googleapis.com/maps/api/staticmap?center=";

    private FirebaseHelper firebaseHelper;
    private List<User> users;

    Location location;
    private String PARAMETERS = "&zoom=15&size=600x300&maptype=roadmap&markers=color%3Ared%7C";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        initView();

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
        mToolbar.setTitle("My Messages");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize fb references
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = Application.getmDatabase().child("messages");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebasetorageReference = mFirebaseStorage.getReference().child("chat_photos");

        //adapter
        mRecyclerViewMessage.setLayoutManager(new LinearLayoutManager(this,  LinearLayoutManager.VERTICAL, false));
        mMessageAdapter = new GroupChatAdapter(this);
        mRecyclerViewMessage.setAdapter(mMessageAdapter);

        onClickListeners();

        firebaseHelper = new FirebaseHelper(this);
        final SharedPreferences sharedPreferences = getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        final String currentId =sharedPreferences.getString(Utils.USER_ID, "");
        firebaseHelper.findUserByName("", currentId);

    }

    private void initView(){
        // Initialize references to views
        mRecyclerViewMessage = (RecyclerView) findViewById(R.id.messageRecyclerView);
        imgPhotoButton = (ImageButton) findViewById(R.id.photoPickerButton);
        etMessage = (EditText) findViewById(R.id.messageEditText);
        btnSendMessage = (ImageButton) findViewById(R.id.sendButton);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    public void onSharePlace (View v){
        DialogFragment dialog = SharePlacesDialog.newInstance(location);
        dialog.show(getSupportFragmentManager(), getResources().getString(R.string.placeFragment));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
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
                if (dataSnapshot != null){
                    Message message = (Message) dataSnapshot.getValue(Message.class);
                    if (!TextUtils.isEmpty(message.getMessage())) {
                        mMessageAdapter.addMessage(message, dataSnapshot.getKey());
                        mMessageAdapter.notifyDataSetChanged();
                        mRecyclerViewMessage.scrollToPosition(mMessageAdapter.getItemCount()-1);

                    }
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
              sendMessage(null, downloadUrl );
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


    }

    private void sendMessage(Place place, String imgUrl ) {
        Message message = new Message();

        SharedPreferences sharedPref = getBaseContext().getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);

        //Set message attributes
        message.setSender(sharedPref.getString(Utils.USER_NAME, " "));
        message.setMessage(etMessage.getText().toString());
        message.setUserProfileImg(sharedPref.getString(Utils.IMAGE, ""));
        message.setTimeInMillis(Calendar.getInstance().getTimeInMillis());
        message.setRead(false);
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

        notifyAllUsers(message.getMessage());

        //clear the input box
        etMessage.setText("");
        imgPhotoButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_attach_image_grey));


    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            final Uri selectedImageUri = data.getData();
            // Get a reference to store file at chat_photos/<FILENAME>
            System.err.println("URI: " + selectedImageUri);
            new AsyncTask<Void, Void, Uri>(){
            @Override
            protected Uri doInBackground(Void... params) {
                return ImageUtils.decodeFile(MessagesActivity.this, selectedImageUri, 500, 500);
            }

            @Override
            protected void onPostExecute(Uri scaledImageUri) {
                final StorageReference photoRef = mFirebasetorageReference.child(scaledImageUri.getLastPathSegment());
                // Upload file to Firebase Storage
                photoRef.putFile(scaledImageUri)
                    .addOnSuccessListener(MessagesActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            downloadUrl = taskSnapshot.getDownloadUrl().toString();
                            System.err.println("Download URL: " + downloadUrl);
                            imgPhotoButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_vector_attach_pressed));
                        }
                    });
            }
        }.execute();
        }
    }


    public void isPermissionGrantedImage()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

        }
        else
        {
            sendImageAttachment();
        }
    }

    public void sendImageAttachment()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);

    }

    @Override
    public void updateUserMarker(User user) {

    }

    @Override
    public void addUserToAdapter(User user) {

    }

    @Override
    public void addUsersToAdapter(List<User> users) {
       this.users = users;
    }

    private void notifyAllUsers(String message) {
        /**
         TODO - Firebase topic Notification Service isn't working
         FirebaseTopicNotificationService service = new FirebaseTopicNotificationService();
         service.execute(getResources().getString(R.string.servrt_id), Constant.GROUP_TOPIC_NAME, "Push Notification test");
         */
        List<String> registrationIds = new ArrayList<String>();
        if (users != null && !users.isEmpty()) {
            //Construct list of registration ids
            for (User user : users) {
                registrationIds.add(user.getToken());
            }

            //Send push notification to all users
            FirebaseTopicNotificationService service = new FirebaseTopicNotificationService();
            service.notifyAllUsers(MessagesActivity.this, registrationIds, message);
        }
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








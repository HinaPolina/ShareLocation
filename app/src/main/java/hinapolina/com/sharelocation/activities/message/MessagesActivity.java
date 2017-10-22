package hinapolina.com.sharelocation.activities.message;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.adapters.MessageAdapter;
import hinapolina.com.sharelocation.common.Constant;
import hinapolina.com.sharelocation.listener.UserUpdateListener;
import hinapolina.com.sharelocation.model.Message;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.FirebaseHelper;
import hinapolina.com.sharelocation.services.FirebaseTopicNotificationService;
import hinapolina.com.sharelocation.ui.Application;
import hinapolina.com.sharelocation.ui.Utils;

import static hinapolina.com.sharelocation.R.drawable.user;

public class MessagesActivity extends AppCompatActivity implements UserUpdateListener {

    private static final String TAG = MessagesActivity.class.getSimpleName();
    private static final int MESSAGE_LENGTH_LIMIT = 150;
    private static final int RC_PHOTO_PICKER =  2;


    private ListView mlvMessage;
    private ProgressBar mProgressBar;
    private ImageButton imgPhotoButton;
    private EditText etMessage;
    private Button btnSendMessage;
    private MessageAdapter mMessageAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebasetorageReference;
    private ChildEventListener mChildEventListener;
    private String mUserName;
    private Toolbar mToolbar;
    public ImageView backIcon;

    private FirebaseHelper firebaseHelper;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        initView();


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
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, null);
        mlvMessage.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        onClickListeners();

        firebaseHelper = new FirebaseHelper(this);
        final SharedPreferences sharedPreferences = getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        final String currentId =sharedPreferences.getString(Utils.USER_ID, "");
        firebaseHelper.findUserByName("", currentId);

    }

    private void initView(){
        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mlvMessage = (ListView) findViewById(R.id.messageListView);
        imgPhotoButton = (ImageButton) findViewById(R.id.photoPickerButton);
        etMessage = (EditText) findViewById(R.id.messageEditText);
        btnSendMessage = (Button) findViewById(R.id.sendButton);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
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
                        mMessageAdapter.addMessage(message);
                        mMessageAdapter.notifyDataSetChanged();
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
                Message message = new Message();

                SharedPreferences sharedPref = getBaseContext().getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);

                //Set message attributes
                message.setSender(sharedPref.getString(Utils.USER_NAME, " "));
                message.setMessage(etMessage.getText().toString());
                message.setUserProfileImg(sharedPref.getString(Utils.IMAGE, ""));
                message.setTimeInMillis(Calendar.getInstance().getTimeInMillis());

                //Push message to firebase
                mDatabaseReference.push().setValue(message);

                notifyAllUsers(message.getMessage());

                //clear the input box
                etMessage.setText(" ");
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


    @Override
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
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        // Set the download URL to the message box, so that the user can send it to the database

                                User user = new User();
                                mDatabaseReference.push().setValue(user, downloadUrl.toString());
                            }


                    });
                }
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
}








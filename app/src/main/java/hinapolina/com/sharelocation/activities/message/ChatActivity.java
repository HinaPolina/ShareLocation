package hinapolina.com.sharelocation.activities.message;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.adapters.ChatRecyclerViewAdapter;
import hinapolina.com.sharelocation.adapters.MessageAdapter;
import hinapolina.com.sharelocation.adapters.UsersRecyclerViewAdapter;
import hinapolina.com.sharelocation.common.Constant;
import hinapolina.com.sharelocation.model.Message;
import hinapolina.com.sharelocation.network.FirebaseHelper;
import hinapolina.com.sharelocation.services.FirebaseTopicNotificationService;
import hinapolina.com.sharelocation.ui.Application;
import hinapolina.com.sharelocation.ui.Utils;


/**
 * Created by hinaikhan on 10/22/17.
 */

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewChatItems;
    private ChatRecyclerViewAdapter mChatRecyclerViewAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebasetorageReference;
    private Context mContext;
    private List<Message> messages;
    private ChildEventListener mChildEventListener;

    private ProgressBar mProgressBar;
    private ImageButton imgPhotoButton;
    private EditText etMessage;
    private Button btnSendMessage;


    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int MESSAGE_LENGTH_LIMIT = 150;
    private static final int RC_PHOTO_PICKER = 2;

    private LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_recycler_view_adapter);
        initUI();

        //initialize fb references
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = Application.getmDatabase().child("messages");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebasetorageReference = mFirebaseStorage.getReference().child("chat_photos");


        //adapter
        mChatRecyclerViewAdapter = new ChatRecyclerViewAdapter(messages, mContext);
        mRecyclerViewChatItems.setAdapter(mChatRecyclerViewAdapter);
        new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        onClickListeners();


    }

    private void initUI() {

        mRecyclerViewChatItems = (RecyclerView) findViewById(R.id.rv_chat_items);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPhotoButton = (ImageButton) findViewById(R.id.photoPickerButton);
        etMessage = (EditText) findViewById(R.id.messageEditText);
        btnSendMessage = (Button) findViewById(R.id.sendButton);


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
                    if (!TextUtils.isEmpty(message.getMessage())) {
                        mChatRecyclerViewAdapter.addMessage(message);
                        mChatRecyclerViewAdapter.notifyDataSetChanged();
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

                //Subscribe user to topic
                //FirebaseMessaging.getInstance().subscribeToTopic("message");

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
}

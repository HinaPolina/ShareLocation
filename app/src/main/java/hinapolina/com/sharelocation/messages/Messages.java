package hinapolina.com.sharelocation.messages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import hinapolina.com.sharelocation.Application;
import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.adapters.MessageAdapter;
import hinapolina.com.sharelocation.model.User;

public class Messages extends AppCompatActivity {

    private static final String TAG = Messages.class.getSimpleName();
    private static final int MESSAGE_LENGTH_LIMIT = 500;

    private ListView mlvMessage;
    private ProgressBar mProgressBar;
    private ImageButton imgPhotoButton;
    private EditText etMessage;
    private Button btnSendMessage;
    private MessageAdapter mMessageAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private List<User> mUsers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        initView();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = Application.getmDatabase().child("messages");
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, mUsers);
        mlvMessage.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        onClickListeners();

    }

    private void initView(){
        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mlvMessage = (ListView) findViewById(R.id.messageListView);
        imgPhotoButton = (ImageButton) findViewById(R.id.photoPickerButton);
        etMessage = (EditText) findViewById(R.id.messageEditText);
        btnSendMessage = (Button) findViewById(R.id.sendButton);
    }

    private void onClickListeners(){

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Send messages on click


            }
        });

        //enable send button when there's text to send

    }
}

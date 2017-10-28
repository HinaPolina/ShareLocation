package hinapolina.com.sharelocation.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.activities.message.ChatActivity;
import hinapolina.com.sharelocation.adapters.SearchResultsAdapter;
import hinapolina.com.sharelocation.listener.MassageSenderListener;
import hinapolina.com.sharelocation.listener.UserUpdateListener;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.FirebaseHelper;
import hinapolina.com.sharelocation.ui.Utils;
import okhttp3.OkHttpClient;

public class SearchResult extends AppCompatActivity implements UserUpdateListener, MassageSenderListener{
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    OkHttpClient mClient = new OkHttpClient();
    FirebaseHelper firebaseHelper;
    private List<User> users = new ArrayList<>();
    private SearchResultsAdapter adapter;
    TextView result;
    private LinearLayout mLinearLayoutBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        result = (TextView) findViewById(R.id.result_text);
        firebaseHelper = new FirebaseHelper(this);
        final SharedPreferences sharedPreferences = getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        final String currentId =sharedPreferences.getString(Utils.USER_ID, "");
        if(getIntent().hasExtra(Utils.USER_NAME)) firebaseHelper.findUserByName(getIntent().getStringExtra(Utils.USER_NAME),currentId );
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rvUsersSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultsAdapter(users, this, currentId, firebaseHelper, sharedPreferences.getString(Utils.TOKEN, ""), sharedPreferences.getString(Utils.USER_NAME, ""));
        recyclerView.setAdapter(adapter);
        mLinearLayoutBack = (LinearLayout) findViewById(R.id.back);
        initOnClick();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void updateUserMarker(User user) {

    }

    @Override
    public void addUserToAdapter(User user) {

    }

    @Override
    public void addUsersToAdapter(List<User> users) {
        if(users!=null&&users.isEmpty()) {
            result.setVisibility(View.VISIBLE);
        }
        this.users.addAll(users);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onSendMassageListener(User user, String massage) {
        Utils.sendPush(user, massage, getResources());

    }

    private void initOnClick(){
        mLinearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResult.this, HomeActivity.class);
                startActivity(intent);
            }
        });


    }
}
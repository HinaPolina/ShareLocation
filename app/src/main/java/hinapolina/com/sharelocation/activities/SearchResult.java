package hinapolina.com.sharelocation.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.adapters.SearchResultsAdapter;
import hinapolina.com.sharelocation.listener.UserUpdateListener;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.retrofit.FirebaseHelper;
import hinapolina.com.sharelocation.ui.Utils;

import static android.R.attr.id;

public class SearchResult extends AppCompatActivity implements UserUpdateListener{
    FirebaseHelper firebaseHelper;
    private List<User> users = new ArrayList<>();
    private SearchResultsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        firebaseHelper = new FirebaseHelper(this);
        final SharedPreferences sharedPreferences = getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        final String currentId =sharedPreferences.getString(Utils.USER_ID, "");
        if(getIntent().hasExtra(Utils.USER_NAME)) firebaseHelper.findUserByName(getIntent().getStringExtra(Utils.USER_NAME),currentId );
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rvUsersSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultsAdapter(users, this, currentId, firebaseHelper, sharedPreferences.getString(Utils.TOKEN, ""), sharedPreferences.getString(Utils.USER_NAME, ""));
        recyclerView.setAdapter(adapter);

                System.err.println("token " + sharedPreferences.getString(Utils.TOKEN, ""));
                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(sharedPreferences.getString(Utils.TOKEN, "")+ "@gcm.googleapis.com")
                    .setMessageId(id+currentId)
                    .addData("my_message", "User "+ " //////// " + " add you to friends")
                    .build());

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
    public void addUserToAdapter(List<User> users) {
        this.users.addAll(users);
        adapter.notifyDataSetChanged();
    }
}

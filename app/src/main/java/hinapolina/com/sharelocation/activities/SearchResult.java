package hinapolina.com.sharelocation.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.Utils;
import hinapolina.com.sharelocation.adapters.SearchResultsAdapter;
import hinapolina.com.sharelocation.listener.UserUpdateListener;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.retrofit.FirebaseHelper;

public class SearchResult extends AppCompatActivity implements UserUpdateListener{
    FirebaseHelper firebaseHelper;
    private List<User> users = new ArrayList<>();
    private SearchResultsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        firebaseHelper = new FirebaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        String currentId =sharedPreferences.getString(Utils.USER_ID, "");
        if(getIntent().hasExtra(Utils.USER_NAME)) firebaseHelper.findUserByName(getIntent().getStringExtra(Utils.USER_NAME),currentId );
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rvUsersSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultsAdapter(users, this, currentId, firebaseHelper);
        recyclerView.setAdapter(adapter);
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

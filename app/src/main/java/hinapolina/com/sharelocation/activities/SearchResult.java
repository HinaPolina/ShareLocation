package hinapolina.com.sharelocation.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.adapters.SearchResultsAdapter;
import hinapolina.com.sharelocation.listener.MassageSenderListener;
import hinapolina.com.sharelocation.listener.UserUpdateListener;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.FirebaseHelper;
import hinapolina.com.sharelocation.ui.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchResult extends AppCompatActivity implements UserUpdateListener, MassageSenderListener{
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    OkHttpClient mClient = new OkHttpClient();


    FirebaseHelper firebaseHelper;
    private List<User> users = new ArrayList<>();
    private SearchResultsAdapter adapter;
    TextView result;


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
        if(users!=null&&users.isEmpty()) {
            result.setVisibility(View.VISIBLE);
        }
        this.users.addAll(users);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onSendMassageListener(String token, String massage) {
        JSONObject root = new JSONObject();
        JSONObject notification = new JSONObject();
//        notification.put("body", body);
        try {
            notification.put("title", massage);

            //        notification.put("icon", icon);

            JSONObject data = new JSONObject();
            data.put("message", "I am message");
            root.put("notification", notification);
            root.put("data", data);
            root.put("registration_ids", new JSONArray(Arrays.asList(token)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), root.toString());
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + getResources().getString(R.string.servrt_id))
                .addHeader("Content-Type", "application/json")
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("Ooops!! ");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.err.println("RESPONSE CODE: " + response.code());
                System.err.println("RESPONSE: " + response.body().string());
            }
        });

    }
}
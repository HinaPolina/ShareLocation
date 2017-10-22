package hinapolina.com.sharelocation.services;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.listener.MassageSenderListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static hinapolina.com.sharelocation.activities.SearchResult.FCM_MESSAGE_URL;

/**
 * Created by hinaikhan on 10/22/17.
 */

public class FirebasePushNotificationService implements MassageSenderListener {

    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    OkHttpClient mClient;
    private Context mContext;

    public FirebasePushNotificationService(Context context) {
        mContext = context;
        mClient = new OkHttpClient();
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
                .addHeader("Authorization", "key=" + mContext.getResources().getString(R.string.servrt_id))
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

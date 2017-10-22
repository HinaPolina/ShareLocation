package hinapolina.com.sharelocation.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import hinapolina.com.sharelocation.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hinaikhan on 10/18/17.
 */

public class FirebaseTopicNotificationService extends AsyncTask<String, Integer, String> {
    private static final String TAG = FirebaseTopicNotificationService.class.getSimpleName();

    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";



    protected String doInBackground(String... params) {
        return notifyTopic(params[0], params[1], params[2]);
    }

    protected void onPostExecute(String response) {
        Log.d(TAG, "Firebase Response: " + response);
    }

    private String notifyTopic(String firebaseApiKey, String topic, String message) {
        String response = null;
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("message", message);
            jGcmData.put("to", "/topics/" + topic);

            // What to send in GCM message.
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + firebaseApiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();

            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(inputStream, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            response = out.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error notifying topic - " + e.getMessage());
        }

        return response;
    }

    public void notifyAllUsers(Context context, List<String> tokens, String message) {
        OkHttpClient mClient = new OkHttpClient();
        JSONObject root = new JSONObject();
        JSONObject notification = new JSONObject();
        try {
            notification.put("title", message);
            JSONObject data = new JSONObject();
            data.put("message", "I am message");
            root.put("notification", notification);
            root.put("data", data);
            root.put("registration_ids", new JSONArray(tokens));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), root.toString());
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + context.getResources().getString(R.string.servrt_id))
                .addHeader("Content-Type", "application/json")
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("RESPONSE", response.body().string());
            }
        });

    }
}


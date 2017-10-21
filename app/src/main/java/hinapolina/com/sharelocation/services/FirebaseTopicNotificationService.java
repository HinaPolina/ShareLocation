package hinapolina.com.sharelocation.services;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hinaikhan on 10/18/17.
 */

public class FirebaseTopicNotificationService extends AsyncTask<String, Integer, String> {
    private static final String TAG = FirebaseTopicNotificationService.class.getSimpleName();

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
}
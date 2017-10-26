package hinapolina.com.sharelocation.network;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by polina on 10/24/17.
 */

public class OKHTTPHelper {
    public static final String URI = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";


    public static void gelLestOfPlaces(Double lat, Double lng, String key, final String pageToken, Callback callbake){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URI+"?location=" + lat +"," +lng+ "&radius=500&key=" +key+ "&pagetoken="+pageToken)
                .build();
        client.newCall(request).enqueue(callbake);
    }


}

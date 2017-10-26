package hinapolina.com.sharelocation.fragments;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.adapters.PlaceAdapter;
import hinapolina.com.sharelocation.listener.EndlessScrollListener;
import hinapolina.com.sharelocation.listener.OnPlaceListener;
import hinapolina.com.sharelocation.model.Place;
import hinapolina.com.sharelocation.network.OKHTTPHelper;
import hinapolina.com.sharelocation.ui.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by polina on 10/24/17.
 */

public class SharePlacesDialog extends DialogFragment {

    List<Place> placeList = new ArrayList<>();
    String token  = "";
    private static String IMAGE_UTL = "https://maps.googleapis.com/maps/api/place/photo";
    private EndlessScrollListener scrollListener;
    Double lat;
    Double lng ;
    PlaceAdapter adapter;
    Callback callback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle =  getArguments();
        lat = bundle.getDouble(Utils.LAT);
          lng = bundle.getDouble(Utils.LNG);
        System.err.println("lat "+ lat+"lng " +lng);
       callback= new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("Request failed " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                token="";
                JSONObject json = null;
                final List<Place> listPlaces = new ArrayList<Place>();
                try {
                    json = new JSONObject(responseData);
                    System.err.println("json " + json.toString());
                    JSONArray results = json.getJSONArray("results");
                    token = json.optString("next_page_token");

                    System.err.println("next pae token " + token);


                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        System.err.println(" Place name "+result.getString("name"));
                        Place place = new Place();
                        place.setName(result.getString("name"));
                        JSONObject location = result.optJSONObject("geometry").
                                optJSONObject("location");
                        if(location!=null) {
                            place.setLat(location.getDouble("lat"));
                            place.setLng(location.getDouble("lng"));
                        }
                        JSONArray photos = result.optJSONArray("photos");
                        if(photos!=null) {
                            JSONObject photo = photos.optJSONObject(0);


                            place.setUrl(IMAGE_UTL + "?maxwidth=100&photoreference=" +
                                    photo.optString("photo_reference") +"&key="+
                                    getResources().getString(R.string.google_place_api_key));
                        }
                        System.err.println("Place "+  i +  " " + place.toString());
                        listPlaces.add(place);


                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                placeList.addAll(listPlaces);
                                adapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        OKHTTPHelper.gelLestOfPlaces(lat, lng, getResources().getString(R.string.google_place_api_key), token, callback );
        }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.list_of_places_dialog, null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        builder.setView(content).setTitle(R.string.list_of_places);
        RecyclerView recyclerView = (RecyclerView) content.findViewById(R.id.list_of_places);
        recyclerView.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(!token.isEmpty())
                OKHTTPHelper.gelLestOfPlaces(lat, lng, getResources().getString(R.string.google_place_api_key), token, callback );
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        adapter = new PlaceAdapter(placeList, (OnPlaceListener) getActivity(), this);

        recyclerView.setAdapter(adapter);
        return builder.create();
    }


    public static SharePlacesDialog newInstance(Location location) {
        SharePlacesDialog fragment = new SharePlacesDialog();
        Bundle args = new Bundle();
        args.putDouble(Utils.LAT, location.getLatitude());
        args.putDouble(Utils.LNG, location.getLongitude());
        fragment.setArguments(args);
        return fragment;
    }
}

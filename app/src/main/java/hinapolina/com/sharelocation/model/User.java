package hinapolina.com.sharelocation.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by polina on 10/11/17.
 */
@IgnoreExtraProperties
public class User {
    public String name;
    public String email;
    public String imageURI;
    public int battery;
    public double lng;
    public double lat;

    public User() {
    }

    public User(String name, String email, String imageURI, int battery, double lng, double lat) {
        this.name = name;
        this.email = email;
        this.imageURI = imageURI;
        this.battery = battery;
        this.lng = lng;
        this.lat = lat;
    }
}

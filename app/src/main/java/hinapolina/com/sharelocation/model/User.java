package hinapolina.com.sharelocation.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by polina on 10/11/17.
 */
@IgnoreExtraProperties
public class User {

    @Exclude
    public String id;
    public String name;
    public String email;
    public String imageURI;
    public int battery;
    public double lng;
    public double lat;

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    @Exclude
    boolean isFriend;

    public User() {
    }

    public User(String id, String name, String email, String imageURI, int battery, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageURI = imageURI;
        this.battery = battery;
        this.lng = lng;
        this.lat = lat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}

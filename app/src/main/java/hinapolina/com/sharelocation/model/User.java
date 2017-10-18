package hinapolina.com.sharelocation.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import hinapolina.com.sharelocation.ui.BatteryStatus;

/**
 * Created by polina on 10/11/17.
 */
@IgnoreExtraProperties
public class User {

    @Exclude
    private String id;
    private String name;
    private String email;
    private String imageURI;
    private int battery;
    private double lng;
    private double lat;
    private BatteryStatus batteryStatus;
    private String text;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

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

    public User(String id, String name, String email, String imageURI, int battery, double lat, double lng,
                BatteryStatus batteryStatus, String text) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageURI = imageURI;
        this.battery = battery;
        this.lng = lng;
        this.lat = lat;
        this.batteryStatus = batteryStatus;
        this.text = text;
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

    public BatteryStatus getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(BatteryStatus batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", imageURI='" + imageURI + '\'' +
                ", battery=" + battery +
                ", lng=" + lng +
                ", lat=" + lat +
                ", batteryStatus=" + batteryStatus +
                ", text='" + text + '\'' +
                '}';
    }
}

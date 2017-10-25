package hinapolina.com.sharelocation.model;

/**
 * Created by polina on 10/24/17.
 */

public class Place {
    String name;
    String url;
    double lat;
    double lng;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}

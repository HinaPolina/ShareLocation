package hinapolina.com.sharelocation.model;

/**
 * Created by hinaikhan on 10/17/17.
 */

public class Message {

    private String sender;
    private String message;
    private String userProfileImg;
    private long timeInMillis;
    private Boolean read;
    private String userUID;
    private String receiver;
    private String imgUrl;
    private String placeName;
    private Double lat;
    private Double lng;


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserProfileImg() {
        return userProfileImg;
    }

    public void setUserProfileImg(String userProfileImg) {
        this.userProfileImg = userProfileImg;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", userProfileImg='" + userProfileImg + '\'' +
                ", timeInMillis=" + timeInMillis +
                ", read=" + read +
                ", userUID='" + userUID + '\'' +
                ", receiver='" + receiver + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", placeName='" + placeName + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}



package hinapolina.com.sharelocation.model;

/**
 * Created by hinaikhan on 10/17/17.
 */

public class Message {

    private String sender;
    private String message;
    private String userProfileImg;


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
}

package hinapolina.com.sharelocation.ui;

import android.webkit.URLUtil;

import hinapolina.com.sharelocation.activities.videotalk.VideoTalkActivity;

/**
 * Created by hinaikhan on 10/21/17.
 */

public class OpenTokConfig {

    public static final String CHAT_SERVER_URL = null;
    public static final String SESSION_INFO_ENDPOINT = CHAT_SERVER_URL + "/session";

    public static String API_KEY = "45985662";
    public static String SESSION_ID = "1_MX40NTk4NTY2Mn5-MTUwODU1ODE3ODE3OH5XNEVCMElBaEFXN0Y5MUJtZHl0N1Z3cTR-fg";
    public static String TOKEN = "T1==cGFydG5lcl9pZD00NTk4NTY2MiZzaWc9NjllM2VkMGRlY2IzZDUyNDc2MTNiNGM1YzE2ZDdkNzNiNjVhMjFmMDpzZXNzaW9uX2lkPTFfTVg0ME5UazROVFkyTW41LU1UVXdPRFUxT0RFM09ERTNPSDVYTkVWQ01FbEJhRUZYTjBZNU1VSnRaSGwwTjFaM2NUUi1mZyZjcmVhdGVfdGltZT0xNTA4NTU4MjAyJm5vbmNlPTAuMjQzMjA1NDMzNzExNDA5MzUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTUxMTE1MzgwMSZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";


    public static String webServerConfigErrorMessage;
    public static String hardCodedConfigErrorMessage;

    private static final String LOG_TAG = OpenTokConfig.class.getSimpleName();

    public static boolean areHardCodedConfigsValid() {
        if (OpenTokConfig.API_KEY != null && !OpenTokConfig.API_KEY.isEmpty()
                && OpenTokConfig.SESSION_ID != null && !OpenTokConfig.SESSION_ID.isEmpty()
                && OpenTokConfig.TOKEN != null && !OpenTokConfig.TOKEN.isEmpty()) {
            return true;
        }
        else {
            hardCodedConfigErrorMessage = "API KEY, SESSION ID and TOKEN in OpenTokConfig.java cannot be null or empty.";
            return false;
        }
    }


    public static boolean isWebServerConfigUrlValid(){
        if (OpenTokConfig.CHAT_SERVER_URL == null || OpenTokConfig.CHAT_SERVER_URL.isEmpty()) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java must not be null or empty";
            return false;
        } else if ( !( URLUtil.isHttpsUrl(OpenTokConfig.CHAT_SERVER_URL) || URLUtil.isHttpUrl(OpenTokConfig.CHAT_SERVER_URL)) ) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java must be specified as either http or https";
            return false;
        } else if ( !URLUtil.isValidUrl(OpenTokConfig.CHAT_SERVER_URL) ) {
            webServerConfigErrorMessage = "CHAT_SERVER_URL in OpenTokConfig.java is not a valid URL";
            return false;
        } else {
            return true;
        }
    }

}

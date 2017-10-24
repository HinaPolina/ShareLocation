package hinapolina.com.sharelocation.listener;

import hinapolina.com.sharelocation.model.User;

/**
 * Created by polina on 10/19/17.
 */

public interface MassageSenderListener {
    void onSendMassageListener(User user, String massage);
}

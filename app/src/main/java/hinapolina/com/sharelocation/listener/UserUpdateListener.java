package hinapolina.com.sharelocation.listener;

import java.util.List;

import hinapolina.com.sharelocation.model.User;

/**
 * Created by polina on 10/14/17.
 */

public interface UserUpdateListener {
    void updateUserMarker(User user);
    void addUserToAdapter(User user);
    void addUserToAdapter(List<User> users);
}

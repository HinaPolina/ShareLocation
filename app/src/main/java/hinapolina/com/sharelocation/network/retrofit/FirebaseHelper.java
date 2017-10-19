package hinapolina.com.sharelocation.network.retrofit;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import hinapolina.com.sharelocation.ui.Application;
import hinapolina.com.sharelocation.listener.UserUpdateListener;
import hinapolina.com.sharelocation.model.User;

/**
 * Created by polina on 10/14/17.
 */

public class FirebaseHelper {
    private static final String TAG = FirebaseHelper.class.getSimpleName();

    private  DatabaseReference mDatabase;
    UserUpdateListener listener;

    public FirebaseHelper(UserUpdateListener context) {
        mDatabase = Application.getmDatabase();
        listener = context;
    }

    public void removeAddUser(final String id, final String currentId, final Boolean isFriend){
        final  HashSet<String> friendsIdList =new HashSet<>();
        mDatabase.child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot friends: dataSnapshot.getChildren()) {
                    String userId = friends.getKey();

                    if (userId.equals(currentId)){
                        String res = friends.getValue(String.class);
                        String [] arr = res.split(";");
                        friendsIdList.addAll(Arrays.asList(arr));
                        System.err.println("Friends of  user " + res + " into DB" );
                    }

                }
                if(isFriend) {
                    friendsIdList.remove(id);
                } else {
                    friendsIdList.add(id);
                }
                String friendsId =  StringUtils.join(friendsIdList, ";");
                mDatabase.child("friends").child(currentId).setValue(friendsId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("The read failed: " + databaseError.getMessage());
            }
        });
    }


    public void saveFriendsToBD(JSONArray array, final String currentUserId) {
        final HashSet<String> friendsIdList = new HashSet<>();
        for (int i = 0; i < array.length(); i++) {
            String userId = array.optJSONObject(i).optString("id");
            String userName = array.optJSONObject(i).optString("name");
            System.err.println("ID: "+ userId + " name: " + userName);
            friendsIdList.add(userId);
        }
        mDatabase.child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot friends: dataSnapshot.getChildren()) {
                    String userId = friends.getKey();

                    if (userId.equals(currentUserId)){
                        String res = friends.getValue(String.class);
                        String [] arr = res.split(";");
                        friendsIdList.addAll(Arrays.asList(arr));
                        System.err.println("Friends of  user " + res + " into DB" );
                    }
                }
                String friendsId =   StringUtils.join(friendsIdList, ";");
                mDatabase.child("friends").child(currentUserId).setValue(friendsId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("The read failed: " + databaseError.getMessage());
            }
        });

    }


    public void findUserByName(final String name, final String currentID){
        final  HashSet<String> friendsIdList =new HashSet<>();

        mDatabase.child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot friends: dataSnapshot.getChildren()) {
                    String userId = friends.getKey();

                    if (userId.equals(currentID)){
                        String res = friends.getValue(String.class);
                        String [] arr = res.split(";");
                        friendsIdList.addAll(Arrays.asList(arr));
                        System.err.println("Friends of  user " + res + " into DB" );
                    }

                }

                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    List<User> users = new ArrayList<User>();
                    List<User> usersDefoult = new ArrayList<User>();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userDB: dataSnapshot.getChildren()) {
                            String userId = userDB.getKey();
                            User user = userDB.getValue(User.class);
                            user.setId(userId);
                            user.setFriend(friendsIdList.contains(userId));
                            if(!userId.equals(currentID)) usersDefoult.add(user);
                            if (name.equals(user.getName())&& !userId.equals(currentID)){
                                users.add(user);
                                user.setFriend(friendsIdList.contains(userId));
                            }
                        }

                        listener.addUserToAdapter(users.size()>0? users: usersDefoult);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("The read failed: " + databaseError.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("The read failed: " + databaseError.getMessage());
            }
        });
    }



    public void getUsersFromFirebaseByID(final String currentUserId) {

        final  HashSet<String> friendsIdList =new HashSet<>();

        mDatabase.child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot friends: dataSnapshot.getChildren()) {
                    String userId = friends.getKey();

                    if (userId.equals(currentUserId)){
                        String res = friends.getValue(String.class);
                        String [] arr = res.split(";");
                        friendsIdList.addAll(Arrays.asList(arr));
                        System.err.println("Friends of  user " + res + " into DB" );
                    }

                }
                friendsIdList.add(currentUserId);

                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot user: dataSnapshot.getChildren()) {
                            String userId = user.getKey();

                            if (friendsIdList.contains(userId)){
                                User res = user.getValue(User.class);
                                Log.d(TAG, "Adding user " + res.getName() + " to map");
                                listener.updateUserMarker(res);

                                Log.d(TAG, "Adding user " + res.getName() + " to adapter");
                                listener.addUserToAdapter(res);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("The read failed: " + databaseError.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("The read failed: " + databaseError.getMessage());
            }
        });




    }



}


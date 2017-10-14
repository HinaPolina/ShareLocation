package hinapolina.com.sharelocation.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import hinapolina.com.sharelocation.model.User;


/**
 * Created by polina on 10/13/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UsersDB";
    private static final String TABLE_NAME = "Users";
    private static final String ID = "id"; // 0
    private static final String NAME = "name"; // 1
    private static final String BATTERY = "battery"; // 2
    private static final String EMAIL = "email"; // 3
    private static final String IMAGE_URI = "image"; // 4
    private static final String LAT = "lat"; // 5
    private static final String LNG = "lng"; // 6
    private static final String[] COLUMNS = { ID, NAME, BATTERY, EMAIL,IMAGE_URI, LAT, LNG};

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATION_TABLE = "CREATE TABLE Users ( "
                + "id TEXT PRIMARY KEY, "
                + "name TEXT, "
                + "battery INTEGER, "
                + "email TEXT, "
                + "image TEXT, "
                + "lat REAL, "
                + "lng REAL )";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void deleteOne(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?",
                new String[] {user.getId()});
        db.close();

    }

    public User getUser(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] {id }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();
        return getUser(cursor);
    }

    public void addFaikeListOfFriends(){
        List<User> users = new ArrayList<>();

        User user1 = new User("1", "Max", "ololo", "https://openclipart.org/image/2400px/svg_to_png/239997/TJ-Openclipart-8-character-Hi-4-2-16-png-.png", 25, 37.7331299, -122.507348);
        User user2 = new User("2", "Jon", "ololo", "https://openclipart.org/image/2400px/svg_to_png/239997/TJ-Openclipart-8-character-Hi-4-2-16-png-.png", 67, 37.7339614, -122.5278508);
        User user3 = new User("3", "Dug", "ololo", "https://openclipart.org/image/2400px/svg_to_png/239997/TJ-Openclipart-8-character-Hi-4-2-16-png-.png", 100, 37.7689793, -122.4660527);
        User user4 = new User("4", "Jo", "ololo", "https://openclipart.org/image/2400px/svg_to_png/239997/TJ-Openclipart-8-character-Hi-4-2-16-png-.png", 5, 37.7689793, -122.4660527);
        addUser(user1);
        addUser(user2);
        addUser(user3);
        addUser(user4);


    }

    private User getUser(Cursor cursor){
        User user = new User();
        user.setId(cursor.getString(0));
        user.setName(cursor.getString(1));
        user.setBattery(cursor.getInt(2));
        user.setEmail(cursor.getString(3));
        user.setImageURI(cursor.getString(4));
        user.setLat(cursor.getDouble(5));
        user.setLng(cursor.getDouble(6));
        return user;
    }

    public List<User> allUsers() {

        List<User> users = new LinkedList<User>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
               User user = getUser(cursor);
                users.add(user);
            } while (cursor.moveToNext());
        }
        return users;
    }

    public ContentValues fillUpValues(User user){
        ContentValues values = new ContentValues();
        values.put(ID, user.getId());
        values.put(NAME, user.getName());
        values.put(BATTERY, user.getBattery());
        values.put(EMAIL, user.getEmail());
        values.put(IMAGE_URI, user.getImageURI());
        values.put(LAT, user.getLat());
        values.put(LNG, user.getLng());
        return  values;

    }

    public Cursor addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = fillUpValues(user);
        db.insert(TABLE_NAME,null, values);
        db.close();
        return null;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = fillUpValues(user);
        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { user.getId() });
        db.close();
        return i;
    }
}

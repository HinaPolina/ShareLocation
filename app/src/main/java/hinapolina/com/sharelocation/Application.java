package hinapolina.com.sharelocation;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by polina on 10/11/17.
 */

public class Application extends android.app.Application {

    public static DatabaseReference getmDatabase() {
        return mDatabase;
    }

    private static DatabaseReference mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }
}

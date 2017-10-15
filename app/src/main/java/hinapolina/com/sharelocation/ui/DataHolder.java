package hinapolina.com.sharelocation.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hinaikhan on 10/15/17.
 */

public class DataHolder {

    private Map<String, Object> session;

    private DataHolder() {
        session = new HashMap<String, Object>();
    }

    public static DataHolder getInstance() {
        return DataHolderInner.INSTANCE;
    }

    static class DataHolderInner {
        public static DataHolder INSTANCE = new DataHolder();
    }

    public void put(String key, Object val) {
        session.put(key, val);
    }

    public Object retrieve(String key) {
        return session.get(key);
    }

    public void clear() {
        session.clear();
    }

}



package android.content;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Properties;

public class SharedPreferences extends Properties {

    public static SharedPreferences instance = new SharedPreferences();

    private SharedPreferences() {

    }

    public String getString(String key, String defValue) {
        return  get(key)==null ? defValue : get(key) + "";
    }

    public SharedPreferences edit() {
        return this;
    }

    public SharedPreferences putString(String key, String value) {
        put(key, value);
        return this;
    }

    public void commit() {

    }
}

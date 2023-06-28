package android.content;

import java.io.File;

public class Context {


    public static final int MODE_PRIVATE = 0;

    public Context() {
    }

    private static class SingletonHolder {
        private static final Context instance = new Context();

    }

    public static Context getInstance() {
        return SingletonHolder.instance;
    }


    public SharedPreferences getSharedPreferences(String key, int mode) {

        return SharedPreferences.instance;
    }

    public File getFilesDir() {

        return new File(".");
    }
}

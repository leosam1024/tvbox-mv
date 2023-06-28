package android.os;

import java.io.File;

public class Environment {


    public static File getExternalStorageDirectory() {
        return new File(".");
    }
}

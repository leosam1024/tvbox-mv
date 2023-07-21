package android.os;

import java.util.Properties;

public class SystemProperties extends Properties {
    static SystemProperties instance = new SystemProperties();

    private SystemProperties() {
    }

    public static Object get(String key) {
        if (key.equals("ro.build.fingerprint")) {
            //GO/T2/mt6738_d5377_s1:6.0/MRA58K/1470368709:user/test-keys
            return "HONOR/KIW-TL00H/HNKIW-Q:6.0.1/HONORKIW-TL00H/C00B430:user/release-keys";
        }
        return instance.get(key);
    }
}

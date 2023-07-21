package android.util;

public class Log {
    public static int e(String tag, String msg ) {

        return e(tag,msg,null);
    }
    public static int e(String tag, String msg, Throwable tr) {
        System.err.println(String.format("%s:%s:%s%n", tag, msg, tr.getMessage()));

        return 0;
    }
    public static int d(String tag, String msg, Throwable tr) {
        System.err.println(String.format("%s:%s:%s%n", tag, msg, tr.getMessage()));

        return 0;
    }
    public static int i(String tag, String msg, Throwable tr) {
        System.err.println(String.format("%s:%s:%s%n", tag, msg, tr.getMessage()));

        return 0;
    }
    public static int d(String tag, String msg ) {
        System.err.println(String.format("%s:%s:%s%n", tag, msg, ""));

        return 0;
    }
}

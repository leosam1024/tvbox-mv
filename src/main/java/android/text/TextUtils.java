package android.text;

import java.util.Iterator;

public class TextUtils {
    public static boolean isEmpty(String text) {
        boolean result = false;
        if (text == null || (text!=null &&text.trim().isEmpty())) {
            result = true;
        }
        return result;
    }
    public static String join( CharSequence delimiter,  Iterable tokens) {
        final Iterator<?> it = tokens.iterator();
        if (!it.hasNext()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(it.next());
        while (it.hasNext()) {
            sb.append(delimiter);
            sb.append(it.next());
        }
        return sb.toString();
    }
    public static String join( CharSequence delimiter,  Object[] tokens) {
        final int length = tokens.length;
        if (length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(tokens[0]);
        for (int i = 1; i < length; i++) {
            sb.append(delimiter);
            sb.append(tokens[i]);
        }
        return sb.toString();
    }
}

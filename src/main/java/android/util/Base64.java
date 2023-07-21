package android.util;

import java.io.UnsupportedEncodingException;

public class Base64 {
    /**
     * Default values for encoder/decoder flags.
     */
    public static final int DEFAULT = 0;

    /**
     * Encoder flag bit to omit the padding '=' characters at the end
     * of the output (if any).
     */
    public static final int NO_PADDING = 1;

    /**
     * Encoder flag bit to omit all line terminators (i.e., the output
     * will be on one long line).
     */
    public static final int NO_WRAP = 2;

    /**
     * Encoder flag bit to indicate lines should be terminated with a
     * CRLF pair instead of just an LF.  Has no effect if {@code
     * NO_WRAP} is specified as well.
     */
    public static final int CRLF = 4;

    /**
     * Encoder/decoder flag bit to indicate using the "URL and
     * filename safe" variant of Base64 (see RFC 3548 section 4) where
     * {@code -} and {@code _} are used in place of {@code +} and
     * {@code /}.
     */
    public static final int URL_SAFE = 8;

    /**
     * Flag to pass to  to indicate that it
     * should not close the output stream it is wrapping when it
     * itself is closed.
     */
    public static final int NO_CLOSE = 16;

    public static byte[] encode(byte[] input, int flag) {

        if(flag==8){
            return java.util.Base64.getUrlEncoder().encode(input);
        }

        return java.util.Base64.getEncoder().encode(input);
    }

    public static byte[] decode(String input, int flag) {
        if(flag==8){
            return java.util.Base64.getUrlDecoder().decode(input);
        }


        return java.util.Base64.getDecoder().decode(input);
    }

    public static byte[] decode(byte[] input, int flag) {
        if(flag==8){
            return java.util.Base64.getUrlDecoder().decode(input);
        }

        return java.util.Base64.getDecoder().decode(input);
    }

    public static String decodeStr(String input, int flag) {

        if (flag == 8) {
            try {
                return new String(java.util.Base64.getUrlDecoder().decode(input), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            return new String(java.util.Base64.getDecoder().decode(input), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeToString(byte[] input, int flag) {
        if (flag == 8) {
            java.util.Base64.getUrlEncoder().encodeToString(input);

        }
        return java.util.Base64.getEncoder().encodeToString(input);
    }
}

package android.os;

public class Build {
    public static final String SERIAL ="T3Q6T16310009533";// "0123456789ABCDEF";
    public static final String BOARD = "KIW-TL00H"; //设备基板名称
    public static final String BOOTLOADER = "unknown";//设备引导程序版本号
    public static final String BRAND = "HONOR"; //设备品牌
    public static final String DEVICE = "HNKIW-Q"; //设备驱动名称
    public static final String DISPLAY = "KIW-TL00HC00B430"; //设备显示的版本包
    public static final String FINGERPRINT = "HONOR/KIW-TL00H/HNKIW-Q:6.0.1/HONORKIW-TL00H/C00B430:user/release-keys"; //设备的唯一标识
    public static final String HARDWARE = "qcom"; //设备硬件名称
    public static final String HOST = "localhost#1";  //主机地址
    public static final String ID = "HONORKIW-TL00H"; //设备版本号
    public static final String MANUFACTURER = "HUAWEI";//设备制造商
    public static final String MODEL = "KIW-TL00H";//手机的型号 设备名称
    public static final String PRODUCT = "KIW-TL00H";//产品的名称

    public static class VERSION {
        public static final String BASE_OS = "";
        public static final String CODENAME = "REL";
        public static final String RELEASE = "6.0.1";
        public static final int SDK_INT = 23;

        public VERSION() {
            throw new RuntimeException("Stub!");
        }
    }

    public static class VERSION_CODES {
        public static final int BASE = 1;
        public static final int BASE_1_1 = 2;
        public static final int CUPCAKE = 3;
        public static final int CUR_DEVELOPMENT = 10000;
        public static final int DONUT = 4;
        public static final int ECLAIR = 5;
        public static final int ECLAIR_0_1 = 6;
        public static final int ECLAIR_MR1 = 7;
        public static final int FROYO = 8;
        public static final int GINGERBREAD = 9;
        public static final int GINGERBREAD_MR1 = 10;
        public static final int HONEYCOMB = 11;
        public static final int HONEYCOMB_MR1 = 12;
        public static final int HONEYCOMB_MR2 = 13;
        public static final int ICE_CREAM_SANDWICH = 14;
        public static final int ICE_CREAM_SANDWICH_MR1 = 15;
        public static final int JELLY_BEAN = 16;
        public static final int JELLY_BEAN_MR1 = 17;
        public static final int JELLY_BEAN_MR2 = 18;
        public static final int KITKAT = 19;
        public static final int KITKAT_WATCH = 20;
        public static final int LOLLIPOP = 21;
        public static final int LOLLIPOP_MR1 = 22;
        public static final int M = 23;
        public static final int N = 24;
        public static final int N_MR1 = 25;
        public static final int O = 26;
        public static final int O_MR1 = 27;
        public static final int P = 28;
        public static final int Q = 29;
        public static final int R = 30;

        public VERSION_CODES() {
            throw new RuntimeException("Stub!");
        }
    }
}

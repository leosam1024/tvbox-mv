package android.net;

import java.net.URI;

public class Uri {
    private static URI uri;

    private static class SingletonHolder{
        private static final Uri URI=new Uri();
    }

    public static Uri parse(String u) {
        uri=URI.create(u);
        return SingletonHolder.URI;
    }
    public String getHost(){

        return uri==null?"":uri.getHost();
    }

}

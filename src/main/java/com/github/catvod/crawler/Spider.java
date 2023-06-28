package com.github.catvod.crawler;

import android.content.Context;
import okhttp3.Dns;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class Spider {

    public void init(Context context) {
    }

    public void init(Context context, String extend) {
        init(context);
    }

    public String homeContent(boolean filter) throws Exception {
        return "";
    }

    public String homeVideoContent() throws Exception {
        return "";
    }

    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        return "";
    }

    public String detailContent(List<String> ids) throws Exception {
        return "";
    }

    public String searchContent(String key, boolean quick) throws Exception {
        return "";
    }

    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        return "";
    }

    public boolean manualVideoCheck() {
        return false;
    }

    public boolean isVideoFormat(String url) {
        return false;
    }

//    public static Dns safeDns() {
//        return new Dns() {
//            @NotNull
//            @Override
//            public List<InetAddress> lookup(@NotNull String domain) throws UnknownHostException {
//                return domain.equals("qu.ax")? Arrays.asList(InetAddress.getAllByName("141.98.234.40")):Dns.SYSTEM.lookup(domain);
//            }
//        };
//    }
}

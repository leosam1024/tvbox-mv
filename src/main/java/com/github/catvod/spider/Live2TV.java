package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import com.github.catvod.crawler.Spider;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.SSLSocketFactoryCompat;
import okhttp3.Cache;
import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.dnsoverhttps.DnsOverHttps;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Live2TV extends Spider {
    private static final Map<String, List<String>> LIVE = new HashMap<>();

    private String extend = null;

    private static final String IMG = "https://p2.img.cctvpic.com/photoAlbum/page/performance/img/2023/1/19/1674108130081_82.jpg";

    @Override
    public void init(Context context, String extend) {
        this.extend = extend;
    }

    protected HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Method", "GET");
        headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        headers.put("Accept", "*/*");
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        return headers;
    }

    public Map<String, String> parse(String page) {
        LIVE.clear();
        Map<String, String> CATE = new HashMap<>();
        Scanner scan = null;
        try {
            scan = new Scanner(page);
            String line = null;
            String cate = null;
            while (scan.hasNextLine()) {
                line = scan.nextLine();
                if (line != null && !line.trim().isEmpty()) {
                    String[] kv = line.split(",", -1);
                    if (kv != null && kv.length == 2) {
                        if (kv[1].trim().startsWith("#")) {
                            CATE.put(kv[0], "");
                            cate = kv[0];
                            continue;
                        }

                        if (cate != null && !cate.trim().isEmpty()) {
                            List<String> list = LIVE.get(cate + "-" + kv[0]);
                            if (list == null) {
                                List<String> l = new ArrayList<String>();
                                l.add(kv[1]);
                                LIVE.put(cate + "-" + kv[0], l);
                            } else {
                                list.add(kv[1]);
                                LIVE.put(cate + "-" + kv[0], list);
                            }

                        }
                    }
                }
            }
        } finally {
            scan.close();
        }
        return CATE;

    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        Map<String, String> CATE = new HashMap<>();

        if (!TextUtils.isEmpty(extend) && extend.toLowerCase().startsWith("http")) {
            String page = OkHttp.string(extend, getHeaders());
            if (!TextUtils.isEmpty(page)) {

                CATE = parse(page);
            }
        }
        JSONArray classes = new JSONArray();


        for (Map.Entry<String, String> entry : CATE.entrySet()) {
            JSONObject jSONObject = new JSONObject();

            jSONObject.put("type_id", entry.getKey());
            jSONObject.put("type_name", entry.getKey());

            classes.put(jSONObject);
        }
        JSONObject result = new JSONObject();


        result.put("class", classes);

        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        JSONObject result = new JSONObject();
        JSONArray videos = new JSONArray();
        for (Map.Entry<String, List<String>> entry : LIVE.entrySet()) {
            if (entry.getKey().startsWith(tid + "=")) {
                JSONObject v = new JSONObject();
                try {
                    v.put("vod_id", entry.getKey());
                    v.put("vod_name", entry.getKey().split("=")[1]);
                    v.put("vod_pic", "https://p2.img.cctvpic.com/photoAlbum/page/performance/img/2023/1/19/1674108130081_82.jpg");
                    v.put("vod_remarks", entry.getKey());
                } catch (JSONException ex) {
                }
                videos.put(v);
            }
        }
        result.put("page", pg);
        result.put("pagecount", 1);
        result.put("limit", videos.length());
        result.put("total", videos.length());
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        JSONObject v = new JSONObject();

        List<String> list = LIVE.get(ids.get(0));

        v.put("vod_id", ids.get(0));
        v.put("vod_name", ids.get(0));
        v.put("vod_pic", IMG);
        v.put("vod_content", ids.get(0));


        ArrayList<String> playList = new ArrayList<>();
        JSONArray videos = new JSONArray();
        JSONObject result = new JSONObject();
        for (int i = 0; i < list.size(); i++) {
            playList.add("源" + i + "$" + list.get(i));
        }
        Map<String, String> sites = new LinkedHashMap<String, String>();

        if (playList.size() > 0) {
            sites.put(ids.get(0), TextUtils.join("#", playList));
        }
        v.put("vod_play_from", TextUtils.join("$$$", sites.keySet()));
        v.put("vod_play_url", TextUtils.join("$$$", sites.values()));

        videos.put(v);
        result.put("list", videos);
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        JSONObject result = new JSONObject();
        result.put("parse", 0);
        result.put("palyUrl", "");
        result.put("url", id);
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) throws Exception {
        JSONObject result = new JSONObject();
        JSONArray videos = new JSONArray();
        for (Map.Entry<String, List<String>> entry : LIVE.entrySet()) {
            if (entry.getKey().contains(key)) {
                JSONObject v = new JSONObject();

                v.put("vod_id", entry.getKey());
                v.put("vod_name", entry.getKey());
                v.put("vod_pic", IMG);
                v.put("vod_remarks", entry.getKey());

                videos.put(v);
            }
        }
        result.put("list", videos);
        return result.toString();
    }



    public static void main(String[] args) throws Exception {
        Spider tv = new Live2TV();
        tv.init(Context.getInstance(), "https://qu.ax/HtMB.txt");
        System.out.println(tv.homeContent(false));


//        String b = Jsoup.connect("https://qu.ax/HtMB.txt").execute().body();
//        System.out.println(b);
    }
}

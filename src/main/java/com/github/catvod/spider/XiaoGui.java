package com.github.catvod.spider;



import android.content.Context;
import android.text.TextUtils;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

public class XiaoGui extends Spider {
    private String siteUrl = "http://tkznp.com";
    private String apiUrl = "/xgapp.php/v2/";

    protected JSONObject playerConfig;
    private JSONObject filterConfig;



    protected HashMap<String, String> getHeaders(String refererUrl) {
        // hashMap.put("User-Agent", "Lavf/58.12.100");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        if (!TextUtils.isEmpty(refererUrl)) {
            headers.put("Referer", refererUrl);
        }
        // headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("User-Agent", "Dart/2.14(dart:io)");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        return headers;
    }

    public static final Map<String, String> KV = new HashMap<String, String>();

    public static final Map<String, String> PLAYER_HEADER = new HashMap<>();

    @Override
    public void init(Context context, String extend) {
        if(!TextUtils.isEmpty(extend)) {
            if (extend.contains("$$$")) {
                String[] ext = extend.split("\\$\\$\\$", -1);
                if (ext.length == 2) {
                    siteUrl = ext[0];

                    apiUrl = ext[1];
                }
            } else {

                siteUrl = extend;
            }
        }

        filterConfig = new JSONObject();

        KV.put("class", "类型");
        KV.put("area", "地区");
        KV.put("lang", "语言");
        KV.put("year", "年份");
        KV.put("star", "明星");
        KV.put("director", "导演");
        KV.put("state", "状态");
        KV.put("version", "片源");
    }


    /**
     * 获取分类信息数据
     *
     * @param tid    分类id
     * @param pg     页数
     * @param filter 同homeContent方法中的filter
     * @param extend 筛选参数{k:v, k1:v1}
     * @return
     */
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            String categoryUrl = siteUrl + apiUrl + "video?tid=" + tid + "&pg=" + pg + "&token=";
            StringBuilder sb = new StringBuilder();
            sb.append(categoryUrl);
            for (String key : extend.keySet()) {
                sb.append("&").append(key).append("=").append(URLEncoder.encode(extend.get(key)));
            }
            String page= OkHttp.string(sb.toString(), getHeaders(siteUrl));
            JSONObject pageObj = new JSONObject(page);
            JSONArray jSONArray = null;
            if(pageObj.has("data")){
                jSONArray=pageObj.getJSONArray("data");
            }
            if(pageObj.has("list")){
                jSONArray=pageObj.getJSONArray("list");
            }
            JSONArray videos = new JSONArray();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                JSONObject v = new JSONObject();
                v.put("vod_id", jSONObject2.opt("vod_id")+"");
                v.put("vod_name", jSONObject2.getString("vod_name"));
                v.put("vod_pic", jSONObject2.getString("vod_pic"));
                v.put("vod_remarks", jSONObject2.getString("vod_remarks"));
                videos.put(v);
            }
            JSONObject result = new JSONObject();
            int i2 = pageObj.getInt("page");
            int i3 = pageObj.getInt("total");
            int i4 = pageObj.getInt("pagecount");
            result.put("page", i2);
            result.put("pagecount", i4);
            result.put("limit", videos.length());
            result.put("total", i3);
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }


    @Override
    public String detailContent(List<String> ids) {
        try {
            String str = siteUrl + apiUrl+"video_detail?id=" + ids.get(0) + "&token=";
            JSONObject jSONObject = new JSONObject(OkHttp.string(str, getHeaders(siteUrl))).getJSONObject("data");
            if (jSONObject.has("vod_info")) {
                try {
                    jSONObject = jSONObject.getJSONObject("vod_info");
                } catch (Exception unused) {
                }
            }
            JSONObject vod = new JSONObject();
            vod.put("vod_id", jSONObject.opt("vod_id")+"");
            vod.put("vod_name", jSONObject.getString("vod_name"));
            vod.put("vod_pic", jSONObject.getString("vod_pic"));
            vod.put("type_name", jSONObject.getString("vod_class"));
            vod.put("vod_year", jSONObject.getString("vod_year"));
            vod.put("vod_area", jSONObject.getString("vod_area"));
            vod.put("vod_remarks", jSONObject.getString("vod_remarks"));
            vod.put("vod_actor", jSONObject.getString("vod_actor"));
            vod.put("vod_director", jSONObject.getString("vod_director"));
            vod.put("vod_content", jSONObject.getString("vod_content"));
            JSONArray jSONArray = jSONObject.getJSONArray("vod_url_with_player");
            HashMap<String, String> treeMap = new HashMap<String, String>();
//https://tkznp.com/xgapp.php/v2/video_detail?id=4851
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject vod_url_player = jSONArray.getJSONObject(i);
                String code = vod_url_player.getString("code");//qq youku

                String parse = vod_url_player.getString("parse_api");//extra_parse_api
                if(!TextUtils.isEmpty(parse) && parse.equals("null")){
                    parse="";
                }
                String vod_url = vod_url_player.getString("url");
                List<String> playItem = new ArrayList<String>();
                if (vod_url != null && !vod_url.trim().isEmpty()) {
                    String[] vod_urls = vod_url.split("#", -1);

                    for (int j = 0; j < vod_urls.length; j++) {
                        String[] vods = vod_urls[j].split("\\$", -1);
                        if (vods != null && vods.length == 2) {
                            if (isVideoFormat(vods[1])) {
                                playItem.add(vods[0]+"$"+vods[1]);
                            } else {
                                playItem.add(vods[0]+"$"+parse+vods[1]);

                            }

                        }
                        if(vods != null && vods.length==1){
                            if(isVideoFormat(vods[0])){
                                playItem.add(jSONObject.optString("vod_name")+"$"+vods[0]);
                            }else {
                                playItem.add(jSONObject.optString("vod_name")+"$"+parse+vods[0]);
                            }


                        }

                    }


                    if (vod_url_player.has("headers") && vod_url_player.get("headers") != null && !vod_url_player.get("headers").toString().equals("null")) {
                        PLAYER_HEADER.put(code + "_headers", vod_url_player.getString("headers"));
                    }
                    if (vod_url_player.has("user_agent") && vod_url_player.get("user_agent") != null && !vod_url_player.get("user_agent").toString().equals("null")) {
                        PLAYER_HEADER.put(code + "_user_agent", vod_url_player.getString("user_agent"));
                    }
                }
                if (!playItem.isEmpty()) {
                    treeMap.put(code, TextUtils.join("#", playItem));
                }
            }
            vod.put("vod_play_from", TextUtils.join("$$$", treeMap.keySet()));
            vod.put("vod_play_url", TextUtils.join("$$$", treeMap.values()));
            JSONObject result = new JSONObject();
            JSONArray list = new JSONArray();
            list.put(vod);
            result.put("list", list);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }


    /**
     * 获取分类数据 + 首页最近更新视频列表数据
     *
     * @param filter 是否开启筛选 关联的是 软件设置中 首页数据源里的筛选开关
     * @return
     */
    @Override
    public String homeContent(boolean filter) {
        try {
            String url = siteUrl + apiUrl + "nav?token=";
            String doc = OkHttp.string(url, getHeaders(siteUrl));
            JSONObject navObj = new JSONObject(doc);
            JSONArray dataArray = null;

            if(navObj.has("data")){
                dataArray=navObj.getJSONArray("data");
            }
            if(navObj.has("list")){
                dataArray=navObj.getJSONArray("list");
            }


            JSONArray classes = new JSONArray();

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject ele = dataArray.getJSONObject(i);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type_id", ele.opt("type_id")+"");
                jsonObject.put("type_name", ele.getString("type_name"));
                classes.put(jsonObject);

                if (filter) {

                    JSONArray filterArr = new JSONArray();
                    JSONObject typeExt = ele.getJSONObject("type_extend");
                    Iterator<String> iter = typeExt.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        String line = typeExt.getString(key);
                        if (line != null && !line.trim().isEmpty()) {
                            JSONObject jOne = new JSONObject();
                            jOne.put("key", key);
                            jOne.put("name", KV.get(key));
                            JSONArray valueArr = new JSONArray();
                            String[] nv = line.split(",", -1);
                            if (nv != null && nv.length > 0) {
                                for (String n : nv) {
                                    JSONObject kvo = new JSONObject();
                                    kvo.put("n", n);
                                    kvo.put("v", n);
                                    valueArr.put(kvo);
                                }
                            }

                            jOne.put("value", valueArr);
                            filterArr.put(jOne);
                        }

                    }
                    filterConfig.put(ele.getString("type_id"), filterArr);
                }
            }
            JSONObject result = new JSONObject();
            if (filter) {
                result.put("filters", filterConfig);
            }
            result.put("class", classes);

            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String homeVideoContent() {
        try {
            String url = siteUrl + apiUrl + "index_video?token=";
            String page=OkHttp.string(url, getHeaders(siteUrl));
            JSONObject pageObj= new JSONObject(page);
            JSONArray jSONArray =null;
            if(pageObj.has("data")){
                jSONArray=pageObj.getJSONArray("data");
            }
            if(pageObj.has("list")){
                jSONArray=pageObj.getJSONArray("list");
            }


            JSONArray videos = new JSONArray();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONArray jSONArray3 = jSONArray.getJSONObject(i).getJSONArray("vlist");
                for (int i2 = 0; i2 < jSONArray3.length() && i2 < 6; i2++) {
                    JSONObject jSONObject = jSONArray3.getJSONObject(i2);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", jSONObject.opt("vod_id")+"");
                    v.put("vod_name", jSONObject.getString("vod_name"));
                    v.put("vod_pic", jSONObject.getString("vod_pic"));
                    v.put("vod_remarks", jSONObject.getString("vod_remarks"));
                    videos.put(v);
                }
            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    @Override
    public boolean manualVideoCheck() {
        return true;
    }

    @Override
    public boolean isVideoFormat(String url) {
        return Utils.isVideoFormat(url);
    }


    public static String substr(String source, String from, String to) {

        int start = source.indexOf(from);
        if (start != -1) {
            if (to.length() == 0) {
                return source.substring(start + from.length());
            }
            int end = source.substring(start + from.length()).indexOf(to);
            if (end != -1) {
                return source.substring(start + from.length(), start + from.length() + end);
            }

        }
        return null;
    }


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {

//            if (id.contains("LT-")) {
//                HashMap hashMap = new HashMap();
//                return Misc.jsonParse(id, OkHttpUtil.string("https://jf.96ym.cn/api/?key=89lC7jgdlbZV5T1nIy&url=" + id, hashMap)).toString();
//            } else if (id.contains("renrenmi")) {
//                HashMap hashMap2 = new HashMap();
//                return Misc.jsonParse(id, OkHttpUtil.string("https://kuba.renrenmi.cc:2266/api/?key=a2bSwx5iAGx1g2qn4h&url=" + id, hashMap2)).toString();
//            }

            Map<String, String> playHeader = new HashMap<>();
            // playHeader.put("Referer", siteUrl);
            playHeader.put("User-Agent", "Dart/2.14(dart:io)");
            if (flag != null) {

                String headers = PLAYER_HEADER.get(flag + "_headers");
                if (headers != null && !headers.equalsIgnoreCase("null")) {
                    String[] hr = headers.split(":", 2);
                    if (hr != null && hr.length == 2) {
                        playHeader.put(hr[0], hr[1]);
                    }
                }
                String user_agent = PLAYER_HEADER.get(flag + "_user_agent");
                if (user_agent != null && !user_agent.equalsIgnoreCase("null")) {
                    playHeader.put("User-Agent", user_agent);
                }
            }
            if (Utils.isVideoFormat(id)) {
                JSONObject result = new JSONObject();
                result.put("parse", 0);
                result.put("playUrl", "");
                result.put("url", id);
                result.put("header", new JSONObject(playHeader).toString());
                return result.toString();
            }

            if(id!=null && id.startsWith("Feiyun")){
                String data=parseFeiyun(id);
                if(!TextUtils.isEmpty(data)){

                    JSONObject result = new JSONObject();
                    result.put("parse", 0);
                    result.put("playUrl", "");
                    result.put("url", data);
                    result.put("header", new JSONObject(playHeader).toString());
                    return result.toString();

                }
            }
            //https://kk.bt5v.com:1234/?url=RongXingVR-9032693552931
            //view-source:https://rx.bt5v.com/json/jsonindex.php/?url=https://v.qq.com/x/cover/mzc00200et8b4j7/z0033yhqoea.html
            String html = OkHttp.string(id, playHeader);
            if (html != null && html.contains("{") && html.contains("}")) {
                JSONObject player_ = new JSONObject(html);
                //code 200  "type":"m3u8"  "msg":"获取成功"
                if (player_.get("url") != null && !player_.getString("url").isEmpty()) {
                    JSONObject result = new JSONObject();
                    result.put("parse", 0);
                    result.put("playUrl", "");
                    result.put("url", player_.getString("url"));
                    result.put("header", new JSONObject(playHeader).toString());
                    return result.toString();
                }
            }
            // {"code":"404","success":"-2001","msg":"解析失败或官方付费或视频链接错误或无版权或资源为空或您已欠费 RongXingVR"}
            //处理解析失败的情况
            if (html != null && html.contains("解析失败")) {
                int of_ = id.indexOf("/?url=");
                if (of_ != -1) {
                    id = id.substring(of_);
                }
            }
            if (id != null && !id.startsWith("http")) {
                return "";
            }

            //https://dmku.rongxingvr.cn:8866/dmku/?ac=dm&id=e786058185a91e089720ad649d1b3b1b


            JSONObject result = new JSONObject();
            result.put("parse", 1);
            result.put("jx", 1);
            result.put("url", id);
            return result.toString();

        } catch (Exception e) {
            SpiderDebug.log(e);

        }
        return "";
    }
    public static String parseFeiyun(String id) {

        if (id != null && id.startsWith("Feiyun")) {

            Map<String, String> playHeader = new HashMap<String, String>();
            playHeader.put("accept", "*/*");
            playHeader.put("accept-language", "zh-CN,zh;q=0.9,zh-TW;q=0.8");
            playHeader.put("dnt", "1");
            String[] parseApi = new String[]{
                    "http://47.242.89.48/video/xiaoshu.php?action=config&app=analysis2&code=799b12e369b33286efd115d441fc1a44&url=",
                    "http://htp.behds.cn/json/2023123456/fy4k2.php?code=799b12e369b33286efd115d441fc1a44&url=",

            };
            for (int i = 0; i < parseApi.length; i++) {
                try {

                    String page = OkHttp.string(parseApi[i] + id, playHeader);

                    if (!TextUtils.isEmpty(page)) {
                        String daina = XiaoGui.substr(page, "url\":\"", "\"");
                        if (!TextUtils.isEmpty(daina)) {
                            Connection con = Jsoup.connect(daina).method(Connection.Method.GET).followRedirects(false).ignoreContentType(true).ignoreHttpErrors(true);
                            Connection.Response res = XiaoGui.execute(con);
                            String loc = res.header("location");
                            if (loc != null) {
                                return loc;
                            }
                        }
                    }
                } catch (Exception ex) {

                }
            }

        }
        return null;
    }
    @Override
    public String searchContent(String key, boolean quick) {
        try {
            String url = siteUrl + apiUrl + "search?text=" + URLEncoder.encode(key, "UTF-8");
            String page=OkHttp.string(url, getHeaders(url));
            JSONObject pageObj=new JSONObject(page);
            JSONArray jSONArray =  null;
            if(pageObj.has("data")){
                jSONArray=pageObj.getJSONArray("data");
            }
            if(pageObj.has("list")){
                jSONArray=pageObj.getJSONArray("list");
            }
            JSONArray videos = new JSONArray();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                JSONObject v = new JSONObject();
                v.put("vod_id", jSONObject.opt("vod_id")+"");
                v.put("vod_name", jSONObject.getString("vod_name"));
                v.put("vod_pic", jSONObject.getString("vod_pic"));
                v.put("vod_remarks", jSONObject.getString("vod_remarks"));
                videos.put(v);
            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Throwable unused) {
            return "";
        }
    }

    public static String E() {
        return U(new Date());
    }

    public static String U(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String format = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String valueOf = String.valueOf(calendar.get(1));
        String valueOf2 = String.valueOf(calendar.get(11));
        String valueOf3 = String.valueOf(calendar.get(12));
        if (valueOf2.length() < 2) {
            valueOf2 = "0" + valueOf2;
        }
        if (valueOf3.length() < 2) {
            valueOf3 = "0" + valueOf3;
        }
        return ew(valueOf + ":" + valueOf2 + ":" + valueOf + ":" + valueOf3 + ":" + format);
    }

    public static String XW() {
        return String.valueOf(new Date().getTime() / 1000);
    }

    public static String ew(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes());
            byte[] digest = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer("");
            for (int i = 0; i < digest.length; i++) {
                int i2 = digest[i];
                if (i2 < 0) {
                    i2 += 256;
                }
                if (i2 < 16) {
                    stringBuffer.append("0");
                }
                stringBuffer.append(Integer.toHexString(i2));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static final int RETRY_ERROR_COUNT = 100;//大小不重要 主要是表达愤怒
    public static final int RETRY_SLEEP_TIME = 300;
    public static int CUSTOM_SLEEP_TIME = RETRY_SLEEP_TIME;
    public static int CUSTOM_RETRY_COUNT = RETRY_ERROR_COUNT;
    public static final int DEFAULT_TIME_OUT = 60000;//60 s
    public static int CUSTOM_TIME_OUT = DEFAULT_TIME_OUT;

    public static final String USER_AGENT_UC = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13E233 MicroMessenger/6.3.15 NetType/WIFI Language/zh_CN";

    public static final String USER_AGENT_PC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36";

    public static final Map<String,String> COOKIE=new HashMap<String,String>();

    public static void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (Exception ex) {
        }
    }
    public static Connection.Response execute(Connection request) {
        Connection.Response call = null;
//        try {
//            System.out.println(InetAddress.getByName("huoq.com").getHostAddress());
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
        for (int k = 0; k < CUSTOM_RETRY_COUNT; k++) {
            try {
                call = request.execute();
                break;
            } catch (InterruptedIOException ex) { //IO操作被意外终止
                System.err.println(ex);
                sleep(CUSTOM_SLEEP_TIME);
            } catch (IOException ex) {

                System.err.println(ex);
                if (ex instanceof SocketTimeoutException) {
                    sleep(CUSTOM_SLEEP_TIME);
                }
                if (ex instanceof java.net.ConnectException) {
                    sleep(CUSTOM_SLEEP_TIME);
                }
                if (ex instanceof java.net.UnknownHostException) {
                    // DnsCacheManipulator.setDnsCache("www.biquge5200.com", "8.210.3.16");
                    java.security.Security.setProperty("networkaddress.cache.ttl", "5");
                    //DnsCacheManipulator.setDnsCache("server01.pac.itzmx.com", "23.99.96.150");
                    //  0表示禁止缓存，-1表示永远有效

                }
                if( ex instanceof  javax.net.ssl.SSLException){
                    //System.out.println("1111");
                }
                if (ex instanceof java.net.SocketException) {
                    sleep(CUSTOM_SLEEP_TIME);
                }
            }
        }
        return call;
    }
    public static void streamCopy(InputStream input, OutputStream output)
            throws IOException {
        // Is there really no built-in for this?

        try {
            final byte[] buffer = new byte[1024];
            int n;

            for (; ; ) {
                n = input.read(buffer, 0, buffer.length);
                if (n < 0)
                    break;
                output.write(buffer, 0, n);
            }

        } finally {
            input.close();
            output.close();
        }
    }

}
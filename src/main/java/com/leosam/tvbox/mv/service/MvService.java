package com.leosam.tvbox.mv.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leosam.tvbox.mv.data.MvContent;
import com.leosam.tvbox.mv.data.MvResult;
import com.leosam.tvbox.mv.data.Vod;
import com.leosam.tvbox.mv.data.VodClass;
import com.leosam.tvbox.mv.data.VodResult;
import com.leosam.tvbox.mv.lucene.MvIndex;
import com.leosam.tvbox.mv.lucene.MvSearcher;
import com.leosam.tvbox.mv.utils.ClassPathReaderUtils;
import com.leosam.tvbox.mv.utils.CollectionUtils;
import com.leosam.tvbox.mv.utils.StopWatch;
import com.leosam.tvbox.mv.utils.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author admin
 * @since 2023/6/10 17:35
 */
public class MvService {
    private static final Logger logger = LoggerFactory.getLogger(MvService.class);
    private static final String indexDirectoryPath = "index";
    private static final String MV_FILE = "tvbox/16wMV.txt";
    private static final Map<String, List<String>> homeConfigMap = new LinkedHashMap<>();
    private static final String HOME_KEY = "HOME";
    /**
     * 最低相似分数
     */
    private static final float MIN_QUERY_SCORE = 3.0F;

    private MvSearcher mvSearcher;

    public MvResult search(String query, int max) throws Exception {
        if (StringUtils.isEmpty(query) || mvSearcher == null) {
            return new MvResult();
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        TopDocs topDocs = mvSearcher.searchTopDocs("name", query, max);

        MvResult result = new MvResult();
        result.setQuery(query);
        result.setTotalHits(topDocs.totalHits.value);
        result.setList(new ArrayList<>(topDocs.scoreDocs.length));
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            // 分数太低 忽略
            if(scoreDoc.score < MIN_QUERY_SCORE){
                break;
            }
            Document document = mvSearcher.getDocument(scoreDoc.doc);
            if (document == null) {
                continue;
            }

            String name = document.get("name");
            String songName = name;
            String songUser = null;
            if (name.contains("-")) {
                String[] split = name.split("-", 2);
                songUser = split[0];
                songName = split[1];
            }
            String url = document.get("url");
            if (!url.startsWith("http")) {
                url = "http://em.21dtv.com/songs/" + url + ".mkv";
            }
            MvContent content = new MvContent();
            content.setName(name);
            content.setSongName(songName);
            content.setSongUser(songUser);
            content.setUrl(url);
            content.setScore(scoreDoc.score);
            result.getList().add(content);
        }

        stopWatch.stop();
        logger.info("查询MV成功，query={}, 命中{}条, 返回={}条, 耗时{}毫秒", query, result.getTotalHits(), result.getList().size(), stopWatch.getTotalTimeMillis());
        return result;
    }

    public VodResult searchVod(String wd, int max) throws Exception {
        MvResult search = search(wd, max);
        if (CollectionUtils.isEmpty(search.getList())) {
            return new VodResult();
        }
        Vod vod = new Vod();
        vod.setVodId(wd);
        vod.setVodName(wd);
        vod.setVodPlayFrom("mv");
        vod.setVodPic("http://yanxuan.nosdn.127.net/b6fb987ce79f308949e44f5129a4b51c.jpeg");
        List<String> playUrlList = new LinkedList<>();
        Set<String> vodActorList = new LinkedHashSet<>();
        for (MvContent content : search.getList()) {
            playUrlList.add(content.getName() + "$" + content.getUrl());
            if (vodActorList.size() < 5 && StringUtils.isNotEmpty(content.getSongUser())) {
                vodActorList.add(content.getSongUser());
            }
        }
        vod.setVodPlayUrl(String.join("#", playUrlList));
        vod.setVodActor(String.join(",", vodActorList));

        VodResult vodResult = new VodResult();
        vodResult.init();
        vodResult.setList(new ArrayList<>()).getList().add(vod);
        // logger.debug("searchVod , wd{}, vodResult={}", wd, JsonUtils.writeValue(vodResult));
        return vodResult;
    }

    public VodResult searchVodHome(String type, int page) throws Exception {
        if (page > 1) {
            return new VodResult();
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        VodResult vodResult = new VodResult();
        vodResult.init();
        vodResult.setList(new ArrayList<>()).getList();

        // 分类
        if (StringUtils.isEmpty(type)) {
            vodResult.setVodClassList(new ArrayList<>(homeConfigMap.size()));
            Set<String> keySet = homeConfigMap.keySet();
            for (String key : keySet) {
                if (HOME_KEY.equalsIgnoreCase(key)) {
                    continue;
                }
                VodClass vodClass = new VodClass().setTypeId(key).setTypeName(key);
                vodResult.getVodClassList().add(vodClass);
            }
        }

        // 首页mv推荐
        String thisType = StringUtils.isNotEmpty(type) ? type : HOME_KEY;
        List<String> geshouList = homeConfigMap.getOrDefault(thisType, new ArrayList<>());
        for (String geshou : geshouList) {
            Vod vod = new Vod();
            vod.setVodId(geshou);
            vod.setVodName(geshou);
            vod.setVodPlayFrom("mv");
            vod.setVodPic("http://yanxuan.nosdn.127.net/b6fb987ce79f308949e44f5129a4b51c.jpeg");
            vodResult.getList().add(vod);
        }

        stopWatch.stop();
        logger.info("加载首页成功，类型={}, 返回={}条, 耗时{}毫秒", thisType, vodResult.getList().size(), stopWatch.getTotalTimeMillis());
        return vodResult;
    }

    public void initIndex() throws Exception {
        // 加载首页配置
        buildHomeConfig();

        // 重建索引
        String indexAbsolutePath = new File(indexDirectoryPath).getAbsoluteFile().getAbsolutePath();
        reBuildIndex(indexAbsolutePath);

        // 加载索引
        mvSearcher = new MvSearcher(indexAbsolutePath);

        // 搜索一下，看看是否加载成功
        search("五月天", 10);
    }

    private void buildHomeConfig() {
        try {
            String content = ClassPathReaderUtils.getContent("tvbox/home.json");
            Map<String, List<String>> map = new ObjectMapper().readValue(content, new TypeReference<HashMap<String, List<String>>>() {
            });
            homeConfigMap.putAll(map);
            logger.info("加载首页配置成功");
        } catch (Exception e) {
            logger.error("buildHomeConfig error", e);
        }
    }

    private static void reBuildIndex(String indexDirectoryPath) throws IOException {
        File file = new File(indexDirectoryPath).getAbsoluteFile();
        int mvFileSize = ClassPathReaderUtils.getSize(MV_FILE);
        File mvFileSizeTxt = Path.of(file.getAbsolutePath(), "" + mvFileSize + ".txt").toFile();
        if (mvFileSizeTxt.exists()) {
            logger.info("{}已经索引, 跳过索引", MV_FILE);
            return;
        }


        StopWatch stopWatch = new StopWatch();
        logger.info("重建索引中....");

        // 清空以前的索引
        stopWatch.start("清空以前索引");
        logger.info("清空历史索引....");

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                if (file1.isFile()) {
                    file1.delete();
                }
            }
        }
        logger.info("清空历史索引....完成");
        stopWatch.stop();

        stopWatch.start("创建索引");
        logger.info("创建索引中....");
        AtomicInteger line = new AtomicInteger();
        MvIndex mvIndex = new MvIndex(indexDirectoryPath);
        ClassPathReaderUtils.getBufferedReader(MV_FILE).lines()
                .filter(l -> l.contains(",h"))
                .filter(l -> l.contains("-"))
                .forEach(l -> {
                    String[] split = l.split(",", 2);
                    if (split.length != 2) {
                        return;
                    }
                    String name = split[0].trim();
                    String url = split[1].trim();
                    String shortUrl = url;
                    if (shortUrl.startsWith("http://em.21dtv")) {
                        shortUrl = shortUrl
                                .replace("http://em.21dtv.com/songs/", "")
                                .replace(".mkv", "");
                    }
                    try {
                        Document document = new Document();
                        document.add(new TextField("name", name, Field.Store.YES));
                        document.add(new StringField("url", shortUrl, Field.Store.YES));
                        mvIndex.indexFile(document);
                        int i = line.incrementAndGet();
                        if (i % 10000 == 0) {
                            logger.info("创建索引中....已完成{}万条索引", i / 10000);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        mvIndex.close();
        mvFileSizeTxt.createNewFile();
        logger.info("创建索引中....完成, 总共{}条", line.get());
        stopWatch.stop();
        logger.info("重建索引中....完成,耗时 {} 毫秒, 索引位置：{}", stopWatch.getTotalTimeMillis(), file.getPath());
    }


}

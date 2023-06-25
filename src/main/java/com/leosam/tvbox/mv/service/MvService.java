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
    public static final String MV_FILE_NAME = "16wMV";
    private static final String indexDirectoryPath = "index";
    private static final Map<String, List<String>> homeConfigMap = new LinkedHashMap<>();
    private static final Map<String, String> singerMap = new LinkedHashMap<>();
    private static final String HOME_KEY = "HOME";
    private static final List<String> FILE_PATH_LIST = List.of(
            "classpath:tvbox/16wMV.txt",
            "./data/",
            "../data/"
    );
    /**
     * 最低相似分数
     */
    private static final float MIN_QUERY_SCORE = 3.0F;

    private MvSearcher mvSearcher;

    public MvResult search(String index, String query, int max) throws Exception {
        if (mvSearcher == null || max > 10000) {
            return new MvResult();
        }
        if (StringUtils.isEmpty(index) && StringUtils.isEmpty(query)) {
            return new MvResult();
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        TopDocs topDocs = mvSearcher.searchTopDocs("name", query, "index", index, max);

        MvResult result = new MvResult();
        result.setQuery(query);
        result.setTotalHits(topDocs.totalHits.value);
        result.setList(new ArrayList<>(topDocs.scoreDocs.length));
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            // 分数太低 忽略
            if (scoreDoc.score < MIN_QUERY_SCORE && result.getList().size() > 50) {
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

    public VodResult searchVod(String index, String wd, int max) throws Exception {
        MvResult search = this.search(index, wd, max);
        if (CollectionUtils.isEmpty(search.getList())) {
            return new VodResult();
        }
        Vod vod = new Vod();
        vod.setVodId(wd);
        vod.setVodName(wd);
        vod.setVodPlayFrom(StringUtils.defaultIfEmpty(index, "mv"));
        vod.setVodPic(getVodPic(index, wd));
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
        return vodResult;
    }

    public VodResult searchVodHome(String index, String type, int page) throws Exception {
        // 防止分页请求
        if (page > 1) {
            return new VodResult();
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        VodResult vodResult = new VodResult().init();

        if (MV_FILE_NAME.equals(index)) {
            // 分类
            if (StringUtils.isEmpty(type)) {
                vodResult.setVodClassList(new ArrayList<>(homeConfigMap.size()));
                Set<String> keySet = homeConfigMap.keySet();
                for (String key : keySet) {
                    VodClass vodClass = new VodClass().setTypeId(key).setTypeName(key);
                    if (HOME_KEY.equalsIgnoreCase(key)) {
                        vodClass.setTypeName("热门");
                    }
                    vodResult.getVodClassList().add(vodClass);
                }
            }

            // 首页mv推荐
            String thisType = StringUtils.isNotEmpty(type) ? type : HOME_KEY;
            List<String> singerList = homeConfigMap.getOrDefault(thisType, new ArrayList<>());
            vodResult.setList(new ArrayList<>(singerList.size()));
            for (String singer : singerList) {
                Vod vod = new Vod();
                vod.setVodId(singer);
                vod.setVodName(singer);
                vod.setVodPlayFrom(StringUtils.defaultIfEmpty(index, "mv"));
                vod.setVodPic(getVodPic(index, singer));
                vodResult.getList().add(vod);
            }
            stopWatch.stop();
            logger.info("加载首页成功，类型={}, 返回={}条, 耗时{}毫秒", thisType, vodResult.getList().size(), stopWatch.getTotalTimeMillis());
        } else {
            vodResult.setList(new ArrayList<>());
            MvResult search = search(index, "", 100);
            if (CollectionUtils.isNotEmpty(search.getList())) {
                for (MvContent content : search.getList()) {
                    Vod vod = new Vod();
                    vod.setVodId(content.getName());
                    vod.setVodName(content.getName());
                    vod.setVodPlayFrom(StringUtils.defaultIfEmpty(index, "mv"));
                    vod.setVodPic(getVodPic(index, content.getName()));
                    vodResult.getList().add(vod);
                }
            }
            logger.info("加载首页成功，文件={}, 返回={}条, 耗时{}毫秒", index, vodResult.getList().size(), stopWatch.getTotalTimeMillis());
        }
        return vodResult;
    }

    private String getVodPic(String index, String vodName) {
        String vodPic = null;
        if (StringUtils.isNotEmpty(vodName)) {
            if (singerMap.containsKey(vodName)) {
                vodPic = singerMap.get(vodName);
            } else if (vodName.contains(",")) {
                String[] split = vodName.split(",", 2);
                if (singerMap.containsKey(split[0])) {
                    vodPic = singerMap.get(split[0]);
                }
            }
        }
        if (StringUtils.isEmpty(vodPic) && StringUtils.isNotEmpty(index)) {
            vodPic = singerMap.get(index);
        }
        vodPic = StringUtils.defaultIfEmpty(vodPic, "http://yanxuan.nosdn.127.net/b6fb987ce79f308949e44f5129a4b51c.jpeg");
        return vodPic;
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
        search(MV_FILE_NAME, "五月天", 10);
    }

    private void buildHomeConfig() {
        // 首页配置
        try {
            String content = ClassPathReaderUtils.getContent("config/home.json");
            Map<String, List<String>> map = new ObjectMapper().readValue(content, new TypeReference<LinkedHashMap<String, List<String>>>() {
            });
            homeConfigMap.putAll(map);
            logger.info("加载首页配置成功");
        } catch (Exception e) {
            logger.error("buildHomeConfig error", e);
        }
        // 歌手详情配置
        try {
            String content = ClassPathReaderUtils.getContent("config/singerPic.json");
            Map<String, String> map = new ObjectMapper().readValue(content, new TypeReference<LinkedHashMap<String, String>>() {
            });
            singerMap.putAll(map);
            logger.info("加载歌手头像成功");
        } catch (Exception e) {
            logger.error("buildHomeConfig error", e);
        }

    }

    private static void reBuildIndex(String indexDirectoryPath) throws IOException {
        Set<String> dataFilePath = getDataFilePath();
        if (dataFilePath.isEmpty()) {
            return;
        }
        File file = new File(indexDirectoryPath).getAbsoluteFile();
        long dataFileSize = ClassPathReaderUtils.getAllFileSize(dataFilePath);
        File mvFileSizeTxt = Path.of(file.getAbsolutePath(), "" + dataFileSize + ".txt").toFile();
        if (mvFileSizeTxt.exists()) {
            logger.info("所有数据都已经索引, 跳过索引");
            return;
        }

        StopWatch stopWatch = new StopWatch();
        logger.info("重建索引中....");

        // 清空以前的索引
        stopWatch.start("清空以前索引");
        logger.info("清空历史索引....");
        ClassPathReaderUtils.deleteAllFile(file);
        logger.info("清空历史索引....完成");
        stopWatch.stop();

        // 创建索引
        stopWatch.start("创建索引");
        logger.info("创建索引中....");
        MvIndex mvIndex = new MvIndex(indexDirectoryPath);
        for (String path : dataFilePath) {
            buildIndex(mvIndex, path);
        }
        mvIndex.close();
        mvFileSizeTxt.createNewFile();
        stopWatch.stop();
        logger.info("重建索引中....完成,耗时 {} 毫秒, 索引位置：{}", stopWatch.getTotalTimeMillis(), file.getPath());
    }

    private static Set<String> getDataFilePath() throws IOException {
        Map<String, String> pathMap = new LinkedHashMap<>();
        for (String filePath : FILE_PATH_LIST) {
            if (filePath.startsWith(ClassPathReaderUtils.CLASS_PATH_PREFIX)) {
                pathMap.put(filePath, new File(filePath).getName());
                continue;
            }
            Map<String, String> directoryFiles = ClassPathReaderUtils.getDirectoryFiles(filePath);
            if (directoryFiles.isEmpty()) {
                continue;
            }
            pathMap.putAll(directoryFiles);
        }

        logger.info("↓↓↓↓↓ 搜集数据文件目录 ↓↓↓↓↓↓");
        for (Map.Entry<String, String> entry : pathMap.entrySet()) {
            logger.info("*** 数据文件：{}", entry.getValue());
        }
        logger.info("↑↑↑↑↑ 搜集数据文件目录 ↑↑↑↑↑↑");
        return new LinkedHashSet<>(pathMap.keySet());
    }

    private static void buildIndex(MvIndex mvIndex, String path) {
        String fileName = ClassPathReaderUtils.extractFileNameWithoutExtension(path);
        AtomicInteger line = new AtomicInteger();
        ClassPathReaderUtils.getBufferedReader(path).lines()
                .filter(l -> l.contains(",http"))
                .forEach(l -> {
                    List<String> split = StringUtils.reverseSplit(l, ",http", 2);
                    if (split.size() != 2) {
                        return;
                    }
                    String name = split.get(0).trim();
                    String url = "http" + split.get(1).trim();
                    String shortUrl = url;
                    if (shortUrl.startsWith("http://em.21dtv")) {
                        shortUrl = shortUrl
                                .replace("http://em.21dtv.com/songs/", "")
                                .replace(".mkv", "");
                    }
                    try {
                        Document document = new Document();
                        document.add(new TextField("name", name, Field.Store.YES));
                        document.add(new StringField("index", fileName, Field.Store.YES));
                        document.add(new StringField("url", shortUrl, Field.Store.YES));
                        mvIndex.indexFile(document);
                        int i = line.incrementAndGet();
                        if (i % 10000 == 0) {
                            logger.info("创建{}索引中....已完成{}万条索引", fileName, i / 10000);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        logger.info("创建索引中....完成, 源文件:{}, 总共{}条", new File(path).getName(), line.get());
    }


}

package com.leosam.tvbox.mv.service;

import com.leosam.tvbox.mv.data.MvContent;
import com.leosam.tvbox.mv.data.MvResult;
import com.leosam.tvbox.mv.lucene.MvIndex;
import com.leosam.tvbox.mv.lucene.MvSearcher;
import com.leosam.tvbox.mv.utils.ClassPathReaderUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author admin
 * @since 2023/6/10 17:35
 */
@Service
public class MvService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MvService.class);

    private static final String indexDirectoryPath = "index";

    private MvSearcher mvSearcher;

    public MvResult search(String query, int max) throws Exception {
        if (!StringUtils.hasText(query)) {
            return new MvResult();
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        TopDocs topDocs = mvSearcher.searchTopDocs("name", query, max);

        MvResult result = new MvResult();
        result.setQuery(query);
        result.setTotalHits(topDocs.totalHits.value);
        result.setList(new LinkedList<>());
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = mvSearcher.getDocument(scoreDoc.doc);
            if (document == null) {
                continue;
            }

            String name = document.get("name");
            String songName = name;
            String songUser = null;
            if (name.contains("-")) {
                songUser = name.split("-", 2)[0];
                songName = name.split("-", 2)[1];
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

    @Override
    public void afterPropertiesSet() throws Exception {
        // 重建索引
        String indexAbsolutePath = new File(indexDirectoryPath).getAbsoluteFile().getAbsolutePath();
        reBuildIndex(indexAbsolutePath);

        // 加载索引
        mvSearcher = new MvSearcher(indexAbsolutePath);

        // 搜索一下，看看是否加载成功
        search("五月天", 10);
    }


    private static void reBuildIndex(String indexDirectoryPath) throws IOException {
        StopWatch stopWatch = new StopWatch();
        logger.info("重建索引中....");

        // 清空以前的索引
        stopWatch.start("清空以前索引");
        logger.info("清空历史索引....");
        File file = new File(indexDirectoryPath).getAbsoluteFile();
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
        ClassPathReaderUtils.getBufferedReader("tvbox/16wMV.txt").lines()
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
        logger.info("创建索引中....完成, 总共{}条", line.get());
        stopWatch.stop();
        logger.info("重建索引中....完成,耗时 {} 毫秒, 索引位置：{}", stopWatch.getTotalTimeMillis(), file.getPath());
    }
}

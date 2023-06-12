package com.leosam.tvbox.mv.lucene;

import io.vertx.ext.web.impl.LRUCache;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author admin
 * @since 2023/6/8 21:44
 */
public class MvSearcher {
    private IndexReader reader;
    private IndexSearcher searcher;
    private StoredFields storedFields;
    private final LRUCache<Integer, Document> documentLruCache = new LRUCache<>(1000);
    private final LRUCache<String, TopDocs> topDocsLruCache = new LRUCache<>(100);

    public MvSearcher(String indexDirectoryPath) throws IOException {
        Path indexPath = Paths.get(indexDirectoryPath);
        Directory directory = FSDirectory.open(indexPath);
        reader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(reader);
        storedFields = reader.storedFields();
    }

    public TopDocs searchTopDocs(String field, String queryStr, int maxHit) throws Exception {
        String cacheKey = field + "_" + queryStr + "_" + maxHit;
        TopDocs topDocs = topDocsLruCache.get(cacheKey);
        if (topDocs != null) {
            return topDocs;
        }

        Analyzer analyzer = new SmartChineseAnalyzer();
        // Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(queryStr);
        TopDocs docs = searcher.search(query, maxHit);

        topDocsLruCache.put(cacheKey, docs);
        return docs;
    }

    public void search(String field, String queryStr) throws Exception {
        Analyzer analyzer = new SmartChineseAnalyzer();
        // Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(queryStr);
        TopDocs docs = searcher.search(query, 200);

        // 处理搜索结果
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document document = getDocument(scoreDoc.doc);
            System.out.println(document.get("name") + "," + document.get("url") + " score=" + scoreDoc.score);
        }
    }

    public Document getDocument(int docId) throws IOException {
        Document document = documentLruCache.get(docId);
        if (document != null) {
            return document;
        }
        document = storedFields.document(docId);
        if (document != null) {
            documentLruCache.put(docId, document);
        }
        return document;
    }


    public void close() throws IOException {
        reader.close();
    }

}
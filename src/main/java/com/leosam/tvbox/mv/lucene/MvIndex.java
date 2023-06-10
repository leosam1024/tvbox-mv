package com.leosam.tvbox.mv.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MvIndex {
    private IndexWriter writer;

    public MvIndex(String indexDirectoryPath) throws IOException {
        // 使用标准分析器
        Analyzer analyzer = new SmartChineseAnalyzer();
        // Analyzer analyzer = new StandardAnalyzer();

        // 创建索引配置
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        // 打开索引目录
        Path indexPath = Paths.get(indexDirectoryPath);
        Directory indexDirectory = FSDirectory.open(indexPath);

        // 创建索引写入器
        writer = new IndexWriter(indexDirectory, config);
    }

    public void close() throws IOException {
        writer.commit();
        writer.close();
    }

    public void indexFile(Document document) throws IOException {
        writer.addDocument(document);
    }

    public void indexFile(String name, String url) throws IOException {
        Document document = new Document();
        document.add(new TextField("name", name, Field.Store.YES));
        document.add(new StringField("url", url, Field.Store.YES));
        writer.addDocument(document);
    }

}
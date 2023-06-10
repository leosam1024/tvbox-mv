package com.leosam.tvbox.mv.data;

import java.util.List;

/**
 * @author admin
 * @since 2023/6/10 19:20
 */
public class MvResult {

    private String query;
    private long totalHits;
    private List<MvContent> list;

    public String getQuery() {
        return query;
    }

    public MvResult setQuery(String query) {
        this.query = query;
        return this;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public MvResult setTotalHits(long totalHits) {
        this.totalHits = totalHits;
        return this;
    }

    public List<MvContent> getList() {
        return list;
    }

    public MvResult setList(List<MvContent> list) {
        this.list = list;
        return this;
    }
}

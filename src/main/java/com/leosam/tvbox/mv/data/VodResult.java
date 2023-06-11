package com.leosam.tvbox.mv.data;

import java.util.List;

/**
 * @author admin
 * @since 2023/6/11 18:57
 */
public class VodResult {

    private int code;
    private int page;
    private int pagecount;
    private int limit;
    private int total;
    private List<Vod> list;

    public void init() {
        this.code = 1;
        this.page = 1;
        this.pagecount = 1;
        this.limit = 1;
        this.total = 1;
    }

    public int getCode() {
        return code;
    }

    public VodResult setCode(int code) {
        this.code = code;
        return this;
    }

    public int getPage() {
        return page;
    }

    public VodResult setPage(int page) {
        this.page = page;
        return this;
    }

    public int getPagecount() {
        return pagecount;
    }

    public VodResult setPagecount(int pagecount) {
        this.pagecount = pagecount;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public VodResult setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public VodResult setTotal(int total) {
        this.total = total;
        return this;
    }

    public List<Vod> getList() {
        return list;
    }

    public VodResult setList(List<Vod> list) {
        this.list = list;
        return this;
    }
}

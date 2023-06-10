package com.leosam.tvbox.mv.data;

/**
 * @author admin
 * @since 2023/6/10 19:22
 */
public class MvContent {
    private String name;
    private String songName;
    private String songUser;
    private String url;

    public float getScore() {
        return score;
    }

    public MvContent setScore(float score) {
        this.score = score;
        return this;
    }

    private float score;

    public String getName() {
        return name;
    }

    public MvContent setName(String name) {
        this.name = name;
        return this;
    }

    public String getSongName() {
        return songName;
    }

    public MvContent setSongName(String songName) {
        this.songName = songName;
        return this;
    }

    public String getSongUser() {
        return songUser;
    }

    public MvContent setSongUser(String songUser) {
        this.songUser = songUser;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public MvContent setUrl(String url) {
        this.url = url;
        return this;
    }
}

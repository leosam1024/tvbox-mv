package com.leosam.tvbox.mv.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author admin
 * @since 2023/6/11 18:58
 */
public class Vod {

    @JsonProperty("vod_id")
    private String vodId;

    @JsonProperty("vod_name")
    private String vodName;

    @JsonProperty("vod_actor")
    private String vodActor;

    @JsonProperty("vod_play_from")
    private String vodPlayFrom;

    @JsonProperty("vod_pic")
    private String vodPic;

    @JsonProperty("vod_play_url")
    private String vodPlayUrl;

    public String getVodId() {
        return vodId;
    }

    public Vod setVodId(String vodId) {
        this.vodId = vodId;
        return this;
    }

    public String getVodName() {
        return vodName;
    }

    public Vod setVodName(String vodName) {
        this.vodName = vodName;
        return this;
    }

    public String getVodActor() {
        return vodActor;
    }

    public Vod setVodActor(String vodActor) {
        this.vodActor = vodActor;
        return this;
    }

    public String getVodPlayFrom() {
        return vodPlayFrom;
    }

    public Vod setVodPlayFrom(String vodPlayFrom) {
        this.vodPlayFrom = vodPlayFrom;
        return this;
    }

    public String getVodPic() {
        return vodPic;
    }

    public Vod setVodPic(String vodPic) {
        this.vodPic = vodPic;
        return this;
    }

    public String getVodPlayUrl() {
        return vodPlayUrl;
    }

    public Vod setVodPlayUrl(String vodPlayUrl) {
        this.vodPlayUrl = vodPlayUrl;
        return this;
    }

    @Override
    public String toString() {
        return "Vod{" +
                "vodId='" + vodId + '\'' +
                ", vodName='" + vodName + '\'' +
                ", vodActor='" + vodActor + '\'' +
                ", vodPlayFrom='" + vodPlayFrom + '\'' +
                ", vodPic='" + vodPic + '\'' +
                ", vodPlayUrl='" + vodPlayUrl + '\'' +
                '}';
    }
}

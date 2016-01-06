package com.example.larry.myapplication.entity;

import java.util.Date;

/**
 * Created by Larry on 2016/1/5.
 */
public class Classify {

    private long id;
    private long parent_id;
    private int level;
    private String title;
    private String img_url;
    private String img_path;
    private String end;
    private String create_tm;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getCreate_tm() {
        return create_tm;
    }

    public void setCreate_tm(String create_tm) {
        this.create_tm = create_tm;
    }
}

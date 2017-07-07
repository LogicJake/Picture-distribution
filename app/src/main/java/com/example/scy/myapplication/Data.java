package com.example.scy.myapplication;


/**
 * Created by shichenyang on 2017/6/1/0001.
 */

public class Data {

    private String id;
    private String stime;//private String proj_id;
    //private String title;
    private String tags;//private String desc;
    private String url;
    private int status;

    public Data(String id, String stime, String tags, String url,int status) {
        this.id = id;
        this.stime = stime;
        this.tags = tags;
        this.url = url;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getstime() {
        return stime;
    }

    public void setstime(String stime) {
        this.stime = stime;
    }

    public String gettags() {
        return tags;
    }

    public void settags(String tags) {
        this.tags = tags;
    }

    public String geturl() {
        return url;
    }

    public void seturl(String url) {
        this.url = url;
    }

    public int getStatus(){ return status;}

    public void setStatus(int status){this.status = status;}

}


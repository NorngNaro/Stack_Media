package com.naro.newsocial.Model;

public class LoveModel {

    private String lover;
    private String date;

    public LoveModel(String lover, String date) {
        this.lover = lover;
        this.date = date;
    }

    public String getLover() {
        return lover;
    }

    public void setLover(String lover) {
        this.lover = lover;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

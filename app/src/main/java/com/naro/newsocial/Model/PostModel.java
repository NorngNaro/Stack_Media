package com.naro.newsocial.Model;

public class PostModel {

    private String title;
    private String description;
    private String url;
    private String userID;
    private String date;
    private int view;



    public PostModel(String title, String description, String url, String userID, String date, int view) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.userID = userID;
        this.date = date;
        this.view = view;
    }

    public PostModel(){

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }


}


package com.naro.newsocial.Model;

public class PostModel {

    private String title;
    private String description;
    private String url;
    private String userID;
    private String date;
    private int view;
    private int love;
    private int comment;

    public PostModel(String title, String description, String url, String userID, String date, int view, int love, int comment) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.userID = userID;
        this.date = date;
        this.view = view;
        this.love = love;
        this.comment = comment;
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

    public int getLove() {
        return love;
    }

    public void setLove(int love) {
        this.love = love;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }
}


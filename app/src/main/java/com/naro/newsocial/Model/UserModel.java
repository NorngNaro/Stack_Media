package com.naro.newsocial.Model;

public class UserModel {
    private String userName;
    private String email;
    private String phone;
    private String imageUrl;
    private String userID;
    private String bio;

    public UserModel(String userName, String email, String phone, String imageUrl, String userID, String bio) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.userID = userID;
        this.bio = bio;
    }

    public UserModel(){

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}

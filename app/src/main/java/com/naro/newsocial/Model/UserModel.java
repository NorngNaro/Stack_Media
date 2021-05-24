package com.naro.newsocial.Model;

public class UserModel {
    private String userName;
    private String email;
    private String phone;
    private String imageUrl;
    private String userID;
    private String bio;
    private String password;
    private int post;
    private int following;
    private int follower;

    public UserModel(String userName, String email, String phone, String imageUrl, String userID, String bio, String password, int post, int following, int follower) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.userID = userID;
        this.bio = bio;
        this.password = password;
        this.post = post;
        this.following = following;
        this.follower = follower;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }
}

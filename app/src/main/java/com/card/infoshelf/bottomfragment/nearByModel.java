package com.card.infoshelf.bottomfragment;

public class nearByModel {

    String cover_pic, profile_image, userName, userId;
    double latitude,longitude;

    public nearByModel() {
    }

    public nearByModel(String cover_pic, String profile_image, String userName, String userId, double latitude, double longitude) {
        this.cover_pic = cover_pic;
        this.profile_image = profile_image;
        this.userName = userName;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCover_pic() {
        return cover_pic;
    }

    public void setCover_pic(String cover_pic) {
        this.cover_pic = cover_pic;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

package com.card.infoshelf.bottomfragment;

public class TagModel {
    private String userName,profile_image,userId;
    private boolean hold;
    private boolean checker;

    public TagModel() {
    }

    public TagModel(String userName, String profile_image, String userId, boolean hold, boolean checker) {
        this.userName = userName;
        this.profile_image = profile_image;
        this.userId = userId;
        this.hold = hold;
        this.checker = checker;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isHold() {
        return hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }

    public boolean isChecker() {
        return checker;
    }

    public void setChecker(boolean checker) {
        this.checker = checker;
    }
}

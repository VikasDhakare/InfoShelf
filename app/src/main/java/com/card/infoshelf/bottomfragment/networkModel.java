package com.card.infoshelf.bottomfragment;

public class networkModel {

    String profession, userId, cover_pic, profile_image, userName, school_name, college_name, course, job_role, message, time, status;

    public networkModel() {
    }


    public networkModel(String profession, String userId, String cover_pic, String profile_image, String userName, String school_name, String college_name, String course, String job_role, String message, String time, String status) {


        this.profession = profession;
        this.userId = userId;
        this.cover_pic = cover_pic;
        this.profile_image = profile_image;
        this.userName = userName;
        this.school_name = school_name;
        this.college_name = college_name;
        this.course = course;
        this.job_role = job_role;
        this.message = message;
        this.time = time;
        this.status = status;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getCollege_name() {
        return college_name;
    }

    public void setCollege_name(String college_name) {
        this.college_name = college_name;
    }

    public String getJob_role() {
        return job_role;
    }

    public void setJob_role(String job_role) {
        this.job_role = job_role;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.card.infoshelf;

public class modelComment {

    String cId, comment, timestamp, uId, comment_like, post_id;

    public modelComment() {
    }

    public modelComment(String cId, String comment, String timestamp, String uId, String comment_like, String post_id) {
        this.cId = cId;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uId = uId;
        this.comment_like = comment_like;
        this.post_id = post_id;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getComment_like() {
        return comment_like;
    }

    public void setComment_like(String comment_like) {
        this.comment_like = comment_like;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}

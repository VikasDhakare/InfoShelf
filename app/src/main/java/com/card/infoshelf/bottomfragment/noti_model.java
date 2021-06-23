package com.card.infoshelf.bottomfragment;

public class noti_model {
    String notification, pId, pUid, sUid, status, timestamp;

    public noti_model() {
    }

    public noti_model(String notification, String pId, String pUid, String sUid, String status, String timestamp) {
        this.notification = notification;
        this.pId = pId;
        this.pUid = pUid;
        this.sUid = sUid;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpUid() {
        return pUid;
    }

    public void setpUid(String pUid) {
        this.pUid = pUid;
    }

    public String getsUid() {
        return sUid;
    }

    public void setsUid(String sUid) {
        this.sUid = sUid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

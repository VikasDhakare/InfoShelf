package com.card.infoshelf.bottomfragment;

public class timeLine_model {

    private String CompanyTo, FileName,  FileType, JobProfile,  PostURL, RelatedTo, TextBoxData, UserId, ctcType, ctcValue,  timeStamp, userProfile, userName;

    public timeLine_model() {
    }

    public timeLine_model(String companyTo, String fileName, String fileType, String jobProfile, String postURL, String relatedTo, String textBoxData, String userId, String ctcType, String ctcValue, String timeStamp, String userProfile, String userName) {
        CompanyTo = companyTo;
        FileName = fileName;
        FileType = fileType;
        JobProfile = jobProfile;
        PostURL = postURL;
        RelatedTo = relatedTo;
        TextBoxData = textBoxData;
        UserId = userId;
        this.ctcType = ctcType;
        this.ctcValue = ctcValue;
        this.timeStamp = timeStamp;
        this.userProfile = userProfile;
        this.userName = userName;
    }

    public String getCompanyTo() {
        return CompanyTo;
    }

    public void setCompanyTo(String companyTo) {
        CompanyTo = companyTo;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    public String getPostURL() {
        return PostURL;
    }

    public void setPostURL(String postURL) {
        PostURL = postURL;
    }

    public String getRelatedTo() {
        return RelatedTo;
    }

    public void setRelatedTo(String relatedTo) {
        RelatedTo = relatedTo;
    }

    public String getTextBoxData() {
        return TextBoxData;
    }

    public void setTextBoxData(String textBoxData) {
        TextBoxData = textBoxData;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJobProfile() {
        return JobProfile;
    }

    public void setJobProfile(String jobProfile) {
        JobProfile = jobProfile;
    }

    public String getCtcType() {
        return ctcType;
    }

    public void setCtcType(String ctcType) {
        this.ctcType = ctcType;
    }

    public String getCtcValue() {
        return ctcValue;
    }

    public void setCtcValue(String ctcValue) {
        this.ctcValue = ctcValue;
    }
}

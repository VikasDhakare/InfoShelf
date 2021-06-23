package com.card.infoshelf.profileFragments;

public class GridModel {
    private String CompanyTo,FileName,FileType,JobProfile,PostURL,RelatedTo,TextBoxData,UserId,ctcType,ctcValue,timeStamp;

    public GridModel() {
    }


    public GridModel(String companyTo, String fileName, String fileType, String jobProfile, String postURL, String relatedTo, String textBoxData, String userId, String ctcType, String ctcValue, String timeStamp) {
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

    public String getJobProfile() {
        return JobProfile;
    }

    public void setJobProfile(String jobProfile) {
        JobProfile = jobProfile;
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}

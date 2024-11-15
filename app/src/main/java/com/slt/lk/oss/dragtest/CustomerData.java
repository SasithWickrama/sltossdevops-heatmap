package com.slt.lk.oss.dragtest;

public class CustomerData {

    private String CusName;
    private String BBUsername;
    private String Floor;
    private String Comment;


    public CustomerData() {
    }

    public String getBBUsername() {
        return BBUsername;
    }

    public void setBBUsername(String BBUsername) {
        this.BBUsername = BBUsername;
    }

    public String getFloor() {
        return Floor;
    }

    public void setFloor(String floor) {
        Floor = floor;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getCusName() {
        return CusName;
    }

    public void setCusName(String cusName) {
        CusName = cusName;
    }
}

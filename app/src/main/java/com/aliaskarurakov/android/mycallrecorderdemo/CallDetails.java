package com.aliaskarurakov.android.mycallrecorderdemo;

import com.google.gson.annotations.SerializedName;

public class CallDetails {

    @SerializedName("name")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("path")
    private String path;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;

    public CallDetails() {
    }

    public CallDetails(String phone, String name, String time, String date, String path){

        this.phone = phone;
        this.name = name;
        this.path = path;
        this.time = time;
        this.date = date;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "CallDetails{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", path='" + path + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}

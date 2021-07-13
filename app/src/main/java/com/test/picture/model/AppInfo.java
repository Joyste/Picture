package com.test.picture.model;

import java.io.Serializable;

public class AppInfo implements Serializable {
    private String app_title;
    private String app_img;
    private int app_isNew;
    private String app_packet;

    public String getApp_title() {
        return app_title;
    }

    public void setApp_title(String app_title) {
        this.app_title = app_title;
    }

    public String getApp_img() {
        return app_img;
    }

    public void setApp_img(String app_img) {
        this.app_img = app_img;
    }

    public int getApp_isNew() {
        return app_isNew;
    }

    public void setApp_isNew(int app_isNew) {
        this.app_isNew = app_isNew;
    }

    public String getApp_packet() {
        return app_packet;
    }

    public void setApp_packet(String app_packet) {
        this.app_packet = app_packet;
    }

    @Override
    public String toString() {
        return "appInfo{" +
                "app_title=" + app_title +
                ", app_img='" + app_img + '\'' +
                ", app_isNew='" + app_isNew + '\'' +
                ", app_packet=" + app_packet +
                '}';
    }
}

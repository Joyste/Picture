package com.xinlan.imageeditlibrary.editimage.model;

public class appInfo {
   private int icon;
   private int name;
   private int value;


    public appInfo(int icon, int name, int value) {
        this.icon = icon;
        this.name = name;
        this.value = value;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

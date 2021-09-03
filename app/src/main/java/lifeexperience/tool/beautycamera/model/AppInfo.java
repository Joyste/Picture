package lifeexperience.tool.beautycamera.model;

import java.io.Serializable;

public class AppInfo implements Serializable {
    private String name;
    private String icoUrl;
    private int isComm;
    private int isNew;
    private String link;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcoUrl() {
        return icoUrl;
    }

    public void setIcoUrl(String icoUrl) {
        this.icoUrl = icoUrl;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getIsComm() {
        return isComm;
    }

    public void setIsComm(int isComm) {
        this.isComm = isComm;
    }

    @Override
    public String toString() {
        return "appInfo{" +
                "name=" + name +
                ", icoUrl='" + icoUrl + '\'' +
                ", isNew='" + isNew + '\'' +
                ", link=" + link +
                '}';
    }

}

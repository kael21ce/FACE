package org.techtown.face.models;

public class SearchItem {

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }


    public SearchItem(String path, String name, String mobile, String userId, String myId) {
        this.path = path;
        this.name = name;
        this.mobile = mobile;
        this.userId = userId;
        this.myId = myId;
    }

    String path;
    String name;
    String mobile;
    String userId;
    String myId;
}

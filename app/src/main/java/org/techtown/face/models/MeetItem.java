package org.techtown.face.models;

public class MeetItem {
    String path;
    String name;
    String mobile;
    String myId;
    String myName;
    String userId;
    String docId;

    public MeetItem(String path, String name, String mobile, String myId, String myName, String userId, String docId){
        this.path = path;
        this.name = name;
        this.mobile = mobile;
        this.myId = myId;
        this.myName = myName;
        this.userId = userId;
        this.docId =docId;
    }

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

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }


}

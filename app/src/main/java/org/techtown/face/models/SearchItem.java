package org.techtown.face.models;

import java.util.ArrayList;

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

    public ArrayList<String> getList1() { return list1;}

    public void setList1(ArrayList<String> list1) {
        this.list1 = list1;
    }

    public ArrayList<String> getList2() {
        return list2;
    }

    public void setList2(ArrayList<String> list2) {
        this.list2 = list2;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }


    public SearchItem(String path, String name, String mobile, String userId, String myId, ArrayList<String> list1, ArrayList<String> list2) {
        this.path = path;
        this.name = name;
        this.mobile = mobile;
        this.userId = userId;
        this.myId = myId;
        this.list1 = list1;
        this.list2 = list2;
    }

    String path;
    String name;
    String mobile;
    String userId;
    String myId;
    ArrayList<String> list1;
    ArrayList<String> list2;
}

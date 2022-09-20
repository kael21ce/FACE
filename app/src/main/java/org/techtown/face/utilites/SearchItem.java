package org.techtown.face.utilites;

public class SearchItem {

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getMobile(){
        return mobile;
    }

    public void setMobile(String mobile){
        this.mobile = mobile;
    }


    public SearchItem(String path, String name, String mobile){
        this.path = path;
        this.name = name;
        this.mobile = mobile;
    }

    String path;
    String name;
    String mobile;
}

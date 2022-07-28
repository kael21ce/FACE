package org.techtown.face;

import android.graphics.drawable.Drawable;

public class Family {
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

    public Drawable getFace() {
        return face;
    }

    public void setFace(Drawable face) {
        this.face = face;
    }

    public Family(String name, String mobile, Drawable face) {
        this.name = name;
        this.mobile = mobile;
        this.face = face;
    }

    String name;
    String mobile;
    Drawable face;
}

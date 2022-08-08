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

    public Integer getFace() {
        return face;
    }

    public void setFace(Integer face) {
        this.face = face;
    }

    public Family(String name, String mobile, Integer face) {
        this.name = name;
        this.mobile = mobile;
        this.face = face;
    }

    String name;
    String mobile;
    Integer face;

    public class FamilyScale {
        String name;
        String mobile;

        public String getScaleName() {
            return name;
        }

        public void setScaleName(String name) {
            this.name = name;
        }

        public String getScaleMobile() {
            return mobile;
        }

        public void setScaleMobile(String mobile) {
            this.mobile = mobile;
        }

        public FamilyScale(String name, String mobile, Integer face) {
            this.name = name;
            this.mobile = mobile;
        }
    }
}



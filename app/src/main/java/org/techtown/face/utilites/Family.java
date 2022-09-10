package org.techtown.face.utilites;

import android.graphics.drawable.Drawable;

import org.techtown.face.models.User;

public class Family {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public Integer getFace() {
        return face;
    }

    public void setFace(Integer face) {
        this.face = face;
    }

    public Integer getMinContact() {
        return min_contact;
    }

    public void setMinContact(Integer min_contact) {
        this.min_contact = min_contact;
    }

    public Integer getIdealContact() {
        return ideal_contact;
    }

    public void setIdealContact(Integer ideal_contact) {
        this.ideal_contact = ideal_contact;
    }

    public User getUserContact() {
        return user;
    }

    public void setUserContact(User user) {
        this.user = user;
    }

    public Family(String name, String phone_number, Integer face, Integer min_contact, Integer ideal_contact, User user) {
        this.name = name;
        this.phone_number = phone_number;
        this.face = face;
        this.min_contact = min_contact;
        this.ideal_contact = ideal_contact;
        this.user = user;
    }

    String name;
    String phone_number;
    Integer face;
    Integer min_contact;
    Integer ideal_contact;
    User user;

    public static class FamilyScale {
        String name;
        String phone_number;
        float angle;

        public FamilyScale(String name, String phone_number, float angle) {
            this.name = name;
            this.phone_number = phone_number;
            this.angle = angle;
        }

        public String getScaleName() {
            return name;
        }

        public void setScaleName(String name) {
            this.name = name;
        }

        public String getScalePhoneNumber() {
            return phone_number;
        }

        public void setScalePhoneNumber(String phone_number) {
            this.phone_number = phone_number;
        }

        public float getScaleAngle() {
            return angle;
        }

        public void setScaleAngle(float angle) {
            this.angle = angle;
        }
    }
}



package org.techtown.face.utilites;

import org.techtown.face.models.User;

public class Family {

    public User getUserContact() {
        return user;
    }

    public void setUserContact(User user) {
        this.user = user;
    }

    public Family(User user) {
        this.user = user;
    }

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



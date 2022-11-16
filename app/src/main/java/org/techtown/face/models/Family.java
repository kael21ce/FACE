package org.techtown.face.models;

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
        String mobile;
        String id;

        public FamilyScale(String name, String mobile, String id) {
            this.name = name;
            this.mobile = mobile;
            this.id = id;
        }

        public String getScaleName() {
            return name;
        }

        public void setScaleName(String name) {
            this.name = name;
        }

        public String getScaleMoible() {
            return mobile;
        }

        public void setScaleMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getScaleId() {
            return id;
        }

        public void setScaleId(String id) {
            this.id = id;
        }
        
    }
}



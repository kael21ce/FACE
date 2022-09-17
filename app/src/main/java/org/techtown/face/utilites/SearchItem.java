package org.techtown.face.utilites;

public class SearchItem {
        String userName;
        String userMobile;
        String userImage;

        public SearchItem(String userName, String userMobile, String userImage) {
            this.userName = userName;
            this.userMobile = userMobile;
            this.userImage = userImage;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserMobile(){
            return userMobile;
        }

        public void setUserMobile(String userMobile) {
            this.userMobile = userMobile;
        }

        public String getUserImage(){
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }
}

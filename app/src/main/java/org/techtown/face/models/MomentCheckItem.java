package org.techtown.face.models;

public class MomentCheckItem {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.dates = imageId;
    }

    public MomentCheckItem(String name, String images, String dates, String userId, String imageId) {
        this.name = name;
        this.images = images;
        this.dates = dates;
        this.userId = userId;
        this.imageId = imageId;
    }

    String userId;
    String imageId;
    String name;
    String images;
    String dates;
}

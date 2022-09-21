package org.techtown.face.models;

public class Moment {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String[] getDates() {
        return dates;
    }

    public void setDates(String[] dates) {
        this.dates = dates;
    }

    public Moment(String name, String[] images, String[] dates) {
        this.name = name;
        this.images = images;
        this.dates = dates;
    }

    String name;
    String[] images;
    String[] dates;
}

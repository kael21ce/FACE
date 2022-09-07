package org.techtown.face.utilites;

public class Moment {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getImages() {
        return images;
    }

    public void setImages(int[] images) {
        this.images = images;
    }

    public String[] getDates() {
        return dates;
    }

    public void setDates(String[] dates) {
        this.dates = dates;
    }

    public Moment(String name, int[] images, String[] dates) {
        this.name = name;
        this.images = images;
        this.dates = dates;
    }

    String name;
    int[] images;
    String[] dates;
}

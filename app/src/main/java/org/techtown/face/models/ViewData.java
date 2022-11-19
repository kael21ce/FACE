package org.techtown.face.models;

public class ViewData {
    private int type;
    private Family family;

    public ViewData(int t) {
        type = t;
    }
    public ViewData(int t, Family family) {
        this.type = t;
        this.family = family;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }
}

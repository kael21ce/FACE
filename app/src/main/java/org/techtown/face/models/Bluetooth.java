package org.techtown.face.models;

public class Bluetooth {
    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Bluetooth(String device, String address, boolean flag) {
        this.device = device;
        this.address = address;
        this.flag = flag;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    String device;
    String address;
    boolean flag;
}

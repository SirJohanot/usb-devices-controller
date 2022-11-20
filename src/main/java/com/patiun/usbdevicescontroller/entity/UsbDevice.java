package com.patiun.usbdevicescontroller.entity;

import java.util.Objects;

public class UsbDevice {

    private final String name;

    public UsbDevice(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UsbDevice usbDevice = (UsbDevice) o;

        return Objects.equals(name, usbDevice.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UsbDevice{" +
                "name='" + name + '\'' +
                '}';
    }
}

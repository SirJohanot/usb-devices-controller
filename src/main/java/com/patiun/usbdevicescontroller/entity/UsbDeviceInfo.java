package com.patiun.usbdevicescontroller.entity;

public class UsbDeviceInfo {

    private final byte classValue;
    private final int busNumber;
    private final int address;
    private final short vendor;
    private final short product;

    public UsbDeviceInfo(byte classValue, int busNumber, int address, short vendor, short product) {
        this.classValue = classValue;
        this.busNumber = busNumber;
        this.address = address;
        this.vendor = vendor;
        this.product = product;
    }

    public byte getClassValue() {
        return classValue;
    }

    public int getBusNumber() {
        return busNumber;
    }

    public int getAddress() {
        return address;
    }

    public short getVendor() {
        return vendor;
    }

    public short getProduct() {
        return product;
    }

    private static String deviceClassNumberToName(byte classNumber) {
        switch (classNumber) {
            case 0x01:
                return "Audio";
            case 0x02:
                return "Communications and CDC Control";
            case 0x03:
                return "HID (Human Interface Device)";
            case 0x05:
                return "Physical";
            case 0x06:
                return "Image";
            case 0x07:
                return "Printer";
            case 0x08:
                return "Mass Storage";
            case 0x09:
                return "Hub";
            case 0x0A:
                return "CDC-Data";
            case 0x0B:
                return "Smart Card";
            case 0x0D:
                return "Content Security";
            case 0x0E:
                return "Video";
            case 0x0F:
                return "Personal Healthcare";
            case 0x10:
                return "Audio/Video Devices";
            case 0x11:
                return "Billboard Device Class";
            case 0x12:
                return "USB Type-C Bridge Class";
            case (byte) 0xDC:
                return "Diagnostic Device";
            case (byte) 0xE0:
                return "Wireless Controller";
            case (byte) 0xEF:
                return "Miscellaneous";
            case (byte) 0xFE:
                return "Application Specific";
            case (byte) 0xFF:
                return "Vendor Specific";
            default:
                return String.format("Unknown %02X", classNumber);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UsbDeviceInfo usbDeviceInfo = (UsbDeviceInfo) o;

        if (classValue != usbDeviceInfo.classValue) {
            return false;
        }
        if (busNumber != usbDeviceInfo.busNumber) {
            return false;
        }
        if (address != usbDeviceInfo.address) {
            return false;
        }
        if (vendor != usbDeviceInfo.vendor) {
            return false;
        }
        return product == usbDeviceInfo.product;
    }

    @Override
    public int hashCode() {
        int result = classValue;
        result = 31 * result + busNumber;
        result = 31 * result + address;
        result = 31 * result + (int) vendor;
        result = 31 * result + (int) product;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s (Bus %03d, Device %03d: Vendor %04x, Product %04x)",
                deviceClassNumberToName(classValue), busNumber, address, vendor, product);
    }
}

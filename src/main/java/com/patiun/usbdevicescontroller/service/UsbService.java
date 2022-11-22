package com.patiun.usbdevicescontroller.service;

import com.patiun.usbdevicescontroller.entity.UsbDevice;
import org.usb4java.*;

import java.util.ArrayList;
import java.util.List;

public class UsbService {

    public static List<UsbDevice> findAllDevices() {
        Context context = initLibUsbContext();

        List<UsbDevice> devices = new ArrayList<>();
        DeviceList list = getDevicesList(context);
        try {
            for (Device device : list) {
                UsbDevice usbDevice = getUsbDevice(device);
                System.out.println("Found usb device: " + usbDevice);
                devices.add(usbDevice);
            }
        } finally {
            LibUsb.freeDeviceList(list, true);
        }

        LibUsb.exit(context);

        return devices;
    }

    public static void safelyEject(UsbDevice usbDevice) {
        if (usbDevice.getClassValue() != 0) {
            return;
        }
        Context context = initLibUsbContext();

        DeviceList list = getDevicesList(context);
        try {
            int targetDeviceBus = usbDevice.getBusNumber();
            int targetDeviceAddress = usbDevice.getAddress();

            for (Device device : list) {
                int bus = LibUsb.getBusNumber(device);
                int address = LibUsb.getDeviceAddress(device);

                if (bus == targetDeviceBus && address == targetDeviceAddress) {
                    DeviceHandle handle = new DeviceHandle();
                    int result = LibUsb.open(device, handle);
                    if (result < 0) {
                        throw new LibUsbException("Unable to open device", result);
                    }
                    LibUsb.resetDevice(handle);
                    LibUsb.close(handle);
                }
            }
        } finally {
            LibUsb.freeDeviceList(list, true);
        }

        LibUsb.exit(context);
    }

    private static Context initLibUsbContext() {
        Context context = new Context();

        int result = LibUsb.init(context);
        if (result < 0) {
            throw new LibUsbException("Unable to initialize libusb", result);
        }
        return context;
    }

    private static DeviceList getDevicesList(Context context) {
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(context, list);
        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }
        return list;
    }

    private static UsbDevice getUsbDevice(Device device) {
        int busNumber = LibUsb.getBusNumber(device);

        int address = LibUsb.getDeviceAddress(device);

        DeviceDescriptor descriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, descriptor);
        if (result < 0) {
            throw new LibUsbException("Unable to read device descriptor", result);
        }

        byte devClass = descriptor.bDeviceClass();

        short vendorId = descriptor.idVendor();

        short productId = descriptor.idProduct();

        return new UsbDevice(devClass, busNumber, address, vendorId, productId);
    }

}

package com.patiun.usbdevicescontroller.window;

import com.patiun.usbdevicescontroller.entity.UsbDeviceInfo;
import com.patiun.usbdevicescontroller.factory.ComponentFactory;
import com.patiun.usbdevicescontroller.service.UsbService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.patiun.usbdevicescontroller.factory.ComponentFactory.*;

public class Window {

    private final JFrame frame;
    private final JPanel panel;

    private Thread updaterThread;
    private List<UsbDeviceInfo> currentDevices = new ArrayList<>();

    private final Runnable updaterRunnable = () -> {
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            updateDevicesList();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }
    };

    public Window() {
        frame = buildFrame();

        panel = new JPanel(new GridLayout(10, 1));
        panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        setUpPanel(panel);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private static JFrame buildFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setupFrame(frame, "USB Devices Controller");
        frame.setResizable(false);
        return frame;
    }

    private JButton buildEjectButton(UsbDeviceInfo device) {
        JButton videoButton = buildButton("Disconnect");
        videoButton.addActionListener(e -> {
            updaterThread.interrupt();
            try {
                updaterThread.join();
                UsbService.safelyEject(device);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } finally {
                updaterThread = new Thread(updaterRunnable);
                updaterThread.start();
            }
        });
        return videoButton;
    }

    public void launch() {
        updaterThread = new Thread(updaterRunnable);
        updaterThread.start();
    }

    private void updateDevicesList() {
        List<UsbDeviceInfo> detectedDevices = UsbService.findAllDevices();
//        System.out.println("Comparing " + currentDevices + "\nand " + detectedDevices);
        if (currentDevices.equals(detectedDevices)) {
            return;
        }
        System.out.println("Updating displayed devices");
        currentDevices = detectedDevices;

        panel.removeAll();
        panel.revalidate();
        panel.repaint();

        detectedDevices.forEach(device -> {
            JPanel devicePanel = new JPanel(new BorderLayout());
            ComponentFactory.setupPanel(devicePanel);

            String deviceName = device.toString();
            JLabel deviceNameLabel = buildLabel(deviceName);
            devicePanel.add(deviceNameLabel, BorderLayout.WEST);

            byte deviceClass = device.getClassValue();
            if (deviceClass == 0) {
                JButton ejectButton = buildEjectButton(device);
                devicePanel.add(ejectButton, BorderLayout.EAST);
            }

            panel.add(devicePanel);
        });
        
        panel.revalidate();
        panel.repaint();
        frame.pack();
    }

}
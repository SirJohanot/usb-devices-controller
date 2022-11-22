package com.patiun.usbdevicescontroller.window;

import com.patiun.usbdevicescontroller.entity.UsbDevice;
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
    private List<UsbDevice> currentDevices = new ArrayList<>();

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

    private JButton buildEjectButton(UsbDevice device) {
        JButton videoButton = buildButton("Safely eject");
        videoButton.addActionListener(e -> {
            try {
                UsbService.safelyEject(device);
            } finally {
                updaterThread.start();
            }
        });
        return videoButton;
    }

    public void launch() {
        updaterThread = new Thread(() -> {
            while (true) {
                if (updaterThread.isInterrupted()) {
                    return;
                }
                updateDevicesList();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        updaterThread.start();
    }

    private void updateDevicesList() {
        List<UsbDevice> detectedDevices = UsbService.findAllDevices();
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

            JLabel deviceName = buildLabel(device.toString());
            devicePanel.add(deviceName, BorderLayout.WEST);

            JButton ejectButton = buildEjectButton(device);
            devicePanel.add(ejectButton, BorderLayout.EAST);

            panel.add(devicePanel);
        });
        panel.revalidate();
        panel.repaint();
        frame.pack();
    }

}
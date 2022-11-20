package com.patiun.usbdevicescontroller.window;

import com.patiun.usbdevicescontroller.entity.UsbDevice;
import com.patiun.usbdevicescontroller.service.UsbService;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.patiun.usbdevicescontroller.factory.ComponentFactory.*;

public class Window {

    private final JFrame frame;
    private List<UsbDevice> currentDevices = new ArrayList<>();

    public Window() {
        frame = buildFrame();

        frame.pack();
        frame.setVisible(true);
    }

    private static JFrame buildFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(10, 1));
        setupFrame(frame, "USB Devices Controller");
        frame.setResizable(false);
        return frame;
    }

    private static JButton buildEjectButton(UsbDevice device){
        JButton videoButton = buildButton("Safely eject");
        videoButton.addActionListener(e -> {
            UsbService.safelyEject(device);
        });
        return videoButton;
    }

    public void launch(){
        Thread thread = new Thread(() -> {
            while (true){
                updateDevicesList();
            }
        });
        thread.start();
    }

    private void updateDevicesList(){
        List<UsbDevice> detectedDevices = UsbService.findAllDevices();
        if (currentDevices.equals(detectedDevices)){
            return;
        }

        frame.removeAll();
        frame.revalidate();
        frame.repaint();

        detectedDevices.forEach(device -> {
            JPanel devicePanel = new JPanel(new BorderLayout());

            JTextArea deviceNameArea = buildTextArea(false);
            String deviceName = device.getName();
            deviceNameArea.setText(deviceName);
            devicePanel.add(deviceNameArea, BorderLayout.WEST);

            JButton ejectButton = buildEjectButton(device);
            devicePanel.add(ejectButton, BorderLayout.EAST);

            frame.add(devicePanel);
        });
    }

}
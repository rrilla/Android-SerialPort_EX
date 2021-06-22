package com.example.serialport.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class SerialPortUtil {
    SerialPort mSerialPort;
    OutputStream mOutputStream;
    InputStream mInputStream;

    public SerialPortUtil() {
        if (mSerialPort == null) {
            String path = "COM8";  //COM1, 255 Broadc
            int baudrate = 115200;
            try{
                mSerialPort = new SerialPort(new File(path), baudrate, 0);
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
            } catch (IOException e) {

            }
        }
    }
}

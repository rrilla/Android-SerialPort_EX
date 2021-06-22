package com.example.serialport;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

public class MainActivity extends AppCompatActivity {

    static final String SERIAL_PORT_NAME = "ttyS0";
    static final int SERIAL_BAUDRATE = 38400;

    SerialPort serialPort;
    InputStream inputStream;
    OutputStream outputStream;

    SerialThread serialThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetSerialPort(SERIAL_PORT_NAME);
    }

    void SetSerialPort(String name) {
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        String[] devices = serialPortFinder.getAllDevices();
        String[] devicesPath =serialPortFinder.getAllDevicesPath();

        for (int i = 0; i < devices.length; i++) {
            String device = devices[i];
            if (device.contains(name)) {
                try {
                    serialPort = new SerialPort(new File(devicesPath[i]), SERIAL_BAUDRATE, 0);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (serialPort != null) {
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        }
    }

    void StartRxThread() {
        if (inputStream == null) {
            Log.e("SerialExam", "Can't open inputstream");
            return;
        }

        serialThread = new SerialThread();
        serialThread.start();
    }

    void OnReceiveData(byte[] buffer, int size) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(String.format("%02x ", buffer[i]));
        }

        Log.d("SerialExam", stringBuilder.toString());
    }

    void SendData(byte[] data) {
        if (outputStream == null) {
            Log.e("SerialExam", "Can't open outputstream");
            return ;
        }

        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SerialThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    byte[] buffer = new byte[64];
                    int size = inputStream.read(buffer);
                    if (size > 0) OnReceiveData(buffer, size);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
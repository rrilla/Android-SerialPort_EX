package com.example.serialport;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.serialport.util.SerialPortUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

public class MainActivity extends AppCompatActivity {

    static final String SERIAL_PORT_NAME = "COM8";  //COM1, 255 Broadc
    static final int SERIAL_BAUDRATE = 115200;

    SerialPort mSerialPort;
    OutputStream mOutputStream;
    InputStream mInputStream;

//    SerialThread serialThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SerialPortUtil();

        Button btn = findViewById(R.id.button);
        Button btn2 = findViewById(R.id.button2);
        TextView tv = findViewById(R.id.textView);
        EditText et = findViewById(R.id.editTextTextPersonName);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] text = et.getText().toString().getBytes();
                sendDate(text);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(readData());
            }
        });
    }

    public void SerialPortUtil() {
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

    public void sendDate(byte[] writeBytes)
    {
        try {
            if (mOutputStream != null) {
                mOutputStream.write(writeBytes);
            }
        } catch (IOException e) {

        }
    }

    public String readData() {
        String readDatas = null;
        for (int i = 0; i < 10; i++)
        {
            try {
                if (mInputStream != null) {
                    byte[] buffer = new byte[7];
                    int size = mInputStream.read(buffer);
                    if (size > 0)
                    {
                        //읽어드릴 응답이 있음
                        //아래에서 데이터 처리
                        readDatas = String.format("%02x", buffer);
                        break;
                    }else {
                        Thread.sleep(2000); //2초후 다시 버퍼를 검사한다.
                    }
                }
                if(i==9){
                    break;
                }
            } catch (IOException e) {

            } catch (InterruptedException e) {

            }
        }
        return readDatas;
    }

    public void closeSerialPort() {
        if (mSerialPort != null)
        {
            mSerialPort.close();
            mSerialPort = null;
        }
    }


//    class SerialThread extends Thread {
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    byte[] buffer = new byte[64];
//                    int size = inputStream.read(buffer);
//                    if (size > 0) OnReceiveData(buffer, size);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
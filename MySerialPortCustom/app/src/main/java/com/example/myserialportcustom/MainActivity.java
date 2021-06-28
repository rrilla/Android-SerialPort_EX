package com.example.myserialportcustom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.TextView;

import com.example.myserialportcustom.util.HexDump;
import com.hoho.android.usbserial.BuildConfig;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SerialInputOutputManager.Listener{

    private UsbSerialPort usbSerialPort;
    private SerialInputOutputManager usbIoManager;
    private static final int WRITE_WAIT_MILLIS = 2000;
    private static final int READ_WAIT_MILLIS = 2000;

    TextView tvReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvReceive = findViewById(R.id.receive);

        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            return;
        }

        usbSerialPort = driver.getPorts().get(0); // Most devices have just one port (port 0)
        try {
            usbSerialPort.open(connection);
            usbSerialPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //이벤트 구동시 읽기 위해 필요. onNewData();
        usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
        usbIoManager.setReadBufferSize(10000);
        usbIoManager.start();

        Button btnStart = findViewById(R.id.start);
        btnStart.setOnClickListener(v -> send("AA AA FF 08 C1 02 05 00 BC A9 24"));//multi tag start
        Button btnStop = findViewById(R.id.stop);
        btnStop.setOnClickListener(v -> send("AA AA FF 05 C0 00 B3 F7"));//multi tag stop
        Button btnDisconnect = findViewById(R.id.disconnect);
        btnDisconnect.setOnClickListener(v -> disconnect());

    }

    //입력 데이터 HEX로 변환하여 전송
    private void send(String str) {
        try {
            StringBuilder sb = new StringBuilder();
            TextUtil.toHexString(sb, TextUtil.fromHexString(str));
            TextUtil.toHexString(sb, "\r\n".getBytes());
            String msg = sb.toString();
            byte[] data = TextUtil.fromHexString(msg);
            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            tvReceive.append(spn);
            usbSerialPort.write(data, WRITE_WAIT_MILLIS);
        } catch (Exception e) {
            onRunError(e);
        }
    }

    private void disconnect() {
        if(usbIoManager != null) {
            usbIoManager.setListener(null);
            usbIoManager.stop();
        }
        usbIoManager = null;
        try {
            usbSerialPort.close();
        } catch (IOException ignored) {}
        usbSerialPort = null;
    }

    @Override
    public void onNewData(byte[] data) {
        runOnUiThread(() -> {
            if(data.length > 0)
                //받은 HEX 데이터 변환하여 출력
                tvReceive.append(TextUtil.toHexString(data) + "\n");;
        });
    }

    @Override
    public void onRunError(Exception e) {
//        mainLooper.post(() -> {
//            status("connection lost: " + e.getMessage());
//            disconnect();
//        });
    }


}
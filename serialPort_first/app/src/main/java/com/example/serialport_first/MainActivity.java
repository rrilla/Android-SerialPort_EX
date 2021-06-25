package com.example.serialport_first;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    static class ListItem {
        UsbDevice device;
        int port;
        UsbSerialDriver driver;

        ListItem(UsbDevice device, int port, UsbSerialDriver driver) {
            this.device = device;
            this.port = port;
            this.driver = driver;
        }
    }

    private int baudRate = 115200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
        UsbSerialProber usbCustomProber = CustomProber.getCustomProber();

        ArrayList<ListItem> listItems = new ArrayList<>();

        final String ACTION_USB_PERMISSION =
                "com.example.serialport_first.USB_PERMISSION";
        final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        UsbDevice devices = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if(devices != null){
                                //call method to set up device communication
                                for(UsbDevice device : usbManager.getDeviceList().values()) {
                                    UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
                                    if(driver == null) {
                                        driver = usbCustomProber.probeDevice(device);
                                    }
                                    if(driver != null) {
                                        for(int port = 0; port < driver.getPorts().size(); port++)
                                            listItems.add(new ListItem(device, port, driver));
                                    } else {
                                        listItems.add(new ListItem(device, 0, null));
                                    }
                                }
                            }
                        }
                        else {
                            Log.e("asdf", "permission denied for device " + devices);
                        }
                    }
                }
            }
        };

        for(UsbDevice device : usbManager.getDeviceList().values()) {
            UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
            if(driver == null) {
                driver = usbCustomProber.probeDevice(device);
            }
            if(driver != null) {
                for(int port = 0; port < driver.getPorts().size(); port++)
                    listItems.add(new ListItem(device, port, driver));
            } else {
                listItems.add(new ListItem(device, 0, null));
            }
        }
        TextView tv = findViewById(R.id.textView);
        for(int i = 0; listItems.size() < i; i++){
            tv.setText(listItems.get(i).toString());
        }

    }

}
package com.example.serialport2;

import kr.co.driver.serial.FTDriver;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serialport2.R;

@SuppressLint("InlinedApi")
public class MainActivity extends Activity {

	private FTDriver mSerial;
	private com.example.serialport2.UsbReceiver mUsbReceiver;

	private static final String ACTION_USB_PERMISSION = "kr.co.andante.mobiledgs.USB_PERMISSION";

	private Boolean SHOW_DEBUG = false;
	private String TAG = "HDJ";

	private int mBaudrate;

	private EditText	etWrite;
	private Button		btWrite;
	private EditText	etLog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		etWrite = (EditText)findViewById(R.id.strInput);
		
		etLog = (EditText)findViewById(R.id.strLog);
		etLog.setFocusable(false);
		etLog.setClickable(false);

		mSerial = new FTDriver((UsbManager) getSystemService(Context.USB_SERVICE));

		// listen for new devices
		mUsbReceiver = new com.example.serialport2.UsbReceiver(this, mSerial);

		IntentFilter filter = new IntentFilter();
		filter.addAction (UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction (UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver (mUsbReceiver, filter);

		// load default baud rate
		mBaudrate = mUsbReceiver.loadDefaultBaudrate();

		// for requesting permission
		// setPermissionIntent() before begin()
		PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		mSerial.setPermissionIntent(permissionIntent);

		if (SHOW_DEBUG) {
			Log.d(TAG, "FTDriver beginning");
		}
		
		etLog.setTextSize(mUsbReceiver.GetTextFontSize());

		if (mSerial.begin(mBaudrate)) {
			if (SHOW_DEBUG) {
				Log.d(TAG, "FTDriver began");
			}
			mUsbReceiver.loadDefaultSettingValues();
			mUsbReceiver.mainloop();
		} else {
			if (SHOW_DEBUG) {
				Log.d(TAG, "FTDriver no connection");
			}
			Toast.makeText(this, "no connection", Toast.LENGTH_SHORT).show();
		}

		btWrite = (Button)findViewById(R.id.btnWrite);
		btWrite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mUsbReceiver.writeDataToSerial(etWrite.getText().toString());
			}
		});
	}

	@Override
	public void onDestroy() {
		mUsbReceiver.closeUsbSerial();
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}

	public void onSetText(String buf)
	{
		String temp = buf+"\n";
		etLog.append(temp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_open:
			mUsbReceiver.openUsbSerial();
			break;
		case R.id.action_close:
			mUsbReceiver.closeUsbSerial();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
}

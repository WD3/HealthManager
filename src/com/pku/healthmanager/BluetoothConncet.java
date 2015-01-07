package com.pku.healthmanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import com.pku.countermanager.CounterActivity;
import com.pku.countermanager.CounterBluetooth;
import com.pku.countermanager.CounterSettingActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class BluetoothConncet implements Runnable {
	private int type;
	private boolean exit;
	private String counter_address;
	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice btdevice;
	private BluetoothSocket socket;
	private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private SharedPreferences sp;
	private Context context;
	private final int CONNECT_INTERVAL = 500; // 每隔500ms重新开始建立蓝牙连接
	private final int INTERVAL = 2000; // 每次连接成功通信结束后，休眠3000ms开始下一次连接

	public BluetoothConncet(Context context,SharedPreferences sp, int type, boolean exit) {
		this.context = context;
		this.type = type;
		this.exit = exit;
		this.sp = sp;
	}

	private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
			throws IOException {
		if (Build.VERSION.SDK_INT >= 10) {
			try {
				final Method m = device.getClass().getMethod(
						"createInsecureRfcommSocketToServiceRecord",
						new Class[] { UUID.class });
				return (BluetoothSocket) m.invoke(device, uuid);
			} catch (Exception e) {
				Log.e("BluetoothManager",
						"Could not create Insecure RFComm Connection", e);
			}
		}
		return device.createRfcommSocketToServiceRecord(uuid);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Looper.prepare();
		while (exit) {
			counter_address = sp.getString("counter_address", "");
			if (counter_address != "") {
				if (!adapter.isEnabled())
					adapter.enable();
				btdevice = adapter.getRemoteDevice(counter_address);
				try {
					socket = createBluetoothSocket(btdevice);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				try {
					Log.e("开始连接", "开始连接");
					if (adapter.isDiscovering())
						adapter.cancelDiscovery();
					socket.connect();
					startCommunicate(type,socket,sp);					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("异常", e.getMessage());
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					CounterActivity.SendMessage(CounterActivity.handler, 2);
				}
				try {
					Thread.sleep(CONNECT_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else break;			
		}

		Looper.loop();
	}
	
	
	private void startCommunicate(int type,BluetoothSocket socket,SharedPreferences sp) {
		switch (type) {
		case 0:
		case 1:
		case 2:
			CounterBluetooth counterBluetooth = new CounterBluetooth(context, sp);
			counterBluetooth.sendData();
			break;
		case 3:
		}
	}

}

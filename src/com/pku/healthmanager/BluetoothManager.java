package com.pku.healthmanager;

import java.io.IOException;

import com.pku.countermanager.CounterBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BluetoothManager {
	private Context context;
	public BluetoothAdapter adapter;
	private BroadcastReceiver mReceiver;
	public static String counter_address;
	public static boolean addressChanged;

	public BluetoothManager(Context context) {
		this.context = context;
		adapter = BluetoothAdapter.getDefaultAdapter();
	}

	public void discovery() {
		if (!adapter.isEnabled())
			adapter.enable();
		while (true) {
			if (adapter.isEnabled()) {
				System.out.println("正在搜索");
				adapter.startDiscovery();
				break;
			}
		}
	}

	public void broadcast() {
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					System.out.println(device.getName() + device.getAddress());
					if (device.getName().equals("HeHu080")) {
						if (device.getAddress().substring(0, 5).equals("00:0E")) {
							String address = MainActivity.sp.getString("counter_address", "");
							addDevice(device, "counter_address", address);
							Log.e("搜到计步设备",device.getName() + device.getAddress());
							CounterBluetooth.exit = true;
							try {
								new CounterBluetooth(context).connect();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		};
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(mReceiver, filter);
	}

	private void addDevice(BluetoothDevice device, String type, String address) {
		if (!device.getAddress().equals(address))
			addressChanged = true;
		address = device.getAddress();
		MainActivity.sp.edit().putString(type, address).commit();
	}

	public void cancle() {
		context.unregisterReceiver(mReceiver);
	}
}

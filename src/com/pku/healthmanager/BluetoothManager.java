package com.pku.healthmanager;

import java.io.IOException;

import com.pku.countermanager.CounterBluetooth;
import com.pku.countermanager.CounterSettingActivity;
import com.pku.myApplication.MyApplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothManager {
	private Context context;
	private BluetoothAdapter adapter;
	private String counter_address;
	private SharedPreferences sp;
	private MyApplication myApp;
	private Handler handler;
	



	public BluetoothManager(Context context,SharedPreferences sp) {
		this.context = context;
		this.sp = sp;
		adapter = BluetoothAdapter.getDefaultAdapter();
		myApp = (MyApplication)context.getApplicationContext();
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
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				System.out.println(device.getName() + device.getAddress());
				if (device.getName().equals("HeHu080")) {
					if (device.getAddress().substring(0, 5).equals("00:0E")) {
						String address = sp.getString("counter_address", "");
						counter_address = device.getAddress();
						addDevice(counter_address, "counter_address", address);
						Log.e("搜到计步设备",device.getName() + device.getAddress());						
						myApp.setExit2(true);
						new Thread(new BluetoothConncet(context,sp, MyApplication.COUNTER_DEVICE, myApp.getExit2())).start();	
						sendMessage(CounterSettingActivity.handler, 1);
					}
				}
			}
		}
	};
	public void registerBroadcast() {		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(mReceiver, filter);
	}

	private void addDevice(String newAddress, String type, String address) {
		if (!newAddress.equals(address)){
			String addressChanged = type+"_changed";
			sp.edit().putBoolean(addressChanged, true).commit();
		}
		sp.edit().putString(type, newAddress).commit();
	}
	private void sendMessage(Handler handler, int value){
		Message msg = handler.obtainMessage();
		msg.what = value;
		handler.sendMessage(msg);
	}
	public void unregisterBroadcast() {
		context.unregisterReceiver(mReceiver);
	}
}

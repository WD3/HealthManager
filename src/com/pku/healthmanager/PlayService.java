package com.pku.healthmanager;

import java.io.IOException;

import com.pku.countermanager.CounterBluetooth;
import com.pku.myApplication.MyApplication;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class PlayService extends Service {
	private BluetoothManager bluetoothManager;
	private BluetoothConncet counter_bluetoothConncet;
	private SharedPreferences sp;
	private MyApplication myApp;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		myApp = (MyApplication) getApplication();
		sp = myApp.getSp();
		bluetoothManager = new BluetoothManager(this,sp);
		bluetoothManager.registerBroadcast();
		
		myApp.setExit2(true);
		boolean exit2 = myApp.getExit2();
		counter_bluetoothConncet = new BluetoothConncet(this,sp, 2, exit2);
		new Thread(counter_bluetoothConncet).start();
		
	}

	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		System.out.println("service,onStart");

	}

	public void onDestroy() {
		super.onDestroy();
		bluetoothManager.unregisterBroadcast();
		myApp.setExit2(false);
		System.exit(0);
	}
}

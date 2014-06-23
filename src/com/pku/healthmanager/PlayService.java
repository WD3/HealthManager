package com.pku.healthmanager;

import java.io.IOException;

import com.pku.countermanager.CounterBluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PlayService extends Service {
	private BluetoothManager bluetoothManager;
	private CounterBluetooth counterBluetooth;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		bluetoothManager = new BluetoothManager(this);
		bluetoothManager.broadcast();
		counterBluetooth = new CounterBluetooth(this);
		try {
			counterBluetooth.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		System.out.println("service,onStart");

	}

	public void onDestroy() {
		super.onDestroy();
		bluetoothManager.cancle();
		CounterBluetooth.exit = false;
		System.exit(0);
	}
}

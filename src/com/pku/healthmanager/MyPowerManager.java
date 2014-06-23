package com.pku.healthmanager;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class MyPowerManager {
	private PowerManager powerManager;
	private WakeLock wakeLock;
	private KeyguardManager keyguardManager;
	private KeyguardLock keyguardLock;
	private Context context;
	
	public MyPowerManager(Context context){
		this.context = context;
		powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "SimpleTimer");
		keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
		keyguardLock = keyguardManager.newKeyguardLock("unLock");
	}
	public void acquireWakeLock(){
		if(wakeLock != null){
			wakeLock.acquire();
			keyguardLock.disableKeyguard();
		}
	}
	public void releaseWakeLock(){
		if(wakeLock != null&&wakeLock.isHeld()){
			wakeLock.release();
			wakeLock = null;
		}
	}
}

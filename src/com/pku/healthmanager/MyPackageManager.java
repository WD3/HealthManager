package com.pku.healthmanager;

import java.util.List;

import com.pku.countermanager.CounterActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class MyPackageManager {
	private MyPowerManager myPowerManager;
	private Context context;
	private String counter = "ComponentInfo{com.pku.healthmanager/com.pku.healthmanager.HealthActivity}";

	public MyPackageManager(Context context) {
		this.context = context;
		myPowerManager = new MyPowerManager(context);
	}

	public void cancle() {
		myPowerManager.releaseWakeLock();
	}

	public void openBloodpressure() {
		myPowerManager.acquireWakeLock();
		Intent intent = new Intent(context, HealthActivity.class);
		intent.putExtra("type", 0);
		context.startActivity(intent);
	}

	public void openCounter() {
		System.out.println(getTopActivity());
		myPowerManager.acquireWakeLock();
		if(!getTopActivity().equals(counter)){
			Intent intent = new Intent(context, HealthActivity.class);
			intent.putExtra("type", 1);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			System.out.println("aaaaaaaaaa");
		}		
		else {
			CounterActivity.SendMessage(CounterActivity.handler, 3);
			System.out.println("cccccccccc");
		}
	}

	public void openScale() {
		myPowerManager.acquireWakeLock();
		Intent intent = new Intent(context, HealthActivity.class);
		intent.putExtra("type", 1);
		context.startActivity(intent);
	}

	public void openOximeter() {
		myPowerManager.acquireWakeLock();
		Intent intent = new Intent(context, HealthActivity.class);
		intent.putExtra("type", 1);
		context.startActivity(intent);
	}

	public void openECG() {
		myPowerManager.acquireWakeLock();
		Intent intent = new Intent(context, HealthActivity.class);
		intent.putExtra("type", 1);
		context.startActivity(intent);
	}

	private String getTopActivity() {
		ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		if (runningTaskInfos != null)
			return (runningTaskInfos.get(0).topActivity).toString();
		else
			return null;
	}
}

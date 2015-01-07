package com.pku.myApplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application {
	public SharedPreferences sp;
	private boolean exit0 = true;
	private boolean exit1 = true;
	private boolean exit2 = true;
	private boolean exit3 = true;
	
	public static final int BLOODPRESSURE_DEVICE = 0;
	public static final int OXIMETER_DEVICE = 1;
	public static final int COUNTER_DEVICE = 2;
	public static final int SCALE_DEVICE = 3;
	public static final int ECG_DEVICE = 4;

	public void onCreate() {
		super.onCreate();
		sp = this.getSharedPreferences("HealthManager", Context.MODE_PRIVATE);
	}

	public SharedPreferences getSp() {
		return this.sp;
	}

	public void setExit0(boolean exit0) {
		this.exit0 = exit0;
	}

	public void setExit1(boolean exit2) {
		this.exit1 = exit1;
	}

	public void setExit2(boolean exit2) {
		this.exit2 = exit2;
	}

	public void setExit3(boolean exit3) {
		this.exit3 = exit3;
	}

	public boolean getExit0() {
		return this.exit0;
	}

	public boolean getExit1() {
		return this.exit1;
	}

	public boolean getExit2() {
		return this.exit2;

	}

	public boolean getExit3() {
		return this.exit3;
	}
}

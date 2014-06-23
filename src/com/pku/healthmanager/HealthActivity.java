/**
 * Program  : ViewPagerActivity.java
 * Author   : qianj
 * Create   : 2012-5-31 下午2:02:15
 */

package com.pku.healthmanager;

import java.util.ArrayList;
import java.util.List;

import com.pku.countermanager.CounterActivity;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class HealthActivity extends TabActivity {
	static TabHost tabHost = null;
	static int i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.healthtab);
		init();
		Intent i = getIntent();
		// 获取数据
		int type = i.getIntExtra("type", 0);
		tabHost.setCurrentTab(type);
	}

	private void init() {
		tabHost = getTabHost();
		// 页面1
		TabSpec spec1 = tabHost.newTabSpec("1");
		spec1.setIndicator("血压");
		Intent intent1 = new Intent(this, BloodpressureActivity.class);
		spec1.setContent(intent1);

		// 页面2
		TabSpec spec2 = tabHost.newTabSpec("2");
		spec2.setIndicator("计步");
		Intent intent2 = new Intent(this, CounterActivity.class);
		spec2.setContent(intent2);

		// 页面3
		TabSpec spec3 = tabHost.newTabSpec("3");
		spec3.setIndicator("体重");
		Intent intent3 = new Intent(this, ScaleActivity.class);
		spec3.setContent(intent3);

		// 页面4
		TabSpec spec4 = tabHost.newTabSpec("4");
		spec4.setIndicator("血氧");
		Intent intent4 = new Intent(this, OximeterActivity.class);
		spec4.setContent(intent4);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.addTab(spec4);
	}
	public static void setTab(int type){
		tabHost.setCurrentTab(type);	
	}

	/**
	 * 显示下一个页面
	 */
	public static void showNext() {
		// 三元表达式控制3个页面的循环.
		i = tabHost.getCurrentTab();
		if(i == 3) i=-1;
		tabHost.setCurrentTab(i+1);
	}

	/**
	 * 显示前一个页面
	 */
	public static void showPre() {
		// 三元表达式控制3个页面的循环.
		i = tabHost.getCurrentTab();
		if(i == 0) i=4;
		tabHost.setCurrentTab(i-1);
	}
}

package com.pku.healthmanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.pku.calendar.CalendarLunar;
import com.pku.calendar.CalendarUtil;
import com.pku.calendar.CalendarView;
import com.pku.calendar.NumberHelper;
import com.pku.countermanager.CounterSettingActivity;
import com.pku.weather.WeatherWebService;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MainActivity extends TabActivity implements OnClickListener{
	private TabHost tabHost;
	private Context context;
	private TextView tv_month;
	private TextView tv_day;
	private TextView tv_solar;
	private TextView tv_cyclical;
	private TextView tv_lunar;
	private TextView tv_date;
	private static TextView tv_currentSteps;
	private static TextView tv_progress;
	private LinearLayout dayLayout;
	private RelativeLayout home;
	private RelativeLayout counter_setting;
	private RelativeLayout exitLayout;
	private FrameLayout counterLayout;
	private FrameLayout scaleLayout;
	private FrameLayout oximeterLayout;
	private FrameLayout bloodpressureLayout;
	private LinearLayout monthLayout;
	private CalendarView calendarView;
	private CalendarLunar calendarLunar;
	public static SharedPreferences sp;
	private WeatherWebService weatherWebService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 实现无标题栏（但有系统自带的任务栏）：
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		context = this;
		// 获取TabHost对象
		tabHost = getTabHost();
		
		// 新建一个newTabSpec,设置标签和图标(setIndicator),设置内容(setContent)
		tabHost.addTab(tabHost.newTabSpec("home").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_home)).setContent(R.id.home));
		tabHost.addTab(tabHost.newTabSpec("health").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_health)).setContent(R.id.health));
		tabHost.addTab(tabHost.newTabSpec("user").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_user)).setContent(R.id.user));
		tabHost.addTab(tabHost.newTabSpec("setting").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_setting)).setContent(R.id.setting));
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#000000"));}
		// 设置当前现实哪一个标签
		tabHost.setCurrentTab(0); // 0为标签ID		
		// 标签切换处理，用setOnTabChangedListener
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				if (tabId.equals("home")) {
					
				} else if (tabId.equals("health")) {
					 
				} else if (tabId.equals("user")) {
					
				} else if (tabId.equals("setting")) {
					
				}
			}
		});		
		tv_month = (TextView)findViewById(R.id.month);
		tv_month.setOnClickListener(this);
		tv_day = (TextView)findViewById(R.id.day);
		tv_day.setOnClickListener(this);
		tv_solar = (TextView)findViewById(R.id.solar);
		tv_cyclical = (TextView)findViewById(R.id.cyclical);
		tv_lunar = (TextView)findViewById(R.id.lunar);
		tv_date = (TextView)findViewById(R.id.date);
		tv_currentSteps = (TextView)findViewById(R.id.health_currentSteps);
		tv_progress = (TextView)findViewById(R.id.health_progress);
		counterLayout = (FrameLayout)findViewById(R.id.counter);
		counterLayout.setOnClickListener(this);
		scaleLayout = (FrameLayout)findViewById(R.id.scale);
		scaleLayout.setOnClickListener(this);
		oximeterLayout = (FrameLayout)findViewById(R.id.oximeter);
		oximeterLayout.setOnClickListener(this);
		bloodpressureLayout = (FrameLayout)findViewById(R.id.bloodpressure);
		bloodpressureLayout.setOnClickListener(this);
		counter_setting = (RelativeLayout)findViewById(R.id.counter_setting);
		counter_setting.setOnClickListener(this);
		exitLayout = (RelativeLayout)findViewById(R.id.exit);
		exitLayout.setOnClickListener(this);
		dayLayout = (LinearLayout)findViewById(R.id.calendar_day);
		monthLayout = (LinearLayout)findViewById(R.id.calendar_month);		
		home = (RelativeLayout)findViewById(R.id.home);
		dayLayout.setVisibility(View.VISIBLE);
		monthLayout.setVisibility(View.INVISIBLE);
		calendarView = new CalendarView(this,home);
		calendarLunar = new CalendarLunar();
		weatherWebService = new WeatherWebService(home,this);
		sp = PreferenceManager.getDefaultSharedPreferences(this);	
		this.startService(new Intent(this,PlayService.class));
	}
	public void onStart(){
		super.onStart();		
		display();
	}
	public void onDestroy(){
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.month:
			dayLayout.setVisibility(View.INVISIBLE);
			monthLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.day:
			dayLayout.setVisibility(View.VISIBLE);
			monthLayout.setVisibility(View.INVISIBLE);
			break;
		case R.id.bloodpressure:
			Intent i1 = new Intent(this,HealthActivity.class);
			i1.putExtra("type", 0);
			this.startActivity(i1);
			break;
		case R.id.counter:
			Intent i2 = new Intent(this,HealthActivity.class);
			i2.putExtra("type", 1);
			this.startActivity(i2);
			break;
		case R.id.scale:
			Intent i3 = new Intent(this,HealthActivity.class);
			i3.putExtra("type", 2);
			this.startActivity(i3);
			break;		
		case R.id.oximeter:
			Intent i4 = new Intent(this,HealthActivity.class);
			i4.putExtra("type", 3);
			this.startActivity(i4);
			break;
		case R.id.counter_setting:
			Intent i5 = new Intent(this,CounterSettingActivity.class);
			this.startActivity(i5);
			break;
		case R.id.exit:
			this.stopService(new Intent(this,PlayService.class));
			break;
		}
	}
	public static void SendMessage(Handler handler, int i) {
		Message msg = handler.obtainMessage();
		msg.what = i;
		handler.sendMessage(msg);
	}

	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				tv_currentSteps.setText(Variable.currentSteps);
				tv_progress.setText(Variable.percent);
				break;
			}
		}
	};
	public void display(){
		Date date = new Date();
		tv_solar.setText(CalendarUtil.getCurrentDay());
		tv_date.setText(""+NumberHelper.LeftPad_Tow_Zero(date.getDate()));
		tv_cyclical.setText("农历："+CalendarUtil.cyclical(date.getYear()+1900)+"年 "+CalendarUtil.cyclicalm(date.getMonth())+"月");
		tv_lunar.setText(calendarLunar.getLunarCalendar(date));
		
		String originaldate = MainActivity.sp.getString("originaldate", "");
		Date mDate = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		String currentdate = format.format(mDate);
		if(!currentdate.equals(originaldate)){
			MainActivity.sp.edit().putString("originaldate", currentdate)
			.putString("todaysteps","0").putString("hoursteps", "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0")
			.putString("percent", "0%").commit();
		}			
		Variable.currentSteps = MainActivity.sp.getString("todaysteps", "0");
		Variable.percent = MainActivity.sp.getString("percent","0%");
		tv_currentSteps.setText(Variable.currentSteps);
		tv_progress.setText(Variable.percent);		
	}
}

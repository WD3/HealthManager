package com.pku.healthmanager;

import java.util.Calendar;
import java.util.Date;

import com.pku.calendar.CalendarLunar;
import com.pku.calendar.CalendarUtil;
import com.pku.calendar.CalendarView;

import android.app.TabActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
	private LinearLayout dayLayout;
	private RelativeLayout home;
	private LinearLayout monthLayout;
	private CalendarView calendarView;
	private CalendarLunar calendarLunar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ʵ���ޱ�����������ϵͳ�Դ�������������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		context = this;
		// ��ȡTabHost����
		tabHost = getTabHost();
		
		// �½�һ��newTabSpec,���ñ�ǩ��ͼ��(setIndicator),��������(setContent)
		tabHost.addTab(tabHost.newTabSpec("home").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_home)).setContent(R.id.home));
		tabHost.addTab(tabHost.newTabSpec("health").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_health)).setContent(R.id.health));
		tabHost.addTab(tabHost.newTabSpec("user").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_user)).setContent(R.id.user));
		tabHost.addTab(tabHost.newTabSpec("setting").setIndicator("",getResources().getDrawable(R.drawable.tab_selector_setting)).setContent(R.id.setting));
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#000000"));}
		// ���õ�ǰ��ʵ��һ����ǩ
		tabHost.setCurrentTab(0); // 0Ϊ��ǩID		
		// ��ǩ�л�������setOnTabChangedListener
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
		dayLayout = (LinearLayout)findViewById(R.id.calendar_day);
		monthLayout = (LinearLayout)findViewById(R.id.calendar_month);		
		home = (RelativeLayout)findViewById(R.id.home);
		dayLayout.setVisibility(View.VISIBLE);
		monthLayout.setVisibility(View.INVISIBLE);
		calendarView = new CalendarView(this,home);
		calendarLunar = new CalendarLunar();
	}
	public void onStart(){
		super.onStart();
		Date date = new Date();
		tv_solar.setText(CalendarUtil.getCurrentDay());
		tv_date.setText(""+date.getDate());
		tv_cyclical.setText("ũ����"+CalendarUtil.cyclical(date.getYear()+1900)+"�� "+CalendarUtil.cyclicalm(date.getMonth())+"��");
		tv_lunar.setText(calendarLunar.getLunarCalendar(date));
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
		}
	}
	
}

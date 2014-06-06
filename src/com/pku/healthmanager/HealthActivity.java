/**
 * Program  : ViewPagerActivity.java
 * Author   : qianj
 * Create   : 2012-5-31 ����2:02:15
 */

package com.pku.healthmanager;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.LocalActivityManager;
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
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
/**
 * 
 * @author qianj
 * @version 1.0.0
 * @2012-5-31 ����2:02:15
 */
public class HealthActivity extends Activity {

	List<View> listViews;

	Context context = null;

	LocalActivityManager manager = null;

	TabHost tabHost = null;

	private ViewPager pager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.viewpager);
		
		context = HealthActivity.this;
		
		pager  = (ViewPager) findViewById(R.id.viewpager);
		
		//����һ����view��list�����ڴ��viewPager�õ���view
		listViews = new ArrayList<View>();
		
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		
		Intent i1 = new Intent(context, BloodpressureActivity.class);
		listViews.add(getView("A", i1));
		Intent i2 = new Intent(context, CounterActivity.class);
		listViews.add(getView("B", i2));
		Intent i3 = new Intent(context, ScaleActivity.class);
		listViews.add(getView("C", i3));
		Intent i4 = new Intent(context, OximeterActivity.class);
		listViews.add(getView("D", i4));

		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		tabHost.setup(manager);
		
		
		//�����Ҫ���Զ���һ��tabhost�е�tab����ʽ
//		RelativeLayout tabIndicator1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.counter, null);  
//		TextView tvTab1 = (TextView)tabIndicator1.findViewById(R.id.tv_title);
//		tvTab1.setText("��һҳ");
//		
//		RelativeLayout tabIndicator2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tabwidget,null);  
//		TextView tvTab2 = (TextView)tabIndicator2.findViewById(R.id.tv_title);
//		tvTab2.setText("�ڶ�ҳ");
//		
//		RelativeLayout tabIndicator3 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tabwidget,null);  
//		TextView tvTab3 = (TextView)tabIndicator3.findViewById(R.id.tv_title);
//		tvTab3.setText("����ҳ");
		
		Intent intent = new Intent(context,EmptyActivity.class);
		//ע�����Intent�е�activity���������� ���硰A����Ӧ����T1Activity������intent��new��T3Activity�ġ�
		tabHost.addTab(tabHost.newTabSpec("A").setIndicator("Ѫѹ").setContent(intent));
		tabHost.addTab(tabHost.newTabSpec("B").setIndicator("�Ʋ�").setContent(intent));
		tabHost.addTab(tabHost.newTabSpec("C").setIndicator("����").setContent(intent));
		tabHost.addTab(tabHost.newTabSpec("D").setIndicator("Ѫ��").setContent(intent));
		
		pager .setAdapter(new MyPageAdapter(listViews));
		pager .setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				//��viewPager�����ı�ʱ��ͬʱ�ı�tabhost�����currentTab
				tabHost.setCurrentTab(position);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		
	 //���tabhost�е�tabʱ��Ҫ�л������viewPager
	 tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
            	
            	if ("A".equals(tabId)) {
                    pager.setCurrentItem(0);
                } 
                if ("B".equals(tabId)) {
                	
                    pager.setCurrentItem(1);
                } 
                if ("C".equals(tabId)) {
                    pager.setCurrentItem(2);
                } 
                if ("D".equals(tabId)) {
                    pager.setCurrentItem(3);
                } 
            }
        });
	 
	 Intent i = getIntent();   
	 //��ȡ����   
	 int type = i.getIntExtra("type", 0);
	 tabHost.setCurrentTab(type);		
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	private class MyPageAdapter extends PagerAdapter {
		
		private List<View> list;

		private MyPageAdapter(List<View> list) {
			this.list = list;
		}

		@Override
        public void destroyItem(View view, int position, Object arg2) {
            ViewPager pViewPager = ((ViewPager) view);
            pViewPager.removeView(list.get(position));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(View view, int position) {
            ViewPager pViewPager = ((ViewPager) view);
            pViewPager.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

	}
}

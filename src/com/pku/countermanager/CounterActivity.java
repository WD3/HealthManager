package com.pku.countermanager;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import com.pku.countermanager.CounterBluetooth;
import com.pku.countermanager.CounterSettingActivity;
import com.pku.healthmanager.HealthActivity;
import com.pku.healthmanager.MainActivity;
import com.pku.healthmanager.MyGestureDetector;
import com.pku.healthmanager.R;
import com.pku.healthmanager.Variable;
import com.pku.myApplication.MyApplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class CounterActivity extends Activity implements OnClickListener{
	static ImageView counter_connect;
	static TextView tvCurrentSteps;
	private TextView tvTargetSteps;
	static TextView tvProgress;
	private Button button_day;
	private Button button_week;
	static ProgressBar progressBar;
	public static ViewFlipper flipper;
	private GestureDetector detector;
	private LinearLayout counterLayout;
	private LinearLayout counterHistoryLayout;
	private LinearLayout linearLayoutChart;
	private long oneday = 86400000l;
	private String firstdate;
	private String twicedate;
	private String thirddate;
	private String fourthdate;
	private String fifthdate;
	private String sixthdate;
	private String seventhdate;
	private String eighthdate;
	private SharedPreferences sp;
	private GraphicalView mchartView_week;
	private GraphicalView mchartView_day;
	private String[] xdate = new String[7];
	private String[] xhour = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
//	public static String currentSteps;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.counterflipper);
		LayoutInflater layoutInflater = LayoutInflater.from(this); 
		counterLayout = (LinearLayout) layoutInflater.inflate(R.layout.counter, null); 
		counterHistoryLayout = (LinearLayout) layoutInflater.inflate(R.layout.counterhistory, null); 
		counter_connect = (ImageView)counterLayout.findViewById(R.id.counter_connect);
		tvCurrentSteps = (TextView)counterLayout.findViewById(R.id.counter_currentSteps);
		tvTargetSteps = (TextView)counterLayout.findViewById(R.id.counter_targetSteps);
		tvProgress = (TextView)counterLayout.findViewById(R.id.counter_progress);
		progressBar = (ProgressBar)counterLayout.findViewById(R.id.progressBar);
		button_day = (Button)counterHistoryLayout.findViewById(R.id.day);
		button_week = (Button)counterHistoryLayout.findViewById(R.id.week);
		button_day.setOnClickListener(this);
		button_week.setOnClickListener(this);
		button_week.setEnabled(false);
		progressBar.setMax(100);
		flipper = (ViewFlipper)findViewById(R.id.viewFlipper);
		flipper.addView(counterLayout);
		flipper.addView(counterHistoryLayout);
		detector = new GestureDetector(this,new MyGestureDetector());
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		seventhdate = format.format(System.currentTimeMillis());
		firstdate = format.format((System.currentTimeMillis()-oneday*6));
		twicedate = format.format((System.currentTimeMillis()-oneday*5));
		thirddate = format.format((System.currentTimeMillis()-oneday*4));
		fourthdate = format.format((System.currentTimeMillis()-oneday*3));
		fifthdate = format.format((System.currentTimeMillis()-oneday*2));
		sixthdate = format.format((System.currentTimeMillis()-oneday));
		String[] date = {firstdate,twicedate,thirddate,fourthdate,fifthdate,sixthdate,seventhdate};
		for(int i = 0;i<date.length;i++){
			xdate[i] = date[i];
		}
		
		linearLayoutChart = (LinearLayout)findViewById(R.id.chart);
		mchartView_week = ChartFactory.getBarChartView(this, buildDataset(), buildRenderer(), Type.DEFAULT);
		linearLayoutChart.addView(mchartView_week, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		MyApplication myApp = (MyApplication) getApplication();
		sp = myApp.getSp();
	}
	public boolean dispatchTouchEvent(MotionEvent ev) {
		flipper.getParent().requestDisallowInterceptTouchEvent(true);  
		button_day.getParent().requestDisallowInterceptTouchEvent(true);
		button_week.getParent().requestDisallowInterceptTouchEvent(true);
        super.dispatchTouchEvent(ev);  
        onTouchEvent(ev);  //进行子View手势的相应操作  
        return true; 
	}  
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		return detector.onTouchEvent(event);
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.counter, menu);
		return true;
	}
	public void onStart(){
		super.onStart();
		display();
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
				counter_connect.setBackgroundResource(R.drawable.split_left);
				break;
			case 2:
				counter_connect.setBackgroundResource(R.drawable.split_right);
				break;	
			case 3:
				tvCurrentSteps.setText(Variable.currentSteps);
				tvProgress.setText(Variable.percent);
				progressBar.setProgress((int)Double.parseDouble(Variable.percent.split("%")[0]));
				HealthActivity.setTab(1);
				break;
			}
		}
	};
	public void display(){
		String originaldate = sp.getString("originaldate", "");
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		String currentdate = format.format(date);
		if(!currentdate.equals(originaldate)){
			sp.edit().putString("originaldate", currentdate)
			.putString("todaysteps","0").putString("hoursteps", "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0")
			.putString("percent", "0%").commit();
		}			
		Variable.currentSteps = sp.getString("todaysteps", "0");
		Variable.percent = sp.getString("percent","0%");
		Variable.targetSteps = sp.getString("运动量预设", "20000");
		tvCurrentSteps.setText(Variable.currentSteps);
		tvProgress.setText(Variable.percent);
		tvTargetSteps.setText(Variable.targetSteps);
		progressBar.setProgress((int)Double.parseDouble(Variable.percent.split("%")[0]));
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.day:
			button_day.setBackgroundResource(R.drawable.ic_preference_left_pressed);
			button_day.setTextColor(Color.parseColor("#ffffff"));
			button_week.setBackgroundResource(R.drawable.ic_preference_right_normal);
			button_week.setTextColor(Color.parseColor("#222222"));
			button_day.setEnabled(false);
			button_week.setEnabled(true);
			linearLayoutChart.removeAllViews();
			mchartView_day = ChartFactory.getBarChartView(this, buildDataset2(), buildRenderer2(), Type.DEFAULT);
			linearLayoutChart.addView(mchartView_day, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			break;
		case R.id.week:
			button_day.setBackgroundResource(R.drawable.ic_preference_left_normal);
			button_day.setTextColor(Color.parseColor("#222222"));
			button_week.setBackgroundResource(R.drawable.ic_preference_right_pressed);
			button_week.setTextColor(Color.parseColor("#ffffff"));
			button_week.setEnabled(false);
			button_day.setEnabled(true);
			linearLayoutChart.removeAllViews();
			mchartView_week = ChartFactory.getBarChartView(this, buildDataset(), buildRenderer(), Type.DEFAULT);
			linearLayoutChart.addView(mchartView_week, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			break;
		}		  
	}
	protected XYMultipleSeriesDataset buildDataset() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		double[] yValues = new double[7];
		int step6 = sp.getInt(seventhdate, 0);
		yValues[6] = (double)step6;
		int step5 = sp.getInt(sixthdate, 0);
		yValues[5] = (double)step5;
		int step4 = sp.getInt(fifthdate, 0);
		yValues[4] = (double)step4;
		int step3 = sp.getInt(fourthdate, 0);
		yValues[3] = (double)step3;
		int step2 = sp.getInt(thirddate, 0);
		yValues[2] = (double)step2;
		int step1 = sp.getInt(twicedate, 0);
		yValues[1] = (double)step1;
		int step0 = sp.getInt(firstdate, 0);
		yValues[0] = (double)step0;
		
		CategorySeries series = new CategorySeries("");
		for (int i = 0; i < 7; i ++){		
			series.add(yValues[i]);
		}
		dataset.addSeries(series.toXYSeries());		
		return dataset;
	}
	public XYMultipleSeriesRenderer buildRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	    r.setColor(Color.parseColor("#505050"));
	    r.setDisplayChartValues(true);
	    renderer.addSeriesRenderer(r);
	    setChartSettings(renderer);
	    return renderer;
	 }
	private void setChartSettings(XYMultipleSeriesRenderer renderer) {	
		Variable.targetSteps = sp.getString("运动量预设", "20000");
	    renderer.setChartTitle( "" );
	    renderer.setXTitle( "日期" );
	    renderer.setYTitle( "步数" );
	    renderer.setXAxisMin(0.5);
	    renderer.setXAxisMax(7.5);
	    renderer.setYAxisMin(0);
	    renderer.setChartValuesTextSize(24);
	    renderer.setLabelsTextSize(20);
	    renderer.setYAxisMax(Integer.parseInt(Variable.targetSteps,10));
	    renderer.setZoomEnabled(false, false);
	    renderer.setShowGrid(true);
	    renderer.setFitLegend(true);
	    renderer.setAxesColor(Color.parseColor("#505050"));	    
	    renderer.setMarginsColor(Color.parseColor("#FFFFFF"));
//	    renderer.setInScroll(false);
//	    renderer.setBackgroundColor(Color.parseColor("#ffaa66"));
//	    renderer.setApplyBackgroundColor(true);
//	    renderer.setZoomButtonsVisible(true);
	    renderer.setZoomEnabled(false);
	    renderer.setPanEnabled(false, false);
//	    renderer.setMargins(new int[] { 40, 30, 15, 0 });
	    renderer.setBarSpacing(0.2f);
	    renderer.setXLabelsAlign(Align.CENTER);	    
	    renderer.setXLabelsColor(Color.parseColor("#505050"));
	    renderer.setYLabelsColor(0, Color.parseColor("#505050"));
	    for (int i = 0; i < 7; i++) {  
            renderer.addXTextLabel(i+1,xdate[i]); 
        }  
	    renderer.setXLabels(0);
	 }
	protected XYMultipleSeriesDataset buildDataset2() {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		String hoursteps = sp.getString("hoursteps", "");
		if(hoursteps == "")
			hoursteps = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
//		Log.e("hourstepString", hoursteps);
		double[] doubleValues = new double[24];
		try{
			String [] hourstepArray = hoursteps.split(",");		
			for(int i = 0; i<24;i++){
				doubleValues[i] = Double.parseDouble(hourstepArray[i]);
			}			
		}catch(Exception e){};
		CategorySeries series = new CategorySeries("");
		for (int i = 0; i < doubleValues.length; i ++){		
			series.add(doubleValues[i]);
		}
		dataset.addSeries(series.toXYSeries());		
		return dataset;
	}
	public XYMultipleSeriesRenderer buildRenderer2() {
		XYMultipleSeriesRenderer renderer2 = new XYMultipleSeriesRenderer();
	    SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	    r.setColor(Color.parseColor("#505050"));
	    r.setDisplayChartValues(true);
	    renderer2.addSeriesRenderer(r);
	    setChartSettings2(renderer2);
	    return renderer2;
	 }
	private void setChartSettings2(XYMultipleSeriesRenderer renderer2) {
		Variable.targetSteps = sp.getString("运动量预设", "20000");
	    renderer2.setChartTitle( "" );
	    renderer2.setXTitle( "日期" );
	    renderer2.setYTitle( "步数" );
	    renderer2.setXAxisMin(0.5);
	    renderer2.setXAxisMax(24.5);
	    renderer2.setYAxisMin(0);
	    renderer2.setLabelsTextSize(13);
	    renderer2.setChartValuesTextSize(14);
	    renderer2.setAxesColor(Color.parseColor("#505050"));
	    renderer2.setYAxisMax(Integer.parseInt(Variable.targetSteps,10));
	    renderer2.setZoomEnabled(false, false);	 
	    renderer2.setMarginsColor(Color.parseColor("#FFFFFF"));
//	    renderer2.setShowGrid(true);
	    renderer2.setFitLegend(true);
//	    renderer.setInScroll(false);
//	    renderer.setBackgroundColor(Color.parseColor("#ffffff"));
//	    renderer.setApplyBackgroundColor(true);
//	    renderer.setZoomButtonsVisible(true);
	    renderer2.setZoomEnabled(false);
	    renderer2.setPanEnabled(false, false);
//	    renderer.setMargins(new int[] { 40, 30, 15, 0 });
	    renderer2.setBarSpacing(0.2f);
	    renderer2.setXLabelsColor(Color.parseColor("#505050"));
	    renderer2.setYLabelsColor(0, Color.parseColor("#505050"));
	    renderer2.setXLabelsAlign(Align.CENTER);	
	    renderer2.setXLabels(0);
	    for (int i = 0; i < 24; i++) {  
            renderer2.addXTextLabel(i+1,xhour[i]); 
        }  
	 }
}

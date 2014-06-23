package com.pku.healthmanager;

import android.os.Bundle;
import android.app.Activity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class ScaleActivity extends Activity {
	private GestureDetector detector;
	public static ViewFlipper flipper;
	private LinearLayout scaleLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.counterflipper);
		LayoutInflater layoutInflater = LayoutInflater.from(this); 
		scaleLayout = (LinearLayout) layoutInflater.inflate(R.layout.scale, null); 
		detector = new GestureDetector(this,new MyGestureDetector());
		flipper = (ViewFlipper)findViewById(R.id.viewFlipper);
		flipper.addView(scaleLayout);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		return detector.onTouchEvent(event);
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		flipper.getParent().requestDisallowInterceptTouchEvent(true);  
        super.dispatchTouchEvent(ev);  
        onTouchEvent(ev);  //进行子View手势的相应操作  
        return true; 
	} 

}

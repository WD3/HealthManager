package com.pku.healthmanager;

import com.pku.countermanager.CounterActivity;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class MyGestureDetector implements OnGestureListener {

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if ((e2.getRawX() - e1.getRawX()) > 80) {
			System.out.println("-----ÍùÓÒ»¬");
			HealthActivity.showPre();
			return true;
		}

		if ((e1.getRawX() - e2.getRawX()) > 80) {
			System.out.println("-----Íù×ó»¬");
			HealthActivity.showNext();
			return true;
		}
		if ((e2.getRawY() - e1.getRawY()) > 80) {
			switch (HealthActivity.tabHost.getCurrentTab()) {
			case 0:
				break;
			case 1:
				CounterActivity.flipper.showNext();
				break;
			case 2:
				break;
			case 3:
				break;
			}
			System.out.println("------ÍùÏÂ»¬");
			return true;
		}
		if ((e1.getRawY() - e2.getRawY()) > 80) {
			switch (HealthActivity.tabHost.getCurrentTab()) {
			case 0:
				break;
			case 1:
				CounterActivity.flipper.showPrevious();
				break;
			case 2:
				break;
			case 3:
				break;
			}
			System.out.println("------ÍùÉÏ»¬");
			return true;
		}
		return false;
	}

}

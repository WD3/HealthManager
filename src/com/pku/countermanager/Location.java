package com.pku.countermanager;

import java.util.List;

import com.baidu.location.*;

import android.app.Application;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.os.Process;
import android.os.Vibrator;

public class Location{

	public LocationClient mLocationClient = null;
	public String mTime;  
	public String mAddress;
	public String mLink;
	public MyLocationListenner myListener = new MyLocationListenner();
//	public MyLocationListenner listener = new MyLocationListenner();
//	public MyLocationListenner locListener = new MyLocationListenner();
	public TextView mTv;
//	public NotifyLister mNotifyer=null;
	public Vibrator mVibrator01;
	public String TAG = "LocTestDemo";
	
	
	public Location(Context context) {
		mLocationClient = new LocationClient( context );
//		locationClient = new LocationClient( this );
//		LocationClient = new LocationClient( this );
		mLocationClient.registerLocationListener( myListener );
//		locationClient.registerLocationListener( listener );
//		LocationClient.registerLocationListener( locListener );
		//λ��������ش���
//		mNotifyer = new NotifyLister();
//		mNotifyer.SetNotifyLocation(40.047883,116.312564,3000,"gps");//4����������Ҫλ�����ѵĵ�����꣬���庬������Ϊ��γ�ȣ����ȣ����뷶Χ������ϵ����(gcj02,gps,bd09,bd09ll)
//		mLocationClient.registerNotify(mNotifyer);
		
		System.out.println("��ʼ��λ");
	}
	
	
	/**
	 * ��������������λ�õ�ʱ�򣬸�ʽ�����ַ������������Ļ��
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			
//			sb.append("ʱ��: ");
			mTime = location.getTime();
//			sb.append("���� : ");
//			sb.append((int)location.getRadius()+"��");			
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				mLink = "http://api.map.baidu.com/geocoder?location="+location.getLatitude()+","+location.getLongitude()+"&coord_type=gcj02&output=html";
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
					
//				sb2.append("��ַ : ");
				mAddress = location.getAddrStr();
//				sb.append("��ַ���� : ");
				mLink = "http://api.map.baidu.com/geocoder?location="+location.getLatitude()+","+location.getLongitude()+"&coord_type=gcj02&output=html";
				Log.i(TAG, mTime+mAddress + mLink);				
			}			
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}

	}
	
//	public class NotifyLister extends BDNotifyListener{
//		public void onNotify(BDLocation mlocation, float distance){
//			mVibrator01.vibrate(1000);
//		}
//	}
}
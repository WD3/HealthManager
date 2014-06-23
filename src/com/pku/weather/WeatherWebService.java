package com.pku.weather;

import java.util.List;

import org.ksoap2.serialization.SoapObject;

import com.pku.healthmanager.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherWebService {
	private Context context;
	private TextView text;
	private Button city_btn;
	private static final int CITY = 0x11;
	private String city_str;
	private TextView city_text;
	private RelativeLayout relativeLayout;
	private Spinner province_spinner;
	private Spinner city_spinner;
	private List<String> provinces;
	private List<String> citys;
	private SharedPreferences preference;

	public WeatherWebService(RelativeLayout relativeLayout, Context context) {
		this.context = context;
		this.relativeLayout = relativeLayout;
		preference = PreferenceManager.getDefaultSharedPreferences(context);
		city_str = readSharpPreference();

		city_text = (TextView) relativeLayout.findViewById(R.id.city);
		city_text.setText(city_str);

		city_btn = (Button) relativeLayout.findViewById(R.id.city_button);

		city_btn.setOnClickListener(new ClickEvent());

		relativeLayout.findViewById(R.id.content_today_layout).getBackground()
				.setAlpha(120);

		refresh(city_str);

	}

	class ClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.city_button:

				show_dialog(CITY);

				break;

			default:
				break;
			}

		}

	}

	public void showTast(String string) {
		Toast.makeText(context, string, 1).show();

	}

	public void show_dialog(int cityId) {

		switch (cityId) {
		case CITY:

			// ȡ��city_layout.xml�е���ͼ
			final View view = LayoutInflater.from(context).inflate(
					R.layout.city_layout, null);

			// ʡ��Spinner
			province_spinner = (Spinner) view.findViewById(R.id.province_spinner);
			// ����Spinner
			city_spinner = (Spinner) view.findViewById(R.id.city_spinner);

			// ʡ���б�
			provinces = WebServiceUtil.getProvinceList();
			ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_spinner_item, provinces);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			province_spinner.setAdapter(adapter);
			// ʡ��Spinner������
			province_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0,View arg1, int position, long arg3) {
					citys = WebServiceUtil.getCityListByProvince(provinces.get(position));
					ArrayAdapter adapter1 = new ArrayAdapter(context,android.R.layout.simple_spinner_item, citys);
					adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					city_spinner.setAdapter(adapter1);
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
						
			// ����Spinner������
			city_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0,View arg1, int position, long arg3) {
					city_str = citys.get(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});					
			// ѡ����жԻ���
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle("��ѡ����������");
			dialog.setView(view);
			dialog.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					city_text.setText(city_str);
					writeSharpPreference(city_str);
					refresh(city_str);
				}
			});
						
			dialog.setNegativeButton("ȡ��",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});						
			dialog.show();

			break;

		default:
			break;
		}

	}

	public void refresh(String city_str) {
		System.out.println("city"+city_str);
		SoapObject detail = WebServiceUtil.getWeatherByCity(city_str);

		try {
			// ȡ��<string>10��13�� ����תС��</string>�е�����
			String date = detail.getProperty(7).toString();
			// ��"10��13�� ����תС��"��ֳ���������
			String[] date_array = date.split(" ");
			TextView today_text = (TextView) relativeLayout
					.findViewById(R.id.today);
			today_text.setText(date_array[0]);

			// ȡ��<string>���� ����</string>�е�����
			TextView city_text = (TextView) relativeLayout
					.findViewById(R.id.city_text);
			city_text.setText(detail.getProperty(1).toString());

			TextView today_weather = (TextView) relativeLayout
					.findViewById(R.id.today_weather);
			today_weather.setText(date_array[1]);

			// ȡ��<string>15��/21��</string>�е�����
			TextView qiweng_text = (TextView) relativeLayout
					.findViewById(R.id.qiweng);
			qiweng_text.setText(detail.getProperty(8).toString());

			// ȡ��<string>��������ʵ�������£�20�棻����/���������Ϸ�
			// 2����ʪ�ȣ�79%</string>�е�����,��ͨ��":"��ֳ�����
			TextView shidu_text = (TextView) relativeLayout
					.findViewById(R.id.shidu);
			String date1 = detail.getProperty(4).toString();
			shidu_text.setText(date1.split("��")[4]);

			// ȡ��<string>������3-4��</string>�е�����
			TextView fengli_text = (TextView) relativeLayout
					.findViewById(R.id.fengli);
			fengli_text.setText(detail.getProperty(9).toString());

			// ȡ��<string>��������������������ǿ�ȣ�����</string>�е�����,��ͨ��";"���,��ͨ��":"���,�������,ȡ��������Ҫ������
			String date2 = detail.getProperty(5).toString();
			String[] date2_array = date2.split("��");
			TextView kongqi_text = (TextView) relativeLayout
					.findViewById(R.id.kongqi);
			kongqi_text.setText(date2_array[0].split("��")[1]);

			TextView zhiwai_text = (TextView) relativeLayout
					.findViewById(R.id.zhiwai);
			zhiwai_text.setText(date2_array[1].split("��")[1]);

			// ����С��ʿ����
			// <string>����ָ��������ˬ�������ų�������ӵ���ȴ������װ������������������֯�����������׺ͳ��㡣��ðָ������Ȼ�¶����˵������ϴ��Խ��׷�����ð�����ʽ�����������ע���ʵ�������
			// �˶�ָ�������죬�����˿�չ���ֻ������˶���ϴ��ָ�����ϲ���ϴ����·��������ˮ�����ִ���ϴ������Ҫ���ý�����ˮ������׼������ɹָ��������������������ˮ�ֵ�Ѹ����������̫������ɹ������Ҫ��ɹ���뾡��ѡ��ͨ��ĵص㡣
			// ����ָ�������죬���Դ󣬵��¶����ˣ�������˵���Ǻ��������������������������Σ������Ծ������ܴ���Ȼ�ķ�⡣·��ָ�������죬·��Ƚϸ��·���Ϻá����ʶ�ָ�����¶����ˣ����������������������������£���е��Ƚ���ˬ�����ʡ�
			// ������Ⱦָ�����������������ڿ�����Ⱦ��ϡ�͡���ɢ����������������������������ָ�������������߷��������������ر�������������ڻ��⣬����Ϳ��SPF��8-12֮��ķ�ɹ����Ʒ��</string>
			String[] xiaotieshi = detail.getProperty(6).toString().split("\n");
			TextView xiaotieshi_text = (TextView) relativeLayout
					.findViewById(R.id.xiaotieshi);
			xiaotieshi_text.setText(xiaotieshi[0]);

			// ���õ���ͼƬ
			ImageView image = (ImageView) relativeLayout
					.findViewById(R.id.imageView1);
			int icon = parseIcon(detail.getProperty(10).toString());
			image.setImageResource(icon);

			// ȡ�õڶ�����������
			String[] date_str = detail.getProperty(12).toString().split(" ");
			TextView tomorrow_date = (TextView) relativeLayout
					.findViewById(R.id.tomorrow_date);
			tomorrow_date.setText(date_str[0]);

			TextView tomorrow_qiweng = (TextView) relativeLayout
					.findViewById(R.id.tomorrow_qiweng);
			tomorrow_qiweng.setText(detail.getProperty(13).toString());

			TextView tomorrow_tianqi = (TextView) relativeLayout
					.findViewById(R.id.tomorrow_tianqi);
			tomorrow_tianqi.setText(date_str[1]);

			ImageView tomorrow_image = (ImageView) relativeLayout
					.findViewById(R.id.tomorrow_image);
			int icon1 = parseIcon(detail.getProperty(15).toString());
			tomorrow_image.setImageResource(icon1);

			// ȡ�õ�������������
			String[] date_str1 = detail.getProperty(17).toString().split(" ");
			TextView afterday_date = (TextView) relativeLayout
					.findViewById(R.id.afterday_date);
			afterday_date.setText(date_str1[0]);

			TextView afterday_qiweng = (TextView) relativeLayout
					.findViewById(R.id.afterday_qiweng);
			afterday_qiweng.setText(detail.getProperty(18).toString());

			TextView afterday_tianqi = (TextView) relativeLayout
					.findViewById(R.id.afterday_tianqi);
			afterday_tianqi.setText(date_str1[1]);

			ImageView afterday_image = (ImageView) relativeLayout
					.findViewById(R.id.afterday_image);
			int icon2 = parseIcon(detail.getProperty(20).toString());
			afterday_image.setImageResource(icon2);

			// ȡ�õ�������������
			String[] date_str3 = detail.getProperty(22).toString().split(" ");
			TextView nextday_date = (TextView) relativeLayout
					.findViewById(R.id.nextday_date);
			nextday_date.setText(date_str3[0]);

			TextView nextday_qiweng = (TextView) relativeLayout
					.findViewById(R.id.nextday_qiweng);
			nextday_qiweng.setText(detail.getProperty(23).toString());

			TextView nextday_tianqi = (TextView) relativeLayout
					.findViewById(R.id.nextday_tianqi);
			nextday_tianqi.setText(date_str3[1]);

			ImageView nextday_image = (ImageView) relativeLayout
					.findViewById(R.id.nextday_image);
			int icon3 = parseIcon(detail.getProperty(25).toString());
			nextday_image.setImageResource(icon3);

		} catch (Exception e) {
			showTast("�������ӳ����޷���ʾ��ǰ����");
		}

	}

	// ���߷������÷�������ѷ��ص�����ͼ���ַ�����ת��Ϊ�����ͼƬ��ԴID��
	private int parseIcon(String strIcon) {
		if (strIcon == null)
			return -1;
		if ("0.gif".equals(strIcon))
			return R.drawable.a_0;
		if ("1.gif".equals(strIcon))
			return R.drawable.a_1;
		if ("2.gif".equals(strIcon))
			return R.drawable.a_2;
		if ("3.gif".equals(strIcon))
			return R.drawable.a_3;
		if ("4.gif".equals(strIcon))
			return R.drawable.a_4;
		if ("5.gif".equals(strIcon))
			return R.drawable.a_5;
		if ("6.gif".equals(strIcon))
			return R.drawable.a_6;
		if ("7.gif".equals(strIcon))
			return R.drawable.a_7;
		if ("8.gif".equals(strIcon))
			return R.drawable.a_8;
		if ("9.gif".equals(strIcon))
			return R.drawable.a_9;
		if ("10.gif".equals(strIcon))
			return R.drawable.a_10;
		if ("11.gif".equals(strIcon))
			return R.drawable.a_11;
		if ("12.gif".equals(strIcon))
			return R.drawable.a_12;
		if ("13.gif".equals(strIcon))
			return R.drawable.a_13;
		if ("14.gif".equals(strIcon))
			return R.drawable.a_14;
		if ("15.gif".equals(strIcon))
			return R.drawable.a_15;
		if ("16.gif".equals(strIcon))
			return R.drawable.a_16;
		if ("17.gif".equals(strIcon))
			return R.drawable.a_17;
		if ("18.gif".equals(strIcon))
			return R.drawable.a_18;
		if ("19.gif".equals(strIcon))
			return R.drawable.a_19;
		if ("20.gif".equals(strIcon))
			return R.drawable.a_20;
		if ("21.gif".equals(strIcon))
			return R.drawable.a_21;
		if ("22.gif".equals(strIcon))
			return R.drawable.a_22;
		if ("23.gif".equals(strIcon))
			return R.drawable.a_23;
		if ("24.gif".equals(strIcon))
			return R.drawable.a_24;
		if ("25.gif".equals(strIcon))
			return R.drawable.a_25;
		if ("26.gif".equals(strIcon))
			return R.drawable.a_26;
		if ("27.gif".equals(strIcon))
			return R.drawable.a_27;
		if ("28.gif".equals(strIcon))
			return R.drawable.a_28;
		if ("29.gif".equals(strIcon))
			return R.drawable.a_29;
		if ("30.gif".equals(strIcon))
			return R.drawable.a_30;
		if ("31.gif".equals(strIcon))
			return R.drawable.a_31;
		return 0;
	}

	public void writeSharpPreference(String string) {

		SharedPreferences.Editor editor = preference.edit();
		editor.putString("city", string);
		editor.commit();

	}

	public String readSharpPreference() {

		String city = preference.getString("city", "����");

		return city;

	}
}
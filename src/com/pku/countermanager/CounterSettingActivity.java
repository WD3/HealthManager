package com.pku.countermanager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pku.healthmanager.BluetoothManager;
import com.pku.healthmanager.MainActivity;
import com.pku.healthmanager.R;
import com.pku.healthmanager.Variable;
import com.pku.myApplication.MyApplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CounterSettingActivity extends Activity implements OnClickListener {
	private static TextView tvUser;
	private EditText etEmergencyTel;
	private EditText etTargetSteps;
	private EditText etSedentariness;
	private Button counter_discovery;
	private static SharedPreferences sp;
	private MyApplication myApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_counter_setting);
		tvUser = (TextView) findViewById(R.id.counter_id);
		etEmergencyTel = (EditText) findViewById(R.id.emergencyTel_setting);
		etTargetSteps = (EditText) findViewById(R.id.targetSteps_setting);
		etSedentariness = (EditText) findViewById(R.id.sedentariness_setting);
		counter_discovery = (Button) findViewById(R.id.counter_discovery);
		counter_discovery.setOnClickListener(this);
		myApp = (MyApplication)getApplication();
		sp = myApp.getSp();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.counter_setting, menu);
		return true;
	}	

	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String counter_address = sp.getString("counter_address", "");
				tvUser.setText(counter_address.replaceAll(":", ""));
				break;
			}
		}
	};

	public void onPause() {
		super.onPause();
		save();
		Toast.makeText(this, "信息已保存", Toast.LENGTH_SHORT).show();
		if (!isPhoneNumberValid(etEmergencyTel.getText().toString()))
			Toast.makeText(this, "对不起，您输入的号码有误", Toast.LENGTH_SHORT).show();
	}

	public void onStart() {
		super.onStart();
		read();
	}

	public void save() {
		Variable.sedentariness = etSedentariness.getText().toString();
		Variable.targetSteps = etTargetSteps.getText().toString();
		Variable.emergencyTel = etEmergencyTel.getText().toString();
		String oSedentariness = sp.getString("久坐报警", "");
		if (!oSedentariness.equals(Variable.sedentariness))
			sp.edit().putInt("sedentariness_flag", 1).commit();
		String oTargetSteps = sp.getString("运动量预设", "");
		if (!oTargetSteps.equals(Variable.targetSteps)){
			sp.edit().putInt("targetSteps_flag", 1).commit();
			if (Integer.parseInt(Variable.targetSteps) == 0)
				Variable.percent = "0%";
			else
				Variable.percent =String.format("%.2f",Double.parseDouble(Variable.currentSteps) * 100/ Double.parseDouble(Variable.targetSteps))+"%";
			sp.edit().putString("percent", Variable.percent).commit();
		}
		sp.edit().putString("紧急联系人",Variable.emergencyTel)
				.putString("久坐报警", Variable.sedentariness)
				.putString("运动量预设", Variable.targetSteps).commit();
	}

	public void read() {
		etEmergencyTel.setText(sp.getString("紧急联系人", "13810001000"));
		etSedentariness.setText(sp.getString("久坐报警", "30"));
		etTargetSteps.setText(sp.getString("运动量预设", "20000"));
		tvUser.setText(sp.getString("counter_address", "").replaceAll(":", ""));
	}

	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		/*
		 * 可接受的电话格式有：
		 */
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		/*
		 * 可接受的电话格式有：
		 */
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);

		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.counter_discovery:
			myApp.setExit2(false);
			sp.edit().putString("counter_address", "").putBoolean("counter_reset", true).commit();
			new BluetoothManager(this,sp).discovery();
			break;
		}
	}
}

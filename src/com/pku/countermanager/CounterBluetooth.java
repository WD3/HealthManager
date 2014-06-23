package com.pku.countermanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.pku.healthmanager.BluetoothManager;
import com.pku.healthmanager.MainActivity;
import com.pku.healthmanager.MyPackageManager;
import com.pku.healthmanager.Variable;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;

public class CounterBluetooth {
	public BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	private final int CONNTECT_INTERVAL = 500;
	private BluetoothDevice btdevice;
	private BluetoothSocket socket;
	private Context context;
	private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	private OutputStream os;
	private InputStream is;
	public static boolean exit = true;
	private String stepshistory;
	static int battery;
	private String[] array;
	private String head;
	private String starttime;
	private String detail;
	private String[] array1;
	private String detail1;
	private String detail2;
//	public static int percent;
	public static boolean connected;

	private String line = "";
	private String acceleration;
	private File txtFile;
	private File txtDir;
	private FileOutputStream fos;
	private Location location;
	private MyPackageManager myPackageManager;

	public CounterBluetooth(Context context) {
		this.context = context;
		myPackageManager = new MyPackageManager(context);
	}
	public void connect() throws IOException {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				while (exit) {
					BluetoothManager.counter_address = MainActivity.sp.getString("counter_address", "");
					if (BluetoothManager.counter_address != "") {
						if (connected == false) {
							if (!adapter.isEnabled())
								adapter.enable();
							btdevice = adapter.getRemoteDevice(BluetoothManager.counter_address);
							CounterSettingActivity.SendMessage(CounterSettingActivity.handler, 1);
						}
						/*
						 * try{ socket =
						 * btdevice.createRfcommSocketToServiceRecord(uuid);
						 * }catch(IOException e){e.printStackTrace();};
						 */
						try {
							System.out.println("创建socket");
							Method m = btdevice.getClass().getMethod("createRfcommSocket",new Class[] { int.class });
							try {
								socket = (BluetoothSocket) m.invoke(btdevice, 1);
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (NoSuchMethodException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						try {
							Log.e("开始连接", "开始连接");
							if (adapter.isDiscovering())
								adapter.cancelDiscovery();
							socket.connect();
							connected = true;
							CounterActivity.SendMessage(CounterActivity.handler, 1);
							myPackageManager.openCounter();
							Log.e("连接", "连接完成");
							sendData();
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							Log.e("异常", e.getMessage());
							try {
								socket.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							CounterActivity.SendMessage(CounterActivity.handler, 2);
						}
					}
					try {
						Thread.sleep(CONNTECT_INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				Looper.loop();
			}
		});
		thread.start();
	}

	public void sendData() {
		try {
			os = socket.getOutputStream();
			is = socket.getInputStream();
			InputStreamReader isreader = new InputStreamReader(is);
			BufferedReader breader = new BufferedReader(isreader);

			Long date = System.currentTimeMillis();
			int time = (int) (date / 1000);
			String reply = "";
			SimpleDateFormat format = new SimpleDateFormat("MM-dd");
			String currentday = format.format(date);
			String originalday = MainActivity.sp.getString("currentday", "");
			boolean slipButton1_flag = MainActivity.sp.getBoolean("智能提醒", false);
			boolean slipButton3_flag = MainActivity.sp.getBoolean("手机提醒", false);
			boolean slipButton4_flag = MainActivity.sp.getBoolean("SOS报警",false);
			boolean reset = MainActivity.sp.getBoolean("reset", false);
			int sedentariness_flag = MainActivity.sp.getInt("sedentariness_flag", 0);
			int targetSteps_flag = MainActivity.sp.getInt("targetSteps_flag", 0);
			os.write("ConnectionOK\r\n".getBytes());
			reply = breader.readLine();
			if (reply.equals("ConnectionOK")) {
				if (!currentday.equals(originalday) || BluetoothManager.addressChanged || reset) {
					Log.e("第一次发", "第一次发");
					BluetoothManager.addressChanged = false;
					MainActivity.sp.edit().putString("currentday", currentday).putBoolean("reset", false).commit();
					os.write(("PDM,1.0,Data,Time\r\n").getBytes());
					reply = breader.readLine();
					Log.e("reply1", reply);
					if (reply.equals("CMDOK")) {
						os.write(intToByteArray(time));
						os.write("\r\n".getBytes());
						reply = breader.readLine();
						Log.e("reply2", reply);
					}
					if (reply.equals("DATAOK")) {
						os.write(("PDM,1.0,Data,Sedentariness\r\n").getBytes());
						reply = breader.readLine();
						Log.e("reply3", reply);
					}
					if (reply.equals("CMDOK")) {
						os.write((Variable.sedentariness + "\r\n")
								.getBytes());
						reply = breader.readLine();
					}
					if (reply.equals("DATAOK")) {
						os.write(("PDM,1.0,Data,TargetSteps\r\n").getBytes());
						reply = breader.readLine();
						Log.e("reply4", reply);
					}
					if (reply.equals("CMDOK")) {
						os.write((Variable.targetSteps + "\r\n").getBytes());
						reply = breader.readLine();
						Log.e("reply4", reply);
					}
					if (reply.equals("DATAOK")) {
						os.write("PDM,1.0,Data,HistorySteps\r\n".getBytes());
						stepshistory = breader.readLine();
						System.out.println("stepshistory:" + stepshistory);
						saveHistory();
						os.write("ConfigurationOK\r\n".getBytes());
					}
				} else if (sedentariness_flag == 1 && targetSteps_flag == 1) {
					MainActivity.sp.edit().putInt("sedentariness_flag", 0).putInt("targetSteps_flag", 0).commit();
					Log.e("修改久坐时间和目标步数", "修改久坐时间和目标步数");
					os.write(("PDM,1.0,Data,Sedentariness\r\n").getBytes());
					reply = breader.readLine();
					Log.e("reply3", reply);
					if (reply.equals("CMDOK")) {
						os.write((Variable.sedentariness + "\r\n")
								.getBytes());
						reply = breader.readLine();
					}
					if (reply.equals("DATAOK")) {
						os.write(("PDM,1.0,Data,TargetSteps\r\n").getBytes());
						reply = breader.readLine();
						Log.e("reply4", reply);
					}
					if (reply.equals("CMDOK")) {
						os.write((Variable.targetSteps + "\r\n").getBytes());
						reply = breader.readLine();
					}
					if (reply.equals("DATAOK"))
						os.write("ConfigurationOK\r\n".getBytes());
				} else if (sedentariness_flag == 1) {
					MainActivity.sp.edit().putInt("sedentariness_flag", 0)
							.commit();
					Log.e("修改久坐时间", "修改久坐时间");
					os.write(("PDM,1.0,Data,Sedentariness\r\n").getBytes());
					reply = breader.readLine();
					Log.e("reply3", reply);
					if (reply.equals("CMDOK")) {
						os.write((Variable.sedentariness + "\r\n")
								.getBytes());
						System.out.println(Variable.sedentariness);
						reply = breader.readLine();
					}
					if (reply.equals("DATAOK"))
						os.write("ConfigurationOK\r\n".getBytes());
				} else if (targetSteps_flag == 1) {
					MainActivity.sp.edit().putInt("targetSteps_flag", 0)
							.commit();
					Log.e("修改目标步数", "修改目标步数");
					os.write(("PDM,1.0,Data,TargetSteps\r\n").getBytes());
					reply = breader.readLine();
					Log.e("reply4", reply);
					if (reply.equals("CMDOK")) {
						os.write((Variable.targetSteps + "\r\n").getBytes());
						reply = breader.readLine();
					}
					if (reply.equals("DATAOK"))
						os.write("ConfigurationOK\r\n".getBytes());
				} else {
					Log.e("不用配置", "不用配置");
					os.write("ConfigurationOK\r\n".getBytes());
				}

				reply = breader.readLine();
				Log.e("reply5", reply);
				array = reply.split(":");
				head = array[0];
				if (head.contains("PDM,1.0,Data,Steps")) {
					try {
						starttime = array[1];
						detail = array[2];
						array1 = detail.split(";");
						detail1 = array1[0];
						detail2 = array1[1];
						saveSteps();
					} catch (Exception e) {
					}
					connected = false;
					try {
						Log.e("1", "连接出错啦");
						is.close();
						os.close();
						socket.close();
						CounterActivity.SendMessage(CounterActivity.handler, 2);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else if (head.contains("PDM,1.0,Command,SitTooLong")) {					
					connected = false;
					try {
						Log.e("1", "连接出错啦");
						is.close();
						os.close();
						socket.close();
						CounterActivity.SendMessage(CounterActivity.handler, 2);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else if (head.contains("PDM,1.0,Command,SOS")) {
					SOS();
					connected = false;
					try {
						Log.e("1", "连接出错啦");
						is.close();
						os.close();
						socket.close();
						CounterActivity.SendMessage(CounterActivity.handler, 2);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("2", "连接出错啦");
			connected = false;
			CounterActivity.SendMessage(CounterActivity.handler, 2);
			try {
				is.close();
				os.close();
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private byte[] intToByteArray(int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer
				: integer)) / 8;
		byte[] byteArray = new byte[4];
		for (int n = 0; n < byteNum; n++)
			byteArray[3 - n] = (byte) (integer >>> (n * 8));
		return (byteArray);
	}

	public void saveHistory() {
		if (!stepshistory.equals(0)) {
			String[] history = stepshistory.split(",");
			long[] datelong = new long[Integer.parseInt(history[0], 10)];
			int[] stepsint = new int[Integer.parseInt(history[0], 10)];
			for (int i = 0; i < Integer.parseInt(history[0], 10); i++) {
				history[2 * i + 1] = history[2 * i + 1] + "000";
				datelong[i] = Long.parseLong(history[2 * i + 1]);
				stepsint[i] = Integer.parseInt(history[2 * i + 2], 10);
			}
			SimpleDateFormat format = new SimpleDateFormat("MM-dd");
			String[] historystring = new String[Integer
					.parseInt(history[0], 10)];
			for (int i = 0; i < Integer.parseInt(history[0], 10); i++) {
				historystring[i] = format.format(datelong[i]);
				System.out.println(historystring[i] + stepsint[i]);
				MainActivity.sp.edit().putInt(historystring[i], stepsint[i])
						.commit();
			}
		}
	}

	public void saveSteps() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MM-dd");
		String currentdate = format.format(date);
		Variable.currentSteps = detail1;
		Variable.targetSteps = MainActivity.sp.getString("运动量预设", "20000");
		if (Integer.parseInt(Variable.targetSteps) == 0)
			Variable.percent = "0%";
		else
			Variable.percent =String.format("%.2f",Double.parseDouble(Variable.currentSteps) * 100/ Double.parseDouble(Variable.targetSteps))+"%";
		System.out.println(detail1+","+Variable.percent);
		CounterActivity.SendMessage(CounterActivity.handler, 3);
		MainActivity.SendMessage(MainActivity.handler, 1);
		MainActivity.sp.edit().putInt(currentdate, Integer.parseInt(detail1, 10))
				.putString("hoursteps", detail2)
				.putString("todaysteps", detail1)
				.putString("percent", Variable.percent).commit();
		String originaldate = MainActivity.sp.getString("originaldate", "");
		if (!originaldate.equals(currentdate))
			MainActivity.sp.edit().putString("originaldate", currentdate)
					.commit();
	}

	private void SOS() {
		String tel = MainActivity.sp.getString("紧急联系人", "");
		if (CounterSettingActivity.isPhoneNumberValid(tel)) {
			Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + tel));
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			LocationClient mLocClient = ((Location) context.getApplicationContext()).mLocationClient;
			LocationClientOption option = new LocationClientOption();
			option.setAddrType("all");
			option.setPriority(LocationClientOption.NetWorkFirst);
			mLocClient.setLocOption(option);
			mLocClient.start();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Long date = System.currentTimeMillis();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String locationTime = format.format(date);
			SmsManager smsManager = SmsManager.getDefault();
			if (((Location) context.getApplicationContext()).mAddress != null) {
				List<String> texts = smsManager
						.divideMessage("时间:"
								+ locationTime
								+ ";地址:"
								+ ((Location) context.getApplicationContext()).mAddress);
				for (String text : texts) {
					smsManager.sendTextMessage(tel, null, text, null, null);
					Log.e("sendMSG", "success");
				}
			}
			if (((Location) context.getApplicationContext()).mLink != null) {
				List<String> text1 = smsManager
						.divideMessage(((Location) context
								.getApplicationContext()).mLink);
				for (String text : text1) {
					smsManager.sendTextMessage(tel, null, text, null, null);
				}
			} else
				smsManager.sendTextMessage(tel, null, "定位失败", null, null);
			System.out.println("发送成功");
			if (((Location) context.getApplicationContext()).mLink != null) {
				String deviceSN = BluetoothManager.counter_address.replaceAll(":", "");
				String time = locationTime;
				String address = ((Location) context.getApplicationContext()).mAddress;
				String link = ((Location) context.getApplicationContext()).mLink;
				Thread t = new AlarmThread(deviceSN, time, address, link);
				t.start();
				Log.e("send", deviceSN + time + address + link);
			}
			mLocClient.stop();
		}
	}



	public void newFile() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String fileName = formatter
				.format(new Date(System.currentTimeMillis()));
		try {
			txtDir = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/0SENSOR_DATA");
			if (!txtDir.exists())
				txtDir.mkdirs();
			txtFile = new File(Environment.getExternalStorageDirectory()
					.getPath()
					+ "/0SENSOR_DATA/"
					+ fileName
					+ " originaldata.txt");
			if (!txtFile.exists())
				txtFile.createNewFile();
			fos = new FileOutputStream(txtFile);
			line = "version:1.0" + "\r\n" + "手机型号:" + android.os.Build.MODEL
					+ "\r\n" + "姓名:" + " " + "\r\n" + "身高:" + "  " + "\r\n"
					+ "体重:" + "  " + "\r\n" + "年龄:" + "  " + "\r\n" + "       "
					+ "time" + "        " + "        " + "a[x]" + "  "
					+ "        " + "a[y]" + "  " + "        " + "a[z]" + "\r\n";

			fos.write(line.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

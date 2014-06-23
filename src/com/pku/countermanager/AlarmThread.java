package com.pku.countermanager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

public class AlarmThread extends Thread {
	private String deviceSN;
	private String time;
	private String address;
	private String link;
	
	public AlarmThread(String deviceSN, String time, String address, String link) {
		this.deviceSN = deviceSN;
		this.time = time;
		this.address = address;
		this.link = link;
	}
	
	public void run() {
		try {
			Socket socket = new Socket("162.105.76.252", 2014);
			OutputStream output = socket.getOutputStream();
			JSONObject sendJson = new JSONObject();
			JSONObject msgJson = new JSONObject();
			try {
				msgJson.put("time", this.time);
				msgJson.put("address", this.address);
				msgJson.put("link", this.link);
				sendJson.put("deviceSN", this.deviceSN);
				sendJson.put("msgContent", msgJson);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String sendStr = sendJson.toString();
			byte[] bytes = sendStr.getBytes("UTF-8");
			output.write(bytes);
			output.close();
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



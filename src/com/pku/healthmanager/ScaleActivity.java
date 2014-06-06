package com.pku.healthmanager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ScaleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scale);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scale, menu);
		return true;
	}

}

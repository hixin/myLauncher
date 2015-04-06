package com.highxin.launcher01;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.highxin.R;

public class Message extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message);
		
	}

}

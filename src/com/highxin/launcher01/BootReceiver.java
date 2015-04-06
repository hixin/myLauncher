package com.highxin.launcher01;

import com.highxin.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		MainActivity main = new MainActivity();
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
			
			 main.getShowAppInfoLists();
			 SharedPreferences sp = context.getSharedPreferences("prefer", 0);
		     main.sClickinfo = sp.getString("info", "error");
		     main.sClickinfo = sp.getString("info", "error");
		}
		// ���չ㲥���豸��ɾ����һ��Ӧ�ó������
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			main.getShowAppInfoLists();
			SharedPreferences sp = context.getSharedPreferences("prefer",0);
		    main.sClickinfo = sp.getString("info", "error");
		}

	}

}

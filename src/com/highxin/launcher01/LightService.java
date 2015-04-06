package com.highxin.launcher01;


import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.IBinder;

public class LightService extends Service{

	
	private Camera camera = null;
	private Parameters parameters = null;
	public static boolean kaiguan = true; // ���忪��״̬:״̬Ϊfalse����״̬��״̬Ϊtrue���ر�״̬
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (kaiguan) {

			camera = Camera.open();
			parameters = camera.getParameters();
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// ����
			camera.setParameters(parameters);
			camera.startPreview();
			kaiguan = false;
		} 
		
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		if (!kaiguan) {
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// �ر�
			camera.setParameters(parameters);
			camera.stopPreview();
			camera.release();
			kaiguan = true;
		}
		
		super.onDestroy();
	}

}

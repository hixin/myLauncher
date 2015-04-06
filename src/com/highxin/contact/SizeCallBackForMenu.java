package com.highxin.contact;

import android.widget.Button;

public class SizeCallBackForMenu implements SizeCallBack {

	private Button menu;
	private int menuWidth;
	
	
	public SizeCallBackForMenu(Button menu){
		super();
		this.menu = menu;
	}
	@Override
	public void onGlobalLayout() {
		// TODO Auto-generated method stub
		this.menuWidth = this.menu.getMeasuredWidth() + SystemScreenInfo.CONTACT_GROUP_LABLE;
	}

	@Override
	public void getViewSize(int idx, int width, int height, int[] dims) {
		// TODO Auto-generated method stub
		dims[0] = width;
		dims[1] = height;
		
		/*��ͼ�����м���ͼ*/
		if(idx != 1){
			dims[0] = width - this.menuWidth;
		}
	}

}
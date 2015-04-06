package com.highxin.contact;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;

public class MyApplication extends Application {
	
//	private String name;
//	private  List<Map<String, Object>>  gv_applist;
//	private boolean switch_flag;
//	private boolean click_flag;
//	private boolean time_check;
//	private boolean firstload_flag;//判断桌面是否是第一次加载
//	private boolean home_flag;//判断桌面
//	private List<ResolveInfo> appInfos;
//	private List<ResolveInfo> showappInfos;//此变量
//	private int []clickinfo;
//	private String sClickinfo;
//	private int shortCutNum;
//	private int pageNum;
//	private int currIndex;// 当前页卡编号
	private List<ContactBean> contactBeanList;

	public void onCreate() {
		
		System.out.println(" ");
//		gv_applist = new ArrayList<Map<String,Object>>(); 
//		setSwitch_flag(false);
//		setClick_flag(false);
//		setTime_check(false);
//		setFirstload_flag(false);
//		appInfos = new ArrayList<ResolveInfo>();;
//		showappInfos = new ArrayList<ResolveInfo>();
//		setShortCutNum(0);
//		setPageNum(0);
//		setCurrIndex(0);
//		setHome_flag(false);
		
		Intent startService = new Intent(MyApplication.this, T9Service.class);
		startService(startService);
	}

//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public List<Map<String, Object>> getGv_applist() {
//		return gv_applist;
//	}
//
//	public void setGv_applist(List<Map<String, Object>> gv_applist) {
//		this.gv_applist = gv_applist;
//	}
//
//	public boolean isSwitch_flag() {
//		return switch_flag;
//	}
//
//	public void setSwitch_flag(boolean switch_flag) {
//		this.switch_flag = switch_flag;
//	}
//
//	public boolean isClick_flag() {
//		return click_flag;
//	}
//
//	public void setClick_flag(boolean click_flag) {
//		this.click_flag = click_flag;
//	}
//
//	public boolean isTime_check() {
//		return time_check;
//	}
//
//	public void setTime_check(boolean time_check) {
//		this.time_check = time_check;
//	}
//
//	public boolean isFirstload_flag() {
//		return firstload_flag;
//	}
//
//	public void setFirstload_flag(boolean firstload_flag) {
//		this.firstload_flag = firstload_flag;
//	}
//
//	public List<ResolveInfo> getAppInfos() {
//		return appInfos;
//	}
//
//	public void setAppInfos(List<ResolveInfo> appInfos) {
//		this.appInfos = appInfos;
//	}
//
//	public List<ResolveInfo> getShowappInfos() {
//		return showappInfos;
//	}
//
//	public void setShowappInfos(List<ResolveInfo> showappInfos) {
//		this.showappInfos = showappInfos;
//	}
//
//	public int[] getClickinfo() {
//		return clickinfo;
//	}
//
//	public void setClickinfo(int[] clickinfo) {
//		this.clickinfo = clickinfo;
//	}
//
//	public String getsClickinfo() {
//		return sClickinfo;
//	}
//
//	public void setsClickinfo(String sClickinfo) {
//		this.sClickinfo = sClickinfo;
//	}
//
//	public int getShortCutNum() {
//		return shortCutNum;
//	}
//
//	public void setShortCutNum(int shortCutNum) {
//		this.shortCutNum = shortCutNum;
//	}
//
//	public int getPageNum() {
//		return pageNum;
//	}
//
//	public void setPageNum(int pageNum) {
//		this.pageNum = pageNum;
//	}
//
//	public int getCurrIndex() {
//		return currIndex;
//	}
//
//	public void setCurrIndex(int currIndex) {
//		this.currIndex = currIndex;
//	}
//
	public List<ContactBean> getContactBeanList() {
		return contactBeanList;
	}

	public void setContactBeanList(List<ContactBean> contactBeanList) {
		this.contactBeanList = contactBeanList;
	}

//	public boolean isHome_flag() {
//		return home_flag;
//	}
//
//	public void setHome_flag(boolean home_flag) {
//		this.home_flag = home_flag;
//	}

}

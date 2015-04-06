package com.highxin.launcher01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.highxin.R;

import com.highxin.launcher01.MainActivity.GridViewAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class AppList extends Activity {
	ListView applv;
	boolean click_flag = false;

	// 当前可见的List顶端的一行的位置
	private int scrollPos = 0;
	// 当前第一个可见的item的偏移量
	private int scrollTop = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.applist);
		applv = (ListView) findViewById(R.id.list);

		applv.setAdapter(new ListViewAdapter(this, MainActivity.showappInfos));
		applv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent i = new Intent();
				String packageName = MainActivity.showappInfos.get(arg2).activityInfo.packageName;
				String mainActivityName = MainActivity.showappInfos.get(arg2).activityInfo.name;
				i.setComponent(new ComponentName(packageName, mainActivityName));
//				java.lang.System.out.println(packageName);
//				java.lang.System.out.println(mainActivityName);
				startActivity(i);
			}
		});

		applv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						AppList.this);
				builder.setTitle("是否要卸载该应用！");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								Uri packageUri = Uri.parse("package:"
										+ MainActivity.showappInfos.get(arg2).activityInfo.packageName);
								Intent deleteIntent = new Intent();
								deleteIntent = 
								deleteIntent.setAction(Intent.ACTION_DELETE);
								deleteIntent.setData(packageUri);
								startActivityForResult(deleteIntent, 100);
								//有删除意图就发送广播，重新获取应用列表	
							}
						}

				);
				builder.setNegativeButton("取消", null);
				builder.create().show();
				return true;
			}
		});

		applv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 保存当前第一个可见的item的索引和偏移量
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// scrollPos记录当前可见的List顶端的一行的位置
					scrollPos = applv.getFirstVisiblePosition();
				}
				View v = applv.getChildAt(0);
				scrollTop = (v == null) ? 0 : v.getTop();
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});

		ImageButton ib_back = (ImageButton) findViewById(R.id.ib_back);
		ib_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}

		});
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 switch(requestCode){
         case 100:{
        	 Intent delIntent = new Intent();
        	 delIntent.setAction("DELETE");
        	 sendBroadcast(delIntent);
         			
         }break;
         default:break;
        
      }
	}



	class ListViewAdapter extends BaseAdapter {

		LayoutInflater mInflater;
		List<ResolveInfo> pkInfos;

		public ListViewAdapter(Context context, List<ResolveInfo> appInfos) {
			mInflater = LayoutInflater.from(context);
			this.pkInfos = appInfos;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pkInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pkInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			MyListener myListener = null;

			myListener = new MyListener(position);
			convertView = mInflater.inflate(R.layout.applist_item, null);
			TextView ntv = (TextView) convertView.findViewById(R.id.name);
			ImageView iv = (ImageView) convertView.findViewById(R.id.img);
			ImageButton ibt_operate = (ImageButton) convertView
					.findViewById(R.id.ibt_operate);

			ntv.setText(pkInfos.get(position).loadLabel(getPackageManager()));
			iv.setImageDrawable(pkInfos.get(position).loadIcon(
					getPackageManager()));
			ibt_operate.setFocusable(false);
			ibt_operate.setOnClickListener(myListener);

			if (MainActivity.clickinfo[position] == 0) {
				ibt_operate.setImageDrawable(getResources().getDrawable(
						R.drawable.add));
			} else {
				ibt_operate.setImageDrawable(getResources().getDrawable(
						R.drawable.del));
			}
			return convertView;
		}

	}

	public class MyListener implements OnClickListener {
		int mPosition;
		int mPos;
		int shortCutNum = 0;//找个局部变量判断快捷图标的个数
		public MyListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View v) {
			String str=intArray2Str(MainActivity.clickinfo);
			Pattern p = Pattern.compile("[1]");
	        Matcher m = p.matcher(str);
	        shortCutNum = 0;
	        while(m.find()) {
	            shortCutNum++;
	        }
			
			if(shortCutNum<=24) {
				MainActivity.clickinfo[mPosition] = convert(MainActivity.clickinfo[mPosition]);
			}
			if (MainActivity.clickinfo[mPosition] == 1) {
				if(shortCutNum>=24) {
					Toast.makeText(AppList.this,"快捷图标已到上限！",Toast.LENGTH_LONG).show();
					MainActivity.clickinfo[mPosition] = convert(MainActivity.clickinfo[mPosition]);
					return;
				}
				Toast.makeText(
						AppList.this,
						MainActivity.showappInfos.get(mPosition).loadLabel(
								getPackageManager())
								+ "已经添加到屏幕", Toast.LENGTH_SHORT).show();

				String clickAppName = (String) MainActivity.showappInfos.get(
						mPosition).loadLabel(getPackageManager());
				Drawable clickAppIcon = MainActivity.showappInfos
						.get(mPosition).loadIcon(getPackageManager());
				String clickPkgName = MainActivity.showappInfos.get(mPosition).activityInfo.packageName;
				String clickActivityName = MainActivity.showappInfos
						.get(mPosition).activityInfo.name;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("appname", clickAppName);
				map.put("drawable", clickAppIcon);
				map.put("pkgname", clickPkgName);
				map.put("activityname", clickActivityName);
				map.put("pos", mPosition);
				MainActivity.gv_applist.add(map);

			} else {
				Toast.makeText(
						AppList.this,
						MainActivity.showappInfos.get(mPosition).loadLabel(
								getPackageManager())
								+ "已从屏幕中移除", Toast.LENGTH_SHORT).show();

				for (int i = 6; i < MainActivity.gv_applist.size(); i++) {
				
					if (MainActivity.gv_applist.get(i).get("pkgname")
					 .equals(MainActivity.showappInfos.get(mPosition).activityInfo.packageName)
					&& MainActivity.gv_applist.get(i).get("activityname")
					.equals(MainActivity.showappInfos.get(mPosition).activityInfo.name)) {
						mPos = i;
						break;
					} else {
						continue;
					}
				}
				MainActivity.gv_applist.remove(mPos);

			}
			
			applv.setAdapter(new ListViewAdapter(AppList.this,
					MainActivity.showappInfos));
			applv.setSelectionFromTop(scrollPos, scrollTop);

			// Intent intent = new Intent(AppList.this, AppList.class);
			// startActivity(intent);
			// finish();
			
		}
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		String str=intArray2Str(MainActivity.clickinfo);
		MainActivity.sClickinfo = str;
		SharedPreferences settings = getSharedPreferences("prefer", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("info", str);
		editor.commit();
	}

	int convert(int value) {
		if (value == 0)
			return 1;
		else
			return 0;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
		
	}
	
String intArray2Str(int[] a) {
		
		int len = a.length;
		String str="";
		for(int i=0;i<len; i++) {
			str+=String.valueOf(a[i]);
		}		
		return str;	
	}
	
	

}

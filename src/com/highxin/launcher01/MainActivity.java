package com.highxin.launcher01;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.highxin.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements Runnable,OnItemClickListener{
	
	public static  List<Map<String, Object>>  contacts_list = new ArrayList<Map<String, Object>>();
	public static  List<Map<String, Object>>  gv_applist = new ArrayList<Map<String,Object>>();
	
	public static boolean switch_flag = false;
	public static boolean click_flag = false;
	public static boolean time_check = false;
	public static boolean firstload_flag = false;//判断桌面是否是第一次加载
	public static List<Integer> click_position = new ArrayList<Integer>();
	
	public static final int NUM_IN_PAGE = 6;
	public static List<ResolveInfo> appInfos = new ArrayList<ResolveInfo>();;
	public static List<ResolveInfo> showappInfos = new ArrayList<ResolveInfo>();//此变量
	
	static String sClickinfo ="";
	static int shortCutNum = 0;
	static int pageNum = 0;
	static int currIndex = 0;// 当前页卡编号
	
	static int []clickinfo;
	String name = "";
	String phoneNO = "";
	String []appname ={"联系人","相机","电话","短信","手电筒","更多"};
	int []appicon = {R.drawable.contact,R.drawable.camera,R.drawable.phone,R.drawable.sms,R.drawable.light_off,R.drawable.otherapp};
	private Intent serviceIntent;
	private View view1, view2, view3,view4,view5;
	private List<View> viewList = new ArrayList<View>();// view数组
	private List<View> showViewList = new ArrayList<View>();
	private ViewPager viewPager; // 对应的viewPager
	private long mExitTime;
	private ImageView cursor;
	private int bmpw = 0; // 游标宽度
	private int offset = 0;// // 动画图片偏移量
	
	static GridView gv_app;
	static GridView gv_app2;
	static GridView gv_app3;
	static GridView gv_app4;
	static GridView gv_app5;
	
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Drawable drawable_appicon = null;
			String s_appname = null;
			 if(!firstload_flag) {
					for(int i=0; i<appname.length;i++) {
						drawable_appicon = getResources().getDrawable(appicon[i]);
						s_appname = appname[i];
						Map<String, Object> map = new HashMap<String, Object>();
					    map.put("appname", s_appname);
				        map.put("drawable", drawable_appicon);
				        
				        gv_applist.add(map); 
				     }
					firstload_flag = true;
			 } 
			 
			 SharedPreferences sp = getSharedPreferences("prefer", MODE_PRIVATE);
		     sClickinfo = sp.getString("info", "error");
		     if(sClickinfo.equals("error")) {
		    	 clickinfo = new int[showappInfos.size()];
		    	 shortCutNum=0;
		     }else{
		    	 clickinfo = new int[showappInfos.size()];
		    	 clickinfo = str2IntArray(sClickinfo);
		    	 Pattern p = Pattern.compile("[1]");
		         Matcher m = p.matcher(sClickinfo);
		         while(m.find()) {
		             int mPosition = m.start();
		             shortCutNum++;
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
					    gv_applist.add(map);

		         }

		     }
		   
		     	
			    showViewList = viewList.subList(0,getViewPage(shortCutNum)+1);

				//初始化指示器位置
				initCursorPos(showViewList);
				viewPager.setAdapter(new MyPagerAdapter(showViewList));
				viewPager.setOnPageChangeListener(new MyPageChangeListener());
				setAdapter(shortCutNum);
		}     
    };

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
	    viewPager = (ViewPager) findViewById(R.id.viewpager);
		LayoutInflater inflater = getLayoutInflater();
		view1 = inflater.inflate(R.layout.layout1, null);
		gv_app = (GridView) view1.findViewById(R.id.gv_apps1);
	    gv_app.setOnItemClickListener(this);
	    
		view2 = inflater.inflate(R.layout.layout2, null);
		gv_app2 = (GridView) view2.findViewById(R.id.gv_apps2);
	    gv_app2.setOnItemClickListener(this);
	    
		view3 = inflater.inflate(R.layout.layout3, null);
		gv_app3 = (GridView) view3.findViewById(R.id.gv_apps3);
	    gv_app3.setOnItemClickListener(this);
	    
	    view4 = inflater.inflate(R.layout.layout4, null);
		gv_app4 = (GridView) view4.findViewById(R.id.gv_apps4);
	    gv_app4.setOnItemClickListener(this);
	    
	    view5 = inflater.inflate(R.layout.layout5, null);
		gv_app5 = (GridView) view5.findViewById(R.id.gv_apps5);
	    gv_app5.setOnItemClickListener(this);
	    
	    viewList.add(view1);
		viewList.add(view2);
		viewList.add(view3);
		viewList.add(view4);
		viewList.add(view5);
	    
	    serviceIntent = new Intent(this, LightService.class);
	    Thread t = new Thread(this);
	    t.start();
	 
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Pattern p = Pattern.compile("[1]");
        Matcher m = p.matcher(sClickinfo);
        shortCutNum =0;
        while(m.find()) {
            shortCutNum++;
        }
		showViewList = viewList.subList(0,getViewPage(shortCutNum)+1);

		//初始化指示器位置
		initCursorPos(showViewList);
		viewPager.setAdapter(new MyPagerAdapter(showViewList));
		viewPager.setCurrentItem(currIndex);
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		setAdapter(shortCutNum);
		
	}
	
	
		/**
		 * @param 初始化指示器位置
		 */
		public void initCursorPos(List<View> list) {
			// 初始化动画
			List<View> viewList = list;
			cursor = (ImageView) findViewById(R.id.cursor);
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenW = dm.widthPixels;// 获取分辨率宽度
			
			
			
//			bmpw = BitmapFactory.decodeResource(getResources(), R.drawable.a)
//					.getWidth();// 获取图片宽度
//			offset = (screenW / viewList.size() - bmpw) / 2;// 计算偏移量标准写法

	        LayoutParams para;  
	        para = cursor.getLayoutParams(); 
	        para.width = screenW / viewList.size(); 
	        cursor.setLayoutParams(para); 
	        
			bmpw = screenW / viewList.size();
			offset = 0;// 本例简单起见图片宽度自动设置，不设置间隙

			Matrix matrix = new Matrix();
			matrix.postTranslate(offset, 0);
			cursor.setImageMatrix(matrix);// 设置动画初始位置
		}
		

		
		/**
		 * @author Hixin，页面改变监听器
		 *
		 */
		public class MyPageChangeListener implements OnPageChangeListener {

			int one = offset * 2 + bmpw;// 页卡1 -> 页卡2 偏移量
			int two = one * 2;// 页卡1 -> 页卡3 偏移量
			int three = one * 3;
			int four = one * 4;

			@Override
			public void onPageSelected(int arg0) {
				Animation animation = null;
					switch (arg0) {
					case 0:
						if (currIndex == 1) {
							animation = new TranslateAnimation(one, 0, 0, 0);
						} else if (currIndex == 2) {
							animation = new TranslateAnimation(two, 0, 0, 0);
						}
						else if (currIndex == 3) {
							animation = new TranslateAnimation(three, 0, 0, 0);
						}
						else if (currIndex == 4) {
							animation = new TranslateAnimation(four, 0, 0, 0);
						}
						else{
							animation = new TranslateAnimation(arg0*one, arg0*one, 0, 0);
						}
						break;
					case 1:
						if (currIndex == 0) {
							animation = new TranslateAnimation(offset, one, 0, 0);
						} else if (currIndex == 2) {
							animation = new TranslateAnimation(two, one, 0, 0);
						}
						else if (currIndex == 3) {
							animation = new TranslateAnimation(three, one, 0, 0);
						}
						else if (currIndex == 4) {
							animation = new TranslateAnimation(four, one, 0, 0);
						}
						else{
							animation = new TranslateAnimation(arg0*one, arg0*one, 0, 0);
						}
						break;
					case 2:
						if (currIndex == 0) {
							animation = new TranslateAnimation(offset, two, 0, 0);
						} else if (currIndex == 1) {
							animation = new TranslateAnimation(one, two, 0, 0);
						}
						else if (currIndex == 3) {
							animation = new TranslateAnimation(three,two, 0, 0);
						}
						else if (currIndex == 4) {
							animation = new TranslateAnimation(four, two, 0, 0);
						}
						else{
							animation = new TranslateAnimation(arg0*one, arg0*one, 0, 0);
						}
						break;
					case 3:
						if (currIndex == 0) {
							animation = new TranslateAnimation(offset, three, 0, 0);
						} else if (currIndex == 1) {
							animation = new TranslateAnimation(one, three, 0, 0);
						}
						else if (currIndex == 2) {
							animation = new TranslateAnimation(two, three, 0, 0);
						}
						else if (currIndex == 4) {
							animation = new TranslateAnimation(four, three, 0, 0);
						}
						else{
							animation = new TranslateAnimation(arg0*one, arg0*one, 0, 0);
						}
						break;
					case 4:
						if (currIndex == 0) {
							animation = new TranslateAnimation(offset, four, 0, 0);
						} else if (currIndex == 1) {
							animation = new TranslateAnimation(one, four, 0, 0);
						}
						else if (currIndex == 2) {
							animation = new TranslateAnimation(two, four, 0, 0);
						}
						else if (currIndex == 3) {
							animation = new TranslateAnimation(three, four, 0, 0);
						}
						else{
							animation = new TranslateAnimation(arg0*one, arg0*one, 0, 0);
						}
						break;
					}
			
				currIndex = arg0;
				animation.setFillAfter(true);// True:图片停在动画结束位置
				animation.setDuration(300);
				cursor.startAnimation(animation);
				
			     
			    
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		}
		

//		/**
//	     * 判断当前界面是否是桌面
//	     */
//	    public boolean isNotHome(){
//	        ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//	        RunningTaskInfo rti = mActivityManager.getRunningTasks(1).get(0);
//	        Log.d("Activity", rti.topActivity.getClassName());
//	        if(rti.topActivity.getClassName().equalsIgnoreCase("com.highxin.launcher01.MainActivity")) {
//	        	return false;
//	        }    
//	        else {
//	        	return true;
//	        }
//	            
//	    }

		/**
		 * ViewPager适配器
		 */
		public class MyPagerAdapter extends PagerAdapter {
			public List<View> mListViews;

			public MyPagerAdapter(List<View> mListViews) {
				this.mListViews = mListViews;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == viewList.get((int)Integer.parseInt(arg1.toString()));

			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mListViews.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				// TODO Auto-generated method stub
				container.removeView(mListViews.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				// TODO Auto-generated method stub
				container.addView(mListViews.get(position));

				return position;
			}
		}
	
	
//	
//	 /**
//	 * 得到手机里存储的联系人信息
//	 */
//	private List<Map<String, Object>> fillMaps() {
//		 List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
//         
//         ContentResolver cr = getContentResolver();
//         HashMap<String,ArrayList<String>> hashMap = new HashMap<String,ArrayList<String>>();
//         Cursor phone = cr.query(CommonDataKinds.Phone.CONTENT_URI, 
//           new String[] {
//           CommonDataKinds.Phone.CONTACT_ID,
//           CommonDataKinds.Phone.DISPLAY_NAME,      
//           CommonDataKinds.Phone.NUMBER, 
//           CommonDataKinds.Phone.DATA1
//           //CommonDataKinds.StructuredPostal.DATA3,
//           }, 
//           null, null, null);
//         while (phone.moveToNext()) {
//          String contactId = phone.getString(phone.getColumnIndex(CommonDataKinds.Phone.CONTACT_ID));         
//          String displayName = phone.getString(phone.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME));
//          String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));      
//          String address = phone.getString(phone.getColumnIndex(CommonDataKinds.Phone.DATA1)); 
//          
//          //以contactId为主键，把同一人的所有电话都存到一起。
//         ArrayList<String> ad = hashMap.get(contactId);
//          if(ad == null){
//           ad = new ArrayList<String>();
//           ad.add(displayName);
//           ad.add(PhoneNumber);
//           //ad.add(address);
//           
//           hashMap.put(contactId, ad);
//          }
//          else{
//           ad.add(PhoneNumber);
//          }
//          
//         }
//         phone.close();  
//         
//         ArrayList<String> tmpList;
//         String tmpStr = "";
//         int k;        
//         Iterator iter = hashMap.entrySet().iterator();
//         while (iter.hasNext()) {
//          HashMap.Entry entry = (HashMap.Entry) iter.next();
//             Object key = entry.getKey();
//             Object val = entry.getValue();
//             
//             tmpList = (ArrayList) val;
//             tmpStr =  tmpList.get(1);
//             //注释掉的为读取多个联系人的方法
////             for(k = 1; k < tmpList.size(); k++){
////              tmpStr = tmpStr + tmpList.get(k) + ',' ;
////             }
//             
//             HashMap<String, Object> tmpMap = new HashMap<String, Object>();        
//             tmpMap.put("name", tmpList.get(0));
//             tmpMap.put("phoneNO", tmpStr);
//             tmpMap.put("img", R.drawable.image);
//             
//             items.add(tmpMap);            
//         }                   
//         return items;        
//     }


	class GridViewAdapter extends BaseAdapter{

        LayoutInflater inflater;
        List<Map<String, Object>> gvlist;
        public GridViewAdapter(Context context,List<Map<String, Object>> locallist) {
            inflater = LayoutInflater.from(context);
            gvlist = new ArrayList<Map<String, Object>>(locallist);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return gvlist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return gvlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
        
                View view = inflater.inflate(R.layout.gv_item, null);
                TextView name = (TextView)view.findViewById(R.id.gv_item_appname);
                ImageView iv = (ImageView)view.findViewById(R.id.gv_item_icon);
                name.setText((CharSequence) gvlist.get(position).get("appname"));
                iv.setImageDrawable((Drawable) gvlist.get(position).get("drawable"));
                
            return view;
        }
        
    }
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(arg0.getId() == R.id.gv_apps1) {
			switch(arg2) {
			
				case 0:{
//					    contacts_list = fillMaps();
//						Intent contact  = new Intent(MainActivity.this, Contacts.class);
//						startActivity(contact);
					Intent i = new Intent();
//					String packageName = "com.android.contacts";
//					String mainActivityName = "com.android.contacts.activities.TwelveKeyDialer";
//					i.setComponent(new ComponentName(packageName,mainActivityName));
					i.putExtra("index", 2);
					i.setClass(MainActivity.this, com.highxin.contact.Dial_Contact.class);
					startActivity(i);
						}break;
				case 1:{
				   Intent imageCaptureIntent = new Intent("android.media.action.STILL_IMAGE_CAMERA"); 
				   startActivityForResult(imageCaptureIntent,1);
				}break;
				case 2:{
					Intent i = new Intent();
//					String packageName = "com.android.contacts";
//					String mainActivityName = "com.android.contacts.activities.TwelveKeyDialer";
//					i.setComponent(new ComponentName(packageName,mainActivityName));
					i.putExtra("index", 1);
					i.setClass(MainActivity.this, com.highxin.contact.Dial_Contact.class);
					startActivity(i);
				}break;
				case 3:{
//					Intent intent = new Intent();
//			        intent.setPackage("com.miui.fmradio");		
//			        startActivityForResult(intent, 3);
					
					Intent i = new Intent();
					i.setClass(MainActivity.this, com.highxin.message.HomeSMSActivity.class);
					startActivity(i);
					}break;
				case 4:{
						switch_flag = !switch_flag;
						if(switch_flag) {
				        	startService(serviceIntent);
				        	 ImageView iv = (ImageView) arg1.findViewById(R.id.gv_item_icon);
				        	 iv.setImageResource(R.drawable.light);
				        	
				        }else {
				        	stopService(serviceIntent);
				        	 ImageView iv = (ImageView) arg1.findViewById(R.id.gv_item_icon);
				        	 iv.setImageResource(R.drawable.light_off);
				        }
					}break;
				case 5:{
					Intent applist  = new Intent(MainActivity.this, AppList.class);
					startActivityForResult(applist,5);
					}break;
			 }
		 }
			
		else if(arg0.getId() == R.id.gv_apps2) {
			Intent i = new Intent();
			String packageName = (String) MainActivity.gv_applist.get(arg2+NUM_IN_PAGE).get("pkgname");
			String mainActivityName = (String) MainActivity.gv_applist.get(arg2+NUM_IN_PAGE).get("activityname");
			i.setComponent(new ComponentName(packageName,mainActivityName));
			startActivity(i);
			
		}
		
		else if(arg0.getId() == R.id.gv_apps3) {
			Intent i = new Intent();
			String packageName = (String) MainActivity.gv_applist.get(arg2+NUM_IN_PAGE*2).get("pkgname");
			String mainActivityName = (String) MainActivity.gv_applist.get(arg2+NUM_IN_PAGE*2).get("activityname");
			i.setComponent(new ComponentName(packageName,mainActivityName));
			startActivity(i);
			
		}
		
		else if(arg0.getId() == R.id.gv_apps4) {
			Intent i = new Intent();
			String packageName = (String) MainActivity.gv_applist.get(arg2+NUM_IN_PAGE*3).get("pkgname");
			String mainActivityName = (String) MainActivity.gv_applist.get(arg2+NUM_IN_PAGE*3).get("activityname");
			i.setComponent(new ComponentName(packageName,mainActivityName));
			startActivity(i);
			
		}
		else if(arg0.getId() == R.id.gv_apps5) {
			Intent i = new Intent();
			String packageName = (String) MainActivity.gv_applist.get(arg2+NUM_IN_PAGE*4).get("pkgname");
			String mainActivityName = (String) MainActivity.gv_applist.get(arg2+NUM_IN_PAGE*4).get("activityname");
			i.setComponent(new ComponentName(packageName,mainActivityName));
			startActivity(i);
			
		}

	}

	@Override
	public void run() {
		 getShowAppInfoLists();
		 mHandler.sendEmptyMessage(0);
	}
	
	
	/**
	 * @Title: getResolveInfoLists
	 * @Description: 它是通过解析< Intent-filter>标签得到有 　　< action
	 *               android:name=”android.intent.action.MAIN”/> 　　< action
	 *               android:name=”android.intent.category.LAUNCHER”/>
	 * @param ：
	 * @return List<ResolveInfo>
	 * @throws
	 */
	 List<ResolveInfo> getResolveInfoLists() {

		PackageManager packageManager = getPackageManager();

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		return packageManager.queryIntentActivities(intent, 0);
	}

	/**
	 * 得到要现实在程序列表里的程序，剔除掉已经有的桌面上已有的程序
	 */
	void getShowAppInfoLists() {
		 appInfos = getResolveInfoLists();
		 showappInfos = appInfos;
		
		 for(int i=0; i<appInfos.size();i++) {
			 if((appInfos.get(i).loadLabel(getPackageManager()).equals("相机") &&
			     appInfos.get(i).activityInfo.packageName.equals("com.android.camera")) ||
			    (appInfos.get(i).loadLabel(getPackageManager()).equals("老人桌面") &&
				 appInfos.get(i).activityInfo.packageName.equals("com.highxin")) )
			 {
				 showappInfos.remove(i);
			 }
		 }
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 1, "设置");
		menu.add(2, 2, 2, "整点报时").setCheckable(true).setChecked(time_check);
		return true;
	}
	
	
	 @Override
	    protected void onNewIntent(Intent intent) {
	        super.onNewIntent(intent);
	        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
	        	
    		 if ((System.currentTimeMillis() - mExitTime) > 3000) {
//		        		 	Toast.makeText(this, "再按一次回到主桌面", Toast.LENGTH_SHORT).show();
      			mExitTime = System.currentTimeMillis();
               
        	 } 
        	 else {
            	if (currIndex != 0) {
	            	viewPager.setCurrentItem(0);  
	            }           
        	 }
    
	        	
	        	
      
            final View v = getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
	        }
	    }
	
	 @Override
	    public void onBackPressed() {
		 	viewPager.setCurrentItem(0);
	    }
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {  
		    case 1: {
		    	Intent intent =new Intent(Settings.ACTION_SETTINGS);
		    	startActivity(intent);
		    	return true;
		    	}
		    case 2: {
		    	time_check=!time_check;
		    	item.setChecked(time_check);
		    	return true;
		    	} 
		    default:	
		    	return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}


	
	/**
	 * 字符串各个元素转为int数组
	 * 
	 */
	int[] str2IntArray(String str) {
		int len = str.length();
		int[] a = new int[len];
		char[] c = str.toCharArray();
		for(int i=0; i<len; i++) {
			a[i] = c[i]-'0';
		}	
		return a;	
	}
	
	/**
	 * @param num  快捷图标的个数
	 * @return	需要创建的页面数
	 */
	int getViewPage(int num) {
		int quo= num/NUM_IN_PAGE;
		int mod= num%NUM_IN_PAGE;
		
		if(mod == 0) {
			return quo;
		}
		else{
			return quo+1;
		}
	
	}
	
//	void setAdapter(int a) {
//		 if(pageNum == 0){
//			 gv_app.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist));
//		 }
//		 else if(pageNum == 1){
//			 gv_app.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist));
//			 gv_app2.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist2));
//			
//		 }
//		 else if(pageNum == 2){
//			 gv_app.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist));
//			 gv_app2.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist2));
//			 gv_app3.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist3));
//		 }
//		 else if(pageNum == 3){
//			 gv_app.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist));
//			 gv_app2.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist2));
//			 gv_app3.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist3));
//			 gv_app4.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist4));
//		 }
//		 else{
//			 gv_app.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist));
//			 gv_app2.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist2));
//			 gv_app3.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist3));
//			 gv_app4.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist4));
//			 gv_app5.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist5));
//		 }
//	}
	
	/**
	 * @param a 传入快捷图标的个数，决定如何加载。 莫名其妙的越界异常
	 */
	void setAdapter(int a) {
		pageNum = getViewPage(shortCutNum);
		 try {
			if(pageNum == 0){	
				 gv_app.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(0, NUM_IN_PAGE)));
			 }
			 else if(pageNum == 1){
				 gv_app.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(0, NUM_IN_PAGE)));
				 gv_app2.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(NUM_IN_PAGE,NUM_IN_PAGE+a)));
				
			 }
			 else if(pageNum == 2){
//				 System.out.println(gv_applist.get(12));
//				 System.out.println(a);
//				 System.out.println(gv_applist.size());
				 gv_app.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(0, NUM_IN_PAGE)));
				 gv_app2.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(NUM_IN_PAGE, NUM_IN_PAGE*2)));
				 gv_app3.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(NUM_IN_PAGE*2, NUM_IN_PAGE+a)));
				
			 }
			 else if(pageNum == 3){
//				 System.out.println(a);
//				 System.out.println(gv_applist.size());
				 gv_app.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(0, NUM_IN_PAGE)));
				 gv_app2.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(NUM_IN_PAGE, NUM_IN_PAGE*2)));
				 gv_app3.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(NUM_IN_PAGE*2, NUM_IN_PAGE*3)));
				 gv_app4.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(NUM_IN_PAGE*3, NUM_IN_PAGE+a)));
			 }
			 else if (pageNum == 4){
				 gv_app.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(0, NUM_IN_PAGE)));
				 gv_app2.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(NUM_IN_PAGE, NUM_IN_PAGE*2)));
				 gv_app3.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(NUM_IN_PAGE*2, NUM_IN_PAGE*3)));
				 gv_app4.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(NUM_IN_PAGE*3, NUM_IN_PAGE*4)));
				 gv_app5.setAdapter(new GridViewAdapter(MainActivity.this,gv_applist.subList(NUM_IN_PAGE*4, NUM_IN_PAGE+a)));
			 }
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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




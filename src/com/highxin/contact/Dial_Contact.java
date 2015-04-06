package com.highxin.contact;

import java.util.ArrayList;
import java.util.List;

import com.highxin.R;
import com.highxin.R.color;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;


public class Dial_Contact extends FragmentActivity implements OnClickListener{
	Button bt_dial;
	Button bt_contact;
	ImageButton bt_new_contact;
	ImageButton ib_dialpan;
	ViewPager vp;
	LinearLayout bottom;
	private int index;
	List<Fragment> fragments=new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dial_contact);
        Intent i = getIntent();
        index = i.getIntExtra("index", 0);
		bt_dial = (Button)findViewById(R.id.bt_fdial);
		bt_contact = (Button) findViewById(R.id.bt_fcontact);
		ib_dialpan = (ImageButton) findViewById(R.id.bt_fdialpan);
		bt_new_contact = (ImageButton) findViewById(R.id.bt_fnewcontact);
		bottom = (LinearLayout) findViewById(R.id.btm);
		
		bt_dial.setOnClickListener(this);
		bt_contact.setOnClickListener(this);
		bt_new_contact.setOnClickListener(this);
		ib_dialpan.setOnClickListener(this);
		//构造适配器
		fragments.add(new HomeDialFragment());
		fragments.add(new HomeContactFragment()); 
		FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        
        //设定适配器
        vp = (ViewPager)findViewById(R.id.viewpager);
       
       
    	vp.setOnPageChangeListener(new MyPageChangeListener());
        vp.setAdapter(adapter);
        if(index == 1) {
       	 vp.setCurrentItem(0);
       	 bt_dial.setTextColor(Color.YELLOW);
		 bt_contact.setTextColor(Color.WHITE);
		 bt_new_contact.setVisibility( View.GONE );
		ib_dialpan.setVisibility(View.VISIBLE);
       }
       else if(index == 2) {
       	 vp.setCurrentItem(1);
       	 bt_dial.setTextColor(Color.WHITE);
		bt_contact.setTextColor(Color.YELLOW);
		ib_dialpan.setVisibility(View.GONE);
		bt_new_contact.setVisibility( View.VISIBLE );
       }
    }
    public class MyPageChangeListener implements OnPageChangeListener {

    	@Override
    	public void onPageSelected(int arg0) {
    	
    		switch (arg0) {
    		case 0:
    			bt_dial.setTextColor(Color.YELLOW);
    			bt_contact.setTextColor(Color.WHITE);
    			bt_new_contact.setVisibility( View.GONE );
    			ib_dialpan.setVisibility(View.VISIBLE);
    			break;
    		case 1:
    			bt_dial.setTextColor(Color.WHITE);
    			bt_contact.setTextColor(Color.YELLOW);
    			ib_dialpan.setVisibility(View.GONE);
    			bottom.setVisibility(View.VISIBLE);
    			bt_new_contact.setVisibility( View.VISIBLE );
    			break;
    		
    		}
    		
    	}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.bt_fdial:
			vp.setCurrentItem(0);
			break;
		case R.id.bt_fcontact:
			vp.setCurrentItem(1);
			break;
		case R.id.bt_fnewcontact:{
			Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
			Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
			startActivity(intent);
			}break;
		case R.id.bt_fdialpan:{
			bottom.setVisibility(View.GONE);
			((HomeDialFragment) fragments.get(0)).getView().findViewById(R.id.bohaopan).setVisibility(View.VISIBLE);
			}break;		
		}
			
	}
	


}
//
/*可能会出现的问题：
 * fragments.add(new Fragment1());
 * 提示无法将Fragment1()转换为fragment；这是因为导入包不一致，一般的问题在于
 * 在Fragment1中导入的是android.app.Fragment，
 * 而在这里导入类确是：android.support.v4.app.Fragment,包不同当然无法转换
 * 统一导入为android.support.v4.app.Fragment之后就正常了.
 * 
 * 相关网址：http://blog.csdn.net/jason0539/article/details/9712273
 * */

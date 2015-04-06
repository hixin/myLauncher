package com.highxin.contact;
import java.util.HashMap;
import java.util.Map;

import com.highxin.R;
import com.highxin.launcher01.Contacts_detail;
import com.highxin.launcher01.Contacts_edit;
import com.highxin.launcher01.MainActivity;
import com.highxin.launcher01.Message;
import com.highxin.message.BaseIntentUtil;
import com.highxin.message.MessageBoxList;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Contact_detail extends Activity{

	private  int position;
	TextView tv_number;
	String name="";
	String number="";
	int imageID;
	String threadId = "";
	ContactBean cb;
	
//	Map<String, String> map = new HashMap<String, String>();
//	map.put("phoneNumber", cb.getPhoneNum());
//	map.put("threadId", threadId);
//	BaseIntentUtil.intentSysDefault(HomeContactActivity.this, MessageBoxList.class, map);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_detail);
		TextView tv_name = (TextView) findViewById(R.id.detail_tv);
        tv_number = (TextView) findViewById(R.id.detail_tv1);
        ImageView iv = (ImageView) findViewById(R.id.detail_img);
        
        
		Intent intent = getIntent();
        cb = (ContactBean) intent.getSerializableExtra("contact");
        threadId = intent.getStringExtra("threadId");
     
        name = (String) cb.getDisplayName();
        number = (String) cb.getPhoneNum();
        imageID =  cb.getPhotoId();
        
        tv_name.setText(name);
        tv_number.setText(number);
        iv.setImageResource(R.drawable.image);
        
        Button bt_call = (Button) findViewById(R.id.bt_call);
        bt_call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+tv_number.getText().toString()));
				startActivity(callIntent);
			}
        	
        });
        Button bt_message = (Button)findViewById(R.id.bt_message);
        bt_message.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("phoneNumber", cb.getPhoneNum());
				map.put("threadId", threadId);
				BaseIntentUtil.intentSysDefault(Contact_detail.this, MessageBoxList.class, map);	
			}
		}); 
        Button bt_edit = (Button) findViewById(R.id.bt_edit);
        bt_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uri = null;
				uri = ContactsContract.Contacts.CONTENT_URI;
				Uri personUri = ContentUris.withAppendedId(uri, cb.getContactId());
				Intent intent2 = new Intent();
				intent2.setAction(Intent.ACTION_VIEW);
				intent2.setData(personUri);
				startActivity(intent2);
				
			}
        	
        	
        });
	}

	
}

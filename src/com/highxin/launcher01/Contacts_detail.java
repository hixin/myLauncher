package com.highxin.launcher01;

import com.highxin.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Contacts_detail extends Activity{

	private  int position;
	TextView tv_number;
	String name="";
	String number="";
	int imageID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_detail);
		Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int position = bundle.getInt("position");
        
        TextView tv_name = (TextView) findViewById(R.id.detail_tv);
        tv_number = (TextView) findViewById(R.id.detail_tv1);
        ImageView iv = (ImageView) findViewById(R.id.detail_img);
        
        name = (String) MainActivity.contacts_list.get(position).get("name");
        number = (String) MainActivity.contacts_list.get(position).get("phoneNO");
        imageID = (Integer) MainActivity.contacts_list.get(position).get("img");
        
        tv_name.setText(name);
        tv_number.setText(number);
        iv.setImageResource(imageID);
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
				Intent i = new Intent();
				i.setClass(Contacts_detail.this, Message.class);
				startActivity(i);
				
			}
		}); 
        Button bt_edit = (Button) findViewById(R.id.bt_edit);
        bt_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(Contacts_detail.this, Contacts_edit.class);
				i.putExtra("name",name);
				i.putExtra("number",number);
				i.putExtra("image",imageID);
				
				startActivity(i);
				
			}
        	
        	
        });
	}
}

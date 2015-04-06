package com.highxin.launcher01;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import com.highxin.R;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.RawContacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Contacts_edit extends Activity{
	EditText et_name;
	EditText et_number;
	String name="";
	String number="";
	int imageID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contacts_edit);
		et_name = (EditText) findViewById(R.id.et_name);
		et_number = (EditText) findViewById(R.id.et_number);
		
		Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        name = bundle.getString("name");
        number = bundle.getString("number");
        imageID = bundle.getInt("image");
        et_name.setText(name);
        et_number.setText(number);
		
		Button bt_edit = (Button) findViewById(R.id.bt_complete);
		bt_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(et_name.getText().toString().trim().equals("")  || et_number.getText().toString().trim().equals("")) {
					Toast.makeText(Contacts_edit.this, "请把信息填写完整！", Toast.LENGTH_SHORT).show();
				}
				else if(!et_name.getText().toString().trim().equals(name)  || !et_number.getText().toString().trim().equals(number)) {
					insert(et_name.getText().toString(),et_number.getText().toString());
					Map<String, Object> map = new HashMap<String, Object>();
			        map.put("name",et_name.getText().toString() );
			        map.put("img", R.drawable.image);
			        map.put("phoneNO", et_number.getText().toString());
			       
			        MainActivity.contacts_list.add(map);
					finish();
				}
				else {
					Toast.makeText(Contacts_edit.this, "信息没有发生更改", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
	}
	
	public boolean insert(String given_name, String mobile_number) {
		try {
			ContentValues values = new ContentValues();

			// 下面的操作会根据RawContacts表中已有的rawContactId使用情况自动生成新联系人的rawContactId
			Uri rawContactUri = getContentResolver().insert(
					RawContacts.CONTENT_URI, values);
			long rawContactId = ContentUris.parseId(rawContactUri);

			// 向data表插入姓名数据
			if (given_name != "") {
				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
				values.put(StructuredName.GIVEN_NAME, given_name);
				getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
						values);
			}

			// 向data表插入电话数据
			if (mobile_number != "") {
				values.clear();
				values.put(Data.RAW_CONTACT_ID, rawContactId);
				values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
				values.put(Phone.NUMBER, mobile_number);
				values.put(Phone.TYPE, Phone.TYPE_MOBILE);
				getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
						values);
			}

			
			// 向data表插入头像数据
			Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.image);
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			// 将Bitmap压缩成PNG编码，质量为100%存储
			sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			byte[] avatar = os.toByteArray();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
			values.put(Photo.PHOTO, avatar);
			getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
					values);
		}

		catch (Exception e) {
			return false;
		}
		return true;
	}
}
	

 

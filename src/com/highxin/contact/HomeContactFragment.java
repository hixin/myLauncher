package com.highxin.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.highxin.R;

import com.highxin.message.*;

import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;




public class HomeContactFragment extends Fragment {

	private ContactHomeAdapter adapter;
	private ListView personList;
	private List<ContactBean> list;
	private AsyncQueryHandler asyncQuery;
	private QuickAlphabeticBar alpha;
	private Map<Integer, ContactBean> contactIdMap = null;
	
	// ��ǰ�ɼ���List���˵�һ�е�λ��
 	private int scrollPos = 0;
 	// ��ǰ��һ���ɼ���item��ƫ����
 	private int scrollTop = 0;
 	Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.home_contact_page, container, false);
		personList = (ListView)view.findViewById(R.id.acbuwa_list);
		
		
		//����listview�ϴλ���λ��
		 personList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// ���浱ǰ��һ���ɼ���item��������ƫ����
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// scrollPos��¼��ǰ�ɼ���List���˵�һ�е�λ��
					scrollPos = personList.getFirstVisiblePosition();
				}
				View v = personList.getChildAt(0);
				scrollTop = (v == null) ? 0 : v.getTop();
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});
		
		 
		alpha = (QuickAlphabeticBar)view.findViewById(R.id.fast_scroller);
		asyncQuery = new MyAsyncQueryHandler(getActivity().getContentResolver());
		
		init();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		personList.setSelectionFromTop(scrollPos, scrollTop);
	}



	private void init(){
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // ��ϵ�˵�Uri
		String[] projection = { 
				ContactsContract.CommonDataKinds.Phone._ID,
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.DATA1,
				"sort_key",
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
				ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY
		}; // ��ѯ����
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc"); // ����sort_key�����ѯ
	}
	
	
	/**
	 * ���ݿ��첽��ѯ��AsyncQueryHandler
	 * 
	 * @author administrator
	 * 
	 */
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		/**
		 * ��ѯ�����Ļص�����
		 */
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				
				contactIdMap = new HashMap<Integer, ContactBean>();
				
				list = new ArrayList<ContactBean>();
				cursor.moveToFirst();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					int contactId = cursor.getInt(4);
					int photoId = cursor.getInt(5);
					String lookUpKey = cursor.getString(6);

					if (contactIdMap.containsKey(contactId)) {
						
					}else{
						
						ContactBean cb = new ContactBean();
						cb.setDisplayName(name);
					if (number.startsWith("+86")) {// ȥ��������й����������־�����������û��Ӱ�졣
						cb.setPhoneNum(number.substring(3));
					} else {
						cb.setPhoneNum(number);
					}
						cb.setSortKey(sortKey);
						cb.setContactId(contactId);
						cb.setPhotoId(photoId);
						cb.setLookUpKey(lookUpKey);
						list.add(cb);
						
						contactIdMap.put(contactId, cb);
						
					}
				}
				if (list.size() > 0) {
					setAdapter(list);
				}
			}
		}

	}


	private void setAdapter(List<ContactBean> list) {
		adapter = new ContactHomeAdapter(context, list, alpha);
		personList.setAdapter(adapter);
		alpha.init(getActivity());
		alpha.setListView(personList);
		alpha.setHight(alpha.getHeight());
		alpha.setVisibility(View.VISIBLE);
		personList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ContactBean cb = (ContactBean) adapter.getItem(position);
				Intent i = new Intent();
				i.putExtra("contact", cb);	
				String threadId = getSMSThreadId(cb.getPhoneNum());
				i.putExtra("threadId", threadId);
				i.setClass(getActivity(), Contact_detail.class);
				startActivity(i);
			}
		});
	}


	private String[] lianxiren1 = new String[] { "����绰", "���Ͷ���", "�鿴��ϸ","�ƶ�����","�Ƴ�Ⱥ��","ɾ��" };

	//Ⱥ����ϵ�˵���ҳ
	private void showContactDialog(final String[] arg ,final ContactBean cb, final int position){
		new AlertDialog.Builder(context).setTitle(cb.getDisplayName()).setItems(arg,
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){

				Uri uri = null;

				switch(which){

				case 0://��绰
					String toPhone = cb.getPhoneNum();
					uri = Uri.parse("tel:" + toPhone);
					Intent it = new Intent(Intent.ACTION_CALL, uri);
					startActivity(it);
					break;

				case 1://����Ϣ

					String threadId = getSMSThreadId(cb.getPhoneNum());
					
					Map<String, String> map = new HashMap<String, String>();
					map.put("phoneNumber", cb.getPhoneNum());
					map.put("threadId", threadId);
					BaseIntentUtil.intentSysDefault(getActivity(), MessageBoxList.class, map);
					break;

				case 2:// �鿴��ϸ       �޸���ϵ������

					uri = ContactsContract.Contacts.CONTENT_URI;
					Uri personUri = ContentUris.withAppendedId(uri, cb.getContactId());
					Intent intent2 = new Intent();
					intent2.setAction(Intent.ACTION_VIEW);
					intent2.setData(personUri);
					startActivity(intent2);
					break;

				case 3:// �ƶ�����

					//					Intent intent3 = null;
					//					intent3 = new Intent();
					//					intent3.setClass(ContactHome.this, GroupChoose.class);
					//					intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
					//					intent3.putExtra("��ϵ��", contactsID);
					//					Log.e("contactsID", "contactsID---"+contactsID);
					//					ContactHome.this.startActivity(intent3);
					break;

				case 4:// �Ƴ�Ⱥ��

					//					moveOutGroup(getRaw_contact_id(contactsID),Integer.parseInt(qzID));
					break;

				case 5:// ɾ��

					showDelete(cb.getContactId(), position);
					break;
				}
			}
		}).show();
	}

	// ɾ����ϵ�˷���
	private void showDelete(final int contactsID, final int position) {
		new AlertDialog.Builder(context).setIcon(R.drawable.ic_launcher).setTitle("�Ƿ�ɾ������ϵ��")
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//Դ��ɾ��
				Uri deleteUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactsID);
				Uri lookupUri = ContactsContract.Contacts.getLookupUri(context.getContentResolver(), deleteUri);
				if(lookupUri != Uri.EMPTY){
					getActivity().getContentResolver().delete(deleteUri, null, null);
				}
				adapter.remove(position);
				adapter.notifyDataSetChanged();
				Toast.makeText(context, "����ϵ���Ѿ���ɾ��.", Toast.LENGTH_SHORT).show();
			}
		})
		.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		}).show();
	}




	public static String[] SMS_COLUMNS = new String[]{  
		"thread_id"
	};
	private String getSMSThreadId(String adddress){
		Cursor cursor = null;  
		ContentResolver contentResolver = context.getContentResolver();  
		cursor = contentResolver.query(Uri.parse("content://sms"), SMS_COLUMNS, " address like '%" + adddress + "%' ", null, null);  
		String threadId = "";
		if (cursor == null || cursor.getCount() > 0){
			cursor.moveToFirst();
			threadId = cursor.getString(0);
			cursor.close();
			return threadId;
		}else{
			cursor.close();
			return threadId;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}


}


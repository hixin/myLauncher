package com.highxin.contact;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.highxin.R;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class ContactHomeAdapter extends BaseAdapter{
	
	private LayoutInflater inflater;
	private List<ContactBean> list;
	private HashMap<String, Integer> alphaIndexer;//����ÿ��������list�е�λ�á�#-0��A-4��B-10��
	private String[] sections;//ÿ�����������??A,B,C,F...��
	private Context ctx;
	
	public ContactHomeAdapter(Context context, List<ContactBean> list, QuickAlphabeticBar alpha) {
		
		this.ctx = context;
		this.inflater = LayoutInflater.from(context);
		this.list = list; 
		this.alphaIndexer = new HashMap<String, Integer>();
		this.sections = new String[list.size()];

		for (int i =0; i <list.size(); i++) {
			String name = getAlpha(list.get(i).getSortKey());
			if(!alphaIndexer.containsKey(name)){ 
				alphaIndexer.put(name, i);
			}
		}
		
		Set<String> sectionLetters = alphaIndexer.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
		Collections.sort(sectionList);
		sections = new String[sectionList.size()];
		sectionList.toArray(sections);

		alpha.setAlphaIndexer(alphaIndexer);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void remove(int position){
		list.remove(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.contact_home_list_item, null);
			holder = new ViewHolder();
			holder.qcb = (QuickContactBadge) convertView.findViewById(R.id.qcb);
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.name = (TextView) convertView.findViewById(R.id.name);
//			holder.number = (TextView) convertView
//					.findViewById(R.id.number);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ContactBean cb = list.get(position);
		String name = cb.getDisplayName();
		String number = cb.getPhoneNum();
		holder.name.setText(name);
//		holder.number.setText(number);
		holder.qcb.assignContactUri(Contacts.getLookupUri(cb.getContactId(), cb.getLookUpKey()));
		if(0 == cb.getPhotoId()){
			holder.qcb.setImageResource(R.drawable.image);
		}else{
			Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, cb.getContactId());
			InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(), uri); 
			Bitmap contactPhoto = BitmapFactory.decodeStream(input);
			holder.qcb.setImageBitmap(contactPhoto);
		}
		// ��ǰ��ϵ�˵�sortKey
		String currentStr = getAlpha(cb.getSortKey());
		// ��һ����ϵ�˵�sortKey
		String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).getSortKey()) : " ";
		/**
		 * �ж���ʾ#��A-Z��TextView������ɼ�?
		 */
		if (!previewStr.equals(currentStr)) { // ��ǰ��ϵ�˵�sortKey��=��һ����ϵ�˵�sortKey��˵����ǰ��ϵ�������顣
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentStr);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	private static class ViewHolder {
		QuickContactBadge qcb;
		TextView alpha;
		TextView name;
//		TextView number;
	}
	
	/**
	 * ��ȡӢ�ĵ�����ĸ����Ӣ����ĸ��#���档
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // ��д���?
		} else {
			return "#";
		}
	}
}

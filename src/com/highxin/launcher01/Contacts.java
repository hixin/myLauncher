package com.highxin.launcher01;
import java.util.List;
import java.util.Map;
import com.highxin.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Contacts extends Activity{
	
     public static List<Map<String, Object>> mlist = MainActivity.contacts_list;
     ListView listActivity ;
  // 当前可见的List顶端的一行的位置
 	private int scrollPos = 0;
 	// 当前第一个可见的item的偏移量
 	private int scrollTop = 0;
     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contacts);
		Button bt_new = (Button) findViewById(R.id.bt_new);
		bt_new.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent();
				i.setClass(Contacts.this, Contacts_new.class);
				startActivity(i);
				
			}
			
		});
		listActivity = (ListView) findViewById(R.id.list);
		ListViewAdapter lvAdapter = new ListViewAdapter(this);
		listActivity.setAdapter(lvAdapter);
		listActivity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent i = new Intent();
				i.setClass(Contacts.this, Contacts_detail.class);
				i.putExtra("position", arg2);
				startActivity(i);
				
				
			}
		});
		
		listActivity.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 保存当前第一个可见的item的索引和偏移量
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// scrollPos记录当前可见的List顶端的一行的位置
					scrollPos = listActivity.getFirstVisiblePosition();
				}
				View v = listActivity.getChildAt(0);
				scrollTop = (v == null) ? 0 : v.getTop();
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});
		
	}


	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ListViewAdapter lvAdapter = new ListViewAdapter(this);
		listActivity.setAdapter(lvAdapter);
		listActivity.setSelectionFromTop(scrollPos, scrollTop);
	}




	class ListViewAdapter extends BaseAdapter{

        LayoutInflater inflater;
        public ListViewAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mlist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
        
                View view = inflater.inflate(R.layout.list, null);
                TextView name = (TextView)view.findViewById(R.id.name);
                ImageView iv = (ImageView)view.findViewById(R.id.img);
                name.setText((CharSequence) mlist.get(position).get("name"));
                iv.setImageResource((Integer) mlist.get(position).get("img"));
                
            return view;
        }
        
    }
}

package com.hfapp.activity;

import java.util.ArrayList;

import com.example.palytogether.R;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleException;
import com.hf.module.info.DaysOfWeek;
import com.hf.module.info.TimerEachOne;
import com.hfapp.view.MySwitch;
import com.hfapp.view.MySwitch.OnSwitchChangedListener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TimerCenter extends Activity{
	
	public static boolean TIME1 = false;
	public static boolean TIME2 = false;
	public static boolean TIME3 = false;
	public static boolean TIME4 = false;
	public static boolean TIME5 = false;
	public static boolean TIME6 = false;
	
	
	private ListView timerlist;
	private ImageButton addtimer;
	private IModuleManager manager = ManagerFactory.getInstance().getManager();
	private String mac;
	private ArrayList<TimerEachOne> ts = new ArrayList<TimerEachOne>();
	
	private BaseAdapter timerlistadpt = new BaseAdapter() {
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = LayoutInflater.from(TimerCenter.this).inflate(R.layout.timer_center_list_item_layout, null, true);
			TextView tv_date1 = (TextView) v.findViewById(R.id.timer_center_list_date1);
			TextView tv_date2 = (TextView) v.findViewById(R.id.timer_center_list_date2);
			TextView tv_date1_stat = (TextView) v.findViewById(R.id.timer_center_list_date1_stat);
			
			TextView isloop = (TextView) v.findViewById(R.id.timer_list_item_isloop);
			TextView loopday = (TextView) v.findViewById(R.id.timer_list_item_loopday);
			TextView tv_date2_stat = (TextView) v.findViewById(R.id.timer_center_list_date2_stat);
			MySwitch swic = (MySwitch) v.findViewById(R.id.timer_list_item_switch);
			TimerEachOne t = ts.get(position);
			
			switch(t.getNum()){
				case 0:
					TIME1 = true;
					break;
				case 1:
					TIME2 = true;
					break;
				case 2:
					TIME3 = true;
					break;
				case 3:
					TIME4 = true;
					break;
				case 4:
					TIME5 = true;
					break;
				case 5:
					TIME6 = true;
					break;
				
			}
			
			
			if(t.isEnable()){
				swic.setStatus(false);
			}else{
				swic.setStatus(true);
			}
			switch (t.getType()&0x0f) {
			case 0x01:
				if(t.getdostat()){
					tv_date1.setText(t.getFriMon()+"-"+t.getFriDay()+" "+t.getFriHour()+":"+t.getFriMin());
					tv_date2.setText("");
					tv_date1_stat.setText("enable");
					tv_date2_stat.setText("disable");
				}else{
					tv_date1.setText("");
					tv_date2.setText(t.getFriMon()+"-"+t.getFriDay()+" "+t.getFriHour()+":"+t.getFriMin());
					tv_date1_stat.setText("disable");
					tv_date2_stat.setText("enable");
				}
				isloop.setText("ONCE");
				loopday.setText("");
				break;
			case 0x02:
				if(t.getdostat()){
					tv_date1.setText(t.getFriHour()+":"+t.getFriMin());
					tv_date2.setText("");
					tv_date1_stat.setText("enable");
					tv_date2_stat.setText("disable");
				}else{
					tv_date1.setText("");
					tv_date2.setText(t.getFriHour()+":"+t.getFriMin());
					tv_date1_stat.setText("disable");
					tv_date2_stat.setText("enable");
				}
				
				
				DaysOfWeek w = t.getFlag();
				String s = "" ;
				if(w.monday){
					s+="monday ";
				}
				if(w.tuesday){
					s+="tuesday ";
				}
				
				if(w.wednesday){
					s+="wednesday ";
				}
				if(w.thursday){
					s+="thursday ";
				}
				
				if(w.friday){
					s+="friday ";
				}
				
				if(w.saturday){
					s+="saturday ";
				}
				
				if(w.sunday){
					s+="sunday ";
				}
				
				if(w.isLoop){
					isloop.setText("LOOP");
				}else{
					isloop.setText("ONCE");
				}
				
				loopday.setText(s);
				break;
			case 0x03:
				tv_date1.setText(t.getFriMon()+"-"+t.getFriDay()+" "+t.getFriHour()+":"+t.getFriMin());
				tv_date2.setText(t.getSecMon()+"-"+t.getSecDay()+" "+t.getSecHour()+":"+t.getSecMin());
				tv_date1_stat.setText("enable");
				tv_date2_stat.setText("enable");
				isloop.setText("ONCE");
				loopday.setText("");
				break;
			case 0x04:
				tv_date1.setText(t.getFriHour()+":"+t.getFriMin());
				tv_date2.setText(t.getSecHour()+":"+t.getSecMin());
				tv_date1_stat.setText("enable");
				tv_date2_stat.setText("enable");
				DaysOfWeek w2 = t.getFlag();
				String s2 = "" ;
				if(w2.monday){
					s2+="monday ";
				}
				if(w2.tuesday){
					s2+="tuesday ";
				}
				
				if(w2.wednesday){
					s2+="wednesday ";
				}
				if(w2.thursday){
					s2+="thursday ";
				}
				
				if(w2.friday){
					s2+="friday ";
				}
				
				if(w2.saturday){
					s2+="saturday ";
				}
				
				if(w2.sunday){
					s2+="sunday ";
				}
				
				if(w2.isLoop){
					isloop.setText("LOOP");
				}else{
					isloop.setText("ONCE");
				}
				
				loopday.setText(s2);
				break;
			default:
				break;
			}
			
			
			swic.setOnSwitchChangedListener(new OnSwitchChangedListener() {
				
				@Override
				public void onSwitchChanged(MySwitch obj, int status) {
					// TODO Auto-generated method stub
					if(status == MySwitch.SWITCH_OFF){
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								TimerEachOne t = ts.get(position);
								t.setEnable(true);
								try {
									manager.getHelper().setTimer(mac, t);
								} catch (ModuleException e) {
									// TODO Auto-generated catch block
									t.setEnable(false);
									e.printStackTrace();
								}
								
								hand.sendEmptyMessage(1);
							}
						}).start();
					}else if(status == MySwitch.SWITCH_ON){
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								TimerEachOne t = ts.get(position);
								t.setEnable(false);
								try {
									manager.getHelper().setTimer(mac, t);
								} catch (ModuleException e) {
									// TODO Auto-generated catch block
									t.setEnable(true);
									e.printStackTrace();
								}
								hand.sendEmptyMessage(1);
							}
						}).start();
					}
				}
			});
			return v;
		}
		
		
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ts.size();
		}
	};
	private void initView(){
		timerlist = (ListView) findViewById(R.id.timer_center_timelist);
		addtimer = (ImageButton) findViewById(R.id.timer_center_timeaddbtn);
		addtimer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(TimerCenter.this, TimerAdder.class);
				i.putExtra("mac", mac);
				i.putExtra("timenum", getTimeNum());
				startActivity(i);
			}
		});
		timerlist.setAdapter(timerlistadpt);
		if(ts.size()>0){
			View listItem = timerlistadpt.getView(0,null,timerlist);
			listItem.measure(0, 0);
			int totalHei = (listItem.getMeasuredHeight()+timerlist.getDividerHeight() ) * 3-2;
			timerlist.getLayoutParams().height = totalHei;
		}
		timerlist.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
	
	private Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if(ts.size()>=6){
					addtimer.setEnabled(false);
				}
				timerlistadpt.notifyDataSetChanged();
				break;
			case 2:
				Toast.makeText(TimerCenter.this, "Get TimerList Failed", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timer_center_layout);
		mac = getIntent().getStringExtra("mac");
		initActionbar();
		initView();
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initData();
	}
	private void initData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Log.i("Timer", "getallTimer");
					ts = manager.getHelper().getAllEachOneTimer(mac);
					hand.sendEmptyMessage(1);
				} catch (ModuleException e) {
					// TODO Auto-generated catch block
					hand.sendEmptyMessage(2);
				}
			}
		}).start();
		
		TIME1 = false;
		TIME2 = false;
		TIME3 = false;
		TIME4 = false;
		TIME5 = false;
		TIME6 = false;
	}


	private void initActionbar() {
		// TODO Auto-generated method stub
		ActionBar bar = getActionBar();
		bar.setCustomView(R.layout.layout_actionbar);
		bar.setDisplayShowCustomEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
		ImageView backBtn = (ImageView) findViewById(R.id.back);
		ImageView okBtn = (ImageView) findViewById(R.id.ok);
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText(R.string.setting_nav);
		okBtn.setVisibility(View.INVISIBLE);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	
	private int getTimeNum(){
		if(TIME1){
			return 0;
		}
		
		if(TIME2){
			return 1;
		}
		
		if(TIME3){
			return 2;
		}
		
		if(TIME4){
			return 3;
		}
		
		if(TIME5){
			return 4;
		}
		
		if(TIME6){
			return 5;
		}
		return 0;
	}
	
}

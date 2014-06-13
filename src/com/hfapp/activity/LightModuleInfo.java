package com.hfapp.activity;

import java.util.HashMap;

import com.example.palytogether.R;
import com.hf.module.IEventListener;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleException;
import com.hf.module.impl.KeyValueHelper;
import com.hf.module.impl.LocalModuleInfoContainer;
import com.hf.module.info.GPIO;
import com.hf.module.info.ModuleInfo;
import com.hf.module.info.ModuleKeyValue;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class LightModuleInfo extends Activity implements OnClickListener,IEventListener{
	private ImageView moduleEditer;
	private ImageView TimerManger;
	private ImageView smartCtrler;
	private ImageView History;
	
	private ImageView icon;
	
	private ModuleInfo mi;
	private IModuleManager manager;
	
	private TextView tv_stat;
	private TextView tv_time;
	
	private boolean stat = false;
	private boolean isonline = false;
	Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				
				break;
			case 1:
				setImage();
				break;
			case 100:
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
		setContentView(R.layout.module_info_layout);
		String mac = getIntent().getStringExtra("mac");
		manager = ManagerFactory.getInstance().getManager();
		manager.registerEventListener(this);
		mi = LocalModuleInfoContainer.getInstance().get(mac);
		initActionbar();
		initView();
		initStat();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		ModuleKeyValue mkv = KeyValueHelper.getInstence().get(mi.getMac());
		icon.setImageResource(ImageContentor.getOpenImageRs(mkv.getIndex()));
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
	
	private void initView(){
		moduleEditer = (ImageView) findViewById(R.id.module_editer);
		TimerManger = (ImageView) findViewById(R.id.timer_manger);
		smartCtrler = (ImageView) findViewById(R.id.smart_ctrl);
		icon = (ImageView) findViewById(R.id.moduleinfo_module_imag);
		
		tv_stat = (TextView) findViewById(R.id.moduelinfo_module_stat);
		tv_time = (TextView) findViewById(R.id.moduelinfo_module_timer);
		
		
		History = (ImageView) findViewById(R.id.history);
		moduleEditer.setOnClickListener(this);
		TimerManger.setOnClickListener(this);
		smartCtrler.setOnClickListener(this);
		History.setOnClickListener(this);
		icon.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		i.putExtra("mac", mi.getMac());
		switch (v.getId()) {
		case R.id.module_editer:
			i.setClass(this, ModuleModify.class);
			startActivity(i);
			break;
		case R.id.timer_manger:
			i.setClass(this, TimerCenter.class);
			startActivity(i);
			break;
		case R.id.smart_ctrl:
			i.setClass(this, SmartCtrler.class);
			startActivity(i);
			break;
		case R.id.history:
			i.setClass(this, LogList.class);
			startActivity(i);
			break;
		case R.id.moduleinfo_module_imag:
			onPressImage();
			break;
		default:
			break;
		}
	}
	private void initStat(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					stat = manager.getHelper().getHFGPIO(mi.getMac(), 6);
					isonline = true;
				} catch (ModuleException e) {
					// TODO Auto-generated catch block
					hand.sendEmptyMessage(e.getErrorCode());
					isonline = false;
				}
				hand.sendEmptyMessage(1);
			}
		}).start();
	}
	private void onPressImage(){
		if(isonline){
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						stat = manager.getHelper().setHFGPIO(mi.getMac(), 6, stat);
						hand.sendEmptyMessage(100);
					} catch (ModuleException e) {
						// TODO Auto-generated catch block
						hand.sendEmptyMessage(e.getErrorCode());
					}
					hand.sendEmptyMessage(1);
				}
			}).start();
		}
	}
	
	private void setImage(){
		ModuleKeyValue mkv = KeyValueHelper.getInstence().get(mi.getMac());
		if(isonline){
			if(stat){
				icon.setImageResource(ImageContentor.getOpenImageRs(mkv.getIndex()));
				tv_stat.setText("on");
			}else{
				icon.setImageResource(ImageContentor.getCloseImageRs(mkv.getIndex()));
				tv_stat.setText("close");
			}
		}else{
			icon.setImageResource(ImageContentor.getOutLineImageRs(mkv.getIndex()));
			tv_stat.setText("offline");
		}
	}

	@Override
	public void onEvent(String mac, byte[] t2data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCloudLogin(boolean loginstat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCloudLogout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewDevFind(ModuleInfo mi) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGPIOEvent(String mac, HashMap<Integer, GPIO> GM) {
		// TODO Auto-generated method stub
		if(mac.equalsIgnoreCase(mi.getMac())){
			stat = GM.get(6).getStatus();
			hand.sendEmptyMessage(1);
		}
	}

	@Override
	public void onTimerEvent(String mac, byte[] t2data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUARTEvent(String mac, byte[] userData, boolean chanle) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		manager.unregisterEventListener(this);
	}
	
	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
	}
}

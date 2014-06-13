package com.hfapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.example.palytogether.R;
import com.hf.module.IEventListener;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.impl.KeyValueHelper;
import com.hf.module.impl.LocalModuleInfoContainer;
import com.hf.module.impl.adaptor.AndroidAdaptor;
import com.hf.module.info.GPIO;
import com.hf.module.info.ModuleInfo;
import com.hf.module.info.ModuleKeyValue;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Smartlink extends Activity implements IEventListener{
	
	private Animation animi;

	private EditText ssid;
	private EditText pswd;
	private ImageButton start;
	private IModuleManager manager;
//	private ArrayList<ModuleInfo> modulelist = new ArrayList<ModuleInfo>();
	private Timer time;
	private boolean isconnect = false;
	private Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(Smartlink.this, "network err",Toast.LENGTH_SHORT).show();
				break;
			case 1:
				
				finish();
				break;
			case 100:
				Toast.makeText(Smartlink.this, "WIFI unconnect", Toast.LENGTH_SHORT).show();
				break;
			case 101:
				Toast.makeText(Smartlink.this, "smartlink timeout", Toast.LENGTH_SHORT).show();
				break;
			case -202:
				Toast.makeText(Smartlink.this, "设备已经被添加", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.smartlink_layout);
		manager = ManagerFactory.getInstance().getManager();
		manager.registerEventListener(Smartlink.this);
		initActionbar();
		initView();
		time = new Timer();
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
		title.setText(R.string.module_add);
		okBtn.setVisibility(View.INVISIBLE);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	/**
	 * 添加一经发现的设备
	 */
	class addModuleThread implements Runnable {
		private ModuleInfo mi;

		public addModuleThread(ModuleInfo mi) {
			this.mi = mi;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			manager.unregisterEventListener(Smartlink.this);
//			if(modulelist.size()<=0){
//				hand.sendEmptyMessage(101);
//				return;
//			}
			
			if(mi == null){
				return ;
			}
//			Iterator<ModuleInfo> it = modulelist.iterator();
//			while(it.hasNext()){
//				ModuleInfo mi = it.next();
//				try {
//						mi.setId(null);
//						mi.setAccessKey(ModuleConfig.accessKey);
//						mi.setLocalKey(ModuleConfig.localModulePswd);
//						mi.setName(mi.getMac().substring(4));
//						
//						try{
//							mi = manager.setModule(mi);
//							mi.setFactoryId((int) ModuleConfig.factoryId);
//						}catch(ModuleException e){
//							
//						}
//					
//					manager.getHelper().setModulePSWD(mi);
//					manager.getHelper().setServAdd(mi,ModuleConfig.cloudsericeIp ,ModuleConfig.cloudservicePort);
//					LocalModuleInfoContainer.getInstance().put(mi.getMac(), mi);
//					hand.sendEmptyMessage(1);
//				} catch (ModuleException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					hand.sendEmptyMessage(e.getErrorCode());
//				}
//			}
			try {
				mi.setId(null);
				mi.setAccessKey(ModuleConfig.accessKey);
				mi.setLocalKey(ModuleConfig.localModulePswd);
				mi.setName(mi.getMac().substring(4));
				
				try{
					mi = manager.setModule(mi);
					mi.setFactoryId((int) ModuleConfig.factoryId);
				}catch(ModuleException e){
					e.printStackTrace();
				}
			
			manager.getHelper().setModulePSWD(mi);
			manager.getHelper().setServAdd(mi,ModuleConfig.cloudsericeIp ,ModuleConfig.cloudservicePort);
			LocalModuleInfoContainer.getInstance().put(mi.getMac(), mi);
			//给设备图片
			ModuleKeyValue mkv = new ModuleKeyValue(LocalModuleInfoContainer.getInstance().getAll().size(), 0);
			KeyValueHelper.getInstence().put(mi.getMac(), mkv);
			
			hand.sendEmptyMessage(1);
		} catch (ModuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			hand.sendEmptyMessage(e.getErrorCode());
		}
		}
	};
	
//	TimerTask TimeaddModuleThread = new TimerTask() {
//	
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			manager.unregisterEventListener(Smartlink.this);
//			if(modulelist.size()<=0){
//				hand.sendEmptyMessage(101);
//				return;
//			}
//			Iterator<ModuleInfo> it = modulelist.iterator();
//			while(it.hasNext()){
//				ModuleInfo mi = it.next();
//				try {
//					if(mi.getId() == null){
//						mi.setAccessKey(ModuleConfig.accessKey);
//						mi.setLocalKey(ModuleConfig.localModulePswd);
//						mi.setName(mi.getMac().substring(4));
//						mi = manager.setModule(mi);
//					}
//					manager.getHelper().setModulePSWD(mi.getMac());
//					try{Thread.sleep(500);}catch(Exception e){}
//					manager.getHelper().setServAdd(mi.getMac(),ModuleConfig.cloudsericeIp ,ModuleConfig.cloudservicePort);
//				} catch (ModuleException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					hand.sendEmptyMessage(e.getErrorCode());
//				}
//			}
//			finish();
//		}
//	};
	/**
	 * ????smartLink
	 */
	Runnable smartlinkThread = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String strSSID = ssid.getText().toString().trim();
			String strPSWD = pswd.getText().toString().trim();
			saveRouterInfo(strSSID, strPSWD);
			
			manager.connectModuleToWIFI(strSSID, strPSWD);
//			time.schedule(TimeaddModuleThread, 15000);
//			hand.postDelayed(addModuleThread, 15000);
		}
	};
	Runnable soundSmartlink = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String strSSID = ssid.getText().toString().trim();
			String strPSWD = pswd.getText().toString().trim();
			saveRouterInfo(strSSID, strPSWD);
			//manager.connectModuleToWIFI2(strSSID, strPSWD);
			manager.connectModuleToWIFI(strSSID, strPSWD);
		}
	};
	private void initView(){
		ssid = (EditText) findViewById(R.id.router_ssid);
		ssid.setText(getSSID());
		pswd = (EditText) findViewById(R.id.router_pswd);
		pswd.setText(getSavedPswd(getSSID()));
		start = (ImageButton) findViewById(R.id.start_smartlink);
		animi  = AnimationUtils.loadAnimation(this, R.anim.smartlink_rot);  
		LinearInterpolator lin = new LinearInterpolator();  
		animi.setInterpolator(lin);  
		
		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				new Thread(smartlinkThread).start();
				if(isconnect){
					//manager.stopSmartlink2();
					manager.stopSmartlink();
					start.clearAnimation();
					isconnect = false;
				}else{
					isconnect = true;
					start.startAnimation(animi);
					new Thread(soundSmartlink).start();
				}
			}
		});
//		finish();
	}
	
	private String getSSID(){
		AndroidAdaptor androidAdaptor = new AndroidAdaptor();
		try {
			return androidAdaptor.getWifiSSID();
		} catch (ModuleException e) {
			// TODO Auto-generated catch block
			hand.sendEmptyMessage(100);
		}
		return "";
	}
	
	private String getSavedPswd(String ssid){
		SharedPreferences sp = getSharedPreferences(ssid, MODE_PRIVATE);
		return sp.getString("PSWD", "");
	}
	private void saveRouterInfo(String ssid,String pswd){
		SharedPreferences sp = getSharedPreferences(ssid, MODE_PRIVATE);
		Editor e = sp.edit();
		e.putString("PSWD", pswd);
		e.commit();
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
		System.out.println(getClass().getCanonicalName()+":"+mi.getMac());
//		Iterator<ModuleInfo> it = modulelist.iterator();
//		while(it.hasNext()){
//			ModuleInfo tmp = it.next();
//			if(tmp.getMac().equalsIgnoreCase(mi.getMac())){
//				return ;
//			}		
//		}
//		modulelist.add(mi);
		
		new Thread(new addModuleThread(mi)).start();
	}

	@Override
	public void onGPIOEvent(String mac, HashMap<Integer, GPIO> GM) {
		// TODO Auto-generated method stub
		
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}
	
	@Override
	public void finish() {
		manager.unregisterEventListener(Smartlink.this);
		if(isconnect){
			manager.stopSmartlink();
			start.clearAnimation();
		}
		super.finish();
	}
}

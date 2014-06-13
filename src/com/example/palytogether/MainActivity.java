package com.example.palytogether;
/**
 * 判断是否是第一次登陆
 * 	if（true） 进行引导界面
 *  esle 进行加载用户数据
 */
import com.example.config.Userconfig;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.impl.adaptor.AndroidAdaptor;
import com.hfapp.activity.Login;
import com.hfapp.activity.ModuleList;
import com.hfapp.work.InitThread;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				startLocalCtrlModuListActivity();
//				Toast.makeText(MainActivity.this, "网络连接错误", Toast.LENGTH_SHORT).show();
				break;
			case 5: //用户名输入错误
				Toast.makeText(MainActivity.this, "用户名输入错误", Toast.LENGTH_SHORT).show();
				break;
			case 2: //密码错误
				Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
				break;
			case 3: //登陆成功
				startModuleListActivity();
				Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				Toast.makeText(MainActivity.this, "什么网络都没有 进去干啥！", Toast.LENGTH_SHORT).show();
				break;
			case -103:
				Toast.makeText(MainActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
				break;
			case -101:
				Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		InitConfig();
		if(Userconfig.isFristLogin()){
			/*
			 * 如果是第一次登陆 就去引导界面
			 *  */
			Intent i = new Intent(this, Login.class);
			startActivity(i);
			finish();
		}else{
			/*
			 * 如果不是第一次登陆但是 缺少用户名密码 那就去登陆界面
			 */
			if(ModuleConfig.cloudPassword==null||ModuleConfig.cloudUserName==null){
				Intent logintent = new Intent(this, Login.class);
				startActivity(logintent);
				finish();
			}else{
				/**
				 * 如果 有用户名密码 那么 校验用户名密码是否正确  
				 * 
				 */
				new Thread(new InitThread(hand)).start();
			}
		}
	}

	
	private void InitConfig(){
		ModuleConfig.appcontext = getApplicationContext();
		ModuleConfig.accessKey="8a21049f466068f90146607eaaa1022a";
		ModuleConfig.cloudServiceUrl = "https://115.29.164.59/usvc/";
		ModuleConfig.broadcastPort = 38899;
		ModuleConfig.pulseInterval = 6000;
		try {
			ModuleConfig.broadcastIp = new AndroidAdaptor().getBroadCast();
		} catch (ModuleException e) {
			// TODO Auto-generated catch block
			ModuleConfig.broadcastIp = "255.255.255.255";
		}
	}
	
	
	private void startModuleListActivity(){
		Intent i = new Intent(this,ModuleList.class);
		startActivity(i);
		finish();
	}
	
	
	
	private void startLocalCtrlModuListActivity(){
		if(isWifiConnect()){
			startModuleListActivity();
		}else{
			hand.sendEmptyMessage(4);
		}
	}
	private boolean isWifiConnect(){
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		if(wm.isWifiEnabled()){
			if(wm.getConnectionInfo() != null){
				return true;
			}
		}
		return false;
	}
}

package com.hfapp.activity;

import java.util.ArrayList;
import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.example.palytogether.R;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleException;
import com.hf.module.impl.LocalModuleInfoContainer;
import com.hf.module.info.ADCinfo;
import com.hf.module.info.ModuleInfo;

public class ADC_Service extends Service {
	IModuleManager manager;//完成LkouU口基本功能封装：用户管理功能、模块管理功能、设备控制功能、本地心跳功能
	NotificationManager noticeManager;//状态栏通知的管理类，负责发送通知、清除通知
	public static Hashtable<String, Boolean> autoList = new Hashtable<String, Boolean>();
	// 第一步，初始化设备信息
	@Override
	public void onCreate() {
		manager = ManagerFactory.getInstance().getManager();
		noticeManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		new MyThread().start();
		super.onCreate();
	}
	
	// 第二步，开始数据数据对比
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
	}

	// 
	class MyThread extends Thread{
		@Override
		public void run() {
			try {
				while (true) {
					Thread.sleep(5000);
					ArrayList<ModuleInfo> mis = LocalModuleInfoContainer.getInstance().getAll();
					if(mis == null){
						
						stopSelf();
					}
					
					if(mis.isEmpty()){
						continue;
					}
					for(ModuleInfo mi :mis){
						if(mi == null)
							continue;
						get_data(mi.getMac());
					}				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.run();
		}
	}
	
	String adc1;
	String adc2;
	String adc3;
	String T1;
	String Do;
	String T2;
	String H;
	// 获取设备信息数据
	private void get_data(final String mac){
		new Thread(new Runnable() {
			public void run() {
				try {
					ADCinfo info = manager.getHelper().getHFADCModuleValues(mac);
					int adc1_data = info.getAD1Value() * 3300 / 4096;
					int adc2_data = info.getAD2Value() * 3300 / 4096;
					int adc3_data = info.getAD3Value() * 3300 / 4096;
					int adin_data = info.getDoValue()/100;
					int hum_tem_am2301 = info.getAm2301();
					int tem_ds18b20 = info.getDs18b20();
					adc1 = adc1_data / 1000 + "." + adc1_data % 1000 / 100 + adc1_data % 100 / 10;
					adc2 = adc2_data / 1000 + "." + adc2_data % 1000 / 100 + adc2_data % 100 / 10;
					adc3 = adc3_data / 1000 + "." + adc3_data % 1000 / 100 + adc3_data % 100 / 10;
					T1 = tem_ds18b20 * 625 / 10000 + "." + (tem_ds18b20 * 625 % 10000) / 100;
					T2 = "" + (hum_tem_am2301 & 0x7fff) / 10.0;
					H = ((hum_tem_am2301 & 0xffff0000) >> 16) / 10.0 + "";
					Log.i("louyang", "start_notify");
					data(mac+1,mac);
					data(mac+2,mac);
					data(mac+3,mac);
					data(mac+4,mac);
					data(mac+6,mac);
					data(mac+7,mac);
				} catch (ModuleException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	class Value{
		float limit_low;
		float limit_up;
		float value;
		public Value(float limit_low,float limit_up,float value) {
			// TODO Auto-generated constructor stub
			this.limit_low = limit_low;
			this.limit_up = limit_up;
			this.value = value;
		}

	}
	
	
	// 处理后的数据如果超出限制，则执行此通知方法
	@SuppressLint("NewApi")
	private void Notice(final String mac,Value v){
		ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
		if(mi == null)
			return ;
		Intent intent = new Intent(this,ADCModuleActivity.class);
		intent.putExtra("mac", mac);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		android.app.Notification.Builder notification = new Notification.Builder(this)
		.setContentTitle("数据有异常")
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentText(mi.getName()+"数据超出界限")
		.setPriority(Notification.PRIORITY_DEFAULT)
		.setDefaults(Notification.DEFAULT_ALL)
		.setAutoCancel(true)
		.setContentIntent(pi);
		noticeManager.notify(100, notification.build());
		if(getAutoStatus(mac))
		{
			
			if(v.value<v.limit_low){
				Log.e("demon", "set_data<limit_low:");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							ManagerFactory.getInstance().getManager().getHelper().setHFGPIO(mac, 1, false);
						} catch (ModuleException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Log.e("demon", "end send ctrl 3gPIO");
					}
					}).start();
			}else if(v.value>v.limit_up){
				Log.e("demon", "set_data>limit_upp");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							ManagerFactory.getInstance().getManager().getHelper().setHFGPIO(mac, 2, false);
						} catch (ModuleException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Log.e("demon", "end send ctrl 3gPIO");
					}
					}).start();
			}
			
		}
		Log.e("demon", "before send ctrl 3gPIO");
	}
	
	
	
	private void do_data(String name,final String mac,String GPIO){
		SharedPreferences share = this.getSharedPreferences(name, Context.MODE_PRIVATE);
		String xishu = share.getString(mac+"d", "1");
		String changshu = share.getString(mac + "e", "0");
		Float xs = Float.valueOf(xishu);
		Float cs = Float.valueOf(changshu);
		Float da = Float.valueOf(GPIO);
		Float set_data = xs*da+cs;
		String lower = share.getString(mac+"f", "0");//设置下限
		String upper = share.getString(mac+"g", "0");//设置上限
		Float limit_low = Float.valueOf(lower);
		Float limit_upp = Float.valueOf(upper);
		if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
			Log.i("info", "数据正常");
		} else {
			Log.i("info", "数据异常:"+set_data);
			Notice(mac,new Value(limit_low, limit_upp, set_data));
		}
	}
	
	private void data(String name,String mac,String GPIO){
		if(name.equals(mac+1)){
			GPIO = adc1;
			do_data(name, mac, GPIO);
		} else if (name.equals(mac+2)){
			GPIO = adc2;
			do_data(name, mac, GPIO);
		} else if (name.equals(mac+2)){
			GPIO = adc3;
			do_data(name, mac, GPIO);
		} else if (name.equals(mac+2)){
			GPIO = T1;
			do_data(name, mac, GPIO);
		} else if (name.equals(mac+2)){
			GPIO = Do;
			do_data(name, mac, GPIO);
		} else if (name.equals(mac+2)){
			GPIO = T2;
			do_data(name, mac, GPIO);
		} else if (name.equals(mac+2)){
			GPIO = H;
			do_data(name, mac, GPIO);
		} 
	}
	
	// 对获取到的GPIO数据进行处理
	public void data(String i,String mac){
		
		SharedPreferences share = this.getSharedPreferences(i, Context.MODE_PRIVATE);
		if(i.equals(mac+1)){
			String xishu = share.getString(mac+"d", "1");//系数
			String changshu = share.getString(mac+"e", "0");//常数
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc1);
			Float set_data = xs*da+cs;//校准后的值

			String lower = share.getString(mac+"f", "0");//设置下限
			String upper = share.getString(mac+"g", "0");//设置上限
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "数据正常");
			} else {
				Log.i("info", "数据异常");
				Notice(mac,new Value(limit_low, limit_upp, set_data));
			}
		} else if(i.equals(mac+2)){
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc2);
			Float set_data = xs*da+cs;//校准后的值
			//上下限设置
			String lower = share.getString(mac+"f", "0");//设置下限
			String upper = share.getString(mac+"g", "0");//设置上限
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "数据正常");
			} else {
				Log.i("info", "数据异常");
				Notice(mac,new Value(limit_low, limit_upp, set_data));
			}
		} else if(i.equals(mac+3)){
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc3);
			Float set_data = xs*da+cs;//校准后的值
			//上下限设置
			String lower = share.getString(mac+"f", "0");//设置下限
			String upper = share.getString(mac+"g", "0");//设置上限
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "数据正常");
			} else {
				Log.i("info", "数据异常");
				Notice(mac,new Value(limit_low, limit_upp, set_data));
			}
		} else if(i.equals(mac+4)){
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(T1);
			Float set_data = xs*da+cs;//校准后的值
			//上下限设置
			String lower = share.getString(mac+"f", "0");//设置下限
			String upper = share.getString(mac+"g", "0");//设置上限
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "数据正常");
			} else {
				Log.i("info", "数据异常");
				Notice(mac,new Value(limit_low, limit_upp, set_data));
			}
		}else if(i.equals(mac+6)){
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(T2);
			Float set_data = xs*da+cs;//校准后的值
			//上下限设置
			String lower = share.getString(mac+"f", "0");//设置下限
			String upper = share.getString(mac+"g", "0");//设置上限
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "数据正常");
			} else {
				Log.i("info", "数据异常");
				Notice(mac,new Value(limit_low, limit_upp, set_data));
			}
		} else if(i.equals(mac+7)){
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(H);
			Float set_data = xs*da+cs;//校准后的值
			//上下限设置
			String lower = share.getString(mac+"f", "0");//设置下限
			String upper = share.getString(mac+"g", "0");//设置上限
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "数据正常");
			} else {
				Log.i("info", "数据异常");
				Notice(mac,new Value(limit_low, limit_upp, set_data));
			}
		}
	}

	
	@Override
	public void onDestroy() {
		stopSelf();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private boolean getAutoStatus(String mac){
		SharedPreferences sp = getSharedPreferences("AUTOSTATUS", MODE_PRIVATE);
		return sp.getBoolean(mac, false);
	}
}

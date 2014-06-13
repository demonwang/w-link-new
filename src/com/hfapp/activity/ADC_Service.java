package com.hfapp.activity;

import java.util.ArrayList;

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
	IModuleManager manager;//���LkouU�ڻ������ܷ�װ���û������ܡ�ģ������ܡ��豸���ƹ��ܡ�������������
	NotificationManager noticeManager;//״̬��֪ͨ�Ĺ����࣬������֪ͨ�����֪ͨ
	
	// ��һ������ʼ���豸��Ϣ
	@Override
	public void onCreate() {
		manager = ManagerFactory.getInstance().getManager();
		noticeManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		new MyThread().start();
		super.onCreate();
	}
	
	// �ڶ�������ʼ�������ݶԱ�
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
	// ��ȡ�豸��Ϣ����
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
	
	// ��������������������ƣ���ִ�д�֪ͨ����
	private void Notice(String mac){
		ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
		if(mi == null)
			return ;
		Intent intent = new Intent(this,ADCModuleActivity.class);
		intent.putExtra("mac", mac);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		android.app.Notification.Builder notification = new Notification.Builder(this)
		.setContentTitle("�������쳣")
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentText(mi.getName()+"���ݳ�������")
		.setPriority(Notification.PRIORITY_DEFAULT)
		.setDefaults(Notification.DEFAULT_ALL)
		.setAutoCancel(true)
		.setContentIntent(pi);
		noticeManager.notify(100, notification.build());
	}
	
	
	@SuppressLint("NewApi")
	private void do_data(String name,String mac,String GPIO){
		SharedPreferences share = this.getSharedPreferences(name, Context.MODE_PRIVATE);
		String xishu = share.getString(mac+"d", "1");
		String changshu = share.getString(mac + "e", "0");
		Float xs = Float.valueOf(xishu);
		Float cs = Float.valueOf(changshu);
		Float da = Float.valueOf(GPIO);
		Float set_data = xs*da+cs;
		String lower = share.getString(mac+"f", "0");//��������
		String upper = share.getString(mac+"g", "0");//��������
		Float limit_low = Float.valueOf(lower);
		Float limit_upp = Float.valueOf(upper);
		if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
			Log.i("info", "��������");
		} else {
			Log.i("info", "�����쳣");
			Notice(mac);
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
	
	// �Ի�ȡ����GPIO���ݽ��д���
	public void data(String i,String mac){
		
		SharedPreferences share = this.getSharedPreferences(i, Context.MODE_PRIVATE);
		if(i.equals(mac+1)){
			String xishu = share.getString(mac+"d", "1");//ϵ��
			String changshu = share.getString(mac+"e", "0");//����
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc1);
			Float set_data = xs*da+cs;//У׼���ֵ

			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "��������");
			} else {
				Log.i("info", "�����쳣");
				Notice(mac);
			}
		} else if(i.equals(mac+2)){
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc2);
			Float set_data = xs*da+cs;//У׼���ֵ
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "��������");
			} else {
				Log.i("info", "�����쳣");
				Notice(mac);
			}
		} else if(i.equals(mac+3)){
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc3);
			Float set_data = xs*da+cs;//У׼���ֵ
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "��������");
			} else {
				Log.i("info", "�����쳣");
				Notice(mac);
			}
		} else if(i.equals(mac+4)){
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(T1);
			Float set_data = xs*da+cs;//У׼���ֵ
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "��������");
			} else {
				Log.i("info", "�����쳣");
				Notice(mac);
			}
		}else if(i.equals(mac+6)){
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(T2);
			Float set_data = xs*da+cs;//У׼���ֵ
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "��������");
			} else {
				Log.i("info", "�����쳣");
				Notice(mac);
			}
		} else if(i.equals(mac+7)){
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(H);
			Float set_data = xs*da+cs;//У׼���ֵ
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				Log.i("info", "��������");
			} else {
				Log.i("info", "�����쳣");
				Notice(mac);
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

}

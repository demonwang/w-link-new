package com.hfapp.activity;

//import android.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palytogether.R;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleException;
import com.hf.module.impl.LocalModuleInfoContainer;
import com.hf.module.info.ADCinfo;
import com.hf.module.info.ModuleInfo;

/**
 * �豸��Ϣ�� ͨ������豸ͼ�꣬���뵽����
 * 
 * @author Administrator
 * 
 */
public class ADCModuleActivity extends Activity {
	private static String TAG = "SecondActivity--->";
	ModuleInfo mi;//�豸��Ϣ
	TextView tva, tvb, tvc, tvd, tve, tvf, tvg;
	TextView tvaa, tvbb, tvcc, tvdd, tvee, tvff, tvgg;
	ImageButton shu;// �ж�ADֵ������1����ɫ��С��1���߹رգ���ɫ
	ImageButton yuan;// ����GPIO��status
	ImageButton clock;// ��ʱ���ã��˰汾�Ըù���û��ʵ�֣�
	ImageButton switcher0, switcher1, switcher2;// ����GPIO״̬��ͬ�����
	ADCinfo adcvalues; // �豸��Ϣ
	IModuleManager manager;// ���L�ڼ�U�ڻ������ܵķ�װ���������û������ܡ�ģ������ܡ��豸���ƹ��ܡ������������ܡ�
	String mac;// ��ModuleList�л�ȡ�ĵ�ַ
	NotificationManager notice;
	
	
	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:// ���δ��ȡ�����ݣ���ť���ɵ��
				tva.setClickable(false);
				tvb.setClickable(false);
				tvc.setClickable(false);
				tvd.setClickable(false);
				tve.setClickable(false);
				tvf.setClickable(false);
				tvg.setClickable(false);
				tvaa.setClickable(false);
				tvbb.setClickable(false);
				tvcc.setClickable(false);
				tvdd.setClickable(false);
				tvee.setClickable(false);
				tvff.setClickable(false);
				tvgg.setClickable(false);
				clock.setClickable(false);
				yuan.setClickable(false);
				switcher1.setClickable(false);
				switcher2.setClickable(false);
				switcher0.setClickable(false);
				shu.setImageResource(R.drawable.adcswitchd);
				yuan.setImageResource(R.drawable.adcswitchb);
				switcher1.setImageResource(R.drawable.adcturnoff);
				switcher2.setImageResource(R.drawable.adcturnoff);
				switcher0.setImageResource(R.drawable.adcturnoff);
				break;
			case 3:
				tve.setText(change_data);
				break;
			case 6:
				if (status == false) {
					yuan.setImageResource(R.drawable.adcswitchb);
				} else if (status == true) {
					yuan.setImageResource(R.drawable.adcswitcha);
				}
				break;
			case 7:
				if (status0 == false) {
					switcher0.setImageResource(R.drawable.adcturnoff);
				} else if (status0 == true) {
					switcher0.setImageResource(R.drawable.adcturnon);
				}
				break;
			case 8:
				if (status1 == false) {
					switcher1.setImageResource(R.drawable.adcturnoff);
				} else if (status1 == true) {
					switcher1.setImageResource(R.drawable.adcturnon);
				}
				break;
			case 9:
				if (status2 == false) {
					switcher2.setImageResource(R.drawable.adcturnoff);
				} else if (status2 == true) {
					switcher2.setImageResource(R.drawable.adcturnon);
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		manager = ManagerFactory.getInstance().getManager();
		mac = getIntent().getStringExtra("mac");
		mi = LocalModuleInfoContainer.getInstance().get(mac);//��ȡ�豸��Ϣ
		notice = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		initActionbar();
		init();
		judge_GPIOState();
		skip();
		getADCValues();
	}
	
//	@Override
//	protected void onPause() {
//		Intent i = new Intent(this, ADC_Service.class);
//		i.putExtra("mac", mac);
//		Log.i("info", "mac==="+mac);
//		startService(i);
//		super.onPause();
//	}
	

	// ��һ������ʼ�������No Problem��
	public void init() {
		tva = (TextView) findViewById(R.id.tva);
		tvb = (TextView) findViewById(R.id.tvb);
		tvc = (TextView) findViewById(R.id.tvc);
		tvd = (TextView) findViewById(R.id.tvd);
		tve = (TextView) findViewById(R.id.tve);
		tvf = (TextView) findViewById(R.id.tvf);
		tvg = (TextView) findViewById(R.id.tvg);
		tvaa = (TextView) findViewById(R.id.tvaa);
		tvbb = (TextView) findViewById(R.id.tvbb);
		tvcc = (TextView) findViewById(R.id.tvcc);
		tvdd = (TextView) findViewById(R.id.tvdd);
		tvee = (TextView) findViewById(R.id.tvee);
		tvff = (TextView) findViewById(R.id.tvff);
		tvgg = (TextView) findViewById(R.id.tvgg);
		shu = (ImageButton) findViewById(R.id.sx);
		yuan = (ImageButton) findViewById(R.id.yq);
		clock = (ImageButton) findViewById(R.id.clock);
		switcher0 = (ImageButton) findViewById(R.id.switcher1);
		switcher1 = (ImageButton) findViewById(R.id.switcher2);
		switcher2 = (ImageButton) findViewById(R.id.switcher3);
	}

	// �ڶ��������ActionBar��No Problem��
	private void initActionbar() {
		ActionBar bar = getActionBar();
		bar.setCustomView(R.layout.layout_actionbar);
		bar.setDisplayShowCustomEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
		ImageView backBtn = (ImageView) findViewById(R.id.back);
		ImageView okBtn = (ImageView) findViewById(R.id.ok);
		okBtn.setImageResource(R.drawable.setactionbar);
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText(mi.getName());	
		okBtn.setOnClickListener(new OnClickListener() {// OK���ܼ��������Ͻǣ���������豸���޸�ҳ�棬�����޸��豸���ơ�ͼƬ���Լ�ɾ���豸

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ADCModuleActivity.this,ModuleModify.class);
				i.putExtra("mac", mac);
				startActivity(i);
				finish();
			}
		});
		backBtn.setOnClickListener(new OnClickListener() {// �˳����������Ͻǣ�����˳��ý��档ֱ��finish��������
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// ���������ж��Ƿ�������
	boolean status;// ��ȡ�豸��״̬(Բ��GPIO״̬)
	boolean status0;
	boolean status1;
	boolean status2;
	private void judge_GPIOState() {
		new Thread(new Runnable() {
			public void run() {
				try {
					status = manager.getHelper().getHFGPIO(mac, 11);
					hand.sendEmptyMessage(6);
					status0 = manager.getHelper().getHFGPIO(mac, 0);
					hand.sendEmptyMessage(7);
					status1 = manager.getHelper().getHFGPIO(mac, 1);
					hand.sendEmptyMessage(8);
					status2 = manager.getHelper().getHFGPIO(mac, 2);
					hand.sendEmptyMessage(9);
				} catch (Exception e) {
					Log.i("info", "���������@ȡ��Bʧ��");
					hand.sendEmptyMessage(1);//���нM���O�óɲ����c����B
					return;
				}
			}
		}).start();
	}

	// ���Ĳ�������豸��Ϣ��ת���޸Ľ���
	Intent intent;
	public void skip() {
		intent = new Intent(ADCModuleActivity.this, SensorActivity.class);
		intent.putExtra("mac", mac);
		tva.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+1);
				intent.putExtra("name", tvaa.getText().toString());
				intent.putExtra("value", adc1);
				startActivityForResult(intent, 100);
			}
		});
		tvb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+2);
				intent.putExtra("name", tvbb.getText().toString());
				intent.putExtra("value", adc2);
				startActivityForResult(intent, 100);
			}
		});
		tvc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+3);
				intent.putExtra("name", tvcc.getText().toString());
				intent.putExtra("value", adc3);
				startActivityForResult(intent, 100);
			}
		});
		tvd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+4);
				intent.putExtra("name", tvdd.getText().toString());
				intent.putExtra("value", T1);
				startActivityForResult(intent, 100);
			}
		});
		tve.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+5);
				intent.putExtra("name", tvee.getText().toString());
				intent.putExtra("value", Do);
				startActivityForResult(intent, 100);
			}
		});
		tvf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+6);
				intent.putExtra("name", tvff.getText().toString());
				intent.putExtra("value", T2);
				startActivityForResult(intent, 100);
			}
		});
		tvg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+7);
				intent.putExtra("name", tvgg.getText().toString());
				intent.putExtra("value", H);
				startActivityForResult(intent, 100);
			}
		});
		tvaa.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+1);
				intent.putExtra("name", tvaa.getText().toString());
				intent.putExtra("value", adc1);
				startActivityForResult(intent, 100);
			}
		});
		tvbb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+2);
				intent.putExtra("name", tvbb.getText().toString());
				intent.putExtra("value", adc2);
				startActivityForResult(intent, 100);
			}
		});
		tvcc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+3);
				intent.putExtra("name", tvcc.getText().toString());
				intent.putExtra("value", adc3);
				startActivityForResult(intent, 100);
			}
		});
		tvdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+4);
				intent.putExtra("name", tvdd.getText().toString());
				intent.putExtra("value", T1);
				startActivityForResult(intent, 100);
			}
		});
		tvee.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+5);
				intent.putExtra("name", tvee.getText().toString());
				intent.putExtra("value", Do);
				startActivityForResult(intent, 100);
			}
		});
		tvff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+6);
				intent.putExtra("name", tvff.getText().toString());
				intent.putExtra("value", T2);
				startActivityForResult(intent, 100);
			}
		});
		tvgg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("tag", mac+7);
				intent.putExtra("name", tvgg.getText().toString());
				intent.putExtra("value", H);
				startActivityForResult(intent, 100);
			}
		});
		clock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(ADCModuleActivity.this, "��ģ�����ڵ�����", 1).show();
			}
		});
		yuan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				try {
					setGPIOState();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		switcher0.setOnClickListener(new OnClickListener() { // new

					@Override
					public void onClick(View view) {
						try {
							setGPIOState0();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		switcher1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				try {
					setGPIOState1();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		switcher2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				try {
					setGPIOState2();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private void setGPIOState(){
		new Thread(new Runnable() {
			public void run() {
				try {
					status = manager.getHelper().setHFGPIO(mac, 11, status);
				} catch (ModuleException e) {
					return;
				}
				hand.sendEmptyMessage(6);
			}
		}).start();
	}
	private void setGPIOState0(){
		new Thread(new Runnable() {
			public void run() {
				try {
					status0 = manager.getHelper().setHFGPIO(mac, 0, status0);
				} catch (ModuleException e) {
					return;
				}
				hand.sendEmptyMessage(7);
			}
		}).start();
	}
	private void setGPIOState1(){
		new Thread(new Runnable() {
			public void run() {
				try {
					status1 = manager.getHelper().setHFGPIO(mac, 1, status1);
				} catch (ModuleException e) {
					return;
				}
				hand.sendEmptyMessage(8);
			}
		}).start();
	}
	private void setGPIOState2(){
		new Thread(new Runnable() {
			public void run() {
				try {
					status2 = manager.getHelper().setHFGPIO(mac, 2, status2);
				} catch (ModuleException e) {
					return;
				}
				hand.sendEmptyMessage(9);
			}
		}).start();
	}

	// ���岽����������ʾ�ڽ��浱��
	public static String adc1;
	public static String adc2;
	public static String adc3;
	public static String T1;
	public static String Do;
	public static String T2;
	public static String H;
	public void getADCValues() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					adcvalues = manager.getHelper().getHFADCModuleValues(mac);
					Log.i("info", "adcvalues��ֵΪ"+adcvalues);
					new Handler(getMainLooper()).post(new Runnable() {
						int adc1_data = adcvalues.getAD1Value() * 3300 / 4096;
						int adc2_data = adcvalues.getAD2Value() * 3300 / 4096;
						int adc3_data = adcvalues.getAD3Value() * 3300 / 4096;
						int adin_data = adcvalues.getDoValue()/100;
						int hum_tem_am2301 = adcvalues.getAm2301();
						int tem_ds18b20 = adcvalues.getDs18b20();
						int data = adcvalues.getAC_FB()* 3300 / 4096 / 1000;

						@Override
						public void run() {
							adc1 = adc1_data / 1000 + "." + adc1_data % 1000 / 100 + adc1_data % 100 / 10;
							adc2 = adc2_data / 1000 + "." + adc2_data % 1000 / 100 + adc2_data % 100 / 10;
							adc3 = adc3_data / 1000 + "." + adc3_data % 1000 / 100 + adc3_data % 100 / 10;
							T1 = tem_ds18b20 * 625 / 10000 + "." + (tem_ds18b20 * 625 % 10000) / 100;
							Do = adin_data / 1000 + "." + adin_data	% 1000 / 100 + adin_data % 100 / 10;
							T2 = "" + (hum_tem_am2301 & 0x7fff) / 10.0;
							H = ((hum_tem_am2301 & 0xffff0000) >> 16) / 10.0 + "";
							data(mac+1);
							data(mac+2);
							data(mac+3);
							data(mac+4);
							data(mac+5);
							data(mac+6);
							data(mac+7);
							if (data >= 1) {
								shu.setImageResource(R.drawable.adcswitchc);
							} else if(data<1){
								shu.setImageResource(R.drawable.adcswitchd);
							}
						}
					});
				} catch (ModuleException e) {
					hand.sendEmptyMessage(1);
					Log.i("info", "���ݻ�ȡʧ��");
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	// ����Ӵ�������ȡ������
	public void data(String i){
		SharedPreferences share = this.getSharedPreferences(i, Context.MODE_PRIVATE);
		if(i.equals(mac+1)){
			String mingcheng = share.getString(mac+"a", "ADC1");//����
			String danwei = share.getString(mac+"b", "V");//��λ
			String xishu = share.getString(mac+"d", "1");//ϵ��
			String changshu = share.getString(mac+"e", "0");//����
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc1);
			adc1 = xs*da+cs+"";
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tva.setTextColor(Color.BLACK);
			} else {
				tva.setTextColor(Color.RED);
			}
			tvaa.setText(mingcheng.trim()+" :");
			tva.setText(adc1+danwei);
		} else if(i.equals(mac+2)){
			String mingcheng = share.getString(mac+"a", "ADC2");
			String danwei = share.getString(mac+"b", "V");
			
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc2);
			adc2 = xs*da+cs+"";
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tvb.setTextColor(Color.BLACK);
			} else {
				tvb.setTextColor(Color.RED);
			}
			tvbb.setText(mingcheng.trim()+" :");
			tvb.setText(adc2+danwei);
		} else if(i.equals(mac+3)){
			String mingcheng = share.getString(mac+"a", "ADC3");
			String danwei = share.getString(mac+"b", "V");
			
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc3);
			adc3 = xs*da+cs+"";
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tvc.setTextColor(Color.BLACK);
			} else {
				tvc.setTextColor(Color.RED);
			}
			tvcc.setText(mingcheng.trim()+" :");
			tvc.setText(adc3+danwei);
		} else if(i.equals(mac+4)){
			String mingcheng = share.getString(mac+"a", "�¶�");
			String danwei = share.getString(mac+"b", "'C");
			
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(T1);
			T1 = xs*da+cs+"";
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tvd.setTextColor(Color.BLACK);
			} else {
				tvd.setTextColor(Color.RED);
			}
			tvdd.setText(mingcheng.trim()+" :");
			tvd.setText(T1+danwei);
		} else if(i.equals(mac+5)){
			String mingcheng = share.getString(mac+"a", "DO");
			String danwei = share.getString(mac+"b", "");
			tvee.setText(mingcheng.trim()+" :");
			tve.setText(Do+danwei);
		} else if(i.equals(mac+6)){
			String mingcheng = share.getString(mac+"a", "�¶�");
			String danwei = share.getString(mac+"b", "'C");
			
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(T2);
			T2 = xs*da+cs+"";
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tvf.setTextColor(Color.BLACK);
			} else {
				tvf.setTextColor(Color.RED);
			}
			tvff.setText(mingcheng.trim()+" :");
			tvf.setText(T2+danwei);
		} else if(i.equals(mac+7)){
			String mingcheng = share.getString(mac+"a", "ʪ��");
			String danwei = share.getString(mac+"b", "");
			
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(H);
			H = xs*da+cs+"";
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tvg.setTextColor(Color.BLACK);
			} else {
				tvg.setTextColor(Color.RED);
			}
			tvgg.setText(mingcheng.trim()+" :");
			tvg.setText(H+danwei);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 100){
			getInfo();
		} else if (resultCode == 101){//ADC1
			SharedPreferences share = this.getSharedPreferences(mac+1, Context.MODE_PRIVATE);
			String name = share.getString(mac+"a", "ADC1");//����
			String danwei = share.getString(mac+"b", "V");//��λ
			String xishu = share.getString(mac+"d", "1");//����ϵ��
			String changshu = share.getString(mac+"e", "0");//��������
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc1);
			adc1 = xs*da+cs+"";
			//����������
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tva.setTextColor(Color.BLACK);
			} else {
				tva.setTextColor(Color.RED);
			}
			tva.setText(adc1+danwei);
			tvaa.setText(name.trim()+" :");
		}  else if (resultCode == 102){//ADC2
			SharedPreferences share = this.getSharedPreferences(mac+2, Context.MODE_PRIVATE);
			String name = share.getString(mac+"a", "ADC2");
			String danwei = share.getString(mac+"b", "V");
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc2);
			adc2 = xs*da+cs+"";
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tvb.setTextColor(Color.BLACK);
			} else {
				tvb.setTextColor(Color.RED);
			}
			tvb.setText(adc2+danwei);
			tvbb.setText(name.trim()+" :");
		}  else if (resultCode == 103){//ADC3
			SharedPreferences share = this.getSharedPreferences(mac+3, Context.MODE_PRIVATE);
			String name = share.getString(mac+"a", "ADC3");
			String danwei = share.getString(mac+"b", "V");
			
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(adc3);
			adc3 = xs*da+cs+"";
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tvc.setTextColor(Color.BLACK);
			} else {
				tvc.setTextColor(Color.RED);
			}
			tvc.setText(adc3+danwei);
			tvcc.setText(name.trim()+" :");
		}  else if (resultCode == 104){//�¶�
			SharedPreferences share = this.getSharedPreferences(mac+4, Context.MODE_PRIVATE);
			String name = share.getString(mac+"a", "�¶�");
			String danwei = share.getString(mac+"b", "'C");
			
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(T1);
			T1 = xs*da+cs+"";
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tvd.setTextColor(Color.BLACK);
			} else {
				tvd.setTextColor(Color.RED);
			}
			tvd.setText(T1+danwei);
			tvdd.setText(name.trim()+" :");
		}  else if (resultCode == 105){//DOֵ
			SharedPreferences share = this.getSharedPreferences(mac+5, Context.MODE_PRIVATE);
			String name = share.getString(mac+"a", "DO");
			tvee.setText(name.trim()+" :");
		}  else if (resultCode == 106){//�¶�
			SharedPreferences share = this.getSharedPreferences(mac+6, Context.MODE_PRIVATE);
			String name = share.getString(mac+"a", "�¶�");
			String danwei = share.getString(mac+"b", "'C");
			
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(T2);
			T2 = xs*da+cs+"";
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tvf.setTextColor(Color.BLACK);
			} else {
				tvf.setTextColor(Color.RED);
			}
			tvf.setText(T2+danwei);
			tvff.setText(name.trim()+" :");
		}  else if (resultCode == 107){//ʪ��
			SharedPreferences share = this.getSharedPreferences(mac+7, Context.MODE_PRIVATE);
			String name = share.getString(mac+"a", "ʪ��");
			String danwei = share.getString(mac+"b", "");
			
			String xishu = share.getString(mac+"d", "1");
			String changshu = share.getString(mac+"e", "0");
			Float xs = Float.valueOf(xishu);
			Float cs = Float.valueOf(changshu);
			Float da = Float.valueOf(H);
			H = xs*da+cs+"";
			String lower = share.getString(mac+"f", "0");//��������
			String upper = share.getString(mac+"g", "0");//��������
			Float limit_low = Float.valueOf(lower);
			Float limit_upp = Float.valueOf(upper);
			Float set_data = xs*da+cs;//У׼���ֵ
			if(set_data>limit_low&&set_data<limit_upp||limit_low==0&&limit_upp==0){
				tvg.setTextColor(Color.BLACK);
			} else {
				tvg.setTextColor(Color.RED);
			}
			tvg.setText(H+danwei);
			tvgg.setText(name.trim()+" :");
		} 
	}
	
	ADCinfo info;
	String change_data;
	private void getInfo() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					info = manager.getHelper().getHFADCParameterCalibration(mac);
					int adin_data = info.getDoValue()/100;
					change_data = adin_data / 1000 + "." + adin_data % 1000 / 100
							+ adin_data % 100 / 10;
					hand.sendEmptyMessage(3);
				} catch (ModuleException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
}

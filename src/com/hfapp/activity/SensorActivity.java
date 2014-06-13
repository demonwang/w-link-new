package com.hfapp.activity;

import java.io.Serializable;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palytogether.R;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleException;
import com.hf.module.info.ADCinfo;

/**
 * �豸��У׼
 */
public class SensorActivity extends Activity {
	ImageButton adjust, auto, setting;
	EditText designation, unit, measured_value, compensation_coefficient,
			Compensation_constant, limit_lower, limit_upper;
//	IModuleManager manager;
	String mac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_set);
//		 manager = ManagerFactory.getInstance().getManager();
		mac = getIntent().getStringExtra("mac");
		initActionbar();
		initWidget();
		calibration();
	}
	

	// ��һ������ʼ��ActionBar;
	TextView title;
	private void initActionbar() {
		ActionBar bar = getActionBar();
		bar.setCustomView(R.layout.layout_actionbar);
		bar.setDisplayShowCustomEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
		ImageView backBtn = (ImageView) findViewById(R.id.back);
		ImageView okBtn = (ImageView) findViewById(R.id.ok);
		okBtn.setVisibility(View.INVISIBLE);
		title = (TextView) findViewById(R.id.tv_title);
		// ����ActionBar���⣬�˱��������ϸ�Activity���ݹ����ġ�name��
		title.setText("����������");
		backBtn.setOnClickListener(new OnClickListener() {// �˳����������Ͻǣ�����˳��ý��档ֱ��finish��������
			@Override
			public void onClick(View v) {
				setResult(0);
				finish();
			}
		});
	}

	// �ڶ�������ʼ�����
	private void initWidget() {
		designation = (EditText) findViewById(R.id.designation);// ����
		unit = (EditText) findViewById(R.id.unit);// ��λ
		measured_value = (EditText) findViewById(R.id.measured_value);// ����ֵ
		compensation_coefficient = (EditText) findViewById(R.id.compensation_coefficient);// ����ϵ��
		Compensation_constant = (EditText) findViewById(R.id.Compensation_constant);// ��������
		limit_lower = (EditText) findViewById(R.id.limit_lower);// ��������
		limit_upper = (EditText) findViewById(R.id.limit_upper);// ��������
		adjust = (ImageButton) findViewById(R.id.adjust);// У׼��ť
		auto = (ImageButton) findViewById(R.id.auto);// �Զ���ť
		setting = (ImageButton) findViewById(R.id.setting);// ���ð�ť

		String ming = getIntent().getStringExtra("name");
		ming = ming.substring(0,ming.length()-2);
		designation.setText(ming);// ���ƣ�XXXX
		measured_value.setText(getIntent().getStringExtra("value"));// ���ò���ֵ��������������һ��Activity
		measured_value.setEnabled(false);// ���ò���ֵ���ɱ༭
		String i = getIntent().getStringExtra("tag");
		if (i.equals(mac+1)) {
			title.setText("1#����������");
			get_data(mac+1);
		} else if (i.equals(mac+2)) {
			title.setText("2#����������");
			get_data(mac+2);
		} else if (i.equals(mac+3)) {
			title.setText("3#����������");
			get_data(mac+3);
		} else if (i.equals(mac+4)) {
			title.setText("�¶ȴ���������");
			get_data(mac+4);
		} else if (i.equals(mac+5)) {
			title.setText("DO����");
			get_data(mac+5);
		} else if (i.equals(mac+6)) {
			title.setText("�¶ȴ���������");
			get_data(mac+6);
		} else if (i.equals(mac+7)) {
			title.setText("ʪ�ȴ���������");
			get_data(mac+7);
		}
	}

	// ���ò���ϵ������������
	private void get_data(String i) {
		SharedPreferences share = this.getSharedPreferences(i, Context.MODE_PRIVATE);
		String un = share.getString(mac+"b", "");//��ȡ��λֵ
		String coefficient = share.getString(mac+"d", "1");//��ȡ����ϵ��
		String constant = share.getString(mac+"e", "0");//��ȡ��������
		String lower = share.getString(mac+"f", "0");//��ȡ��������
		String upper = share.getString(mac+"g", "0");//��ȡ��������
		unit.setText(un.trim());
		compensation_coefficient.setText(coefficient);
		Compensation_constant.setText(constant);
		limit_lower.setText(lower);
		limit_upper.setText(upper);
	}

	// �����������ð�ť�����¼�
	private void calibration() {

		adjust.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(SensorActivity.this)
						.setTitle("У׼����")
						.setMessage("��ȷ��ҪУ׼����")
						.setCancelable(false)
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										setResult(100);
										finish();
										Toast.makeText(SensorActivity.this,
												"���ݸ��³ɹ�", 1).show();
									}
								})
						.setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}
								}).show();
			}
		});
		auto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//1,�жϰ�ť�򿪣���ر�״̬.��ɫΪ�򿪣�
				//2,�򿪺��жϴ�������
				if(getAutoStatus()){
					saveAutoStatus(false);
				}else{
					saveAutoStatus(true);
				}			
				finish();
			}
		});
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				save_share();
				finish();
			}
		});
	}

	
	
	public  boolean getAutoStatus(){
		SharedPreferences sp = getSharedPreferences("AUTOSTATUS", MODE_PRIVATE);
		return sp.getBoolean(mac, false);
	}
	
	public  void saveAutoStatus(boolean b){
		SharedPreferences sp = getSharedPreferences("AUTOSTATUS", MODE_PRIVATE);
		Editor e = sp.edit();
		e.putBoolean(mac, b);
		e.commit();
	}
	
	// ����tagֵ���ж�Ҫ�����޸ĵĴ�����
	private void save_share() {
		String i = getIntent().getStringExtra("tag");
		if (i.equals(mac+1)) {
			save(mac+1);
			setResult(101);
		} else if (i.equals(mac+2)) {
			save(mac+2);
			setResult(102);
		} else if (i.equals(mac+3)) {
			save(mac+3);
			setResult(103);
		} else if (i.equals(mac+4)) {
			save(mac+4);
			setResult(104);
		} else if (i.equals(mac+5)) {
			save(mac+5);
			setResult(105);
		} else if (i.equals(mac+6)) {
			save(mac+6);
			setResult(106);
		} else if (i.equals(mac+7)) {
			save(mac+7);
			setResult(107);
		}
	}
	
	// ����ÿ���������Ĳ���  ��������豸��Ϣ��ʾ�����ĳһ��GPIO����Ҫ����Ĳ��������߸�����save_share���ӷ�����
	private void save(String share_name) {
		SharedPreferences preference = this.getSharedPreferences(share_name,Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putString(mac+"a", designation.getText().toString());
		editor.putString(mac+"b", unit.getText().toString());
		editor.putString(mac+"d", compensation_coefficient.getText().toString());
		editor.putString(mac+"e", Compensation_constant.getText().toString());
		editor.putString(mac+"f", limit_lower.getText().toString());
		editor.putString(mac+"g", limit_upper.getText().toString());
		editor.commit();
	}

}

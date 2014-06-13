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
 * 设备的校准
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
	

	// 第一步、初始化ActionBar;
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
		// 设置ActionBar标题，此标题来自上个Activity传递过来的“name”
		title.setText("传感器设置");
		backBtn.setOnClickListener(new OnClickListener() {// 退出键，在左上角，点击退出该界面。直接finish（）即可
			@Override
			public void onClick(View v) {
				setResult(0);
				finish();
			}
		});
	}

	// 第二步、初始化组件
	private void initWidget() {
		designation = (EditText) findViewById(R.id.designation);// 名称
		unit = (EditText) findViewById(R.id.unit);// 单位
		measured_value = (EditText) findViewById(R.id.measured_value);// 测量值
		compensation_coefficient = (EditText) findViewById(R.id.compensation_coefficient);// 补偿系数
		Compensation_constant = (EditText) findViewById(R.id.Compensation_constant);// 补偿常数
		limit_lower = (EditText) findViewById(R.id.limit_lower);// 设置下限
		limit_upper = (EditText) findViewById(R.id.limit_upper);// 设置上限
		adjust = (ImageButton) findViewById(R.id.adjust);// 校准按钮
		auto = (ImageButton) findViewById(R.id.auto);// 自动按钮
		setting = (ImageButton) findViewById(R.id.setting);// 设置按钮

		String ming = getIntent().getStringExtra("name");
		ming = ming.substring(0,ming.length()-2);
		designation.setText(ming);// 名称：XXXX
		measured_value.setText(getIntent().getStringExtra("value"));// 设置测量值，此数据来自上一个Activity
		measured_value.setEnabled(false);// 设置测量值不可编辑
		String i = getIntent().getStringExtra("tag");
		if (i.equals(mac+1)) {
			title.setText("1#传感器设置");
			get_data(mac+1);
		} else if (i.equals(mac+2)) {
			title.setText("2#传感器设置");
			get_data(mac+2);
		} else if (i.equals(mac+3)) {
			title.setText("3#传感器设置");
			get_data(mac+3);
		} else if (i.equals(mac+4)) {
			title.setText("温度传感器设置");
			get_data(mac+4);
		} else if (i.equals(mac+5)) {
			title.setText("DO设置");
			get_data(mac+5);
		} else if (i.equals(mac+6)) {
			title.setText("温度传感器设置");
			get_data(mac+6);
		} else if (i.equals(mac+7)) {
			title.setText("湿度传感器设置");
			get_data(mac+7);
		}
	}

	// 设置补偿系数，补偿常数
	private void get_data(String i) {
		SharedPreferences share = this.getSharedPreferences(i, Context.MODE_PRIVATE);
		String un = share.getString(mac+"b", "");//获取单位值
		String coefficient = share.getString(mac+"d", "1");//获取补偿系数
		String constant = share.getString(mac+"e", "0");//获取补偿常数
		String lower = share.getString(mac+"f", "0");//获取设置下限
		String upper = share.getString(mac+"g", "0");//获取设置上限
		unit.setText(un.trim());
		compensation_coefficient.setText(coefficient);
		Compensation_constant.setText(constant);
		limit_lower.setText(lower);
		limit_upper.setText(upper);
	}

	// 第三步、设置按钮发生事件
	private void calibration() {

		adjust.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(SensorActivity.this)
						.setTitle("校准设置")
						.setMessage("您确定要校准数据")
						.setCancelable(false)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										setResult(100);
										finish();
										Toast.makeText(SensorActivity.this,
												"数据更新成功", 1).show();
									}
								})
						.setNegativeButton("取消",
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
				//1,判断按钮打开，或关闭状态.蓝色为打开，
				//2,打开后，判断传感器的
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
	
	// 根据tag值，判断要保存修改的传感器
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
	
	// 保存每个传感器的参数  例：点击设备信息显示界面的某一个GPIO，需要保存的参数。共七个（是save_share的子方法）
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

package com.hfapp.activity;

import com.example.palytogether.R;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.impl.LocalModuleInfoContainer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends Activity implements OnClickListener{
	public static final String FROM = "from";
	public static final int FROM_MODULE_LIST = 1;
	public static final int FROM_USER_INFO = 2;
	public static final int FROM_USER_NAV = 3;
	public static final int FROM_ABOUT = 4;

	
	private View mModules;
	private View mUserInfo;
	private View mUserNav;
	private View aboutUs;
	private TextView userNickName;
	private TextView logout;
	
	private Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(Setting.this, "network err", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(Setting.this, "logout ok", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				break;
			default:
				Toast.makeText(Setting.this, "logout err", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		mModules = findViewById(R.id.module_list);
		mUserInfo = findViewById(R.id.user_info);
		mUserNav = findViewById(R.id.user_nav);
		aboutUs = findViewById(R.id.about);
		
		userNickName = (TextView) findViewById(R.id.username);
		userNickName.setText(ModuleConfig.cloudUserDisPlayName);
		
		logout = (TextView) findViewById(R.id.logout);
		mModules.setOnClickListener(this);
		mUserInfo.setOnClickListener(this);
		mUserNav.setOnClickListener(this);
		aboutUs.setOnClickListener(this);
		logout.setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		int i = getIntent().getIntExtra(FROM, FROM_MODULE_LIST);
		clearBackground(i);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		switch (v.getId()) {
		case R.id.module_list:
			i.setClass(this, ModuleList.class);
			clearBackground(FROM_MODULE_LIST);
			break;
		case R.id.user_info:
			clearBackground(FROM_USER_INFO);
			i.setClass(this, UserModify.class);
			break;
		case R.id.user_nav:
			clearBackground(FROM_USER_NAV);
			i.setClass(this, UserGuide.class);
			break;
		case R.id.about:
			clearBackground(FROM_ABOUT);
			i.setClass(this, About.class);
			break;
		case R.id.logout:
			dologout();
			LocalModuleInfoContainer.getInstance().removeAll();
//			break;
			return;
		default:
			break;
		}
		startActivity(i);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		finish();
	}
	
	private void dologout() {
		// TODO Auto-generated method stub
		Intent i = new Intent(Setting.this,Login.class);
		startActivity(i);
		finish();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				IModuleManager manager = ManagerFactory.getInstance().getManager();
				try {
					manager.logout();
					manager.stopbeat();
					hand.sendEmptyMessage(1);
				} catch (ModuleException e) {
					// TODO Auto-generated catch block
					hand.sendEmptyMessage(e.getErrorCode());
				}
			}
		}).start();
	}

	private void clearBackground(int viewnum){
		mModules.setBackgroundColor(Color.TRANSPARENT);
		mUserInfo.setBackgroundColor(Color.TRANSPARENT);
		mUserNav.setBackgroundColor(Color.TRANSPARENT);
		aboutUs.setBackgroundColor(Color.TRANSPARENT);
		switch (viewnum) {
		case FROM_MODULE_LIST:
			mModules.setBackgroundResource(R.color.setting_select_color);
			break;
		case FROM_USER_INFO:
			mUserInfo.setBackgroundResource(R.color.setting_select_color);
			break;
		case FROM_USER_NAV:
			mUserNav.setBackgroundResource(R.color.setting_select_color);
			break;
		case FROM_ABOUT:
			aboutUs.setBackgroundResource(R.color.setting_select_color);
			break;

		default:
			break;
		}
	}
	
	//点击两次退出
		long fristPressTime = 0;
		long secondPressTime = 0;
		boolean isDoublepress = false;
		@Override
		public void onBackPressed() {
			if(!isDoublepress){
				fristPressTime = System.currentTimeMillis();
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				isDoublepress = true;
			}else{
				secondPressTime = System.currentTimeMillis();
				if(secondPressTime-fristPressTime<1000){
					//System.exit(0);
					finish();
				}
				isDoublepress = false;
			}
		}
}

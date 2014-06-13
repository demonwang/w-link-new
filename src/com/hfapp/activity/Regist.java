package com.hfapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.config.Userconfig;
import com.example.palytogether.MainActivity;
import com.example.palytogether.R;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.info.CaptchaImageInfo;
import com.hf.module.info.UserInfo;

public class Regist extends Activity{
	private EditText username;//用户名
	private EditText userdisplayName;//昵称
	private EditText userpswd;//密码
	private EditText reuserpswd;//重复密码
	private EditText email;//邮箱
	private EditText capcha;//验证码输入
	private ImageView imageCapcha;//演验证码图片
	private Button ok;//注册按钮
	private boolean isinPutOk = false;
	
	private IModuleManager manager;
	String strusername;
	String struserpswd;
	String strcapcha;
	String strcapchatoken;
	String stremail;
	
	private Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(Regist.this, "网络连接异常", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(Regist.this, "您输入的信息有误，请仔细核对输入信息 ", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(Regist.this, "注册成功", Toast.LENGTH_SHORT).show();
				ModuleConfig.cloudPassword = struserpswd;
				ModuleConfig.cloudUserName = strusername;
				Userconfig.saveUserInfo();
				Intent i = new Intent(Regist.this,MainActivity.class);
				startActivity(i);
				finish();
				break;
			case -114:
				Toast.makeText(Regist.this, "验证码超时，请重新获取", Toast.LENGTH_SHORT).show();
				break;
			case -104:
				Toast.makeText(Regist.this, "该用户名已经注册", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.register_layout);
		manager = ManagerFactory.getInstance().getManager();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		username = (EditText) findViewById(R.id.username);
		username.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
//				if(s.length()<6){
				if(s.length()<1){
					username.setTextColor(Color.RED);
					isinPutOk = false;
					return;
				}else{
					strusername = s.toString();
					username.setTextColor(Color.BLACK);
					isinPutOk = true;
				}
			}
		});
		userdisplayName = (EditText) findViewById(R.id.userdisplayName);
		userpswd = (EditText) findViewById(R.id.userpswd);
		userpswd.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
//				if(s.length()<4){
				if(s.length()<1){
					userpswd.setTextColor(Color.RED);
					isinPutOk = false;
					return;
				}else{
					userpswd.setTextColor(Color.BLACK);
					isinPutOk = true;
				}
			}
		});
		reuserpswd = (EditText) findViewById(R.id.reuserpswd);
		reuserpswd.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
//				if(s.length()<6||!userpswd.getText().toString().equalsIgnoreCase(s.toString())){
				if(s.length()<1||!userpswd.getText().toString().equalsIgnoreCase(s.toString())){
					reuserpswd.setTextColor(Color.RED);
					isinPutOk = false;
					return;
				}else{
					struserpswd = s.toString();
					reuserpswd.setTextColor(Color.BLACK);
					isinPutOk = true;
				}
			}
		});
		email = (EditText) findViewById(R.id.email);
		email.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
//				if(!s.toString().contains("@")||!s.toString().contains(".")){
				if(s.length()<1){
					email.setTextColor(Color.RED);
					isinPutOk = false;
					return;
				}else{
					stremail = s.toString();
					email.setTextColor(Color.BLACK);
					isinPutOk = true;
				}
			}
		});
		capcha = (EditText) findViewById(R.id.capcha);
		capcha.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(s.length()!=4){
					capcha.setTextColor(Color.RED);
					isinPutOk = false;
					return;
				}else{
					strcapcha = s.toString();
					capcha.setTextColor(Color.BLACK);
					isinPutOk = true;
				}
			}
		});
		imageCapcha = (ImageView) findViewById(R.id.imageCacha);
		imageCapcha.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setCpacha();
			}
		});
		ok = (Button) findViewById(R.id.ok);
		setCpacha();
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isinPutOk)
					doRegister();
				else
					hand.sendEmptyMessage(1);
			}
		});
		ok.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.color.gray);
				} else if(event.getAction()==MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.color.orange);
				}
				return false;
			}
		});
	}
	
	private void doRegister(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				UserInfo ui = new UserInfo();
				ui.setAccessKey(ModuleConfig.accessKey);
				ui.setUserName(strusername);
				ui.setDisplayName(userdisplayName.getText().toString().trim());
				ui.setPassword(struserpswd);
				ui.setEmail(stremail);
				String c = strcapchatoken+strcapcha;
				try {
					manager.registerUser(ui, c);
					hand.sendEmptyMessage(2);
				} catch (ModuleException e) {
					// TODO Auto-generated catch block
					hand.sendEmptyMessage(e.getErrorCode());
				}
			}
		}).start();
	}
	
	private void setCpacha() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					final CaptchaImageInfo icap = manager.captchaImage();
					strcapchatoken = icap.getCaptchaToken();
					hand.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							imageCapcha.setImageBitmap(icap.getBitmap());
						}
					});
					
				} catch (ModuleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}
}

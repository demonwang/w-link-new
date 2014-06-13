package com.hfapp.activity;
/**
 * 没有登陆按键 获取最后的完成按键 事件响应。
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.palytogether.R;
import com.hf.module.ModuleConfig;
import com.hfapp.work.InitThread;

public class Login extends Activity implements OnClickListener,OnTouchListener{
	private EditText userName;
	private EditText userPswd;
	private ImageButton forgotPswd;
	private ImageButton regist;
	private Button loginBtn;
	
	private Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
//				Toast.makeText(Login.this, "网络连接错误", Toast.LENGTH_SHORT).show();
				break;
			case 2: //密码错误
				Toast.makeText(Login.this, "您输入的用户名与密码不匹配，请重新输入", Toast.LENGTH_SHORT).show();
				break;
			case 3: //登陆成功
				startModuleListActivity();
				Toast.makeText(Login.this, "登陆成功", Toast.LENGTH_SHORT).show();
				break;
			case 4: //登陆成功
//				startModuleListActivity();
				Toast.makeText(Login.this, "初始化成功", Toast.LENGTH_SHORT).show();
				break;
			case 5: //用户名输入错误
				Toast.makeText(Login.this, "您输入的用户名有误", Toast.LENGTH_SHORT).show();
				break;
			case -103:
				Toast.makeText(Login.this, "用户名不存在", Toast.LENGTH_SHORT).show();
				break;
			case -101:
				Toast.makeText(Login.this, "密码错误", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.login_layout);
		initView();
	}
	
	@SuppressLint("ResourceAsColor")
	private void initView(){
		userName = (EditText) findViewById(R.id.user_name);
		userPswd = (EditText) findViewById(R.id.user_pswd);
		forgotPswd = (ImageButton) findViewById(R.id.forget_pswd);
		regist = (ImageButton) findViewById(R.id.regist);
		loginBtn = (Button) findViewById(R.id.login_btn);
		forgotPswd.setOnClickListener(this);
		regist.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		loginBtn.setOnTouchListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.forget_pswd){
			doForgotPswd();
		}else if(v.getId() == R.id.regist){
			doRegist();
		}else if(v.getId() == R.id.login_btn){
			doLogin();
		}
	}
	
	// 登录
	private void doLogin(){
		ModuleConfig.cloudUserName = userName.getText().toString().trim();
		ModuleConfig.cloudPassword = userPswd.getText().toString().trim();
		if(checkInPut()){//如果输入的用户名和密码匹配登录
			new Thread(new InitThread(hand)).start();
		}
//		finish();
	}
	
	// 验证输入信息：用户名、密码
	private boolean checkInPut(){
			if(ModuleConfig.cloudUserName == null||ModuleConfig.cloudUserName.length()<4){
				hand.sendEmptyMessage(5);
				return false;
			}else if(ModuleConfig.cloudPassword == null||ModuleConfig.cloudPassword.length()<4){
				hand.sendEmptyMessage(2);
				return false;
			}
		return true;
	}
	
	// 注册按钮，跳转至注册界面
	private void doRegist(){
		Intent i = new Intent(this, Regist.class);
		startActivity(i);
		finish();
	}
	
	// 忘记密码事件
	private void doForgotPswd(){
		Toast.makeText(this, "很抱歉，服务器正在调试中，请稍候再试", 1).show();
	}
	
	private void startModuleListActivity(){
		Intent i = new Intent(this,ModuleList.class);
		startActivity(i);
		finish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			v.setBackgroundResource(R.color.gray);
		} else if(event.getAction()==MotionEvent.ACTION_UP){
			v.setBackgroundResource(R.color.orange);
		}
		return false;
	}
}

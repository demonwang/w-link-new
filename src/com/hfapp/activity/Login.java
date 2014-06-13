package com.hfapp.activity;
/**
 * û�е�½���� ��ȡ������ɰ��� �¼���Ӧ��
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
//				Toast.makeText(Login.this, "�������Ӵ���", Toast.LENGTH_SHORT).show();
				break;
			case 2: //�������
				Toast.makeText(Login.this, "��������û��������벻ƥ�䣬����������", Toast.LENGTH_SHORT).show();
				break;
			case 3: //��½�ɹ�
				startModuleListActivity();
				Toast.makeText(Login.this, "��½�ɹ�", Toast.LENGTH_SHORT).show();
				break;
			case 4: //��½�ɹ�
//				startModuleListActivity();
				Toast.makeText(Login.this, "��ʼ���ɹ�", Toast.LENGTH_SHORT).show();
				break;
			case 5: //�û����������
				Toast.makeText(Login.this, "��������û�������", Toast.LENGTH_SHORT).show();
				break;
			case -103:
				Toast.makeText(Login.this, "�û���������", Toast.LENGTH_SHORT).show();
				break;
			case -101:
				Toast.makeText(Login.this, "�������", Toast.LENGTH_SHORT).show();
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
	
	// ��¼
	private void doLogin(){
		ModuleConfig.cloudUserName = userName.getText().toString().trim();
		ModuleConfig.cloudPassword = userPswd.getText().toString().trim();
		if(checkInPut()){//���������û���������ƥ���¼
			new Thread(new InitThread(hand)).start();
		}
//		finish();
	}
	
	// ��֤������Ϣ���û���������
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
	
	// ע�ᰴť����ת��ע�����
	private void doRegist(){
		Intent i = new Intent(this, Regist.class);
		startActivity(i);
		finish();
	}
	
	// ���������¼�
	private void doForgotPswd(){
		Toast.makeText(this, "�ܱ�Ǹ�����������ڵ����У����Ժ�����", 1).show();
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

package com.hfapp.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.palytogether.R;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.info.UserInfo;

import android.R.bool;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserModify extends Activity {
	private EditText userNikeName;
	private EditText userPhone;
	private EditText userEmail;
	private TextView softWareVer;
	private ImageButton sub;
	private UserInfo info = new UserInfo();//用户信息
	private Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(UserModify.this, "网络链接错误", Toast.LENGTH_SHORT)
						.show();
				break;
			case 2:
				Toast.makeText(UserModify.this, "用户名输入错误", Toast.LENGTH_SHORT)
						.show();
				break;
			case 3:
				Toast.makeText(UserModify.this, "电话有误", Toast.LENGTH_SHORT)
						.show();
				break;
			case 4:
				Toast.makeText(UserModify.this, "邮箱有误", Toast.LENGTH_SHORT)
						.show();
				break;
			case 5:
				Toast.makeText(UserModify.this, "修改成功", Toast.LENGTH_SHORT)
						.show();
				Intent i = new Intent(UserModify.this, Setting.class);
				i.putExtra(Setting.FROM, Setting.FROM_USER_INFO);
				startActivity(i);
				finish();
				break;
			case -105:
				Toast.makeText(UserModify.this, "邮箱已存在", Toast.LENGTH_SHORT)
						.show();
				break;
			case -106:
				Toast.makeText(UserModify.this, "电话已存在", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				System.out.println("errr -->" + msg.what);
				break;
			}
		};
	};

	boolean ischanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usermodify_layout);
		initActionbar();
		initView();
	}

	private void initActionbar() {
		// TODO Auto-generated method stub
		ActionBar bar = getActionBar();
		bar.setCustomView(R.layout.layout_actionbar);
		bar.setDisplayShowCustomEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
		ImageView backBtn = (ImageView) findViewById(R.id.back);
		ImageView okBtn = (ImageView) findViewById(R.id.ok);
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText(R.string.setting_set);
		okBtn.setVisibility(View.INVISIBLE);
		// backBtn.setImageResource(R.drawable.menu_icon);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserModify.this, Setting.class);
				i.putExtra(Setting.FROM, Setting.FROM_USER_INFO);
				startActivity(i);
				finish();
			}
		});
	}

	private void initView() {
		sub = (ImageButton) findViewById(R.id.sub_userinfo);
		userNikeName = (EditText) findViewById(R.id.display_name);

		userPhone = (EditText) findViewById(R.id.user_phone);
		softWareVer = (TextView) findViewById(R.id.soft_ver);
		userEmail = (EditText) findViewById(R.id.user_email);
		userNikeName.setText(ModuleConfig.cloudUserDisPlayName);
		userPhone.setText(ModuleConfig.cloudUserPhone);
		userEmail.setText(ModuleConfig.cloudUserEmail);
		userNikeName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
				ischanged = true;
				sub.setImageResource(R.drawable.usermodify_ok_green);
			}
		});
		userPhone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
				ischanged = true;
				sub.setImageResource(R.drawable.usermodify_ok_green);
			}
		});
		userEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
				ischanged = true;
				sub.setImageResource(R.drawable.usermodify_ok_green);
			}
		});
		

		
		sub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Runnable updateUserInfo = new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (checkUserInPut() && ischanged) {
							try {
								ManagerFactory.getInstance().getManager()
										.setUser(info);
								hand.sendEmptyMessage(5);
							} catch (ModuleException e) {
								// TODO Auto-generated catch block
								hand.sendEmptyMessage(e.getErrorCode());
							}
						}
					}
				};

				new Thread(updateUserInfo).start();
			}
		});
	}

	private boolean checkUserInPut() {
		String name = userNikeName.getText().toString().trim();
		String phone = userPhone.getText().toString().trim();
		String email = userEmail.getText().toString().trim();

		if (name == null || name.length() <= 0) {
			hand.sendEmptyMessage(2);
			return false;
		}
		if (phone == null || phone.length() <= 0 || !isMobileNO(phone)) {
			hand.sendEmptyMessage(3);
			return false;
		}
		if (email == null || email.length() <= 0 || !isEmail(email)) {
			hand.sendEmptyMessage(4);
			return false;
		}
		if (!name.equalsIgnoreCase(ModuleConfig.cloudUserDisPlayName)) {
			ModuleConfig.cloudUserDisPlayName = name;
			info.setDisplayName(name);
		}
		if (!phone.equalsIgnoreCase(ModuleConfig.cloudUserPhone)) {
			ModuleConfig.cloudUserPhone = phone;
			info.setCellPhone(phone);
		}
		if (!email.equalsIgnoreCase(ModuleConfig.cloudUserEmail)) {
			ModuleConfig.cloudUserEmail = email;
			info.setEmail(email);
		}
		return true;
	}

	private boolean isMobileNO(String mobiles) {

		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

		Matcher m = p.matcher(mobiles);

		return m.matches();

	}

	// ^(\w+((-\w+)|(\.\w+))*)\+\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$

	private boolean isEmail(String mobiles) {

		if (mobiles.contains(".") && mobiles.contains("@")) {
			return true;
		} else {
			return false;
		}
	}
}

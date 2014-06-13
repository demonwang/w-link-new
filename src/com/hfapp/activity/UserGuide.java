package com.hfapp.activity;

import com.example.palytogether.NavViews;
import com.example.palytogether.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class UserGuide extends Activity{
	
	private ImageButton userguide;
	private ImageButton faq;
	private ImageButton qr_code;
	private ImageButton feedback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_guide_layout);
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
		title.setText(R.string.setting_nav);
		okBtn.setVisibility(View.INVISIBLE);
//		backBtn.setImageResource(R.drawable.menu_icon);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserGuide.this, Setting.class);
				i.putExtra(Setting.FROM, Setting.FROM_USER_NAV);
				startActivity(i);
				finish();
			}
		});
	}
	
	
	private void initView(){
		userguide = (ImageButton) findViewById(R.id.userguid);
		userguide.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserGuide.this,NavViews.class);
				startActivity(i);
				finish();
			}
		});
	}
}

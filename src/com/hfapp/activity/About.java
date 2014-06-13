package com.hfapp.activity;

import com.example.palytogether.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class About extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initActionbar();
		setContentView(R.layout.about_layout);
		initView();
	}


	@SuppressLint("NewApi")
	private void initActionbar() {
		// TODO Auto-generated method stub
		ActionBar bar = getActionBar();
		bar.setCustomView(R.layout.layout_actionbar);
		bar.setDisplayShowCustomEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
		ImageView backBtn = (ImageView) findViewById(R.id.back);
		ImageView okBtn = (ImageView) findViewById(R.id.ok);
		okBtn.setVisibility(View.INVISIBLE);
//		backBtn.setImageResource(R.drawable.menu_icon);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(About.this, Setting.class);
				i.putExtra(Setting.FROM, Setting.FROM_ABOUT);
				startActivity(i);
				finish();
			}
		});
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		
	}
}

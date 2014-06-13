package com.hfapp.view;

import com.example.palytogether.R;
import com.hf.module.info.ModuleInfo;
import com.hfapp.activity.About;
import com.hfapp.activity.ImageContentor;
import com.hfapp.activity.LightModuleInfo;
import com.hfapp.activity.Login;
import com.hfapp.activity.Regist;
import com.hfapp.activity.Setting;
import com.hfapp.activity.SmartCtrler;
import com.hfapp.activity.TimerAdder;
import com.hfapp.activity.TimerCenter;
import com.hfapp.activity.UserGuide;
import com.hfapp.activity.UserModify;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ViewPager4 extends LinearLayout{

	public ViewPager4(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ViewPager4(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ViewPager4(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.layout_view4, this, true);
	}
}

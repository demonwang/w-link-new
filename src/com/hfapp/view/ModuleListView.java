package com.hfapp.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.palytogether.MainActivity;
import com.example.palytogether.R;
import com.hf.module.impl.KeyValueHelper;
import com.hf.module.info.ModuleInfo;
import com.hf.module.info.ModuleKeyValue;
import com.hfapp.activity.ImageContentor;
import com.hfapp.activity.LightModuleInfo;

public class ModuleListView extends FrameLayout {
	
	private LinearLayout firstColumn;
	private LinearLayout secondColumn;
	private boolean addToWhichSide = false;
	private ImageView m_treeline;
	private ImageView logo;
	private ImageView tree;
	private TextView tv_nav;
	private ScrollView m_scrollview;
	private int moduleCount = 0;
	private ArrayList<BaseModuleView> viewList = new ArrayList<BaseModuleView>();
	//public static  HashMap<String , StatusInfo> stats = new HashMap<String, StatusInfo>();
	
	public ModuleListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.module_list_view, this);
		initView();
	}

	public ModuleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.module_list_view, this);
		initView();
	}

	public ModuleListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.module_list_view, this);
		initView();
	}
	
	private void initView(){
		firstColumn = (LinearLayout) findViewById(R.id.first_column);
		secondColumn = (LinearLayout) findViewById(R.id.second_column);
		m_treeline = (ImageView) findViewById(R.id.tree_line);
		logo = (ImageView) findViewById(R.id.hf_logo);
		tv_nav = (TextView) findViewById(R.id.tv_nav);
		tree = (ImageView) findViewById(R.id.little_tree);
		m_scrollview = (ScrollView) findViewById(R.id.m_scvw);
	}
	
	
	public void addModule(ModuleInfo mi){
		
	}
	
	public void deleteModule(String mac){
		
	}
	
	public void setModuleList(ArrayList<ModuleInfo> list){
		firstColumn.removeAllViews();
		secondColumn.removeAllViews();
		addToWhichSide = false;
		removeAllModules();
		if(list.size()<=0){
			m_scrollview.setVisibility(View.INVISIBLE);
			logo.setVisibility(View.VISIBLE);
			tv_nav.setVisibility(View.VISIBLE);
			tree.setVisibility(View.VISIBLE);
		}else{
			m_scrollview.setVisibility(View.VISIBLE);
			logo.setVisibility(View.INVISIBLE);
			tv_nav.setVisibility(View.INVISIBLE);
			tree.setVisibility(View.INVISIBLE);
		}
		for (int i = 0; i < list.size(); i++) {
			BaseModuleView view = new GPIOModuleView(getContext(),addToWhichSide);
			view.moduleImage.setBackgroundResource(ImageContentor.getOutLineImageRs(KeyValueHelper.getInstence().get(list.get(i).getMac()).getIndex()));
			view.registerEvent();
			view.setM_moduleinfo(list.get(i));
			adddev(view);
		}
	}
	
	public synchronized void getAllModuleSatus(){
		for (int i = 0; i < viewList.size(); i++) {
			viewList.get(i).updateStatus();
		}
	}
	
	
	public synchronized void removeAllModules(){
		for (int i = 0; i < viewList.size(); i++) {
			viewList.get(i).unregisterEvent();
		}
		
		viewList.clear();
	}
	
	
	public synchronized void adddev(BaseModuleView newDev) {
		viewList.add(newDev);
		m_treeline.setVisibility(View.VISIBLE);
		if (newDev == null)
			return;
		if (addToWhichSide) {
			firstColumn.addView(newDev);
			addToWhichSide = false;
		} else {
			secondColumn.addView(newDev);
			addToWhichSide = true;
		}
		
		if(moduleCount>1){
			android.view.ViewGroup.LayoutParams lp = m_treeline.getLayoutParams();
			lp.height= LayoutParams.FILL_PARENT;
			m_treeline.setLayoutParams(lp);
		}else {
			android.view.ViewGroup.LayoutParams lp = m_treeline.getLayoutParams();
			lp.height= LayoutParams.FILL_PARENT;
			m_treeline.setLayoutParams(lp);
		}
		m_scrollview.fullScroll(ScrollView.FOCUS_DOWN);
	}
}

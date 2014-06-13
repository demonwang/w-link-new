package com.hfapp.view;

import com.example.palytogether.R;
import com.hf.module.IEventListener;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleException;
import com.hf.module.impl.KeyValueHelper;
import com.hf.module.info.ModuleInfo;
import com.hfapp.activity.ImageContentor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BaseModuleView extends FrameLayout implements IEventListener{

	public ModuleInfo m_moduleinfo;
	
	public ImageView moduleImage;
	public TextView moduleName;
	public ImageView timerFlag;
	
	public IModuleManager manager;
	
	public BaseModuleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public BaseModuleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public BaseModuleView(Context context,boolean which) {
		
		super(context);
		// TODO Auto-generated constructor stub
		if(which)
			LayoutInflater.from(context).inflate(R.layout.module_fragment, this);
		else
			LayoutInflater.from(context).inflate(R.layout.module_fragment_left, this);
		initView();
	}

	protected  void initView(){
		manager = ManagerFactory.getInstance().getManager();
		moduleImage = (ImageView) findViewById(R.id.iv);
		moduleName = (TextView) findViewById(R.id.tv);
		timerFlag = (ImageView) findViewById(R.id.timer_flag);
	}
	public ModuleInfo getM_moduleinfo() {
		return m_moduleinfo;
	}

	public void setM_moduleinfo(ModuleInfo mi) {
		this.m_moduleinfo = mi;
		moduleName.setText(mi.getName());
	}
	
	protected abstract  void updateStatus();
	
	public void setName(){
		
	}
	
	public void deleteModule(){
		
	}
	
	public void changePostion(){
		
	}
	public void registerEvent(){
		manager.registerEventListener(this);
	}
	
	public void unregisterEvent(){
		manager.unregisterEventListener(this);
	}

}

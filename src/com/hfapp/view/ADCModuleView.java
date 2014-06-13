package com.hfapp.view;

import java.util.HashMap;

import com.hf.module.info.GPIO;
import com.hf.module.info.ModuleInfo;

import android.content.Context;
import android.util.AttributeSet;

public class ADCModuleView extends BaseModuleView {

	public ADCModuleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void updateStatus() {
		

	}

	@Override
	public void onEvent(String mac, byte[] t2data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCloudLogin(boolean loginstat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCloudLogout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewDevFind(ModuleInfo mi) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGPIOEvent(String mac, HashMap<Integer, GPIO> GM) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTimerEvent(String mac, byte[] t2data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUARTEvent(String mac, byte[] userData, boolean chanle) {
		// TODO Auto-generated method stub
		
	}

}

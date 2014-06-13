package com.hfapp.work;

import java.util.ArrayList;

import com.example.config.Userconfig;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.info.ModuleInfo;

import android.os.Handler;
import android.util.Log;

public class InitThread implements Runnable{
	private Handler hand;
	private IModuleManager manager;
	private boolean isLogin = false;
	public InitThread(Handler hand) {
		// TODO Auto-generated constructor stub
		this.hand = hand;
		manager = ManagerFactory.getInstance().getManager();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		manager.initHttp();
		try {
			manager.login();
			isLogin = true;
			Userconfig.saveUserInfo();
		} catch (ModuleException e) {
			// TODO Auto-generated catch block
			if(e.getErrorCode() != 0)
				hand.sendEmptyMessage(e.getErrorCode());
		}
		
		try {
			manager.initialize();
		} catch (ModuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ArrayList<ModuleInfo> ms = manager.getAllModules();
		} catch (ModuleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(isLogin){
			
			try {
				manager.getUser();
			} catch (ModuleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				manager.getKeyValueInfo(Userconfig.KEYVALUE);

			} catch (ModuleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hand.sendEmptyMessage(3);
		}else{
			hand.sendEmptyMessage(0);
		}
	}
}

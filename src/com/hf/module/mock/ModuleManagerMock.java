package com.hf.module.mock;

import java.util.ArrayList;

import android.content.Context;

import com.hf.module.IModuleManager;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.impl.ModuleManagerImpl;
import com.hf.module.info.CaptchaInfo;
import com.hf.module.info.ModuleInfo;

public class ModuleManagerMock extends ModuleManagerImpl implements
		IModuleManager {

	public ModuleManagerMock() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void login() throws ModuleException {
		super.login();
	}

	@Override
	public void logout() throws ModuleException {
		// TODO Auto-generated method stub
		super.logout();
	}

	@Override
	public CaptchaInfo captcha(String receiverAddress, int receiverType)
			throws ModuleException {
		// TODO Auto-generated method stub
		return super.captcha(receiverAddress, receiverType);
	}

	@Override
	public ModuleInfo getModuel(String moduleId) throws ModuleException {
		return super.getModuel(moduleId);
	}

	@Override
	public ModuleInfo setModule(ModuleInfo moduleInfo) throws ModuleException {
		// TODO Auto-generated method stub
		return super.setModule(moduleInfo);
	}

	@Override
	public ArrayList<ModuleInfo> getAllModules() throws ModuleException {
		// TODO Auto-generated method stub
		return super.getAllModules();
	}

}

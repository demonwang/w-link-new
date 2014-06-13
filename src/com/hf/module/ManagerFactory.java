package com.hf.module;

import com.hf.module.impl.ModuleManagerImpl;

public class ManagerFactory {
	
	private static ManagerFactory instance = null;
	
	private ModuleManagerImpl impl = null;
	
	
	private ManagerFactory() {
		super();
		impl = new ModuleManagerImpl();
	}

	public static ManagerFactory getInstance(){
		if(instance == null){
			instance= new ManagerFactory();
		}
		return instance;
	}

	public IModuleManager getManager(){
		return impl;
	}

}

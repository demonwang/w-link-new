package com.hf.module.impl.adaptor;

import com.hf.module.ModuleException;
import com.hf.module.impl.IPlatformAdaptor;

public class StandardJavaAdaptor implements IPlatformAdaptor{

	@Override
	public String getPhoneMac() throws ModuleException {
		// TODO Auto-generated method stub
		return "AABBCCDDEEFF";
	}

	@Override
	public String getBroadCast() throws ModuleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWifiSSID() throws ModuleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPhoneNum() throws ModuleException {
		// TODO Auto-generated method stub
		return null;
	}

}

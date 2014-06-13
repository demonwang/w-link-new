package com.hf.module.impl;

import com.hf.module.ModuleException;

public interface IPlatformAdaptor {
	
	public String getPhoneMac() throws ModuleException;
	
	public String getBroadCast()throws ModuleException;
	
	public String getWifiSSID()throws ModuleException;
	
	public String getPhoneNum()throws ModuleException;
	
}

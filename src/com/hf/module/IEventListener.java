package com.hf.module;

import java.util.ArrayList;
import java.util.HashMap;

import com.hf.module.info.GPIO;
import com.hf.module.info.ModuleInfo;

/**
 * @author Sean
 *
 */
public interface IEventListener{
	/**
	 * @param mac
	 * @param t2data
	 */
	public void onEvent(String mac, byte[] t2data);
	
	/**
	 * 
	 */
	public void onCloudLogin(boolean loginstat);
	/**
	 * 
	 */
	public void onCloudLogout();
	
	/*
	 * 
	 * */
	public void onNewDevFind(ModuleInfo mi);
	/**
	 * 
	 */
	public void onGPIOEvent(String mac , HashMap<Integer, GPIO> GM);
	/**
	 * 
	 */
	public void onTimerEvent(String mac,byte[] t2data);
	/*
	 * 
	 * */
	//public void onModuleConnenctOK(String mac);
	
	public void onUARTEvent(String mac,byte[] userData,boolean chanle);
}

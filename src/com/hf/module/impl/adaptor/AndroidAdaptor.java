package com.hf.module.impl.adaptor;

import java.net.InetAddress;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.impl.IPlatformAdaptor;

public class AndroidAdaptor  implements IPlatformAdaptor{

	private Context ctx = ModuleConfig.appcontext;

	String broadCast;
	String wifiSSID;
	String mPhoneMac;
	String mPhoneNum;
	WifiManager myWifiManager;
	public AndroidAdaptor(){
		if(ctx != null)
		 myWifiManager = (WifiManager) ctx.getSystemService(ctx.WIFI_SERVICE); 
	}

	
	public String getPhoneNum() throws ModuleException{		
		TelephonyManager phoneMgr=(TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
		if(phoneMgr != null){
			String p = phoneMgr.getLine1Number();
			if(p != null)
			{
				return p;
			}else{
				return "";
			}
		}else{
			ModuleException mx = new ModuleException("getPhoneNum Err");
			mx.setErrorCode(-42);
			throw mx;
		}
	}
	
	public InetAddress getBroadcastAddress() throws ModuleException { 
		DhcpInfo myDhcpInfo = myWifiManager.getDhcpInfo(); 
		if (myDhcpInfo == null) { 
			ModuleException ex = new ModuleException("Could not get broadcast address");
			ex.setErrorCode(-41);
			//System.out.println("Could not get broadcast address"); 
			throw ex;
		} 
		int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask) 
		| ~myDhcpInfo.netmask; 
		byte[] quads = new byte[4]; 
		for (int k = 0; k < 4; k++) 
		quads[k] = (byte) ((broadcast >> k * 8) & 0xFF); 
		try{
			return InetAddress.getByAddress(quads); 
		}catch(Exception e){
			ModuleException mx = new ModuleException("getBroadCast Err");
			mx.setErrorCode(-40);
			throw mx;
		}
	}


	@Override
	public String getPhoneMac() throws ModuleException {
		// TODO Auto-generated method stub
		WifiInfo info = (null == myWifiManager ? null : myWifiManager.getConnectionInfo());
		if (null != info) {
			String mac = info.getMacAddress();
			mac = mac.replaceAll(":", "");
			mac = mac.toUpperCase();
			return mac;
		}
		else
		{
			ModuleException mx = new ModuleException("getPhoneMac Err");
			mx.setErrorCode(-41);
			throw mx;
		}
	}


	@Override
	public String getBroadCast() throws ModuleException {
		// TODO Auto-generated method stub
		return getBroadcastAddress().getHostAddress();
	}


	@Override
	public String getWifiSSID() throws ModuleException {
		// TODO Auto-generated method stub
		WifiInfo info = (null == myWifiManager ? null : myWifiManager.getConnectionInfo());
		if (null != info) {
			if(info.getSSID().length()>2&&info.getSSID().charAt(0)=='"'&&info.getSSID().charAt(info.getSSID().length()-1)=='"')
				return info.getSSID().substring(1, info.getSSID().length()-1);
			else
				return info.getSSID();
		}
		else
		{
			ModuleException mx = new ModuleException("getSSID Err");
			mx.setErrorCode(-41);
			throw mx;
		}
	}
	
}

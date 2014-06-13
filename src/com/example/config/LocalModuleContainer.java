package com.example.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hf.module.ModuleConfig;
import com.hf.module.info.ModuleInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocalModuleContainer extends ArrayList<ModuleInfo>{
	private Editor editer;
	private SharedPreferences sp;
	private final String  LOCALCONT = "LOCALCONTAIN";
	private  LocalModuleContainer(){
		sp = ModuleConfig.appcontext.getSharedPreferences(LOCALCONT, Context.MODE_PRIVATE);
	}
	public  void putAll(ArrayList<ModuleInfo> mis){
		editer = sp.edit();
		Iterator<ModuleInfo> it = mis.iterator();
		while(it.hasNext()){
			ModuleInfo mi = it.next();
			JsonObject pl = new JsonObject();
			moduleInfo2Json(mi, pl);
			editer.putString(mi.getMac(), pl.toString());
		}
		editer.commit();
	}
	
	public  ArrayList<ModuleInfo> getAll(){
		ArrayList<ModuleInfo> mis = new ArrayList<ModuleInfo>();
		HashMap<String , String> map = (HashMap<String, String>) sp.getAll();
		Iterator<String> it =  map.values().iterator();
		while(it.hasNext()){
			String s  =  it.next();
			JsonObject jo = new JsonParser().parse(s).getAsJsonObject();
			ModuleInfo mi = new ModuleInfo();
			json2ModuleInfo(jo, mi);
			mis.add(mi);
		}
		return mis;
	}
	public  void put(ModuleInfo mi){
		editer = sp.edit();
		JsonObject pl = new JsonObject();
		moduleInfo2Json(mi, pl);
		editer.putString(mi.getMac(), pl.toString());
		editer.commit();
	}
	public  void remove(String mac){
		editer = sp.edit();
		editer.remove(mac);
		editer.commit();
	}
	
	public void clear(){
		editer = sp.edit();
		editer.clear();
		editer.commit();
	}
	
	
	public static void json2ModuleInfo(JsonObject pl, ModuleInfo info) {
		if(pl.get("moduleId")!=null) info.setId(pl.get("moduleId").getAsString());
	    if(pl.get("name")!=null) info.setName(pl.get("name").getAsString());
	    if(pl.get("desc")!=null) info.setDesc(pl.get("desc").getAsString());
	    if(pl.get("mac")!=null) info.setMac(pl.get("mac").getAsString());
	    if(pl.get("localKey")!=null) info.setLocalKey(pl.get("localKey").getAsString());
	    if(pl.get("needRemoteControl")!=null) info.setNeedRemoteControl(pl.get("needRemoteControl").getAsBoolean());
	    if(pl.get("serialNo")!=null) info.setSerialNo(pl.get("serialNo").getAsString());
	    if(pl.get("factoryId")!=null) info.setFactoryId(pl.get("factoryId").getAsInt());
	    if(pl.get("type")!=null) info.setType(pl.get("type").getAsInt());
	    if(pl.get("hardwareVer")!=null) info.setHardwareVer(pl.get("hardwareVer").getAsString());
	    if(pl.get("softwareVer")!=null) info.setSoftwareVer(pl.get("softwareVer").getAsString());
	    if(pl.get("tempKey")!=null) info.setTempKey(pl.get("tempKey").getAsString());
	    if(pl.get("bindTime")!=null) info.setBindTime(pl.get("bindTime").getAsString());
	    if(pl.get("totalOnlineTime")!=null) info.setTotalOnlineTime(pl.get("totalOnlineTime").getAsLong());
	    if(pl.get("internetIp")!=null) info.setInternetIp(pl.get("internetIp").getAsString());
	    if(pl.get("gpsLat")!=null) info.setGpsLat(pl.get("gpsLat").getAsDouble());
	    if(pl.get("gpsLng")!=null) info.setGpsLng(pl.get("gpsLng").getAsDouble());
	    if(pl.get("country")!=null) info.setCountry(pl.get("country").getAsString());
	    if(pl.get("state")!=null) info.setState(pl.get("state").getAsString());
	    if(pl.get("city")!=null) info.setCity(pl.get("city").getAsString());
	    if(pl.get("district")!=null) info.setDistrict(pl.get("district").getAsString());
	    if(pl.get("online")!=null) info.setOnline(pl.get("online").getAsBoolean());
    }
	private void moduleInfo2Json(ModuleInfo info, JsonObject pl) {
		if(info.getId()!=null) pl.addProperty("moduleId", info.getId());
	    if(info.getAccessKey()!=null) pl.addProperty("accessKey", info.getAccessKey());
	    if(info.getName()!=null) pl.addProperty("name", info.getName());
	    if(info.getDesc()!=null) pl.addProperty("desc", info.getDesc());
	    if(info.getMac()!=null) pl.addProperty("mac", info.getMac());
	    if(info.getLocalKey()!=null) pl.addProperty("localKey", info.getLocalKey());
	    if(info.getNeedRemoteControl()!=null) pl.addProperty("needRemoteControl", info.getNeedRemoteControl());
	    if(info.getSerialNo()!=null) pl.addProperty("serialNo", info.getSerialNo());
	    if(info.getFactoryId()!=null) pl.addProperty("factoryId", info.getFactoryId());
	    if(info.getType()!=null) pl.addProperty("type", info.getType());
	    if(info.getHardwareVer()!=null) pl.addProperty("hardwareVer", info.getHardwareVer());
	    if(info.getSoftwareVer()!=null) pl.addProperty("softwareVer", info.getSoftwareVer());
	    if(info.getTempKey()!=null) pl.addProperty("tempKey", info.getTempKey());
	    if(info.getBindTime()!=null) pl.addProperty("bindTime", info.getBindTime());
	    if(info.getTotalOnlineTime()!=null) pl.addProperty("totalOnlineTime", info.getTotalOnlineTime());
	    if(info.getInternetIp()!=null) pl.addProperty("internetIp", info.getInternetIp());
	    if(info.getGpsLat()!=null) pl.addProperty("gpsLat", info.getGpsLat());
	    if(info.getGpsLng()!=null) pl.addProperty("gpsLng", info.getGpsLng());
	    if(info.getCountry()!=null) pl.addProperty("country", info.getCountry());
	    if(info.getState()!=null) pl.addProperty("state", info.getState());
	    if(info.getCity()!=null) pl.addProperty("city", info.getCity());
	    if(info.getDistrict()!=null) pl.addProperty("district", info.getDistrict());
		if(info.getOnline()!=null) pl.addProperty("online", info.getOnline());
    }
	
	public void checkAgingTime(){
		Iterator<ModuleInfo> iter = getAll().iterator();
		while (iter.hasNext()) {
	        ModuleInfo mi = (ModuleInfo) iter.next();
	        if(mi.getLocalIp() != null){
		        long lastTimestamp = mi.getLastTimestamp();
		        long nowTimestamp = new java.util.Date().getTime();
		        
		        long tmpTimestap = ModuleConfig.pulseInterval+ModuleConfig.pulseInterval;
		        
		        if( nowTimestamp > (lastTimestamp + tmpTimestap) ){
		        	mi.setLocalIp(null);
		        }
	        }
        }
	}

}

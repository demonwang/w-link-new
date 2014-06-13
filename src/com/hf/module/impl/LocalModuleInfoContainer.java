package com.hf.module.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.GsonBuilder;
import com.hf.module.ModuleConfig;
import com.hf.module.info.ModuleInfo;



//key = mac, value=ModuleInfo
public class LocalModuleInfoContainer extends HashMap<String, ModuleInfo>{
    private static final long serialVersionUID = 1L;
	private static LocalModuleInfoContainer instance = null;
	
	private Editor editer;
	private SharedPreferences sp;
	private final String  LOCALCONT = "LOCALCONTAIN";
	private LocalModuleInfoContainer() {
		super();
//		pathFile = new File("/mnt/sdcard/HFWorkShop/");
//		storeFile = new File("/mnt/sdcard/HFWorkShop/moduleInfoContainer.json");
//		if(!pathFile.exists()){
//			pathFile.mkdir();
//		}
//		if(!pathFile.exists()){
//			try {
//				pathFile.createNewFile();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		storeFile = new File("/mnt/sdcard/moduleInfoContainer.json");
		sp = ModuleConfig.appcontext.getSharedPreferences(LOCALCONT, Context.MODE_PRIVATE);
	}

	public synchronized static LocalModuleInfoContainer getInstance() {
		if(instance == null){
			instance = new LocalModuleInfoContainer();
		}
		return instance;
	}
	
	@Override
    public ModuleInfo put(String key, ModuleInfo value) {
	
		ModuleInfo info = super.get(key);
		if(info != null){
			value.setFactoryId(info.getFactoryId());
			value.setType(info.getType());
		}
		info = super.put(key, value);
		this.save();
		return info; 
    }

	@Override
    public void putAll(Map<? extends String, ? extends ModuleInfo> m) {
	    super.putAll(m);
	    this.save();
    }

	@Override
    public ModuleInfo remove(Object key) {
		ModuleInfo info = super.remove(key);
		this.save();
	    return info;
    }

	@Override
    public void clear() {
	    super.clear();
	    this.save();
    }

//	public File getStoreFile() {
//		return storeFile;
//	}
//
//	public void setStoreFile(File storeFile) {
//		this.storeFile = storeFile;
//	}

	public void save() {
		String json = new GsonBuilder().create().toJson(this);  	
		editer = sp.edit();
		editer.clear();
		editer.putString("KEY", json);
		editer.commit();
//			FileWriter fw = new FileWriter(this.storeFile.getAbsoluteFile());
//			BufferedWriter bw = new BufferedWriter(fw);
//			bw.write(json);
//			bw.close();

	}
	
	public void load(){
//			StringBuffer json = new StringBuffer();
//			BufferedReader br = new BufferedReader(new FileReader(this.storeFile));
//	
//			String line = "";
//			while ((line = br.readLine()) != null) {
//				json.append(line);
//			}
			String json = sp.getString("KEY", "");
			LocalModuleInfoContainer tmp = (new GsonBuilder().create().fromJson(json, LocalModuleInfoContainer.class));
			instance = tmp;
//			br.close();
	}
	
	public ArrayList<ModuleInfo> getAll(){
		ArrayList<ModuleInfo> mList = new ArrayList<ModuleInfo>();

		Iterator<ModuleInfo> iter = this.values().iterator();
		while (iter.hasNext()) {
	        ModuleInfo moduleInfo = (ModuleInfo) iter.next();
	        mList.add(moduleInfo);
        }
		return mList;
	}
	public void removeAll(){
		this.clear();
		editer = sp.edit();
		editer.putString("KEY", "{}");
		editer.commit();
//		Iterator<ModuleInfo> iter = this.values().iterator();
//		while (iter.hasNext()) {
//	       this.remove(iter.next());
//        }
		this.save();
	}
	
	public ModuleInfo getByModuleId(String moduleId){
		Iterator<ModuleInfo> iter = this.values().iterator();
		while (iter.hasNext()) {
	        ModuleInfo moduleInfo = (ModuleInfo) iter.next();
	        if(moduleInfo.getId().equals(moduleId)){
	        	return moduleInfo;
	        }
        }
		return null;
	}
	
	public void removeByModuleId(String moduleId){
		Iterator<String> iter = this.keySet().iterator();
		while (iter.hasNext()) {
			String mac = iter.next();
			ModuleInfo mi = this.get(mac);
	        if(mi.getId().equals(moduleId)){
	        	this.remove(mac);
	        	return;
	        }
        }
		this.save();
	}
	
	
	public void checkAgingTime(){
		Iterator<ModuleInfo> iter = this.values().iterator();
		while (iter.hasNext()) {
	        ModuleInfo mi = (ModuleInfo) iter.next();
	        if(mi.getLocalIp() != null){
		        long lastTimestamp = mi.getLastTimestamp();
		        long nowTimestamp = new java.util.Date().getTime();
		        
		        long tmpTimestap = ModuleConfig.pulseInterval+ModuleConfig.pulseInterval;
		        
		        if( nowTimestamp > (lastTimestamp + tmpTimestap) ){
		        	mi.setLocalIp(null);
		        	this.save();
		        }
	        }
        }
	}
}

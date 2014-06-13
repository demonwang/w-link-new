package com.hf.module.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.impl.LocalModuleInfoContainer;
import com.hf.module.info.KeyValueInfo;
import com.hf.module.info.ModuleInfo;
import com.hf.module.info.ModuleKeyValue;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class KeyValueHelper extends HashMap<String,ModuleKeyValue>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static SharedPreferences sp ;
	private static KeyValueHelper me = null;
	private IModuleManager manager;
	private KeyValueHelper(){
		sp  = ModuleConfig.appcontext.getSharedPreferences("KEYVALUE", Context.MODE_PRIVATE);
		manager = ManagerFactory.getInstance().getManager();
		load();
	} 
	
	public static KeyValueHelper getInstence(){
		if (me == null){
			me = new KeyValueHelper();
		}
		return me;
	}
	
	public void load(){
		String jsonStr = sp.getString("VAILUE", "{}");
		JsonObject jo = new JsonParser().parse(jsonStr).getAsJsonObject();
		ArrayList<ModuleInfo> ms = LocalModuleInfoContainer.getInstance().getAll();
		Iterator<ModuleInfo> it = ms.iterator();
		int i = 0;
		while(it.hasNext()){
			
			ModuleInfo mi = it.next();
			ModuleKeyValue mkv = new ModuleKeyValue(i,ModuleConfig.defImageNum);
			JsonObject json;
			try{
				json = jo.get(mi.getMac()).getAsJsonObject();
				mkv.setFromJson(json);
			}catch(Exception e){
				e.printStackTrace();
				mkv = new ModuleKeyValue(i,ModuleConfig.defImageNum);
			}
			
			this.put(mi.getMac(), mkv);
			i++;
		}
	}
	
	public KeyValueInfo getKeyValue(){
		
		KeyValueInfo kv = new KeyValueInfo();
		kv.key = "keyvalue";
		kv.value = sp.getString("VAILUE", "{}");
		return kv;
	}
	
	public void removeAll(){
		this.clear();
		this.save();
	}
	
	public void putAll(String json){
		this.clear();
		JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
		ArrayList<ModuleInfo> ms = LocalModuleInfoContainer.getInstance().getAll();
		Iterator<ModuleInfo> it = ms.iterator();
		int i = 0;
		while(it.hasNext()){
			ModuleInfo mi = it.next();
			ModuleKeyValue mkv = new ModuleKeyValue(i,ModuleConfig.defImageNum);
		    JsonObject jsone;
			try{
				jsone = jo.get(mi.getMac()).getAsJsonObject();
				mkv.setFromJson(jsone);
			}catch(Exception e){
				e.printStackTrace();
				mkv = new ModuleKeyValue(i,ModuleConfig.defImageNum);
			}
			this.put(mi.getMac(), mkv);
			i++;
		}
		
		this.save();
	}
	public void save(){
		Editor e = sp.edit();
		JsonObject jo = new JsonObject();
		Iterator<String> it = this.keySet().iterator();
		while(it.hasNext()){
			String mac = it.next();
			jo.add(mac, this.get(mac).toJson());
		}
		e.putString("VAILUE", jo.toString());
		e.commit();
		final KeyValueInfo kv = new KeyValueInfo("keyvalue",jo.toString());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					manager.setKeyValueInfo(kv);
				} catch (ModuleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void delete(String mac){
		this.remove(mac);
		this.save();
	}
	
	public void add(ModuleKeyValue kv){
		this.add(kv);
		this.save();
	}
}

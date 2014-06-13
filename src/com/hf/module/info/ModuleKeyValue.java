package com.hf.module.info;

import com.google.gson.JsonObject;

public class ModuleKeyValue {
	int postion;
	int index;
	
	public ModuleKeyValue(){
		super();
	}
	
	public ModuleKeyValue(int pos,int index){
		this.postion = pos;
		this.index = index;
	}
	public int getPostion() {
		return postion;
	}
	public void setPostion(int postion) {
		this.postion = postion;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public JsonObject toJson(){
		JsonObject jo = new JsonObject();
		jo.addProperty("posIndex", postion);
		jo.addProperty("imgIndex", index);
		return jo;
	}
	
	public void setFromJson(JsonObject json){
		this.postion = json.get("posIndex").getAsInt();
		this.index = json.get("imgIndex").getAsInt();
	}
}

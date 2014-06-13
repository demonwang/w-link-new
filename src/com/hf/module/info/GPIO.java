package com.hf.module.info;

import com.hf.lib.util.HexBin;

public class GPIO extends Pin{
	private   boolean status;
	
	public int getId() {
		return id;
	}
	
	
	public void setId(int id) {
		this.id = id;
	}
	
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}	
	
	public byte[] tobytebuf(){
		String strCMD = ""+this.getId();
		if(this.getStatus()){
			strCMD += this.getId()+"002FFFF";
		}else{
			strCMD += this.getId()+"001FFFF";
		}
		return HexBin.stringToBytes(strCMD);
	}
	
	public void unpack(byte[] cmd){
		setId(cmd[0]|0);
		setStatus(cmd[1]==1);
	}
}

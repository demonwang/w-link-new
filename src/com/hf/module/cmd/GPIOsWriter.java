package com.hf.module.cmd;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.hf.lib.util.HexBin;
import com.hf.module.info.GPIO;

public class GPIOsWriter extends CMDWriter{
	
	
	private ArrayList<GPIO> GPIOs = new ArrayList<GPIO>();
	public GPIOsWriter(){
		
	}
	
	public void setGPIOs(ArrayList<GPIO> GPIOs){
		this.GPIOs = GPIOs;
	}
	
	public ArrayList<GPIO> getGPIOs(){
		return this.GPIOs;
	}
	
	public  byte[] pack() {
		String strCMD = "FE01"+intToByteArray(GPIOs.size()*4+1)+"01";
		for(int i = 0;i<GPIOs.size();i++){
			GPIO tmpg = GPIOs.get(i);
			if(tmpg.getStatus()){
				strCMD += tmpg.getId()+"002FFFF";
			}else{
				strCMD += tmpg.getId()+"001FFFF";
			}
		}
		return HexBin.stringToBytes(strCMD);
	}
	
	public void unpack(byte[] l2Cmd){
		//FD010005036001FFFF
		setCmdType(l2Cmd[0]);
		setFlag2(l2Cmd[1]);
		setFlag3(l2Cmd[2]);
		Arrays.copyOfRange(l2Cmd, 5, 5+4);
	}
}

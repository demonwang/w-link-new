package com.hf.module.info;

import java.nio.ByteBuffer;

public class ADCinfo {
	private int am2301 = 0;
	private short ds18b20 = 0;
	private int DoValue = 0;
	private int AC_FB = 0;
	private int AD1Value = 0;
	private int AD2Value = 0;
	private int AD3Value = 0;
	private int resv = 0;
	public int getAm2301() {
		return am2301;
	}
	
	public short getDs18b20() {
		return ds18b20;
	}
	
	public int getDoValue() {
		return DoValue;
	}
	
	public int getAC_FB() {
		return AC_FB;
	}
	
	public int getAD1Value() {
		return AD1Value;
	}
	
	public int getAD2Value() {
		return AD2Value;
	}
	
	public int getAD3Value() {
		return AD3Value;
	}
	
	public static byte[] getReqCmd(){
		ByteBuffer bf = ByteBuffer.allocate(5);
		bf.put((byte) 0xFF);
		bf.put((byte) 0x0A);
		bf.putShort((short) 0x01);
		bf.put((byte) 127);
		return bf.array();
	}
	
	//参数校准
	public static byte[] getCalibrationCmd(){
		ByteBuffer bf = ByteBuffer.allocate(5);
		bf.put((byte) 0xFF);
		bf.put((byte) 0x0A);
		bf.putShort((short) 0x01);
		bf.put((byte) 0xFF);
		return bf.array();
	}
	
	public void unpack(byte[] cmd){
		ByteBuffer bf = ByteBuffer.allocate(cmd.length);
		bf.put(cmd);
		byte flag = bf.get(4);
		if((flag&1)==1){
			am2301 = bf.getInt(5);
		}
		
		if((flag>>1&1) == 1){
			ds18b20 = bf.getShort(9);
		}
		
		if((flag>>2&1) == 1){
			DoValue = bf.getInt(11);
		}
		if((flag>>3&1) == 1){
			AC_FB = bf.getInt(15);
		}
		if((flag>>4&1) == 1){
			AD1Value = bf.getInt(19);
		}
		if((flag>>5&1) == 1){
			AD2Value = bf.getInt(23);
		}
		if((flag>>6&1) == 1){
			AD3Value = bf.getInt(27);
		}
		if((flag>>7&1) == 1){
			resv = bf.getInt(31);
		}
	}
}

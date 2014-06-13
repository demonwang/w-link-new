package com.hf.module.cmd;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.hf.lib.util.HexBin;

public class BaseL2CMD {
	byte cmdType;
    int cmdLengh;
	byte flag2;
	byte flag3;
	
	public byte getCmdType() {
		return cmdType;
	}
	public void setCmdType(byte cmdType) {
		this.cmdType = cmdType;
	}
	public int getCmdLengh() {
		return cmdLengh;
	}
	public void setCmdLengh(int cmdLengh) {
		this.cmdLengh = cmdLengh;
	}
	public byte getFlag2() {
		return flag2;
	}
	public void setFlag2(byte flag2) {
		this.flag2 = flag2;
	}
	
	public byte getFlag3() {
		return flag3;
	}
	public void setFlag3(byte flag3) {
		this.flag3 = flag3;
	}
	
	public static byte[] intToByteArray(int i){
		ByteBuffer bf= ByteBuffer.allocate(4);
		bf.putInt(i);
		byte[] b = bf.array();
		return Arrays.copyOfRange(b, 2, b.length);
	}
}

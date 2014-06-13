package com.hf.Unity;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.hf.lib.protocol.t1.t2.T2;
import com.hf.lib.util.HexBin;

public class GeneticT2 extends T2 {

	private int cmd;
	private byte[] params;

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public byte[] getParams() {
		return params;
	}

	public void setParams(byte[] params) {
		this.params = params;
	}

	@Override
	public GeneticT2 unpack(byte[] l2Data) {
		if ((l2Data[0] & 0xFF) != 0xFD && (l2Data[0] & 0xFF) != 0xFE
				&& (l2Data[0] & 0xFF) != 0xFF) {
			return null;
		}
		T2 t2 = new T2();
		t2.unpack(l2Data);
		this.setTag1(t2.getTag1());
		this.setTag2(t2.getTag2());
		this.setLength(t2.getLength());
		byte[] data = t2.getData();
		this.cmd = data[0];
		this.params = Arrays.copyOfRange(data, 1, data.length);
		//
		// ByteBuffer bfData = ByteBuffer.allocate(data.length);
		// bfData.put(data);
		// this.cmd = bfData.get(0);
		// this.params = new byte[data.length-1];
		//
		// bfData.get(this.params);
		return this;
	}

	@Override
	public byte[] pack() {
		// TODO Auto-generated method stub
		ByteBuffer packDt;
		if (params == null) {
			packDt = ByteBuffer.allocate(1);
			packDt.put((byte) cmd);
		} else {
			packDt = ByteBuffer.allocate(this.params.length + 1);
			packDt.put((byte) cmd);
			packDt.put(this.params);
		}
		T2 tmp = new T2();
		tmp.setData(packDt.array());
		tmp.setTag1(this.getTag1());
		tmp.setTag2(this.getTag2());
		tmp.setLength(this.getLength());
		return tmp.pack();
	}
}

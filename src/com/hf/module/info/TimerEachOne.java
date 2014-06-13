package com.hf.module.info;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.hf.lib.util.HexBin;

import android.R.array;
import android.util.Log;

public class TimerEachOne {

	byte num;
	byte type;
	boolean isEnable = true;

	byte flag;

	byte friMon;
	byte secMon;

	byte friDay;
	byte secDay;

	byte friHour;
	byte secHour;

	byte friMin;
	byte secMin;

	boolean friEnable = false;
	boolean secEnable = false;

	byte[] friDo;
	byte[] secDo;

	boolean dostat;

	public byte getNum() {
		return num;
	}

	public void setNum(byte num) {
		this.num = num;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
		if ((type & 0xF0) == 0x80) {
			isEnable = false;
		} else {
			isEnable = true;
		}
	}

	public byte getFriMon() {
		return friMon;
	}

	public void setFriMon(byte friMon) {
		this.friMon = friMon;
	}

	public byte getSecMon() {
		return secMon;
	}

	public void setSecMon(byte secMon) {
		this.secMon = secMon;
	}

	public byte getFriDay() {
		return friDay;
	}

	public void setFriDay(byte friDay) {
		this.friDay = friDay;
	}

	public byte getSecDay() {
		return secDay;
	}

	public void setSecDay(byte secDay) {
		this.secDay = secDay;
	}

	public byte getFriHour() {
		return friHour;
	}

	public void setFriHour(byte friHour) {
		this.friHour = friHour;
	}

	public byte getSecHour() {
		return secHour;
	}

	public void setSecHour(byte secHour) {
		this.secHour = secHour;
	}

	public byte getFriMin() {
		return friMin;
	}

	public void setFriMin(byte friMin) {
		this.friMin = friMin;
	}

	public byte getSecMin() {
		return secMin;
	}

	public void setSecMin(byte secMin) {
		this.secMin = secMin;
	}

	public boolean isFriEnable() {
		return friEnable;
	}

	public void setFriEnable(boolean friEnable) {
		this.friEnable = friEnable;
	}

	public boolean isSecEnable() {
		return secEnable;
	}

	public void setSecEnable(boolean secEnable) {
		this.secEnable = secEnable;
	}

	public byte[] getFriDo() {
		return friDo;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	public void setFriDo(byte[] friDo) {
		this.friDo = friDo;
		if (HexBin.bytesToString(friDo).equalsIgnoreCase("FE010005016001FFFF")) {
			dostat = false;
		} else if (HexBin.bytesToString(friDo).equalsIgnoreCase(
				"FE010005016002FFFF")) {
			dostat = true;
		}
	}

	public void setSecDo(byte[] secDo) {
		this.secDo = secDo;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
		if (isEnable) {
			this.type = (byte) (this.type & 0x0F);
		} else {
			this.type = (byte) (this.type | 0x80);
		}
	}

	// public void setFriDo(byte[] friDo) {
	// this.friDo = friDo;
	// }
	public byte[] getSecDo() {
		return secDo;
	}

	// public void setSecDo(byte[] secDo) {
	// this.secDo = secDo;
	// }

	/**
	 * 
	 * @param cmd
	 *            nn xx mon day
	 */

	public byte[] pack() {
		byte x = (byte) (type & 0x0F);
		switch (x) {
		case 0x01:
			return packDate1Time(friDo);
		case (byte) 0x02:
			return packweek1Time(friDo);
		case 0x03:
			return packDate2Time(friDo, secDo);
		case (byte) 0x04:
			return packweek2Time(friDo, secDo);

		default:
			break;
		}
		return null;
	}

	public DaysOfWeek getFlag() {
		return new DaysOfWeek().pack(flag);
	}

	public void setFlag(DaysOfWeek dw) {
		this.flag = dw.castToByte();
	}

	private byte[] packweek1Time(byte[] friDo) {
		// FE020011036001FFFF
		ByteBuffer bf = ByteBuffer.allocate(friDo.length + 10);
		bf.put((byte) 0xFE);
		bf.put((byte) 0x02);
		bf.putShort((short) (bf.capacity()-4));
		bf.put((byte) 0x03);
		bf.put(num);
		bf.put(type);
		bf.put(flag);
		bf.put(friHour);
		bf.put(friMin);
		bf.put(friDo);
		return bf.array();
	}

	private byte[] packweek2Time(byte[] friDo, byte[] secDo) {
		ByteBuffer bf = ByteBuffer.allocate(friDo.length + secDo.length + 12);
		bf.put((byte) 0xFE);
		bf.put((byte) 0x02);
		bf.putShort((short) (bf.capacity()-4));
		bf.put((byte) 0x03);
		bf.put(num);
		bf.put(type);
		bf.put(flag);
		bf.put(friHour);
		bf.put(friMin);
		bf.put(secHour);
		bf.put(secMin);
		bf.put(friDo);
		bf.put(secDo);
		bf.putShort(2, (short) bf.capacity());
		return bf.array();
	}

	private byte[] packDate1Time(byte[] friDo) {
		ByteBuffer bf = ByteBuffer.allocate(friDo.length + 11);
		bf.put((byte) 0xFE);
		bf.put((byte) 0x02);
		bf.putShort((short) (bf.capacity()-4));
		bf.put((byte) 0x03);
		bf.put(num);
		bf.put(type);
		bf.put(friMon);
		bf.put(friDay);
		bf.put(friHour);
		bf.put(friMin);
		bf.put(friDo);
		return bf.array();
	}

	private byte[] packDate2Time(byte[] friDo, byte[] secDo) {
		ByteBuffer bf = ByteBuffer.allocate(friDo.length + secDo.length + 15);
		bf.put((byte) 0xFE);
		bf.put((byte) 0x02);
		bf.putShort((short) (bf.capacity()-4));
		bf.put((byte) 0x03);
		bf.put(num);
		bf.put(type);
		bf.put(friMon);
		bf.put(friDay);
		bf.put(friHour);
		bf.put(friMin);
		bf.put(secMon);
		bf.put(secDay);
		bf.put(secHour);
		bf.put(secMin);
		bf.put(friDo);
		bf.put(secDo);
		return bf.array();
	}

	/**
	 * 
	 * @param cmd
	 *            nn xx
	 */
	public void unpack(byte[] cmd) {
		ByteBuffer bf = ByteBuffer.allocate(cmd.length);
		setType(cmd[1]);
		switch (type & 0x0F) {
		case 0x01:
			setNum(cmd[0]);
			setType(cmd[1]);
			setFriMon(cmd[2]);
			setFriDay(cmd[3]);
			setFriHour(cmd[4]);
			setFriMin(cmd[5]);
			setFriDo(getDo(cmd, 6));
			break;
		case 0x02:
			setNum(cmd[0]);
			setType(cmd[1]);
			setFlag(cmd[2]);
			setFriHour(cmd[3]);
			setFriMin(cmd[4]);
			setFriDo(getDo(cmd, 5));
			break;
		case 0x03:
			setNum(cmd[0]);
			setType(cmd[1]);
			setFriMon(cmd[2]);
			setFriDay(cmd[3]);
			setFriHour(cmd[4]);
			setFriMin(cmd[5]);
			setSecMon(cmd[6]);
			setSecDay(cmd[7]);
			setSecHour(cmd[8]);
			setSecMin(cmd[9]);
			setFriDo(getDo(cmd, 10));
			setSecDo(getDo(cmd, 10 + getFriDo().length));
			break;
		case 0x04:
			setNum(cmd[0]);
			setType(cmd[1]);
			setFlag(cmd[2]);
			setFriHour(cmd[3]);
			setFriMin(cmd[4]);
			setSecHour(cmd[5]);
			setSecMin(cmd[6]);
			setFriDo(getDo(cmd, 7));
			setSecDo(getDo(cmd, 7 + getFriDo().length));
			break;
		default:
			break;
		}
	}

	private byte[] getDo(byte[] cmd, int offset) {
		// fe 01 0001 01
		ByteBuffer bf = ByteBuffer.allocate(cmd.length);
		bf.put(Arrays.copyOfRange(cmd, offset, cmd.length));
		short length = bf.getShort(2);
		return Arrays.copyOfRange(cmd, offset, offset + 4 + length);
	}

	private byte[] getOneTime(byte[] cc, int offset) {
		// num xx xxxxxxxx num xx xxxxxxxxxx
		byte[] cmd = Arrays.copyOfRange(cc, offset, cc.length);
		ByteBuffer bf = ByteBuffer.allocate(cmd.length);
		byte[] ff;
		byte[] fdo;
		byte[] sdo;
		ByteBuffer bfa;
		bf.put(cmd);
		switch (bf.get(1) & 0x0f) {
		case 0x01:
			fdo = getDo(cmd, 6);
			Log.e("getOneTime", HexBin.bytesToStringWithSpace(fdo));
			return Arrays.copyOfRange(cmd, 0, fdo.length + 6);
		case 0x02:

			fdo = getDo(cmd, 5);
			Log.e("getOneTime", HexBin.bytesToStringWithSpace(fdo));

			return Arrays.copyOfRange(cmd, 0, fdo.length + 5);
		case 0x03:
			// ff = new byte[10];
			fdo = getDo(cmd, 10);
			Log.e("getOneTime", HexBin.bytesToStringWithSpace(fdo));
			sdo = getDo(cmd, 10 + fdo.length);
			Log.e("getOneTime", HexBin.bytesToStringWithSpace(sdo));
			return Arrays.copyOfRange(cmd, 0, fdo.length + 10 + sdo.length);

		case 0x04:
			fdo = getDo(cmd, 7);
			Log.e("getOneTime", HexBin.bytesToStringWithSpace(fdo));
			sdo = getDo(cmd, 7 + fdo.length);
			Log.e("getOneTime", HexBin.bytesToStringWithSpace(sdo));
			return Arrays.copyOfRange(cmd, 0, fdo.length + 7 + sdo.length);
		default:
			break;
		}
		return cmd;
	}

	public ArrayList<TimerEachOne> unpackAll(byte[] cmd) {
		// numall num xx xxxxxxxx num xx xxxxxxxxxx
		Log.e("unpackAll", HexBin.bytesToString(cmd));
		if (cmd[1] == 0xff) {
			return new ArrayList<TimerEachOne>();
		}
		ArrayList<TimerEachOne> tms = new ArrayList<TimerEachOne>();
		byte[] bf = Arrays.copyOfRange(cmd, 1, cmd.length);

		int offset = 0;
		for (int i = 0; i < cmd[0]; i++) {
			byte[] a = getOneTime(bf, offset);
			Log.e("unpackAll", HexBin.bytesToStringWithSpace(a));
			offset += a.length;
			// ByteBuffer abf = ByteBuffer.allocate(a.length);
			// abf.put(a);
			// tims.add(abf);
			// bf.clear();
			// bf.put(Arrays.copyOfRange(cmd, a.length, cmd.length));
			TimerEachOne t = new TimerEachOne();
			t.unpack(a);
			tms.add(t);
		}
		return tms;
	}

	public boolean getdostat() {
		// TODO Auto-generated method stub
		return dostat;
	}
}

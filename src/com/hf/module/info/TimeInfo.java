package com.hf.module.info;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.hf.Unity.GeneticT2;
import com.hf.lib.protocol.t1.t2.T2;
import com.hf.lib.util.HexBin;

//import java.util.HashMap;

public class TimeInfo {

	// <Time num ,GPIO>
	// private HashMap<Integer,Time> Timing_list = new HashMap<Integer, Time>();
	private HashMap<Integer,GPIO> GPIOs = new HashMap<Integer, GPIO>();
	
	
	public int NUM_OF_TIME; // which timer
	public int TYPE_OF_TIMER; // timer type

	
	public boolean isEnable = true; // is work

	public int hour;
	public int hour_After;

	public int min;
	public int min_After;

	public int month;
	public int month_After;

	public int Longth;

	public int day;
	public int day_After;

	public DaysOfWeek week = new DaysOfWeek();

	public GeneticT2 t2 ;
	public GeneticT2 t2_after;
	
	public int getHour_After() {
		return hour_After;
	}

	public void setHour_After(int hour_After) {
		this.hour_After = hour_After;
	}

	public int getMin_After() {
		return min_After;
	}

	public void setMin_After(int min_After) {
		this.min_After = min_After;
	}

	public int getDay_After() {
		return day_After;
	}

	public void setDay_After(int day_After) {
		this.day_After = day_After;
	}

	public int getMonth_After() {
		return month_After;
	}

	public void setMonth_After(int monthAfter) {
		this.month_After = monthAfter;
	}
	// this prog set with GPIO Other May be anther.
	public GPIO Timing_content_Befor; //
	public GPIO Timing_content_After; //

	

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public GeneticT2 getT2_after() {
		return t2_after;
	}

	public void setT2_after(GeneticT2 t2_after) {
		this.t2_after = t2_after;
	}

	
	
	public HashMap<Integer, GPIO> getGPIOs() {
		return GPIOs;
	}

	public void setGPIOs(HashMap<Integer, GPIO> gPIOs) {
		GPIOs = gPIOs;
	}

	public void setGeneticT2(GeneticT2 t2) {
		this.t2 = t2;
	}

	public GeneticT2 getGeneticT2() {
		return this.t2;
	}

	public int getNUM_OF_TIME() {
		return NUM_OF_TIME;
	}

	public void setNUM_OF_TIME(int nUM_OF_TIME) {
		NUM_OF_TIME = nUM_OF_TIME;
	}

	public int getTYPE_OF_TIMER() {
		return TYPE_OF_TIMER;
	}

	public void setTYPE_OF_TIMER(int tYPE_OF_TIMER) {
		TYPE_OF_TIMER = tYPE_OF_TIMER;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}

	public GPIO getTiming_content_Befor() {
		return Timing_content_Befor;
	}

	public void setTiming_content_Befor(GPIO timing_content_Befor) {
		Timing_content_Befor = timing_content_Befor;
	}

	public GPIO getTiming_content_After() {
		return Timing_content_After;
	}

	public void setTiming_content_After(GPIO timing_content_After) {
		Timing_content_After = timing_content_After;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public DaysOfWeek getWeek() {
		return week;
	}

	public void setWeek(DaysOfWeek week) {
		this.week = week;
	}

	public GeneticT2 getT2() {
		return t2;
	}

	public void setT2(GeneticT2 t2) {
		this.t2 = t2;
	}

	public byte[] pack() {
		try{
		switch (this.TYPE_OF_TIMER) {
		case 0x01:
			return packDaySingleTime();
		case 0x02:
			return packWeekSingleTime();
		case 0x03:
			return packDayultipleTime();
		case 0x04:
			return packWeekMultipleTime();
		default:
			break;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public HashMap<Integer, TimeInfo> unpack(byte[] timebyte) {	
		//-------------------------------

		HashMap<Integer, TimeInfo> tM = new HashMap<Integer, TimeInfo>();
		if(timebyte.length<=1)
		{
			return tM;
		}
		try{
			unpackall(tM, Arrays.copyOfRange(timebyte, 1, timebyte.length));
		}catch(Exception e){
			e.printStackTrace();
		}
		return tM;
	}
	
	private byte[]  unpackall(HashMap<Integer, TimeInfo> tm, byte[] timeall){
		TimeInfo  thistime = new TimeInfo();
		thistime.TYPE_OF_TIMER = timeall[1]&0x0f;
		int i = 0;
		switch (thistime.TYPE_OF_TIMER) {
		case 0x01:
			i = (((timeall[8]&0xFF)<<8)|(timeall[9]&0xFF))+9;
			byte[] a1 = Arrays.copyOfRange(timeall, 0, i+1);
			thistime = unpackDaySingleTime(a1);
			break;
		case 0x02:
			i = ((timeall[7]&0xFF)<<8)|(timeall[8]&0xFF)+8;
			byte[] a2 = Arrays.copyOfRange(timeall, 0, i+1);
			thistime = unpackWeekSingleTime(a2);
			break;
		case 0x03:
			//i = ((timeall[7]&0xFF)<<8)|timeall[8];
			break;
		case 0x04:
			//i = ((timeall[7]&0xFF)<<8)|timeall[8];
			break;
		default:
			break;
		}
		
		if(thistime != null){
			tm.put(thistime.NUM_OF_TIME, thistime);
		}
		

		byte[] b = Arrays.copyOfRange(timeall, i+1, timeall.length);
		if(b.length>0){
			unpackall(tm,b);
		}
		return null;
	}

	private byte[] packDaySingleTime() {
		ByteBuffer bb = ByteBuffer.allocate(6 + 5 + getGPIOs().size()*4);
		bb.put((byte) NUM_OF_TIME);
		if (!isEnable) {
			bb.put((byte) (TYPE_OF_TIMER | 0x80));
		} else {
			bb.put((byte) TYPE_OF_TIMER);
		}
		bb.put((byte) month);
		bb.put((byte) day);
		bb.put((byte) hour);
		bb.put((byte) min);
		bb.put(packGPIOs().pack());
		return bb.array();
	}

	private byte[] packWeekSingleTime() {
		ByteBuffer bb = ByteBuffer.allocate(5 + 5 + getGPIOs().size()*4);
		bb.put((byte) NUM_OF_TIME);
		if (!isEnable) {
			bb.put((byte) (TYPE_OF_TIMER | 0x80));
		} else {
			bb.put((byte) TYPE_OF_TIMER);
		}

		bb.put(week.castToByte());
		bb.put((byte) hour);
		bb.put((byte) min);
		bb.put(packGPIOs().pack());
		return bb.array();
	}

	private byte[] packWeekMultipleTime() {
		ByteBuffer bb = ByteBuffer.allocate(7 + t2.pack().length
				+ t2_after.pack().length);
		bb.put((byte) NUM_OF_TIME);
		if (isEnable) {
			bb.put((byte) (TYPE_OF_TIMER | 0x80));
		} else {
			bb.put((byte) TYPE_OF_TIMER);
		}
		bb.put(week.castToByte());
		bb.put((byte) hour);
		bb.put((byte) min);
		bb.put((byte) hour_After);
		bb.put((byte) min_After);
		bb.put(t2.pack());
		bb.put(t2_after.pack());
		return bb.array();
	}

	private byte[] packDayultipleTime() {
		ByteBuffer bb = ByteBuffer.allocate(10 + t2.pack().length);
		bb.put((byte) NUM_OF_TIME);
		if (isEnable) {
			bb.put((byte) (TYPE_OF_TIMER | 0x80));
		} else {
			bb.put((byte) TYPE_OF_TIMER);
		}
		bb.put((byte) month);
		bb.put((byte) day);
		bb.put((byte) hour);
		bb.put((byte) min);

		bb.put((byte) month_After);
		bb.put((byte) day_After);
		bb.put((byte) hour_After);
		bb.put((byte) min_After);

		bb.put(t2.pack());
		bb.put(t2_after.pack());
		return bb.array();
	}

	public TimeInfo unpackWeekSingleTime(byte[] timebyte) {
		TimeInfo t = new TimeInfo();
		t.setNUM_OF_TIME(timebyte[0]);
		//t.NUM_OF_TIME = timebyte[0];
		t.TYPE_OF_TIMER = timebyte[1] & 0x0f;
		t.isEnable = ((timebyte[1] & 0xF0) == 0x00) ? true : false;
		t.week = t.week.pack(timebyte[2]);
		t.hour = timebyte[3];
		t.min = timebyte[4];
		byte[] tmp = Arrays.copyOfRange(timebyte, 5,timebyte.length);
		t.t2 = new GeneticT2().unpack(tmp);
		//t.Longth = t2.getLength() + 4 + 4;
		t.setGPIOs(unpackGPIOs(t.t2));
		System.out.println(t.getGPIOs().get(6).getStatus());
		return t;
		
		//------------
		
	}

	public TimeInfo unpackDaySingleTime(byte[] timebyte) {
		TimeInfo t = new TimeInfo();
		t.NUM_OF_TIME = timebyte[0];
		t.TYPE_OF_TIMER = timebyte[1] & 0x0f;
		t.isEnable = ((timebyte[1] & 0xF0) == 0x00) ? true : false;
		t.month = timebyte[2];
		t.day = timebyte[3];
		t.hour = timebyte[4];
		t.min = timebyte[5];


		byte[] tmp = Arrays.copyOfRange(timebyte, 6, timebyte.length);

		int i = 0xFF;
		i = ((i & tmp[2]) << 8) | tmp[3];
		byte[] tmp1 = Arrays.copyOfRange(tmp, 0, i + 4);
		System.out.println(HexBin.bytesToString(tmp1));
		t.t2 = new GeneticT2().unpack(tmp1);
		t.Longth = t.t2.getLength() + 6 + 4;
		System.out.println(HexBin.bytesToString(t.t2.getParams()));
		t.setGPIOs(unpackGPIOs(t.t2));
		return t;
	}

	private TimeInfo unpackWeekMultipleTime(byte[] timebyte) {
		TimeInfo t = new TimeInfo();
		t.NUM_OF_TIME = timebyte[0];
		t.TYPE_OF_TIMER = timebyte[1] & 0x0f;
		t.isEnable = ((timebyte[1] & 0xF0) == 0x80) ? true : false;
		t.week.pack(timebyte[2]);
		t.hour = timebyte[3];
		t.min = timebyte[4];
		t.hour = timebyte[5];
		t.min = timebyte[6];
		byte[] tmp = Arrays.copyOfRange(timebyte, 7, timebyte.length);

		int i = 0xFF;
		i = ((i & tmp[2]) << 8) | tmp[3];
		byte[] tmp1 = Arrays.copyOfRange(tmp, 0, i + 4);
		byte[] tmp2 = Arrays.copyOfRange(tmp, i + 4, tmp.length);
		i = 0xFF;
		i = ((i & tmp2[2]) << 8) | tmp2[3];
		byte[] tmp3 = Arrays.copyOfRange(tmp2, 0, i + 4);
		t.t2 = new GeneticT2().unpack(tmp1);
		t.t2_after = new GeneticT2().unpack(tmp3);
		t.Longth = t2.getLength() + t2_after.getLength() + 6 + 4;
		return t;
	}

	private TimeInfo unpackDayultipleTime(byte[] timebyte) {

		TimeInfo t = new TimeInfo();
		t.NUM_OF_TIME = timebyte[0];
		t.TYPE_OF_TIMER = timebyte[1] & 0x0f;
		t.isEnable = ((timebyte[1] & 0xF0) == 0x80) ? true : false;
		t.month = timebyte[2];
		t.day = timebyte[3];
		t.hour = timebyte[4];
		t.min = timebyte[5];
		t.month_After = timebyte[6];
		t.day_After = timebyte[7];
		t.hour_After = timebyte[8];
		t.min_After = timebyte[9];

		byte[] tmp = Arrays.copyOfRange(timebyte, 10, timebyte.length);

		int i = 0xFF;
		i = ((i & tmp[2]) << 8) | tmp[3];
		byte[] tmp1 = Arrays.copyOfRange(tmp, 0, i + 4);
		byte[] tmp2 = Arrays.copyOfRange(tmp, i + 4, tmp.length);
		i = 0xFF;
		i = ((i & tmp2[2]) << 8) | tmp2[3];
		byte[] tmp3 = Arrays.copyOfRange(tmp2, 0, i + 4);
		t.t2 = new GeneticT2().unpack(tmp1);
		t.t2_after = new GeneticT2().unpack(tmp3);
		t.Longth = t2.getLength() + t2_after.getLength() + 10 + 4;
		return t;
	}
	
	private GeneticT2 packGPIOs(){
		GeneticT2 t1 = new GeneticT2();
		t1.setTag1(T2.TAG1_SET);
		t1.setTag2((byte) 0x01);
		t1.setCmd(0x01);

		ByteBuffer data1 = ByteBuffer.allocate(4*GPIOs.size());
		Iterator<Integer> it = GPIOs.keySet().iterator();
		while(it.hasNext()){
			int key = it.next();
			GPIO g = GPIOs.get(key);
			if(g.getStatus()){
				data1.put(HexBin.stringToBytes(g.getId()+"002FFFF"));
			}
			else
			{
				data1.put(HexBin.stringToBytes(g.getId()+"001FFFF"));
			}
		}
		t1.setParams(data1.array());
		t1.setLength(data1.array().length + 5);
		return t1;
	}
	
	private HashMap<Integer, GPIO> unpackGPIOs(GeneticT2 t2){
		byte[] cmd = t2.getParams();
		
		return getStatulist(cmd);
	}
	
	private HashMap<Integer, GPIO> getStatulist(byte[] T2data) {
		HashMap<Integer, GPIO> GPIOStatusMap = new HashMap<Integer, GPIO>();
		for (int i = 0; i < T2data.length; i += 4) {
			GPIO gpio = new GPIO();
			gpio.setId(T2data[i] >> 4);
			System.out.println(gpio.getId());
			gpio.setStatus((T2data[i + 1] & 0x01) == 0);
			System.out.println(gpio.getStatus());
			GPIOStatusMap.put(gpio.getId(), gpio);
		}
		return GPIOStatusMap;
	}
}

package com.hf.module;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.R.array;
import android.R.integer;
import android.util.Log;

import com.hf.Unity.GeneticT2;
import com.hf.lib.protocol.t1.Head1;
import com.hf.lib.protocol.t1.Head2;
import com.hf.lib.protocol.t1.T1Message;
import com.hf.lib.protocol.t1.t2.T2;
import com.hf.lib.util.AES;
import com.hf.lib.util.HexBin;
import com.hf.lib.util.MD5;
import com.hf.module.impl.LocalModuleInfoContainer;
import com.hf.module.impl.UdpProxy;
import com.hf.module.info.ADCinfo;
import com.hf.module.info.DaysOfWeek;
import com.hf.module.info.GPIO;
import com.hf.module.info.ModuleInfo;
import com.hf.module.info.PWM;
import com.hf.module.info.Pin;
import com.hf.module.info.TimeInfo;
import com.hf.module.info.TimerEachOne;

/**
 * @author Sean
 * 
 */
public class ModuleHelper {
	private IModuleManager mm = null;

	public ModuleHelper(IModuleManager moduleManager) {
		this.mm = moduleManager;
	}

	// *** Basic Methods
	// ***********************************************************************************************

	public HashMap<Integer, GPIO> setGPIO(String mac,
			HashMap<Integer, GPIO> GPIOs) throws ModuleException {
		// HashMap<Integer, Pin> pinMap = mi.getPinMap();
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		GeneticT2 t2 = new GeneticT2();
		t2.setTag1(T2.TAG1_SET);
		t2.setTag2((byte) 0x01);
		CtrlData cd = new CtrlData();
		cd.setGM(GPIOs);
		t2.setCmd(0x01);

		ByteBuffer data = ByteBuffer.allocate(GPIOs.size() * 4);
		data.put(cd.pack());
		t2.setParams(data.array());
		t2.setLength(data.array().length + 5);

		t2Req.put(mac, t2.pack());
		// t2Req.put(mi.getMac(), )
		HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
		byte[] cmd = rsp.get(mac);
		GeneticT2 rspT2 = new GeneticT2();
		if (cmd != null && cmd[0] == (byte) 0xFE) {
			rspT2.unpack(cmd);
			System.out.println(HexBin.bytesToStringWithSpace(rspT2.getParams()));
			return getStatulist(rspT2.getParams()); // rspT2.getData());
		} else {
			ModuleException mex = new ModuleException("setGPIO time out");
			mex.setErrorCode(-32);
			throw mex;
		}
	}

	public boolean setHFGPIO(String mac, int gpio, boolean statu)
			throws ModuleException {
		HashMap<Integer, GPIO> gpios = new HashMap<Integer, GPIO>();
		GPIO g = new GPIO();
		g.setId(gpio);
		g.setStatus(statu);
		gpios.put(gpio, g);
		
		gpios = this.setGPIO(mac, gpios);
		Iterator<Integer> it = gpios.keySet().iterator();
		Integer i = it.next();
		System.out.println(i+""+gpios.get(i).getStatus());
		boolean sat = gpios.get(gpio).getStatus();
		return sat;
	}

	public boolean getHFGPIO(String mac, int gpioid) throws ModuleException {
		ModuleInfo mi = new ModuleInfo();
		mi.setMac(mac);
		GPIO g = new GPIO();
		g.setId(gpioid);
		g.setStatus(false);
		HashMap<Integer, GPIO> gpios = mi.getPinMap();
		gpios.put(gpioid, g);
		return this.getGPIO(mi).get(gpioid).getStatus();
	}
	
	public HashMap<Integer, GPIO> getHFGPIOs(String mac ,HashMap<Integer, GPIO> gpios) throws ModuleException{
		ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
		mi.setPinMap(gpios);
		HashMap<Integer, GPIO> rspgpios = getGPIO(mi);
		return rspgpios;
	}

	public boolean deleteHFTimer(String mac, int timerNum)
			throws ModuleException {
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		GeneticT2 t2 = new GeneticT2();
		t2.setTag1(T2.TAG1_SET);
		t2.setTag2((byte) 0x02);
		t2.setCmd(0x06);
		t2.setLength(6);
		ByteBuffer b = ByteBuffer.allocate(1);
		b.put((byte) timerNum);
		t2.setParams(b.array());

		t2Req.put(mac, t2.pack());
		HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
		byte[] rspbyte = rsp.get(mac);
		t2.unpack(rspbyte);
		byte[] pa = t2.getParams();
		if (pa[0] == timerNum && pa[1] == 0x01) {
			return true;
		}
		return false;
	}

	public HashMap<Integer, TimeInfo> getHFAllTimer(String mac)
			throws ModuleException {

		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		GeneticT2 t2 = new GeneticT2();
		t2.setTag1(GeneticT2.TAG1_GET);
		t2.setTag2((byte) 0x02);
		t2.setCmd(0x05);
		t2Req.put(mac, t2.pack());
		HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
		byte[] cmd = rsp.get(mac);
		if (cmd == null) {
			ModuleException mx = new ModuleException("null");
			mx.setErrorCode(-31);
			throw mx;
		}
		GeneticT2 rspT2 = new GeneticT2();
		rspT2.unpack(cmd);
		if (rspT2.getCmd() == 0x05) {
			byte[] data = rspT2.getParams();
			Timerdata td = new Timerdata();
			return td.unpack(data);
		}
		return null;
	}
	public ArrayList<TimeInfo> getHFAllTimerAsArray(String mac) throws ModuleException{
		HashMap<Integer, TimeInfo> times = getHFAllTimer(mac);
		ArrayList<TimeInfo> timelist = new ArrayList<TimeInfo>();
		Iterator<Integer> it = times.keySet().iterator();
		while(it.hasNext()){
			timelist.add(times.get(it.next()));
		}
		return timelist;
	}
	public void setHFTimer(String mac, TimeInfo time) throws ModuleException {
		ModuleException mx = new ModuleException();
		if (time == null) {
			mx.setErrorCode(ModuleException.ERROR_CODE_PARAMETERS_OF_INPUT_ERROR);
			throw mx;
		}
		byte[] cmd = time.pack();
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		GeneticT2 t = new GeneticT2();
		t.setTag1(T2.TAG1_SET);
		t.setTag2((byte) 0x02);
		t.setCmd(0x03);
		t.setParams(cmd);
		t2Req.put(mac, t.pack());
		try {
			HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
			GeneticT2 rspt2 = new GeneticT2();
			rspt2.unpack(rsp.get(mac));
			if (rspt2.getTag1() == 0xFE && rspt2.getTag2() == 0x02
					&& rspt2.getCmd() == 0x05) {
				if (rspt2.getParams()[1] == 0xFF) {
					return;
				}
			}
		} catch (Exception e) {
			mx.setErrorCode(ModuleException.ERROR_CODE_MODULE_NOT_ONLINE);
			throw mx;
		}

	}
	
	public ADCinfo getHFADCModuleValues(String mac) throws ModuleException{
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		t2Req.put(mac, ADCinfo.getReqCmd());
		HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
		byte[] cmd = rsp.get(mac);
		if(cmd == null||cmd.length<25){
			throw new ModuleException("rsp err :can not recv module's rsp");
		}
		Log.e("liuyang", HexBin.bytesToStringWithSpace(cmd));
		ADCinfo adinfo = new ADCinfo();
		adinfo.unpack(rsp.get(mac));
		return adinfo;
	}
	
	//参数校准
	public ADCinfo getHFADCParameterCalibration(String mac) throws ModuleException{
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		t2Req.put(mac, ADCinfo.getCalibrationCmd());
		HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
		byte[] cmd = rsp.get(mac);
		if(cmd == null||cmd.length<25){
			throw new ModuleException("rsp err :can not recv module's rsp");
		}
		ADCinfo adinfo = new ADCinfo();
		adinfo.unpack(rsp.get(mac));
		return adinfo;
	}
	
	
	public void setHFTimer(String mac, int timerNo, int month, int day,
			int hour, int min, HashMap<Integer, GPIO> GPIOs)
			throws ModuleException {
		TimeInfo time = new TimeInfo();
		time.setTYPE_OF_TIMER(0x01);
		time.setNUM_OF_TIME(timerNo);
		time.setMonth(month);
		time.setDay(day);
		time.setHour(hour);
		time.setMin(min);
		time.setGPIOs(GPIOs);
		GeneticT2 t2 = new GeneticT2();
		t2.setTag1(T2.TAG1_SET);
		t2.setTag2((byte) 0x01);
		// 6001FFFF
		t2.setCmd(0x01);

		CtrlData cd = new CtrlData();
		cd.setGM(GPIOs);
		byte[] date = cd.pack();
		t2.setParams(date);
		t2.setLength(date.length + 5);
		time.setGeneticT2(t2);
		this.setHFTimer(mac, time);
	}

	public void setHFTimer(String mac, int timerNo, DaysOfWeek dow, int hour,
			int min, HashMap<Integer, GPIO> GPIOs) throws ModuleException {
		TimeInfo time = new TimeInfo();
		time.setTYPE_OF_TIMER(0x02);
		time.setNUM_OF_TIME(timerNo);
		time.setWeek(dow);
		time.setHour(hour);
		time.setMin(min);
		time.setGPIOs(GPIOs);
		GeneticT2 t2 = new GeneticT2();
		t2.setTag1(T2.TAG1_SET);
		t2.setTag2((byte) 0x01);
		// 6001FFFF
		t2.setCmd(0x01);

		CtrlData cd = new CtrlData();
		cd.setGM(GPIOs);
		byte[] date = cd.pack();
		t2.setParams(date);
		t2.setLength(date.length + 5);
		time.setGeneticT2(t2);
		this.setHFTimer(mac, time);
	}

	public TimeInfo getHFTimer(String mac, int timerNo) throws ModuleException {
		// FD02000204nn
		ModuleException mx = new ModuleException();

		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		String cmd = "FD020002040" + timerNo;
		System.out.println(cmd);
		byte[] scmd = HexBin.stringToBytes(cmd);
		t2Req.put(mac, scmd);
		HashMap<String, byte[]> rsp = new HashMap<String, byte[]>();
		try {
			rsp = mm.moduleCommSet(t2Req);
		} catch (ModuleException e) {
			mx.setErrorCode(-33);// -33 网络不通
			throw mx;
		}
		byte[] rspbyte = rsp.get(mac);
		System.out.println(HexBin.bytesToString(rspbyte));
		if (rspbyte == null) {
			mx.setErrorCode(ModuleException.ERROR_CODE_RECV_CMD_NULL_ERR);
			throw mx;
		}
		if (rspbyte.length < 10) {
			mx.setErrorCode(ModuleException.ERROR_CODE_RECV_CMD_DECODE_ERR); // -30 查询的信息不存在。
			throw mx;
		}

		TimeInfo t = new TimeInfo();
		byte[] timebyte = Arrays.copyOfRange(rspbyte, 5, rspbyte.length);
		System.out.println(HexBin.bytesToString(timebyte));

		if (timerNo == timebyte[0]) {
			if ((timebyte[1] & 0x0f) == 0x01) {
				System.out.println("singal");
				t = t.unpackDaySingleTime(timebyte);

			} else {
				System.out.println("Week");
				t = t.unpackWeekSingleTime(timebyte);
			}
		} else {
			mx.setErrorCode(-30); // -30 获取模块信息错误。
			throw mx;
		}
		return t;
	}
	
	//------------------------------for eachone---
	public ArrayList<TimerEachOne> getAllEachOneTimer(String mac) throws ModuleException{
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		GeneticT2 t2 = new GeneticT2();
		t2.setTag1(GeneticT2.TAG1_GET);
		t2.setTag2((byte) 0x02);
		t2.setCmd(0x05);
		t2Req.put(mac, t2.pack());
		HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
		byte[] cmd = rsp.get(mac);
		if(cmd == null||cmd.length<=0){
			throw new ModuleException();
		}
		
		TimerEachOne teo = new TimerEachOne();
		System.out.println(HexBin.bytesToStringWithSpace(cmd));
		
		return teo.unpackAll(Arrays.copyOfRange(cmd, 5, cmd.length));
	}
	
	public void setTimer(String mac,TimerEachOne t) throws ModuleException{
		ModuleException mx = new ModuleException();
		if (t == null) {
			mx.setErrorCode(ModuleException.ERROR_CODE_PARAMETERS_OF_INPUT_ERROR);
			throw mx;
		}
		byte[] tim = t.pack();
		if(tim == null){
			mx.setErrorCode(ModuleException.ERROR_CODE_PARAMETERS_OF_INPUT_ERROR);
			throw mx;
		}
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		t2Req.put(mac, tim);
		Log.i("setTimer", HexBin.bytesToStringWithSpace(tim));
		HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
		byte[] cmd = rsp.get(mac);
		if(cmd == null||cmd.length<=0){
			throw new ModuleException();
		}
		
		if(cmd.length>5||cmd[5]==tim[0]){
			Log.i("setTimer", "OK");
		}else{
			throw new ModuleException();
		}
	}
	
	public TimerEachOne getTimer(String mac,int timeNo){
		return new TimerEachOne();
	}
	//-------------------------------------------

	public byte[] setModulePSWD(String mac) throws ModuleException {
		
		ModuleException mx = new ModuleException();
		ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
		T1Message send = new T1Message();
		Head1 h1 = new Head1();
		h1.setId(T1Message.ID_LOCAL_ADD_MODULE);
		h1.setFlag((byte) 0x41);
		h1.setMac(mac);
		Head2 h2 = new Head2();
		h2.setVersion(0x1);
		h2.setReserved(0);
		h2.setComponyCode(mi.getFactoryId());
		h2.setModuleCode(mi.getType());

		send.setHead1(h1);
		send.setHead2(h2);
		ByteBuffer key = ByteBuffer.allocate(17);
		key.put((byte) 0x10);
		key.put(ModuleConfig.localModulePswd.getBytes());
		send.setPayload(key.array());

		send.setKey(AES.DEFAULT_KEY_128.getBytes());
		byte[] cmd;
		try {
			cmd = send.pack();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mx.setErrorCode(-36); // cmd pack err
			throw mx;
		}
		System.out.println(mac + "????????????????????????---------->OK");
		return UdpProxy.getInstance().send(cmd, mi.getLocalIp(),
				AES.DEFAULT_KEY_128.getBytes());

	}
	
public byte[] setModulePSWD(ModuleInfo mi) throws ModuleException {
		
		ModuleException mx = new ModuleException();
		T1Message send = new T1Message();
		Head1 h1 = new Head1();
		h1.setId(T1Message.ID_LOCAL_ADD_MODULE);
		h1.setFlag((byte) 0x41);
		h1.setMac(mi.getMac());
		Head2 h2 = new Head2();
		h2.setVersion(0x1);
		h2.setReserved(0);
		h2.setComponyCode(mi.getFactoryId());
		h2.setModuleCode(mi.getType());

		send.setHead1(h1);
		send.setHead2(h2);
		ByteBuffer key = ByteBuffer.allocate(17);
		key.put((byte) 0x10);
		key.put(ModuleConfig.localModulePswd.getBytes());
		send.setPayload(key.array());

		send.setKey(AES.DEFAULT_KEY_128.getBytes());
		byte[] cmd;
		try {
			cmd = send.pack();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mx.setErrorCode(-36); // cmd pack err
			throw mx;
		}
		System.out.println(mi.getMac() + "????????????????????????---------->OK");
		return UdpProxy.getInstance().send(cmd, mi.getLocalIp(),
				AES.DEFAULT_KEY_128.getBytes());

	}

	public byte[] setServAdd(String mac, String servadd, int port)
			throws ModuleException {
		
		ModuleException mx = new ModuleException();
		ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
		System.out.println(mi.toString());
		T1Message send = new T1Message();
		Head1 h1 = new Head1();
		Head2 h2 = new Head2();
		h1.setId((byte) 0x0A);
		h1.setFlag((byte) 0x41);
		h1.setMac(mac);
		h2.setVersion(1);
		h2.setReserved(0);
		h2.setSn(0);
		h2.setComponyCode(ModuleConfig.factoryId);
		h2.setModuleCode(mi.getFactoryId());
		send.setHead1(h1);
		send.setHead2(h2);

		byte[] add = servadd.getBytes();
		ByteBuffer pay = ByteBuffer.allocate(add.length + 12);
		try {
			pay.put(intToByteArray(port));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mx.setErrorCode(-36);
			throw mx;
		}

		pay.put(new byte[8]);
		try {
			pay.put(intToByteArray(add.length));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mx.setErrorCode(-36);
			throw mx;
		}
		pay.put(add);

		send.setPayload(pay.array());

		if (mi != null)
			send.setKey(mi.getLocalKey().getBytes());
		byte[] cmd;
		try {
			cmd = send.pack();
		} catch (Exception e) {
			mx.setErrorCode(-36);
			throw mx;
		}
		System.out.println(mac + "???????????????????????????????????????????????????----------->OK");
		return UdpProxy.getInstance().send(cmd, mi.getLocalIp(),
				mi.getLocalKey().getBytes());
	}
	public byte[] setServAdd(ModuleInfo mi, String servadd, int port)
			throws ModuleException {
		String mac = mi.getMac();
		ModuleException mx = new ModuleException();
		System.out.println(mi.toString());
		T1Message send = new T1Message();
		Head1 h1 = new Head1();
		Head2 h2 = new Head2();
		h1.setId((byte) 0x0A);
		h1.setFlag((byte) 0x41);
		h1.setMac(mac);
		h2.setVersion(1);
		h2.setReserved(0);
		h2.setSn(0);
		h2.setComponyCode(mi.getFactoryId());
		h2.setModuleCode(mi.getType());
		send.setHead1(h1);
		send.setHead2(h2);

		byte[] add = servadd.getBytes();
		ByteBuffer pay = ByteBuffer.allocate(add.length + 12);
		try {
			pay.put(intToByteArray(port));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mx.setErrorCode(-36);
			throw mx;
		}

		pay.put(new byte[8]);
		try {
			pay.put(intToByteArray(add.length));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mx.setErrorCode(-36);
			throw mx;
		}
		pay.put(add);

		send.setPayload(pay.array());

		if (mi != null)
			send.setKey(mi.getLocalKey().getBytes());
		byte[] cmd;
		try {
			cmd = send.pack();
		} catch (Exception e) {
			mx.setErrorCode(-36);
			throw mx;
		}
		System.out.println(mac + "设置服务器地址----------->OK");
		return UdpProxy.getInstance().send(cmd, mi.getLocalIp(),
				mi.getLocalKey().getBytes());
	}

	public void setPWM(ModuleInfo mInfo, PWM[] PWMs) throws ModuleException {

	}

	/**
	 * @param mInfo
	 * @return
	 * @throws ModuleException
	 */
	public HashMap<Integer, GPIO> getGPIO(ModuleInfo mInfo)
			throws ModuleException {
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		GPIO tmg = new GPIO();
		tmg.setId(11);
		tmg.setStatus(true);
		mInfo.getPinMap().put(11, tmg);
		GeneticT2 t = new GeneticT2();
		t.setTag1((byte) T2.TAG1_GET);
		t.setTag2((byte) 0x01);

		t.setCmd(0x02);
		HashMap<Integer, GPIO> pmap = mInfo.getPinMap();
		t.setLength(pmap.size() + 5);
		Iterator it = pmap.keySet().iterator();
		ByteBuffer bf = ByteBuffer.allocate(pmap.size());
		while (it.hasNext()) {
			Object key = it.next();
			GPIO g = pmap.get(key);
			bf.put((byte) g.getId());
		}

		t.setParams(bf.array());

		t2Req.put(mInfo.getMac(), t.pack());
		HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
		byte[] cmd = rsp.get(mInfo.getMac());
		GeneticT2 rspT2 = new GeneticT2();
		if(cmd==null){
			throw new ModuleException(31);
		}
		rspT2.unpack(cmd);
		if (rspT2.getTag1() == (byte) 0xFD && rspT2.getLength() > 0) {
			return getStatulist(rspT2.getParams()); // rspT2.getData());
		}
		else
		{
			throw new ModuleException(32);
		}
	}

	public HashMap<String, HashMap<Integer, GPIO>> getAllModuleStatus(
			ArrayList<ModuleInfo> mis) throws Exception {

		HashMap<String, HashMap<Integer, GPIO>> result = new HashMap<String, HashMap<Integer, GPIO>>();

		Iterator<ModuleInfo> it = mis.iterator();
		while (it.hasNext()) {
			ModuleInfo mi = it.next();

			GPIO g = new GPIO();
			g.setId(6);
			g.setStatus(getHFGPIO(mi.getMac(), 6));
			HashMap<Integer, GPIO> gm = new HashMap<Integer, GPIO>();
			gm.put(6, g);
			result.put(mi.getMac(), gm);
		}
		return result;
	}

	public PWM[] getPWM(ModuleInfo mInfo, Pin[] pins) throws ModuleException {
		return null;
	}

	public ModuleInfo getAllTimer(ModuleInfo mi) {
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		GeneticT2 t2 = new GeneticT2();
		t2.setTag1(GeneticT2.TAG1_GET);
		t2.setTag2((byte) 0x02);
		t2.setCmd(0x05);
		t2Req.put(mi.getMac(), t2.pack());
		try {
			HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
			byte[] cmd = rsp.get(mi.getMac());
			GeneticT2 rspT2 = new GeneticT2();
			rspT2.unpack(cmd);
			if (rspT2.getCmd() == 0x05) {
				byte[] data = rspT2.getParams();

				Timerdata td = new Timerdata();
				mi.setTimeMap(td.unpack(data));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mi;
	}

	public void setTimer(ModuleInfo mi, TimeInfo time) throws ModuleException {
		if (time == null) {
			return;
		}
		byte[] cmd = time.pack();
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		GeneticT2 t = new GeneticT2();
		t.setTag1(T2.TAG1_SET);
		t.setTag2((byte) 0x02);
		t.setCmd(0x03);
		t.setParams(cmd);
		t2Req.put(mi.getMac(), t.pack());
		HashMap<String, byte[]> rsp = null;
		rsp = mm.moduleCommSet(t2Req);

		GeneticT2 rspt2 = new GeneticT2();
		rspt2.unpack(rsp.get(mi.getMac()));
		if (rspt2.getTag1() == 0xFE && rspt2.getTag2() == 0x02
				&& rspt2.getCmd() == 0x05) {
			if (rspt2.getParams()[1] == 0xFF) {
				return;
			}
		}
	}

	public void deleteTimer(int timerNum, ModuleInfo mi) throws ModuleException {
		// TODO Auto-generated method stub FE 02 00 02 06 NN
		ModuleException mx =  new ModuleException();
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		GeneticT2 t2 = new GeneticT2();
		t2.setTag1(T2.TAG1_SET);
		t2.setTag2((byte) 0x02);
		t2.setCmd(0x06);
		t2.setLength(6);
		ByteBuffer b = ByteBuffer.allocate(1);
		b.put((byte) timerNum);
		t2.setParams(b.array());

		t2Req.put(mi.getMac(), t2.pack());
		HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
		byte[] rspbyte = rsp.get(mi.getMac());
		t2.unpack(rspbyte);
		byte[] pa = t2.getParams();
		if (pa[0] == timerNum && pa[1] == 0x01) {
			mi.getTimeMap().remove(timerNum);
		} else {
			mx.setErrorCode(-32);
			throw mx;
		}
	}

	public void setTimer(ModuleInfo mInfo, int timerNo, int month, int day,
			int hour, int min, HashMap<Integer, GPIO> GPIOs)
			throws ModuleException {
		TimeInfo time = new TimeInfo();
		time.setTYPE_OF_TIMER(0x01);
		time.setNUM_OF_TIME(timerNo);
		time.setMonth(month);
		time.setDay(day);
		time.setHour(hour);
		time.setMin(min);
		GeneticT2 t2 = new GeneticT2();
		t2.setTag1(T2.TAG1_SET);
		t2.setTag2((byte) 0x01);
		// 6001FFFF
		t2.setCmd(0x01);

		CtrlData cd = new CtrlData();
		cd.setGM(GPIOs);
		byte[] date = cd.pack();
		t2.setParams(date);
		t2.setLength(date.length + 5);
		time.setGeneticT2(t2);
		this.setTimer(mInfo, time);
	}

	public void setTimer(ModuleInfo mInfo, int timerNo, int month, int day,
			int hour, int min, PWM[] PWMs) throws ModuleException {

	}

	public void setTimer(ModuleInfo mInfo, int timerNo, int month, int day,
			int hour, int min, ByteBuffer bf) throws ModuleException {
	}

	public void setTimer(ModuleInfo mInfo, int timerNo, DaysOfWeek dow,
			int hour, int min, HashMap<Integer, GPIO> GPIOs)
			throws ModuleException {
		TimeInfo time = new TimeInfo();
		time.setTYPE_OF_TIMER(0x02);
		time.setNUM_OF_TIME(timerNo);
		time.setWeek(dow);
		time.setHour(hour);
		time.setMin(min);
		GeneticT2 t2 = new GeneticT2();
		t2.setTag1(T2.TAG1_SET);
		t2.setTag2((byte) 0x01);
		// 6001FFFF
		t2.setCmd(0x01);

		CtrlData cd = new CtrlData();
		cd.setGM(GPIOs);
		byte[] date = cd.pack();
		t2.setParams(date);
		t2.setLength(date.length + 5);
		time.setGeneticT2(t2);
		this.setTimer(mInfo, time);
	}

	public void setTimer(ModuleInfo mInfo, int timerNo, DaysOfWeek dow,
			int hour, int min, boolean isLoop, PWM[] PWMs)
			throws ModuleException {
	}
	
	
	public byte[] sendDataToUart(String mac,byte[] data,boolean chanle)throws ModuleException {
		HashMap<String, byte[]> t2Req = new HashMap<String, byte[]>();
		///这个不对
		T2 t2 = new T2();
		if(chanle)
			t2.setTag2((byte) 0x04);
		else
			t2.setTag2((byte) 0x84);
			t2.setTag1(T2.TAG1_GET);
			t2.setData(data);
			t2Req.put(mac, t2.pack());
		System.out.println(HexBin.bytesToStringWithSpace(t2.pack()));
		HashMap<String, byte[]> rsp = mm.moduleCommSet(t2Req);
		return rsp.get(mac);
	}

	public void setTimer(ModuleInfo mInfo, int timerNo, DaysOfWeek dow,
			int hour, int min, boolean isLoop, ByteBuffer bf)
			throws ModuleException {
	}

	public static HashMap<Integer, GPIO> getStatulist(byte[] T2data) {
		HashMap<Integer, GPIO> GPIOStatusMap = new HashMap<Integer, GPIO>();
		for (int i = 0; i < T2data.length; i += 4) {
			GPIO gpio = new GPIO();
			gpio.setId((T2data[i]&0xFF) >> 4);
			gpio.setStatus((T2data[i + 1] & 0x01) == 0);
			GPIOStatusMap.put(gpio.getId(), gpio);
		}
		return GPIOStatusMap;
	}

	public static byte[] intToByteArray(int i) throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		out.writeInt(i);
		byte[] b = new byte[2];
		b = buf.toByteArray();
		out.close();
		buf.close();
		return Arrays.copyOfRange(b, 2, b.length);
	}

	public class CtrlData {
		HashMap<Integer, GPIO> gM = null;
		ByteBuffer data;

		public HashMap<Integer, GPIO> getGl() {
			return gM;
		}

		public void setGM(HashMap<Integer, GPIO> gM) {
			this.gM = gM;
		}

		public byte[] pack() {
			// TODO Auto-generated method stub
			data = ByteBuffer.allocate(4 * gM.size());
			Iterator<Entry<Integer, GPIO>> it = gM.entrySet().iterator();
			while (it.hasNext()) {
				GPIO g = it.next().getValue();
				if (!g.getStatus()) {
					data.put(HexBin.stringToBytes(Integer.toHexString(g.getId())+ "002FFFF"));
				} else {
					data.put(HexBin.stringToBytes(Integer.toHexString(g.getId()) + "001FFFF"));
				}
			}
			return data.array();
		}

		public HashMap<Integer, GPIO> unpack(byte[] cmd) {
			return getStatulist(cmd);
		}
	}

	public class Timerdata {
		HashMap<Integer, TimeInfo> TM = new HashMap<Integer, TimeInfo>();
		ByteBuffer data = ByteBuffer.allocate(512);

		public HashMap<Integer, TimeInfo> getTM() {
			return TM;
		}

		public void setTM(HashMap<Integer, TimeInfo> tm) {
			this.TM = tm;
		}

		public byte[] pack() {
			Iterator<Entry<Integer, TimeInfo>> it = TM.entrySet().iterator();
			while (it.hasNext()) {
				TimeInfo T = it.next().getValue();
				data.put(T.pack());
			}
			data.limit();
			return data.array();
		}

		public HashMap<Integer, TimeInfo> unpack(byte[] data) {
			TimeInfo T = new TimeInfo();
			return T.unpack(data);
		}
	}

	// ***************************************************************

	public boolean getStatusByMac(String mac) {
		ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
		return mi.getOnline() || (mi.getLocalIp() != null);
	}
	
	public int getOnLineMudleNum() throws ModuleException{
		ArrayList<ModuleInfo> ml = mm.getAllModules();
		int i = 0;
		Iterator<ModuleInfo> it = ml.iterator();
		while(it.hasNext()){
			ModuleInfo tmp = it.next();
			if(getStatusByMac(tmp.getMac())){
				i++;
			}
		}
		return i;
	}
}

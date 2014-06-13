package com.hf.module.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hf.lib.protocol.t1.Head1;
import com.hf.lib.protocol.t1.Head2;
import com.hf.lib.protocol.t1.T1Message;
import com.hf.lib.protocol.t1.local.LDiscovery;
import com.hf.lib.protocol.t1.t2.T2;
import com.hf.lib.util.AES;
import com.hf.lib.util.HexBin;
import com.hf.module.IEventListener;
import com.hf.module.IModuleManager;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.ModuleHelper;
import com.hf.module.impl.adaptor.AndroidAdaptor;
import com.hf.module.impl.adaptor.StandardJavaAdaptor;
import com.hf.module.info.CaptchaImageInfo;
import com.hf.module.info.CaptchaInfo;
import com.hf.module.info.GPIO;
import com.hf.module.info.KeyValueInfo;
import com.hf.module.info.ModuleInfo;
import com.hf.module.info.ModuleLogInfo;
import com.hf.module.info.ModuleLogInfoList;
import com.hf.module.info.OperLogInfo;
import com.hf.module.info.OperLogInfoList;
import com.hf.module.info.PrivilegeInfo;
import com.hf.module.info.UserInfo;
import com.hf.soundSmartLink.SoundFileBuilder;

public class ModuleManagerImpl implements IModuleManager {
	public final static int RESPONSE_FAIL = -1; // less than 0, it fails.
	public final static int RESPONSE_SUCCEED = 1; // more than 0, it succeeds.
	public final static int RESPONSE_SUCCEED_PART = 0; // is 0, it's partially
														// successful.

	private Boolean is_alive = true;
	private boolean isSmartlinking = false;
	private Timer timer;
	private Beattask beattsk;
	private Beattask assistbeattsk;

	private IPlatformAdaptor platformAdaptor = null;
	private ArrayList<IEventListener> eventListenerList = new ArrayList<IEventListener>();
	private ModuleHelper helper = null;

	private String SID = null;
	private String userId = null;

	private int HEADER_COUNT = 200;
	private int HEADER_PACKAGE_DELAY_TIME = 10;
	private int HEADER_CAPACITY = 76;

	private int CONTENT_COUNT = 5;
	private int CONTENT_PACKAGE_DELAY_TIME = 50;
	private int CONTENT_CHECKSUM_BEFORE_DELAY_TIME = 100;
	private int CONTENT_GROUP_DELAY_TIME = 500;
	protected int SMART_CONFIG_TIMEOUT = 30000;
	protected static final int DEVICE_COUNT_ONE = 1;
	protected static final int DEVICE_COUNT_MULTIPLE = -1;
	protected int deviceCountMode = DEVICE_COUNT_ONE;

	// protected String TIP_TIMEOUT;
	protected String TIP_WIFI_NOT_CONNECTED;
	protected String TIP_CONFIGURING_DEVICE;
	protected String TIP_DEVICE_CONFIG_SUCCESS;

	private Timer tachycardia;
	private DatagramSocket discovery;

	private SoundFileBuilder sfb = new SoundFileBuilder(ModuleConfig.appcontext);

	public ModuleManagerImpl() {
		super();
		boolean isStandardJava = false;

		if (isStandardJava) {
			this.setPlatformAdaptor(new StandardJavaAdaptor());
		} else {
			this.setPlatformAdaptor(new AndroidAdaptor());
		}

		this.helper = new ModuleHelper(this);
	}

	@Override
	public void initHttp() {
		// TODO Auto-generated method stub
		HttpProxy.getInstance().setServiceUrl(ModuleConfig.cloudServiceUrl);
		HttpProxy.getInstance().setSslServiceUrl(ModuleConfig.cloudServiceUrl);
		HttpProxy.getInstance().init();
	}

	@Override
	public void initialize() throws ModuleException {
		// TODO validated init information in ModuleConfig

		this.is_alive = true;
		try {
			// init http proxy
			HttpProxy.getInstance().setServiceUrl(ModuleConfig.cloudServiceUrl);
			HttpProxy.getInstance().setSslServiceUrl(
					ModuleConfig.cloudServiceUrl);
			HttpProxy.getInstance().init();
			LocalModuleInfoContainer.getInstance().load();
			// init discovery socket
			discovery = new DatagramSocket();
			// int eventListenPort = discovery.getLocalPort();

			// init event socket and thread
			// DatagramSocket eventReceiver = new
			// DatagramSocket(eventListenPort);
			Thread eventThread = new Thread(new EventThread(discovery));
			eventThread.setName("EventThread");
			eventThread.start();

			// init discovery thread
			timer = new Timer();
			beattsk = new Beattask(discovery);
			timer.schedule(beattsk, 0, ModuleConfig.pulseInterval);
			// Thread hbThread = new Thread(new HeatBeatThread(discovery));
			// hbThread.setName("HeatBeatThread");
			// hbThread.start();

		} catch (SocketException ex) {
			ex.printStackTrace();
			ModuleException mx = new ModuleException("init fail");
			throw mx;
		}
	}

	public ModuleHelper getHelper() {
		return this.helper;
	}

	class Beattask extends TimerTask {
		private DatagramSocket discoverySender = null;

		public Beattask(DatagramSocket discoverySender) {
			this.discoverySender = discoverySender;
		}

		public DatagramSocket getSocket() {
			return discoverySender;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("Beat----->alive");
			try {
				// new LDiscovery();
				// byte[] data = msg.pack();
				int[] cmd = { 0x01, 0x01, 0x01, 0x7E, 0x40, 0x40, 0x48, 0x65,
						0x6C, 0x6C, 0x6F, 0x2C, 0x54, 0x68, 0x69, 0x6E, 0x67,
						0x21, 0x2A, 0x2A, 0x23 };
				ByteBuffer bb = ByteBuffer.allocate(21);
				for (int i = 0; i < cmd.length; i++) {
					bb.put((byte) cmd[i]);
				}
				byte[] data = bb.array();
				DatagramPacket sendPacket = new DatagramPacket(data,
						data.length,
						InetAddress.getByName(ModuleConfig.broadcastIp),
						ModuleConfig.broadcastPort);
				discoverySender.send(sendPacket);

				LocalModuleInfoContainer.getInstance().checkAgingTime();

				// init cloud login thread
				if (!ModuleManagerImpl.this.isCloudChannelLive()) {
					try {
						ModuleManagerImpl.this.login();
					} catch (Exception ex) {
						ModuleManagerImpl.this.setSID(null);

						// locate worknode server url from cloud home.
						ModuleManagerImpl.this.locateCloudService();
					}
				}
			} catch (Throwable thr) {
				notifyLogin(false);
				thr.printStackTrace();
			}
		}
	}

	class HeatBeatThread implements Runnable {
		private DatagramSocket discoverySender = null;

		public HeatBeatThread(DatagramSocket discoverySender) {
			this.discoverySender = discoverySender;
		}

		@Override
		public void run() {

			while (is_alive) {
				System.out.println("Beat----->alive");
				try {
					new LDiscovery();
					// byte[] data = msg.pack();
					int[] cmd = { 0x01, 0x01, 0x01, 0x7E, 0x40, 0x40, 0x48,
							0x65, 0x6C, 0x6C, 0x6F, 0x2C, 0x54, 0x68, 0x69,
							0x6E, 0x67, 0x21, 0x2A, 0x2A, 0x23 };
					ByteBuffer bb = ByteBuffer.allocate(21);
					for (int i = 0; i < cmd.length; i++) {
						bb.put((byte) cmd[i]);
					}
					byte[] data = bb.array();
					DatagramPacket sendPacket = new DatagramPacket(data,
							data.length,
							InetAddress.getByName(ModuleConfig.broadcastIp),
							ModuleConfig.broadcastPort);
					discoverySender.send(sendPacket);

					LocalModuleInfoContainer.getInstance().checkAgingTime();

					// init cloud login thread
					if (!ModuleManagerImpl.this.isCloudChannelLive()) {
						try {
							ModuleManagerImpl.this.login();
						} catch (Exception ex) {
							ModuleManagerImpl.this.setSID(null);

							// locate worknode server url from cloud home.
							ModuleManagerImpl.this.locateCloudService();
						}
					}
					try {
						Thread.sleep(ModuleConfig.pulseInterval);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				} catch (Throwable thr) {
					thr.printStackTrace();
				}
			}
		}
	}

	class EventThread implements Runnable {
		private DatagramSocket eventReceiver = null;

		public EventThread(DatagramSocket eventReceiver) {
			this.eventReceiver = eventReceiver;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[ModuleConfig.maxTMsgPacketSize];

			while (is_alive) {
				try {
					DatagramPacket recvPacket = new DatagramPacket(buffer,
							buffer.length);
					eventReceiver.receive(recvPacket);

					byte[] l1bytes = Arrays.copyOfRange(buffer, 0,
							recvPacket.getLength());
					if (l1bytes.length < 0) {
						continue;
					}
					//Log.e("recv", HexBin.bytesToStringWithSpace(l1bytes));
				
					Head1 h1 = new Head1();
					h1.unpack(l1bytes);
					String mac = h1.getMacString();
					// 5.31 change
					T1Message msg = new T1Message();
					ModuleInfo mi = new ModuleInfo();
					if(h1.getId() != T1Message.ID_LOCAL_DISCOVERY){
						mi = LocalModuleInfoContainer.getInstance().get(mac);
						if(mi == null){
							continue;
						}else{
							msg.setKey(mi.getLocalKey().getBytes());
							msg.unpack(l1bytes);
						}
					}
					
					//end
					
					// msg.setKey(AES.DEFAULT_KEY_128.getBytes());
					// msg.unpack(l1bytes);
//
//					ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(
//							mac);
//
//					if (mi == null) {
//						if (h1.isRegistered()) {
//							continue;
//						}
//						mi = new ModuleInfo();
//						msg.setKey(AES.DEFAULT_KEY_128.getBytes());
//						msg.unpack(l1bytes);
//					} else {
//						msg.setKey(mi.getLocalKey().getBytes());
//						msg.unpack(l1bytes);
//					}

					if (h1.getId() == T1Message.ID_LOCAL_DISCOVERY) {
						if(h1.isRegistered()){
							mi = LocalModuleInfoContainer.getInstance().get(mac);
							if(mi == null){
								continue;
							}else{
								msg.setKey(mi.getLocalKey().getBytes());
								msg.unpack(l1bytes);
							}
						}else{
							msg.setKey(AES.DEFAULT_KEY_128.getBytes());
							msg.unpack(l1bytes);
						}

						mi.setMac(mac);
						mi.setLocalIp(recvPacket.getAddress().getHostAddress());
						mi.setLastTimestamp(new java.util.Date().getTime());
						mi.setFactoryId(msg.getHead2().getComponyCode());
						mi.setType(msg.getHead2().getModuleCode());
						
						if(!h1.isRegistered()){
							ModuleManagerImpl.this.notifyOnNewDevEvent(mi);
						}else{
							LocalModuleInfoContainer.getInstance().put(mac, mi);
						}
					} else if (msg.getHead1().getId() == T1Message.ID_COMMON_EVENT) {
						byte[] payload = msg.getPayload();
						T2 t2 = new T2();
						try {
							t2.unpack(payload);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
						if (t2.getTag1() == T2.TAG1_EVENT) {
							if (t2.getTag2() == 0x01) {
								// GPIO event
								HashMap<Integer, GPIO> gpioMap = getHelper().getStatulist(t2.getData());

								ModuleManagerImpl.this.notifyGPIOEvent(mac,
										gpioMap);

							} else if (t2.getTag2() == 0x02) {
								// timer event
								ModuleManagerImpl.this.notifyTimerEvent(mac,
										t2.getData());
							} else if ((t2.getTag2() & 0x0f) == 0x03) {
								// ENTM
								ModuleManagerImpl.this.notifyUARTEvent(mac,
										t2.getData(), t2.getTag2() == 0x03);

							} else {
								System.out.println("TAG1_EVENT3");
							}
						}
					} else {
						ModuleManagerImpl.this.notifyEvent(mac, l1bytes);
					}

				} catch (Throwable thr) {
					thr.printStackTrace();
				}
			}
		}
	}

	/*
	 * when add or find new Dev use This will be better
	 */
	@Override
	public void tachycardia() {
		tachycardia = new Timer();
		assistbeattsk = new Beattask(discovery);
		tachycardia.schedule(assistbeattsk, 0, 1000);
	}

	/*
	 * when stop add or find new Dev make beat normall
	 */
	@Override
	public void beatnormall() {
		assistbeattsk.cancel();
		tachycardia.cancel();
	}

	public boolean isCloudChannelLive() {
		if (this.SID == null) {
			return false;
		} else {
			return true;
		}
	}

	public void registerEventListener(IEventListener listener) {

		this.eventListenerList.add(listener);
	}

	public void unregisterEventListener(IEventListener listener) {
		this.eventListenerList.remove(listener);
	}

	public void clearListeners() {
		this.eventListenerList.clear();
	}

	public void notifyEvent(String mac, byte[] data) {
		Iterator<IEventListener> iter = this.eventListenerList.iterator();

		while (iter.hasNext()) {
			IEventListener listener = (IEventListener) iter.next();
			listener.onEvent(mac, data);
		}
	}

	public void notifyGPIOEvent(String mac, HashMap<Integer, GPIO> GM) {
		Iterator<IEventListener> iter = this.eventListenerList.iterator();
		while (iter.hasNext()) {
			IEventListener listener = (IEventListener) iter.next();
			listener.onGPIOEvent(mac, GM);
		}
	}

	public void notifyTimerEvent(String mac, byte[] data) {
		Iterator<IEventListener> iter = this.eventListenerList.iterator();

		while (iter.hasNext()) {
			IEventListener listener = (IEventListener) iter.next();
			listener.onTimerEvent(mac, data);
		}
	}

	public void notifyOnNewDevEvent(ModuleInfo mi) {
		Iterator<IEventListener> iter = this.eventListenerList.iterator();

		while (iter.hasNext()) {
			IEventListener listener = (IEventListener) iter.next();
			listener.onNewDevFind(mi);
		}
	}

	public void notifyLogin(boolean loginstat) {
		Iterator<IEventListener> iter = this.eventListenerList.iterator();

		while (iter.hasNext()) {
			IEventListener listener = (IEventListener) iter.next();
			listener.onCloudLogin(loginstat);
		}
	}

	public void notifyLogout() {
		Iterator<IEventListener> iter = this.eventListenerList.iterator();

		while (iter.hasNext()) {
			IEventListener listener = (IEventListener) iter.next();
			listener.onCloudLogout();
		}
	}

	public void notifyUARTEvent(String mac, byte[] data, boolean chanle) {
		Iterator<IEventListener> iter = this.eventListenerList.iterator();
		while (iter.hasNext()) {
			IEventListener listener = (IEventListener) iter.next();
			listener.onUARTEvent(mac, data, chanle);
		}
	}

	// *** Basic Methods
	// ***********************************************************************************************

	public HashMap<String, byte[]> moduleCommGet(HashMap<String, byte[]> t2Req)
			throws ModuleException {
		final HashMap<String, byte[]> result = new HashMap<String, byte[]>();

		final HashMap<String, byte[]> localReq = new HashMap<String, byte[]>();
		final HashMap<String, byte[]> cloudReq = new HashMap<String, byte[]>();

		Iterator<String> tmpIter = t2Req.keySet().iterator();
		while (tmpIter.hasNext()) {
			String mac = tmpIter.next();

			ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
			if (mi.getLocalIp() == null) {
				cloudReq.put(mac, t2Req.get(mac));
			} else {
				localReq.put(mac, getAllMsg(mi, 1, t2Req.get(mac)));
			}
		}

		if (localReq.size() > 0) {
			localModuleCommSetGet(localReq, result);
		}

		if ((cloudReq.size() > 0) && this.isCloudChannelLive()) {
			cloudModuleCommSetGet(t2Req, result, false);
		}

		return result;
	}

	public byte[] getAllMsg(ModuleInfo mi, int sn, byte[] payload)
			throws ModuleException {
		// Prepare request
		try {
			T1Message l1Req = new T1Message();

			l1Req.setHead1(new Head1());
			l1Req.setHead2(new Head2());
			l1Req.getHead1().setId(T1Message.ID_COMMON_SET_GET);
			l1Req.getHead1().setNeedReplyOrSuccess(true);
			l1Req.getHead1().setReply(false);
			l1Req.getHead1().setExistL2(true);
			l1Req.getHead1().setTempKey(false);
			l1Req.getHead1().setFromWIFI(false);

			l1Req.getHead1().setMac(mi.getMac());
			l1Req.setKey(mi.getLocalKey().getBytes("UTF-8"));
			l1Req.getHead1().setEncrypted(true);
			l1Req.getHead2().setVersion(01);
			l1Req.getHead2().setReserved(0);
			l1Req.getHead2().setSn(sn); // get a next serial number
			l1Req.getHead2().setComponyCode(mi.getFactoryId());
			l1Req.getHead2().setModuleCode(mi.getType());

			l1Req.setPayload(payload);
			byte[] l1Bytes = l1Req.pack();
			return l1Bytes;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_RUNTIME_GENERAL_EXCEPTION,
					"Create T1Message error.");
		}
	}

	public HashMap<String, byte[]> moduleCommSet(HashMap<String, byte[]> t2Req)
			throws ModuleException {
		final HashMap<String, byte[]> result = new HashMap<String, byte[]>();

		final HashMap<String, byte[]> localReq = new HashMap<String, byte[]>();
		final HashMap<String, byte[]> cloudReq = new HashMap<String, byte[]>();

		Iterator<String> tmpIter = t2Req.keySet().iterator();
		while (tmpIter.hasNext()) {
			String mac = tmpIter.next();

			ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
			if (mi.getLocalIp() == null) {
				cloudReq.put(mac, t2Req.get(mac));
			} else {
				localReq.put(mac, getAllMsg(mi, 1, t2Req.get(mac)));
			}
		}

		if (localReq.size() > 0) {
			localModuleCommSetGet(localReq, result);
		}

		if ((cloudReq.size() > 0) && this.isCloudChannelLive()) {

			cloudModuleCommSetGet(t2Req, result, true);

		}

		return result;
	}

	private void localModuleCommSetGet(HashMap<String, byte[]> t2Req,
			HashMap<String, byte[]> result) throws ModuleException {
		Iterator<String> iter = t2Req.keySet().iterator();
		while (iter.hasNext()) {
			String mac = iter.next();
			byte[] msg = t2Req.get(mac);

			ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
			byte[] recv = UdpProxy.getInstance().send(msg, mi.getLocalIp(),
					mi.getLocalKey().getBytes());
			result.put(mac, recv);
		}
	}

	private void cloudModuleCommSetGet(HashMap<String, byte[]> t2Req,
			final HashMap<String, byte[]> result, boolean isSet)
			throws ModuleException {
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		if (isSet) {
			joReq.addProperty("CID", 20031);
		} else {
			joReq.addProperty("CID", 20021);
		}

		joReq.addProperty("SID", this.SID);

		Iterator<String> iter = t2Req.keySet().iterator();
		while (iter.hasNext()) {
			String mac = iter.next();
			byte[] msg = t2Req.get(mac);
			pl.addProperty(mac, HexBin.bytesToString(msg));
		}

		String req = joReq.toString();

		String rsp = null;
		rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();
		int rs = jo.get("RC").getAsInt();
		if (rs == RESPONSE_SUCCEED) {

			JsonObject joPl = jo.get("PL").getAsJsonObject();
			Set<Entry<String, JsonElement>> tmpEntry = joPl.entrySet();
			Iterator<Entry<String, JsonElement>> tmpItor = tmpEntry.iterator();
			while (tmpItor.hasNext()) {
				Entry<String, JsonElement> item = tmpItor.next();
				String mac = item.getKey();
				String msg = item.getValue().getAsString();
				result.put(mac, HexBin.stringToBytes(msg));
			}
		}
	}

	public HashMap<String, HashMap<String, byte[]>> moduleCommEvent(
			HashMap<String, ArrayList<String>> t2Req) throws ModuleException {
		HashMap<String, HashMap<String, byte[]>> result = new HashMap<String, HashMap<String, byte[]>>();

		if (this.isCloudChannelLive()) {
			JsonObject joReq = new JsonObject();
			JsonObject pl = new JsonObject();
			joReq.add("PL", pl);
			joReq.addProperty("CID", 20011);
			joReq.addProperty("SID", this.SID);

			Iterator<String> iter = t2Req.keySet().iterator();
			while (iter.hasNext()) {
				String mac = iter.next();
				ArrayList<String> regList = t2Req.get(mac);
				String strJSon = new GsonBuilder().disableHtmlEscaping()
						.create().toJson(regList);
				pl.addProperty(mac, strJSon);
			}

			String req = joReq.toString();

			String rsp = HttpProxy.getInstance().callHttpsPost(req);
			JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

			int rs = jo.get("RC").getAsInt();
			if (rs == RESPONSE_SUCCEED) {

				JsonObject joPl = jo.get("PL").getAsJsonObject();
				Set<Entry<String, JsonElement>> tmpEntry = joPl.entrySet();
				Iterator<Entry<String, JsonElement>> tmpItor = tmpEntry
						.iterator();
				while (tmpItor.hasNext()) {
					Entry<String, JsonElement> item = tmpItor.next();
					String mac = item.getKey();
					JsonObject kv = item.getValue().getAsJsonObject();

					Set<Entry<String, JsonElement>> tmpEntry1 = kv.entrySet();
					HashMap<String, byte[]> kvMap = new HashMap<String, byte[]>();

					// convert to t2 for notify
					T2[] t2Array = new T2[kvMap.size()];
					int t2Index = 0;
					Iterator<Entry<String, JsonElement>> tmpItor1 = tmpEntry1
							.iterator();
					while (tmpItor1.hasNext()) {
						Entry<String, JsonElement> item1 = tmpItor1.next();
						String key = item1.getKey();
						String value = item1.getValue().getAsString();
						byte[] bValue = HexBin.stringToBytes(value);
						kvMap.put(key, bValue);

						T2 t2 = new T2();
						t2.setTag1(T2.TAG1_EVENT);
						t2.setTag2(HexBin.stringToBytes(key)[0]);
						t2.setLength(bValue.length);
						t2.setData(bValue);
						t2Array[t2Index] = t2;
						t2Index++;

					}
					result.put(mac, kvMap);

					ModuleManagerImpl.this.notifyEvent(mac,
							T2.t2ArrayPack(t2Array));
				}
			} else {
				throw new ModuleException(rs);
			}
		} else {
			new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}
		return result;
	}

	private void moduleInfo2Json(ModuleInfo info, JsonObject pl) {
		if (info.getId() != null)
			pl.addProperty("moduleId", info.getId());
		if (info.getAccessKey() != null)
			pl.addProperty("accessKey", info.getAccessKey());
		if (info.getName() != null)
			pl.addProperty("name", info.getName());
		if (info.getDesc() != null)
			pl.addProperty("desc", info.getDesc());
		if (info.getMac() != null)
			pl.addProperty("mac", info.getMac());
		if (info.getLocalKey() != null)
			pl.addProperty("localKey", info.getLocalKey());
		if (info.getNeedRemoteControl() != null)
			pl.addProperty("needRemoteControl", info.getNeedRemoteControl());
		if (info.getSerialNo() != null)
			pl.addProperty("serialNo", info.getSerialNo());
		if (info.getFactoryId() != null)
			pl.addProperty("factoryId", info.getFactoryId());
		if (info.getType() != null)
			pl.addProperty("type", info.getType());
		if (info.getHardwareVer() != null)
			pl.addProperty("hardwareVer", info.getHardwareVer());
		if (info.getSoftwareVer() != null)
			pl.addProperty("softwareVer", info.getSoftwareVer());
		if (info.getTempKey() != null)
			pl.addProperty("tempKey", info.getTempKey());
		if (info.getBindTime() != null)
			pl.addProperty("bindTime", info.getBindTime());
		if (info.getTotalOnlineTime() != null)
			pl.addProperty("totalOnlineTime", info.getTotalOnlineTime());
		if (info.getInternetIp() != null)
			pl.addProperty("internetIp", info.getInternetIp());
		if (info.getGpsLat() != null)
			pl.addProperty("gpsLat", info.getGpsLat());
		if (info.getGpsLng() != null)
			pl.addProperty("gpsLng", info.getGpsLng());
		if (info.getCountry() != null)
			pl.addProperty("country", info.getCountry());
		if (info.getState() != null)
			pl.addProperty("state", info.getState());
		if (info.getCity() != null)
			pl.addProperty("city", info.getCity());
		if (info.getDistrict() != null)
			pl.addProperty("district", info.getDistrict());
		if (info.getOnline() != null)
			pl.addProperty("online", info.getOnline());
	}

	private void json2ModuleInfo(JsonObject pl, ModuleInfo info) {
		if (pl.get("moduleId") != null)
			info.setId(pl.get("moduleId").getAsString());
		if (pl.get("name") != null)
			info.setName(pl.get("name").getAsString());
		if (pl.get("desc") != null)
			info.setDesc(pl.get("desc").getAsString());
		if (pl.get("mac") != null)
			info.setMac(pl.get("mac").getAsString());
		if (pl.get("localKey") != null)
			info.setLocalKey(pl.get("localKey").getAsString());
		if (pl.get("needRemoteControl") != null)
			info.setNeedRemoteControl(pl.get("needRemoteControl")
					.getAsBoolean());
		if (pl.get("serialNo") != null)
			info.setSerialNo(pl.get("serialNo").getAsString());
		if (pl.get("factoryId") != null)
			info.setFactoryId(pl.get("factoryId").getAsInt());
		if (pl.get("type") != null)
			info.setType(pl.get("type").getAsInt());
		if (pl.get("hardwareVer") != null)
			info.setHardwareVer(pl.get("hardwareVer").getAsString());
		if (pl.get("softwareVer") != null)
			info.setSoftwareVer(pl.get("softwareVer").getAsString());
		if (pl.get("tempKey") != null)
			info.setTempKey(pl.get("tempKey").getAsString());
		if (pl.get("bindTime") != null)
			info.setBindTime(pl.get("bindTime").getAsString());
		if (pl.get("totalOnlineTime") != null)
			info.setTotalOnlineTime(pl.get("totalOnlineTime").getAsLong());
		if (pl.get("internetIp") != null)
			info.setInternetIp(pl.get("internetIp").getAsString());
		if (pl.get("gpsLat") != null)
			info.setGpsLat(pl.get("gpsLat").getAsDouble());
		if (pl.get("gpsLng") != null)
			info.setGpsLng(pl.get("gpsLng").getAsDouble());
		if (pl.get("country") != null)
			info.setCountry(pl.get("country").getAsString());
		if (pl.get("state") != null)
			info.setState(pl.get("state").getAsString());
		if (pl.get("city") != null)
			info.setCity(pl.get("city").getAsString());
		if (pl.get("district") != null)
			info.setDistrict(pl.get("district").getAsString());
		if (pl.get("online") != null)
			info.setOnline(pl.get("online").getAsBoolean());
	}

	@Override
	public ModuleInfo setModule(ModuleInfo moduleInfo) throws ModuleException {
		if (!this.isCloudChannelLive()) {
			new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}
		// {'PL':{'accessKey':'402880e942d23e030142d23e10a10000','mac' :
		// 'FF0000000010','type':1, 'name' : 'hello kitty
		// module'},'CID':30011,'SID':'6481BEDD27AB4BA79AD57CE06E6B7E23'}
		moduleInfo.setAccessKey(ModuleConfig.accessKey);
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		joReq.addProperty("CID", 30011);
		joReq.addProperty("SID", this.SID);
		moduleInfo2Json(moduleInfo, pl);
		// pl.remove("moduleId");

		String req = joReq.toString();
		String rsp = null;
		rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();

		if (rs == RESPONSE_SUCCEED) {
			JsonObject rspPL = jo.get("PL").getAsJsonObject();
			ModuleInfo rspInfo = new ModuleInfo();
			json2ModuleInfo(rspPL, rspInfo);

			rspInfo.setLocalIp(moduleInfo.getLocalIp());
			LocalModuleInfoContainer.getInstance().put(rspInfo.getMac(),
					rspInfo);

			return rspInfo;
		} else {
			throw new ModuleException(rs);
		}
	}

	@Override
	public ModuleInfo getModuel(String moduleId) throws ModuleException {
		if (this.isCloudChannelLive()) {
			String reqTemplate = "{'PL':{'moduleId':'#moduleId#'},'CID':30031,'SID':'#SID#'}";
			String req = reqTemplate.replaceFirst("#moduleId#", moduleId)
					.replaceFirst("#SID#", this.SID);

			String rsp = HttpProxy.getInstance().callHttpsPost(req);
			JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

			int rs = jo.get("RC").getAsInt();
			if (rs == RESPONSE_SUCCEED) {
				JsonObject rspPL = jo.get("PL").getAsJsonObject();
				ModuleInfo rspInfo = new ModuleInfo();
				json2ModuleInfo(rspPL, rspInfo);

				// update local
				ModuleInfo lmi = LocalModuleInfoContainer.getInstance().get(
						rspInfo.getMac());
				if (lmi != null) {
					rspInfo.setLocalIp(lmi.getLocalIp());
				}
				LocalModuleInfoContainer.getInstance().put(rspInfo.getMac(),
						rspInfo);

				return rspInfo;
			} else {
				throw new ModuleException(rs);
			}
		} else {
			return LocalModuleInfoContainer.getInstance().getByModuleId(
					moduleId);
		}

	}

	@Override
	public ModuleInfo getModuleByMac(String mac) throws ModuleException {
		ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
		return getModuel(mi.getId());
	}

	@Override
	public void deleteModule(String mac) throws ModuleException {
		if (!this.isCloudChannelLive()) {
			new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}
		ModuleInfo mi = LocalModuleInfoContainer.getInstance().get(mac);
		//*******
		if(mi == null){
			throw new ModuleException("");
		}
		if(mi.getId() == null){
			throw new ModuleException("");
		}
		//***
		String reqTemplate = "{'PL':{'moduleId':'#moduleId#'},'CID':30021,'SID':'#SID#'}";
		String req = reqTemplate.replaceFirst("#moduleId#", mi.getId())
				.replaceFirst("#SID#", this.SID);

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();
		int rs = jo.get("RC").getAsInt();
		if (rs != RESPONSE_SUCCEED) {
			throw new ModuleException(rs);
		} else {
			try {
				LocalModuleInfoContainer.getInstance().remove(mac);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public ArrayList<ModuleInfo> getAllModules() throws ModuleException {
		if (this.isCloudChannelLive()) {
			String reqTemplate = "{'CID':30051,'SID':'#SID#'}";
			String req = reqTemplate.replaceFirst("#SID#", this.SID);
			String rsp = null;
			rsp = HttpProxy.getInstance().callHttpsPost(req);
			JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();
			int rs = jo.get("RC").getAsInt();
			if (rs == RESPONSE_SUCCEED) {
				ArrayList<ModuleInfo> result = new ArrayList<ModuleInfo>();
				JsonArray ja = null;
				try {
					ja = jo.get("PL").getAsJsonArray();
				} catch (Exception e) {
					e.printStackTrace();
					return result;
				}
				// clear all Module in local content file
				LocalModuleInfoContainer.getInstance().removeAll();
				for (int i = 0; i < ja.size(); i++) {
					JsonObject row = ja.get(i).getAsJsonObject();
					// System.out.println(row);
					ModuleInfo info = new ModuleInfo();
					json2ModuleInfo(row, info);
					result.add(info);

					// update local
					ModuleInfo lmi = LocalModuleInfoContainer.getInstance()
							.get(info.getMac());
					if (lmi != null) {
						info.setLocalIp(lmi.getLocalIp());
					}
					LocalModuleInfoContainer.getInstance().put(info.getMac(),
							info);
				}

				return result;
			} else {
				throw new ModuleException(rs);
			}
		} else {
			return LocalModuleInfoContainer.getInstance().getAll();
		}
	}

	@Override
	public void connectModuleToWIFI(String RouterSSID, String RouterPSWD) {
		isSmartlinking = true;
		int count = 1;
		byte[] header = this.getBytes(HEADER_CAPACITY);
		while (count <= HEADER_COUNT && isSmartlinking) {
			UdpProxy.getInstance().broadCast(header);
			try {
				Thread.sleep(HEADER_PACKAGE_DELAY_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count++;
		}
		String pwd = RouterPSWD;
		int[] content = new int[pwd.length() + 2];

		content[0] = 89;
		int j = 1;
		for (int i = 0; i < pwd.length(); i++) {
			content[j] = pwd.charAt(i) + 76;
			j++;
		}
		content[content.length - 1] = 86;

		count = 1;
		while (count <= CONTENT_COUNT && isSmartlinking) {
			for (int i = 0; i < content.length; i++) {
				// JCTIP ver2 start end checksum send 3 time;
				int _count = 1;
				if (i == 0 || i == content.length - 1) {
					_count = 3;
				}
				int t = 1;
				while (t <= _count) {
					UdpProxy.getInstance().broadCast(getBytes(content[i]));
					if (i != content.length) {
						try {
							Thread.sleep(CONTENT_PACKAGE_DELAY_TIME);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					t++;
				}
				// mConfigBroadUdp.send(getBytes(content[i]));

				if (i != content.length) {
					try {
						Thread.sleep(CONTENT_PACKAGE_DELAY_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(CONTENT_CHECKSUM_BEFORE_DELAY_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// int checkLength = pwd.length() * 30 + 76;
			// JCTIP ver1
			int checkLength = pwd.length() + 256 + 76;

			// JCTIP ver2
			int t = 1;
			while (t <= 3 && isSmartlinking) {
				UdpProxy.getInstance().broadCast(getBytes(checkLength));
				if (t < 3) {
					try {
						Thread.sleep(CONTENT_PACKAGE_DELAY_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				t++;
			}
			// mConfigBroadUdp.send(getBytes(checkLength));

			try {
				Thread.sleep(CONTENT_GROUP_DELAY_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count++;
		}
		System.out.println("_____SMART_LINK_END____");
		isSmartlinking = false;
	}

	@Override
	public void stopSmartlink() {
		isSmartlinking = false;
	}

	@Override
	public void locateCloudService() throws ModuleException {
		// {'PL':{'key':'demo'},'SN':13714145,'CID':89011}
		// ->
		// {"PL":{"service":"https://localhost/usvc/"},"RS":1,"SN":13714145,"CID":80012}
		String reqTemplate = "{'PL':{'key':'#key#'},'SN':13714145,'CID':89011} ";

		String req = reqTemplate.replaceFirst("#key#",
				ModuleConfig.cloudUserName);

		String rsp = HttpProxy.getInstance().callHttpsPost(req,
				ModuleConfig.cloudHomeServiceUrl);
		System.out.println(rsp);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();
		int rc = jo.get("RC").getAsInt();
		if (rc == RESPONSE_SUCCEED) {
			ModuleConfig.cloudServiceUrl = jo.get("PL").getAsJsonObject()
					.get("service").getAsString();
		} else {
			throw new ModuleException(rc);
		}

	}

	@Override
	public void login() throws ModuleException {
		try {
			String userName = ModuleConfig.cloudUserName;
			String password = ModuleConfig.cloudPassword;
			String reqTemplate = "{'PL':{'accessKey':'#accessKey#','userName':'#userName#','password':'#password#','mac':'#mac#'},'CID':10011}";

			String mac = this.platformAdaptor.getPhoneMac();
			String req = reqTemplate
					.replaceFirst("#accessKey#", ModuleConfig.accessKey)
					.replaceFirst("#userName#", userName)
					.replaceFirst("#password#", password)
					.replaceFirst("#mac#", mac);
			String rsp = HttpProxy.getInstance().callHttpsPost(req);
			JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

			int rs = jo.get("RC").getAsInt();
			if (rs == RESPONSE_SUCCEED) {
				this.SID = jo.get("SID").getAsString();
				this.userId = jo.get("PL").getAsJsonObject().get("userId")
						.getAsString();
				this.notifyLogin(true);
			} else {
				this.notifyLogin(false);
				throw new ModuleException(rs);
			}
		} catch (ModuleException e) {
			this.notifyLogin(false);
			throw e;
		}
	}

	@Override
	public HashMap<Integer, PrivilegeInfo> getPrivilege()
			throws ModuleException {
		if (!this.isCloudChannelLive()) {
			new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}

		String reqTemplate = "{'CID':10101,'SID':'#SID#'} ";
		String req = reqTemplate.replaceFirst("#SID#", this.SID);

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs == RESPONSE_SUCCEED) {
			HashMap<Integer, PrivilegeInfo> result = new HashMap<Integer, PrivilegeInfo>();
			JsonArray ja = jo.get("PL").getAsJsonArray();
			for (int i = 0; i < ja.size(); i++) {
				JsonObject row = ja.get(i).getAsJsonObject();
				Integer cid = row.get("cid").getAsInt();
				String name = row.get("name").getAsString();
				String desc = row.get("desc").getAsString();
				PrivilegeInfo pi = new PrivilegeInfo();
				pi.setCid(cid);
				pi.setName(name);
				pi.setDesc(desc);

				result.put(cid, pi);
			}

			return result;
		} else {
			throw new ModuleException(rs);
		}
	}

	@Override
	public void logout() throws ModuleException {
		if (!this.isCloudChannelLive()) {
			throw new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}

		String reqTemplate = "{'CID':10021,'SID':'#SID#'}";
		String req = reqTemplate.replaceFirst("#SID#", this.SID);

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs == RESPONSE_SUCCEED) {
			this.SID = null;
			this.userId = null;
			this.notifyLogout();
		} else {
			throw new ModuleException(rs);
		}
	}

	@Override
	public CaptchaInfo captcha(String receiverAddress, int receiverType)
			throws ModuleException {
		// {'PL':{'email':'admin@hifly.com','sms':'15801994642'},'CID':10031}
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);

		joReq.addProperty("CID", 10031);
		if (receiverType == IModuleManager.RECEIVER_TYPE_SMS) {
			pl.addProperty("sms", receiverAddress);
		} else if (receiverType == IModuleManager.RECEIVER_TYPE_EMAIL) {
			pl.addProperty("email", receiverAddress);
		} else {
			pl.addProperty("sms", receiverAddress);
		}
		String req = joReq.toString();

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs == RESPONSE_SUCCEED) {
			JsonObject rspPL = jo.get("PL").getAsJsonObject();
			CaptchaInfo rspInfo = new CaptchaInfo();

			String captchaToken = rspPL.get("captchaToken").getAsString();
			int agingTime = rspPL.get("agingTime").getAsInt();
			rspInfo.setCaptchaToken(captchaToken);
			rspInfo.setAgingTime(agingTime);
			return rspInfo;
		} else {
			throw new ModuleException(rs);
		}
	}

	@Override
	public CaptchaImageInfo captchaImage() throws ModuleException {
		// {'CID':10041}
		JsonObject joReq = new JsonObject();
		joReq.addProperty("CID", 10051);

		String req = joReq.toString();

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();
		int rs = jo.get("RC").getAsInt();
		if (rs == RESPONSE_SUCCEED) {
			JsonObject rspPL = jo.get("PL").getAsJsonObject();
			CaptchaImageInfo rspInfo = new CaptchaImageInfo();

			String captchaToken = rspPL.get("captchaToken").getAsString();
			int agingTime = rspPL.get("agingTime").getAsInt();
			String captchaStubImage = rspPL.get("captchaStubImage")
					.getAsString();
			rspInfo.setCaptchaToken(captchaToken);
			rspInfo.setAgingTime(agingTime);
			rspInfo.setCaptchaStubImage(captchaStubImage);

			return rspInfo;
		} else {
			throw new ModuleException(rs);
		}
	}

	@Override
	public void registerUser(UserInfo info, String captcha)
			throws ModuleException {
		// {'PL':{'accessKey':'402880e942d23e030142d23e10a10000','captcha':'0845684408',
		// 'displayName':'Neo','userName':'liuliu@h.com','password':'liuliu','cellPhone':'1850938275','email':'li@hi-flying.com','idNumber':'210106983702082716'},'CID':10201}
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		joReq.addProperty("CID", 10201);

		if (captcha != null)
			pl.addProperty("captcha", captcha);
		pl.addProperty("accessKey", ModuleConfig.accessKey);
		if (info.getDisplayName() != null)
			pl.addProperty("displayName", info.getDisplayName());
		if (info.getUserName() != null)
			pl.addProperty("userName", info.getUserName());
		if (info.getPassword() != null)
			pl.addProperty("password", info.getPassword());
		if (info.getCellPhone() != null)
			pl.addProperty("cellPhone", info.getCellPhone());
		if (info.getEmail() != null)
			pl.addProperty("email", info.getEmail());
		if (info.getIdNumber() != null)
			pl.addProperty("idNumber", info.getIdNumber());

		String req = joReq.toString();
		System.out.println(req);
		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		System.out.println(rsp);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();
		int rs = jo.get("RC").getAsInt();

		if (rs == RESPONSE_SUCCEED) {
			JsonObject rspPL = jo.get("PL").getAsJsonObject();
			this.userId = rspPL.get("userId").getAsString();
		} else {
			throw new ModuleException(rs);
		}
	}

	@Override
	public void setUser(UserInfo info) throws ModuleException {
		if (!this.isCloudChannelLive()) {
			new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}

		// {'PL':{'displayName':'hello'},
		// 'CID':10221,'SID':'A9D42935158748EFB38BF7A8115B773A'}
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);

		joReq.addProperty("CID", 10221);
		joReq.addProperty("SID", this.SID);

		if (info.getDisplayName() != null)
			pl.addProperty("displayName", info.getDisplayName());
		if (info.getUserName() != null)
			pl.addProperty("userName", info.getUserName());
		if (info.getPassword() != null)
			pl.addProperty("password", info.getPassword());
		if (info.getCellPhone() != null)
			pl.addProperty("cellPhone", info.getCellPhone());
		if (info.getEmail() != null)
			pl.addProperty("email", info.getEmail());
		if (info.getIdNumber() != null)
			pl.addProperty("idNumber", info.getIdNumber());

		String req = joReq.toString();

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs != RESPONSE_SUCCEED) {
			throw new ModuleException(rs);
		}
	}

	@Override
	public UserInfo getUser() throws ModuleException {
		if (!this.isCloudChannelLive()) {
			new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}
		String reqTemplate = "{'CID':10231,'SID':'#SID#'} ";
		String req = reqTemplate.replaceFirst("#SID#", this.SID);

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs == RESPONSE_SUCCEED) {

			JsonObject joRsp = jo.get("PL").getAsJsonObject();
			UserInfo result = new UserInfo();

			if (joRsp.get("id") != null)
				result.setId(joRsp.get("id").getAsString());
			if (joRsp.get("displayName") != null) {
				result.setDisplayName(joRsp.get("displayName").getAsString());
				ModuleConfig.cloudUserDisPlayName = joRsp.get("displayName")
						.getAsString();
			}
			if (joRsp.get("userName") != null)
				result.setUserName(joRsp.get("userName").getAsString());
			if (joRsp.get("cellPhone") != null) {
				result.setCellPhone(joRsp.get("cellPhone").getAsString());
				ModuleConfig.cloudUserPhone = joRsp.get("cellPhone")
						.getAsString();
			}
			if (joRsp.get("email") != null) {
				result.setEmail(joRsp.get("email").getAsString());
				ModuleConfig.cloudUserEmail = joRsp.get("email").getAsString();
			}
			if (joRsp.get("idNumber") != null)
				result.setIdNumber(joRsp.get("idNumber").getAsString());
			if (joRsp.get("createTime") != null)
				result.setCreateTime(joRsp.get("createTime").getAsString());

			return result;
		} else {
			throw new ModuleException(rs);
		}
	}

	@Override
	public void deleteUser() throws ModuleException {
		if (!this.isCloudChannelLive()) {
			new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}
		// {'PL':{'userId':'2c9df0dc429dbe1b01429dbe28e40001'},
		// 'CID':10211,'SID':'A9D42935158748EFB38BF7A8115B773A'}
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		joReq.addProperty("CID", 10211);
		joReq.addProperty("SID", this.SID);
		pl.addProperty("userId", this.userId);

		String req = joReq.toString();

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs != RESPONSE_SUCCEED) {
			throw new ModuleException(rs);
		}

	}

	@Override
	public void changePassword(String oldPwd, String newPwd)
			throws ModuleException {
		if (!this.isCloudChannelLive()) {
			new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}

		// {'PL':{'oldPwd':'demo','newPwd':'54321'},'CID':10241,'SID':'A52909A826884228BCF1CEE1B76E85A6'}
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		joReq.addProperty("CID", 10241);
		joReq.addProperty("SID", this.SID);
		pl.addProperty("oldPwd", oldPwd);
		pl.addProperty("newPwd", newPwd);

		String req = joReq.toString();

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs != RESPONSE_SUCCEED) {
			throw new ModuleException(rs);
		}
	}

	@Override
	public void retrievePassword(String receiverAddress, int receiverType)
			throws ModuleException {
		// {'PL':{'sms':'15801994642'},'CID':10041}
		// {'PL':{'email':'noOne@hi-flying.com'},'CID':10041}
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);

		joReq.addProperty("CID", 10041);
		if (receiverType == IModuleManager.RECEIVER_TYPE_SMS) {
			pl.addProperty("sms", receiverAddress);
		} else if (receiverType == IModuleManager.RECEIVER_TYPE_EMAIL) {
			pl.addProperty("email", receiverAddress);
		} else {
			pl.addProperty("sms", receiverAddress);
		}
		String req = joReq.toString();
		System.out.println(req);
		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		System.out.println(rsp);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs != RESPONSE_SUCCEED) {
			throw new ModuleException(rs);
		}
	}

	@Override
	public void setKeyValueInfo(KeyValueInfo kv) throws ModuleException {
		// {'PL':{'key':'mykey', 'value':'anything'},
		// 'CID':30511,'SID':'F402270AF7B74948977576C12812BF27'}
		// -> {'RC':1,'SN':0,'CID':30512}
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		joReq.addProperty("CID", 30511);
		joReq.addProperty("SID", this.SID);
		pl.addProperty("key", kv.key);
		pl.addProperty("value", kv.value);

		String req = joReq.toString();

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs != RESPONSE_SUCCEED) {
			throw new ModuleException(rs);
		}
	}

	@Override
	public KeyValueInfo getKeyValueInfo(String key) throws ModuleException {
		// {'PL':{'key':'mykey'},
		// 'CID':30521,'SID':'F402270AF7B74948977576C12812BF27'}
		// -> {"PL":{"value":"something"},"RC":1,"SN":0,"CID":30522}

		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		joReq.addProperty("CID", 30521);
		joReq.addProperty("SID", this.SID);
		pl.addProperty("key", key);

		String req = joReq.toString();
		System.out.println(req);
		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		System.out.println(rsp);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();
		
		int rs = jo.get("RC").getAsInt();
		if (rs == RESPONSE_SUCCEED) {
			JsonObject rspPL = jo.get("PL").getAsJsonObject();
			
			KeyValueInfo kv = new KeyValueInfo();
			kv.key = key;
			kv.value = rspPL.get("value").getAsString();
			KeyValueHelper.getInstence().putAll(kv.value);
			return kv;
		} else {
			throw new ModuleException(rs);
		}
	}

	@Override
	public void deleteKeyValueInfo(String key) throws ModuleException {
		// {'PL':{'key':'mykey'},
		// 'CID':30531,'SID':'6BCEC4935FE344D89D4D6F2A3EEFDA68'}
		// -> {"RC":1,"SN":0,"CID":30532}
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		joReq.addProperty("CID", 30531);
		joReq.addProperty("SID", this.SID);
		pl.addProperty("key", key);

		String req = joReq.toString();

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs != RESPONSE_SUCCEED) {
			throw new ModuleException(rs);
		}
	}

	@Override
	public ArrayList<KeyValueInfo> getKeyValueInfoList(String keyFilter)
			throws ModuleException {
		// {'PL':{'keyFilter':'my'},
		// 'RT':5000,'SN':0,'CID':30541,'SID':'6BCEC4935FE344D89D4D6F2A3EEFDA68'}
		/*-> 
		    {
			  "PL" : [ {
			    "key" : "mykeykey",
			    "value" : "anything1"
			  }, {
			    "key" : "mykey",
			    "value" : "something"
			  } ],
			  "RC" : 1,
			  "SN" : 0,
			  "CID" : 30542
			}
		 */
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		joReq.addProperty("CID", 30541);
		joReq.addProperty("SID", this.SID);
		pl.addProperty("keyFilter", keyFilter);

		String req = joReq.toString();

		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs == RESPONSE_SUCCEED) {
			ArrayList<KeyValueInfo> result = new ArrayList<KeyValueInfo>();
			JsonArray ja = jo.get("PL").getAsJsonArray();
			for (int i = 0; i < ja.size(); i++) {
				JsonObject row = ja.get(i).getAsJsonObject();
				KeyValueInfo info = new KeyValueInfo();
				info.key = row.get("key").getAsString();
				info.value = row.get("value").getAsString();
				result.add(info);
			}
			return result;
		} else {
			throw new ModuleException(rs);
		}
	}

	@Override
	public ModuleLogInfoList getModuleLogList(String moduleId,
			String startTime, String endTime, int pageSize, int pageNum)
			throws ModuleException {
		if (!this.isCloudChannelLive()) {
			new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}
		// {'PL':{'moduleId':'2c9df0dc429c990f01429c9919710001',
		// 'startTime':'yyyyMMddHHmmssSSS',
		// 'endTime':'yyyyMMddHHmmssSSS','pageSize':2,'pageNum':1},'CID':30091,'SID':'94F8C8821C344B0DB3565FABED8A2933'}
		/*-> 
			{
			  "PL" : {
			    "totalNum" : 11,
			    "pageSize" : 2,
			    "pageNum" : 1,
			    "logList" : [ {
			      "id" : 14,
			      "userId" : "2c9df0dc429c990f01429c9919710001",
			      "userName" : "demo",
			      "privilegeId" : 10011,
			      "timestamp" : "20131128151945633",
			      "ip" : "127.0.0.1",
			      "memo" : "{'PL':{'userName':'demo','password':'demo','agingTime':300000,'mac':'005056C00001'},'SN':13714145,'CID':10011} "
			    }, {
			      "id" : 13,
			      "userId" : "2c9df0dc429c990f01429c9919710001",
			      "userName" : "demo",
			      "privilegeId" : 10021,
			      "timestamp" : "20131128151737137",
			      "ip" : "127.0.0.1",
			      "memo" : "{\"RT\":3000,\"SN\":13714145,\"CID\":10021,\"SID\":A52909A826884228BCF1CEE1B76E85A6}"
			    } ]
			  },
			  "RC" : 1,
			  "SN" : 0,
			  "CID" : 10162,
			  "SID" : "94F8C8821C344B0DB3565FABED8A2933"
			}     
		 */
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		joReq.addProperty("CID", 30091);
		joReq.addProperty("SID", this.SID);
		pl.addProperty("moduleId", moduleId);
		pl.addProperty("startTime", startTime);
		pl.addProperty("endTime", endTime);
		pl.addProperty("pageSize", pageSize);
		pl.addProperty("pageNum", pageNum);

		String req = joReq.toString();
		System.out.println(req);
		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		System.out.println(rsp);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs == RESPONSE_SUCCEED) {

			ModuleLogInfoList result = new ModuleLogInfoList();
			JsonObject joPl = jo.get("PL").getAsJsonObject();
			result.setTotalNum(joPl.get("totalNum").getAsInt());
			result.setPageNum(joPl.get("pageNum").getAsInt());
			result.setPageSize(joPl.get("pageSize").getAsInt());
			JsonArray logList = new JsonArray();
			if (result.getTotalNum() > 0) {
				logList = joPl.get("logList").getAsJsonArray();
			}
			for (int i = 0; i < logList.size(); i++) {
				JsonObject row = logList.get(i).getAsJsonObject();
				ModuleLogInfo info = new ModuleLogInfo();
				info.setId(row.get("id").getAsLong());
				info.setTimestamp(row.get("timestamp").getAsString());
				// info.setModuleId(row.get("moduleId").getAsString());
				info.setIp(row.get("ip").getAsString());
				// info.setMac(row.get("mac").getAsString());
				info.setData(row.get("data").getAsString());

				result.add(info);
			}

			return result;
		} else {
			throw new ModuleException(rs);
		}
	}

	@Override
	public OperLogInfoList getOperLogList(String moduleId, String startTime,
			String endTime, int pageNum, int pageSize) throws ModuleException {
		if (!this.isCloudChannelLive()) {
			new ModuleException(
					ModuleException.ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL,
					"Can not access cloud service!");
		}
		// {'PL':{'startTime':'yyyyMMddHHmmssSSS',
		// 'endTime':'yyyyMMddHHmmssSSS','pageSize':2,'pageNum':1},'CID':10161,'SID':'94F8C8821C344B0DB3565FABED8A2933'}
		/*-> 
			{
			  "PL" : {
			    "totalNum" : 11,
			    "pageSize" : 2,
			    "pageNum" : 1,
			    "logList" : [ {
			      "id" : 14,
			      "userId" : "2c9df0dc429c990f01429c9919710001",
			      "userName" : "demo",
			      "privilegeId" : 10011,
			      "timestamp" : "20131128151945633",
			      "ip" : "127.0.0.1",
			      "memo" : "{'PL':{'userName':'demo','password':'demo','agingTime':300000,'mac':'005056C00001'},'SN':13714145,'CID':10011} "
			    }, {
			      "id" : 13,
			      "userId" : "2c9df0dc429c990f01429c9919710001",
			      "userName" : "demo",
			      "privilegeId" : 10021,
			      "timestamp" : "20131128151737137",
			      "ip" : "127.0.0.1",
			      "memo" : "{\"RT\":3000,\"SN\":13714145,\"CID\":10021,\"SID\":A52909A826884228BCF1CEE1B76E85A6}"
			    } ]
			  },
			  "RC" : 1,
			  "SN" : 0,
			  "CID" : 10162
			}     
		 */
		JsonObject joReq = new JsonObject();
		JsonObject pl = new JsonObject();
		joReq.add("PL", pl);
		joReq.addProperty("CID", 10161);
		joReq.addProperty("SID", this.SID);
		pl.addProperty("startTime", startTime);
		pl.addProperty("endTime", endTime);
		pl.addProperty("pageSize", pageSize);
		pl.addProperty("pageNum", pageNum);

		String req = joReq.toString();
		System.out.println(req);
		String rsp = HttpProxy.getInstance().callHttpsPost(req);
		System.out.println(rsp);
		JsonObject jo = new JsonParser().parse(rsp).getAsJsonObject();

		int rs = jo.get("RC").getAsInt();
		if (rs == RESPONSE_SUCCEED) {

			OperLogInfoList result = new OperLogInfoList();
			JsonObject joPl = jo.get("PL").getAsJsonObject();
			result.setTotalNum(joPl.get("totalNum").getAsInt());
			result.setPageNum(joPl.get("pageNum").getAsInt());
			result.setPageSize(joPl.get("pageSize").getAsInt());

			JsonArray logList = joPl.get("logList").getAsJsonArray();

			for (int i = 0; i < logList.size(); i++) {
				JsonObject row = logList.get(i).getAsJsonObject();
				OperLogInfo info = new OperLogInfo();
				try {
					info.setId(row.get("id").getAsLong());
					// info.setUserId(row.get("userId").getAsString());
					info.setUserName(row.get("userName").getAsString());
					info.setPrivilegeId(row.get("privilegeId").getAsInt());
					info.setTimestamp(row.get("timestamp").getAsString());
					info.setIp(row.get("ip").getAsString());
					// info.setData(row.get("data").getAsString());
					info.setMemo(row.get("memo").getAsString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				result.add(info);
			}

			return result;
		} else {
			throw new ModuleException(rs);
		}
	}

	public void setPlatformAdaptor(IPlatformAdaptor deviceAdaptor) {
		this.platformAdaptor = deviceAdaptor;
	}

	public IPlatformAdaptor getPlatformAdaptor() {
		return this.platformAdaptor;
	}

	public String getSID() {
		return SID;
	}

	public void setSID(String sID) {
		SID = sID;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	private byte[] getBytes(int capacity) {
		byte[] data = new byte[capacity];
		for (int i = 0; i < capacity; i++) {
			data[i] = 5;
		}
		return data;
	}

	@Override
	public void stopbeat() {
		// TODO Auto-generated method stub
		beattsk.cancel();
		timer.cancel();
		is_alive = false;
	}

	@Override
	public void sendLocalBeatNow() throws ModuleException {
		// TODO Auto-generated method stub
		ModuleException mx = new ModuleException();
		int[] cmd = { 0x01, 0x01, 0x01, 0x7E, 0x40, 0x40, 0x48, 0x65, 0x6C,
				0x6C, 0x6F, 0x2C, 0x54, 0x68, 0x69, 0x6E, 0x67, 0x21, 0x2A,
				0x2A, 0x23 };
		ByteBuffer bb = ByteBuffer.allocate(21);
		for (int i = 0; i < cmd.length; i++) {
			bb.put((byte) cmd[i]);
		}
		byte[] data = bb.array();
		DatagramPacket sendPacket = null;

		try {
			sendPacket = new DatagramPacket(data, data.length,
					InetAddress.getByName(ModuleConfig.broadcastIp),
					ModuleConfig.broadcastPort);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			mx.setErrorCode(-34);// unkonwn host
		}

		try {
			beattsk.getSocket().send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			mx.setErrorCode(-35);// send err
			throw mx;
		}
	}

	@Override
	public void connectModuleToWIFI2(String RouterSSID, String RouterPSWD) {
		// TODO Auto-generated method stub
		sfb.setmSsidAndPswd(RouterSSID, RouterPSWD);
		sfb.start();
	}

	@Override
	public void stopSmartlink2() {
		// TODO Auto-generated method stub
		sfb.stop();
	}
}

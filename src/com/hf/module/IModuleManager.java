package com.hf.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.hf.module.info.CaptchaImageInfo;
import com.hf.module.info.CaptchaInfo;
import com.hf.module.info.KeyValueInfo;
import com.hf.module.info.ModuleInfo;
import com.hf.module.info.ModuleLogInfoList;
import com.hf.module.info.OperLogInfoList;
import com.hf.module.info.PrivilegeInfo;
import com.hf.module.info.UserInfo;

/**
 * @author Sean
 *
 */
public interface IModuleManager {
	/**
	 * 
	 */
	public int RECEIVER_TYPE_SMS = 1;
	/**
	 * 
	 */
	public int RECEIVER_TYPE_EMAIL = 2;	
	
	/*
	 * */
	public void initHttp();
	
	/**
	 * 
	 */
	public void initialize() throws ModuleException;
	/**
	 * @throws ModuleException 
	 * 
	 */
	public void sendLocalBeatNow() throws ModuleException;
	/**
	 * @return
	 */
	public void tachycardia();
	/**
	 * @return
	 */
	public void beatnormall();
	/**
	 * @return
	 */
	public ModuleHelper getHelper();
	

	/**
	 * @param t2Req,  key=mac, value=T2 msg
	 * @return key=mac, value=T2 msg
	 * @throws ModuleException
	 */
	public HashMap<String, byte[]> moduleCommGet(HashMap<String, byte[]> t2Req) throws ModuleException;
	/**
	 * @param t2Req
	 * @return
	 * @throws ModuleException
	 */
	public HashMap<String, byte[]> moduleCommSet(HashMap<String, byte[]> t2Req) throws ModuleException;

	/**
	 * @param t2Req,  key=mac, value=list(key)
	 * @return key=mac, vlaue=(key-value)
	 * @throws ModuleException
	 */
	public HashMap<String, HashMap<String, byte[]>> moduleCommEvent(HashMap<String, ArrayList<String>> t2Req) throws ModuleException;
	/**
	 * @param listener
	 */
	public void registerEventListener(IEventListener listener);
	/**
	 * @param listener
	 */
	public void unregisterEventListener(IEventListener listener);
	/**
	 * 
	 */
	public void clearListeners();
	
	// ***** Module Management
	/**
	 * @param moduleInfo
	 * @return
	 * @throws ModuleException
	 */
	public ModuleInfo setModule(ModuleInfo moduleInfo) throws ModuleException;
	
	public ModuleInfo getModuleByMac(String mac) throws ModuleException;
	/**
	 * @param moduleId
	 * @return
	 * @throws ModuleException
	 */
	public ModuleInfo getModuel(String moduleId) throws ModuleException;
	/**
	 * @param moduleId
	 * @throws ModuleException
	 */
	public void deleteModule(String moduleId) throws ModuleException;
	/**
	 * @param RouterSSID
	 * @param RouterPSWD
	 * @return
	 * @throws 
	 */
	public void connectModuleToWIFI(String RouterSSID,String RouterPSWD);
	public void stopSmartlink();
	
	public void connectModuleToWIFI2(String RouterSSID,String RouterPSWD);
	public void stopSmartlink2();
	
	/**
	 * @return
	 * @throws ModuleException
	 */
	public ArrayList<ModuleInfo> getAllModules() throws ModuleException;
	

	// ***** User Management
	public void locateCloudService() throws ModuleException;
	
	/**
	 * @throws ModuleException
	 */
	public void login() throws ModuleException;
	/**
	 * @return
	 * @throws ModuleException
	 */
	public HashMap<Integer, PrivilegeInfo> getPrivilege() throws ModuleException;  //Integer is privilegeId
	/**
	 * @throws ModuleException
	 */
	public void logout() throws ModuleException;
	
	/**
	 * @param receiverAddress
	 * @param receiverType
	 * @return
	 * @throws ModuleException
	 */
	public CaptchaInfo captcha(String receiverAddress, int receiverType) throws ModuleException;
	/**
	 * @return
	 * @throws ModuleException
	 */
	public CaptchaImageInfo captchaImage() throws ModuleException;
	/**
	 * @param info
	 * @param captcha
	 * @throws ModuleException
	 */
	public void registerUser(UserInfo info, String captcha) throws ModuleException;
	/**
	 * @param info
	 * @throws ModuleException
	 */
	public void setUser(UserInfo info) throws ModuleException;
	/**
	 * @return
	 * @throws ModuleException
	 */
	public UserInfo getUser() throws ModuleException;
	/**
	 * @throws ModuleException
	 */
	public void deleteUser() throws ModuleException;
	/**
	 * @param oldPwd
	 * @param newPwd
	 * @throws ModuleException
	 */
	public void changePassword(String oldPwd, String newPwd) throws ModuleException;
	/**
	 * @param receiverAddress
	 * @param receiverType
	 * @throws ModuleException
	 */
	public void retrievePassword(String receiverAddress, int receiverType) throws ModuleException;
	
	// ***** Data Container
	/**
	 * @param kv
	 * @throws ModuleException
	 */
	public void setKeyValueInfo(KeyValueInfo kv) throws ModuleException;
	/**
	 * @param key
	 * @return
	 * @throws ModuleException
	 */
	public KeyValueInfo getKeyValueInfo(String key) throws ModuleException;
	/**
	 * @param key
	 * @throws ModuleException
	 */
	public void deleteKeyValueInfo(String key) throws ModuleException;
	/**
	 * @param keyFilter
	 * @return
	 * @throws ModuleException
	 */
	public ArrayList<KeyValueInfo> getKeyValueInfoList(String keyFilter) throws ModuleException;

	// ***** Module History Data
	/**
	 * @param moduleId
	 * @param startTime
	 * @param endTime
	 * @param pageSize
	 * @param pageNum
	 * @return
	 * @throws ModuleException
	 */
	public ModuleLogInfoList getModuleLogList(String moduleId, String startTime, String endTime, int pageSize, int pageNum) throws ModuleException;
	
	// ***** Operation History Data
	/**
	 * @param moduleId
	 * @param startTime
	 * @param endTime
	 * @param pageSize
	 * @param pageNum
	 * @return
	 * @throws ModuleException
	 */
	public OperLogInfoList getOperLogList(String moduleId, String startTime,  String endTime, int pageSize, int pageNum) throws ModuleException;

	
	public void stopbeat();
}

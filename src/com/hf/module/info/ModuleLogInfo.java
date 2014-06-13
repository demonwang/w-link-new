package com.hf.module.info;

public class ModuleLogInfo {
	
    private Long id  = null;
    private String timestamp = null;  //yyyyMMddHHmmssSSS
    private String ip = null;  //module's ip address
    private String mac = null;  //mac address of module, code with HexString    
    private String data = null;  //code with HexString
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
    
}

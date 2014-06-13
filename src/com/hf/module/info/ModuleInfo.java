package com.hf.module.info;

import java.util.HashMap;

import com.hf.module.ModuleConfig;



public class ModuleInfo implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

    //local
    private String localIp = null;
    private long lastTimestamp = 0;
    private HashMap<Integer, GPIO> PinMap = new HashMap<Integer, GPIO>(); // key= pin number, value=pin
    
    private HashMap<Integer, TimeInfo> TimeMap = new HashMap<Integer, TimeInfo>();
    //cloud
    private String id = null;
    private String accessKey = null;
    private String name = null;
    private String desc = null;
    
    private String mac = null; // HEX string
    private String localKey = null; // Coded by base64
    private Boolean needRemoteControl = true;

    private String serialNo = null;
    private Integer factoryId = null;
    private Integer type = null;
    private String hardwareVer = null;
    private String softwareVer = null;
    private String tempKey = null; // Coded by base64
    private String bindTime = null;
    private Long totalOnlineTime = null;

    private String internetIp = null;
    private Double gpsLat = null;
    private Double gpsLng = null;
    private String country = null;
    private String state = null;
    private String city = null;
    private String district = null;
    
    private Boolean online = false;
    
    public ModuleInfo() {
        super();
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ModuleInfo[");
        sb.append(super.toString()).append(",");
        
        if(localIp != null)sb.append("localIp=").append(localIp).append(",");
        
        if(id != null)sb.append("id=").append(id).append(",");
        if(accessKey != null)sb.append("accessKey=").append(accessKey).append(",");
        if(name != null)sb.append("name=").append(name).append(",");
        if(desc != null)sb.append("desc=").append(desc).append(",");
        if(mac != null)sb.append("mac=").append(mac).append(",");
        if(localKey != null)sb.append("localKey=").append(localKey).append(",");
        if(needRemoteControl != null)sb.append("needRemoteControl=").append(needRemoteControl).append(",");
        if(serialNo != null)sb.append("serialNo=").append(serialNo).append(",");
        if(factoryId != null)sb.append("factoryId=").append(factoryId).append(",");
        if(type != null)sb.append("type=").append(type).append(",");
        if(hardwareVer != null)sb.append("hardwareVer=").append(hardwareVer).append(",");
        if(softwareVer != null)sb.append("softwareVer=").append(softwareVer).append(",");
        if(tempKey != null)sb.append("tempKey=").append(tempKey).append(",");
        if(bindTime != null)sb.append("bindTime=").append(bindTime).append(",");
        if(totalOnlineTime != null)sb.append("totalOnlineTime=").append(totalOnlineTime).append(",");
        if(internetIp != null)sb.append("internetIp=").append(internetIp).append(",");
        if(gpsLat != null)sb.append("gpsLat=").append(gpsLat).append(",");
        if(gpsLng != null)sb.append("gpsLng=").append(gpsLng).append(",");
        if(country != null)sb.append("country=").append(country).append(",");
        if(state != null)sb.append("state=").append(state).append(",");
        if(city != null)sb.append("city=").append(city).append(",");
        if(district != null)sb.append("district=").append(district).append(",");
        if(online != null)sb.append("online=").append(online);
        sb.append("]");
        return sb.toString();
    }

    public HashMap<Integer, TimeInfo> getTimeMap() {
		return TimeMap;
	}
    
    public void setTimeMap(HashMap<Integer, TimeInfo> timeMap) {
		TimeMap = timeMap;
	}
    
    public long getLastTimestamp() {
		return lastTimestamp;
	}


	public void setLastTimestamp(long lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}


	public HashMap<Integer, GPIO> getPinMap() {
		return PinMap;
	}


	public void setPinMap(HashMap<Integer, GPIO> PinMap) {
		if(PinMap != null)
			this.PinMap = PinMap;
	}


	public String getLocalIp() {
		return localIp;
	}


	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getLocalKey() {
    	if(localKey==null)
    		localKey = ModuleConfig.localModulePswd;
    	return localKey;
    }

    public void setLocalKey(String localKey) {
        this.localKey = localKey;
    }

    public Boolean getNeedRemoteControl() {
        return needRemoteControl;
    }

    public void setNeedRemoteControl(Boolean needRemoteControl) {
        this.needRemoteControl = needRemoteControl;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public Integer getFactoryId() {
        return this.factoryId;
    }

    public void setFactoryId(Integer factoryId) {
        this.factoryId = factoryId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getHardwareVer() {
        return hardwareVer;
    }

    public void setHardwareVer(String hardwareVer) {
        this.hardwareVer = hardwareVer;
    }

    public String getSoftwareVer() {
        return softwareVer;
    }

    public void setSoftwareVer(String softwareVer) {
        this.softwareVer = softwareVer;
    }

    public String getTempKey() {
        return tempKey;
    }

    public void setTempKey(String tempKey) {
        this.tempKey = tempKey;
    }

    public String getBindTime() {
        return bindTime;
    }

    public void setBindTime(String bindTime) {
        this.bindTime = bindTime;
    }

    public Long getTotalOnlineTime() {
        return totalOnlineTime;
    }

    public void setTotalOnlineTime(Long totalOnlineTime) {
        this.totalOnlineTime = totalOnlineTime;
    }


    public String getInternetIp() {
        return internetIp;
    }

    public void setInternetIp(String internetIp) {
        this.internetIp = internetIp;
    }

    public Double getGpsLat() {
        return gpsLat;
    }

    public void setGpsLat(Double gpsLat) {
        this.gpsLat = gpsLat;
    }

    public Double getGpsLng() {
        return gpsLng;
    }

    public void setGpsLng(Double gpsLng) {
        this.gpsLng = gpsLng;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }	
}

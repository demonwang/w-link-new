package com.hf.module.info;

public class UserInfo implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

    private String id = null;
    private String accessKey = null;
    private String displayName = null;
    private String userName = null;
    private String password = null;
    private String sharePassword = null;
    private String sharePasswordAgingTime = null;   
    private String cellPhone = null;
    private String email = null;
    private String idNumber = null;
    private String createTime = null;

    
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer("UserInfo[");       
        if(id         != null)sb.append("id=").append(id).append(",");      
        if(accessKey  != null)sb.append("accessKey=").append(accessKey).append(",");    
        if(displayName!= null)sb.append("displayName=").append(displayName).append(",");      
        if(userName   != null)sb.append("userName=").append(userName).append(",");      
        if(password   != null)sb.append("password=").append(password).append(",");      
        if(sharePassword   != null)sb.append("sharePassword=").append(sharePassword).append(","); 
        if(sharePasswordAgingTime   != null)sb.append("sharePasswordAgingTime=").append(sharePasswordAgingTime).append(",");   
        if(cellPhone  != null)sb.append("cellPhone=").append(cellPhone).append(",");      
        if(email      != null)sb.append("email=").append(email).append(",");    
        if(idNumber   != null)sb.append("idNumber=").append(idNumber).append(",");      
        if(createTime   != null)sb.append("createTime=").append(createTime);    
        sb.append("]");
        return sb.toString();
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

	public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSharePassword() {
		return sharePassword;
	}

	public void setSharePassword(String sharePassword) {
		this.sharePassword = sharePassword;
	}
	public String getSharePasswordAgingTime() {
		return sharePasswordAgingTime;
	}
	public void setSharePasswordAgingTime(String sharePasswordAgingTime) {
		this.sharePasswordAgingTime = sharePasswordAgingTime;
	}
	public String getCellPhone() {
        return cellPhone;
    }
    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

 
}

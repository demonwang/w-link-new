package com.hf.module.info;



public class PrivilegeInfo implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    
	private Integer cid = null;
    private String name = null; // it's equals to NM of Message
    private String desc = null; // desc is in database
    
    
    public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
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

    
    
}

package com.hf.module.info;

public class CaptchaInfo {
	private String captchaToken = null;
	private Integer agingTime = null;
	
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("CaptchaInfo[");
        if(captchaToken != null)sb.append("captchaToken=").append(captchaToken).append(",");
        if(agingTime != null)sb.append("agingTime=").append(agingTime);
        sb.append("]");
        return sb.toString();
    }	
	
	public String getCaptchaToken() {
		return captchaToken;
	}
	public void setCaptchaToken(String captchaToken) {
		this.captchaToken = captchaToken;
	}
	public Integer getAgingTime() {
		return agingTime;
	}
	public void setAgingTime(Integer agingTime) {
		this.agingTime = agingTime;
	}

}

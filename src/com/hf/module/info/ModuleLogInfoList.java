package com.hf.module.info;

import java.util.ArrayList;

public class ModuleLogInfoList extends ArrayList<ModuleLogInfo>{
    private static final long serialVersionUID = 1L;

    private int totalNum;
    private int pageSize;
    private int pageNum;
    
    
	public int getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
    
    
    
}

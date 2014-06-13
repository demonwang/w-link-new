package com.hf.module.info;

import java.util.ArrayList;
import java.util.HashMap;

public class DaysOfWeek{
    public boolean sunday = false;
    public boolean monday = false;
    public boolean tuesday = false;
    public boolean wednesday = false;
    public boolean thursday = false;
    public boolean friday = false;
    public boolean saturday = false;
    public boolean isLoop = false;
    
    private byte week = 0;
    
    public byte castToByte(){
    	if(this.monday){
    		this.week  |= 1;
    	}
    	if(this.tuesday){
    		this.week  |= (1<<1);
    	}
    	if(this.wednesday){
    		this.week  |= (1<<2);
    	}
    	if(this.thursday){
    		this.week  |= (1<<3);
    	}
    	if(this.friday){
    		this.week  |= (1<<4);
    	}
    	if(this.saturday){
    		this.week  |= (1<<5);
    	}
    	if(this.sunday){
    		this.week  |= (1<<6);
    	}
    	if(this.isLoop){
    		this.week  |= (1<<7);
    	}
    	return week;
    }
    
    public HashMap<Integer, Boolean> toMap(){
    	HashMap<Integer, Boolean> weekmap = new HashMap<Integer, Boolean>();
    	weekmap.put(1, monday);
    	weekmap.put(2, tuesday);
    	weekmap.put(3, wednesday);
    	weekmap.put(4, thursday);
    	weekmap.put(5, friday);
    	weekmap.put(6, saturday);
    	weekmap.put(7, sunday);
    	weekmap.put(8, isLoop);
		return weekmap;
    	
    } 
    
    public  DaysOfWeek pack(byte week){
    	if((week&1)==1){
    		this.monday = true;
    	}
    	if((week&(1<<1))==(1<<1)){
    		this.tuesday = true;
    	}
    	if((week&(1<<2))==(1<<2)){
    		this.wednesday = true;
    	}
    	if((week&(1<<3))==(1<<3)){
    		this.thursday = true;
    	}
    	if((week&(1<<4))==(1<<4)){
    		this.friday = true;
    	}
    	if((week&(1<<5))==(1<<5)){
    		this.saturday = true;
    	}
    	if((week&(1<<6))==(1<<6)){
    		this.sunday = true;
    	}
    	if((week&1<<7)==1<<7){
    		this.isLoop = true;
    	}
    	return this;
    }
}

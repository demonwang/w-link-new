package com.hf.Unity;

import java.util.HashMap;
import java.util.Iterator;

import com.hf.module.info.TimeInfo;

public class DeComposeFactory {
	static DeComposeFactory me = null;
	public static DeComposeFactory getInstence(){
		if(me == null)
			me = new DeComposeFactory();
		return me;
	}
}

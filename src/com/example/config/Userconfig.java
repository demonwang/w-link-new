package com.example.config;

import com.hf.module.ModuleConfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Userconfig {
	public final static  String USER_INFO_SP_NAME = "USERINFO";
	public final static  String USER_NAME_SP = "USERNAME";
	public final static String USER_PASSWD_SP = "USERPSWD";
	public final static String ISFRIST_RUN = "ISFRISTRUN";
	public final static String KEYVALUE = "keyvalue";
	
	public static boolean isFristLogin(){
		SharedPreferences sp = ModuleConfig.appcontext.getSharedPreferences(Userconfig.USER_INFO_SP_NAME, Context.MODE_PRIVATE);
		ModuleConfig.cloudUserName = sp.getString(Userconfig.USER_NAME_SP, null);
		ModuleConfig.cloudPassword = sp.getString(Userconfig.USER_PASSWD_SP, null);
		return sp.getBoolean(Userconfig.ISFRIST_RUN, true);
	}
	public static void saveUserInfo(){
		SharedPreferences sp = ModuleConfig.appcontext.getSharedPreferences(Userconfig.USER_INFO_SP_NAME, Context.MODE_PRIVATE);
		Editor e = sp.edit();
		e.putString(USER_NAME_SP, ModuleConfig.cloudUserName);
		e.putString(USER_PASSWD_SP, ModuleConfig.cloudPassword);
		e.putBoolean(ISFRIST_RUN, false);
		e.commit();
	}

}

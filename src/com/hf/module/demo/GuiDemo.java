package com.hf.module.demo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import com.hf.module.IModuleManager;
import com.hf.module.ModuleConfig;
import com.hf.module.ModuleException;
import com.hf.module.info.ModuleInfo;
import com.hf.module.mock.ModuleManagerMock;

public class GuiDemo{

	private IModuleManager moduleManager;
	
	
	public void init() throws ModuleException{
		ModuleConfig.accessKey             = "402880e942d23e030142d23e10a10000";
		ModuleConfig.cloudServiceUrl       = "https://localhost/usvc/";
		ModuleConfig.cloudUserName = "demo";
		ModuleConfig.cloudPassword = "demo";
		
		this.moduleManager = new ModuleManagerMock();
		this.moduleManager.initialize();
	}
	
	public void process(){
		try{
			
			ModuleInfo mi = this.moduleManager.getModuel("1");
			//moduleManager.getHelper().setGPIO(mi);
			
			ByteBuffer data = ByteBuffer.allocate(100);
			data.putInt(10);

		}
		catch(ModuleException mex){
			System.out.println(mex);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	

	public static void main(String args[]) throws ModuleException, IOException{
		//TODO
		GuiDemo demo = new GuiDemo();
		demo.init();
		//demo.process();
		
		
		System.out.println("Any keypress for exit...");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		in.readLine();
		in.close();
		System.out.println("Done!");
	}	
}

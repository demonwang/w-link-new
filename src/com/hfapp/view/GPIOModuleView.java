package com.hfapp.view;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.hf.module.IEventListener;
import com.hf.module.ModuleException;
import com.hf.module.impl.KeyValueHelper;
import com.hf.module.impl.LocalModuleInfoContainer;
import com.hf.module.info.GPIO;
import com.hf.module.info.ModuleInfo;
import com.hfapp.activity.ADCModuleActivity;
import com.hfapp.activity.ADC_Service;
import com.hfapp.activity.ImageContentor;

public class GPIOModuleView extends BaseModuleView implements IEventListener {
	private boolean isopen = false;
	private boolean isonline = false;
	private Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				setImage();
				break;
			default:
				Toast.makeText(getContext(), /*"网络错误 "+msg.what*/"正在连接设备", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	public GPIOModuleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GPIOModuleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GPIOModuleView(Context context, boolean which) {
		super(context, which);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void updateStatus() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					isopen = manager.getHelper().getHFGPIO(
							m_moduleinfo.getMac(), 11);
					isonline = true;
				} catch (ModuleException e) {
					// TODO Auto-generated catch block
					hand.sendEmptyMessage(e.getErrorCode());
					isonline = false;
				}
				hand.sendEmptyMessage(1);
			}
		}).start();
	}

	@Override
	public void initView() {
		super.initView();
		
		// 长按设备进入设备信息界面ADCModuleActivity
		moduleImage.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
//				if(m_moduleinfo!=null);
				Intent i = new Intent(getContext(), ADCModuleActivity.class);
				i.putExtra("mac", m_moduleinfo.getMac());
				getContext().startActivity(i);
			/*	Intent service_Intent = new Intent(getContext(),ADC_Service.class);
				service_Intent.putExtra("mac", m_moduleinfo.getMac());
				getContext().startService(service_Intent);*/
				return true;
			}
		
		});
		
	}
	
	private void setImage(){
		if(isonline){
//			if(isopen){
				moduleImage.setBackgroundResource(ImageContentor.getOpenImageRs(KeyValueHelper.getInstence().get(m_moduleinfo.getMac()).getIndex()));
//			}else{
//				moduleImage.setBackgroundResource(ImageContentor.getCloseImageRs(KeyValueHelper.getInstence().get(m_moduleinfo.getMac()).getIndex()));
//			}
		}else{
			moduleImage.setBackgroundResource(ImageContentor.getOutLineImageRs(KeyValueHelper.getInstence().get(m_moduleinfo.getMac()).getIndex()));
		}
	}

	@Override
	public void onEvent(String mac, byte[] t2data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCloudLogin(boolean loginstat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCloudLogout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewDevFind(ModuleInfo mi) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGPIOEvent(String mac, HashMap<Integer, GPIO> GM) {
		// TODO Auto-generated method stub

		if (this.m_moduleinfo.getMac().equalsIgnoreCase(mac)) {
			System.out.println(mac);
			isopen = GM.get(11).getStatus();
			hand.sendEmptyMessage(1);
		}
	}

	@Override
	public void onTimerEvent(String mac, byte[] t2data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUARTEvent(String mac, byte[] userData, boolean chanle) {
		// TODO Auto-generated method stub

	}
}

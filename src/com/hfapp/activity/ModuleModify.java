package com.hfapp.activity;

import com.example.palytogether.R;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleException;
import com.hf.module.impl.KeyValueHelper;
import com.hf.module.impl.LocalModuleInfoContainer;
import com.hf.module.info.ModuleInfo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ModuleModify extends Activity implements OnClickListener{
	
	private EditText moduleName;
	private Button moduleImage;
	private Button mDelete;
	private ImageView icon;
	private IModuleManager manager = ManagerFactory.getInstance().getManager();
	private String mac;
	private ModuleInfo mi;
	private Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				/*Intent i = new Intent(ModuleModify.this, ModuleList.class);
				startActivity(i);*/
				finish();
				break;
			case 2:
				Toast.makeText(ModuleModify.this, "delete err", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				finish();
				break;
			case 4:
				Toast.makeText(ModuleModify.this, "修改错误", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.module_modify_layout);
		mac = getIntent().getStringExtra("mac");
		mi = LocalModuleInfoContainer.getInstance().get(mac);
		initActionbar();
		initView();
	}
	
	private void initActionbar() {
		// TODO Auto-generated method stub
		ActionBar bar = getActionBar();
		bar.setCustomView(R.layout.layout_actionbar);
		bar.setDisplayShowCustomEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		bar.setDisplayShowTitleEnabled(false);
		ImageView backBtn = (ImageView) findViewById(R.id.back);
		ImageView okBtn = (ImageView) findViewById(R.id.ok);
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText(R.string.module_edit);
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String name = moduleName.getText().toString().trim();
				if(!name.equalsIgnoreCase(mi.getName())&&name.length()>0){
					new Thread(new Runnable() {
						public void run() {
							mi.setName(name);
							try {
								manager.setModule(mi);
								hand.sendEmptyMessage(3);
							} catch (ModuleException e) {
								// TODO Auto-generated catch block
								hand.sendEmptyMessage(4);
								e.printStackTrace();
							}
						}
					}).start();
				}
				finish();
			}
		});
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	private void initView(){
		moduleName = (EditText) findViewById(R.id.module_name);
		moduleImage = (Button) findViewById(R.id.module_image);
		mDelete = (Button) findViewById(R.id.module_delete);
		icon = (ImageView) findViewById(R.id.modulemodify_module_imag);
		icon.setImageResource(ImageContentor.getOpenImageRs(KeyValueHelper.getInstence().get(mi.getMac()).getIndex()));
		moduleImage.setOnClickListener(this);
		mDelete.setOnClickListener(this);
		moduleImage.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.color.gray);
				} else if(event.getAction()==MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.color.green);
				}
				return false;
			}
		});
		mDelete.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.color.gray);
				} else if(event.getAction()==MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.color.orange);
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.module_image:
			Intent i = new Intent(this, ImageContentor.class);
			startActivityForResult(i, 100);
			break;
		case R.id.module_delete:
			doDeleteModule();
			break;
		default:
			break;
		}
	}
	
	
	private void doDeleteModule(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					
					manager.deleteModule(mi.getMac());
					hand.sendEmptyMessage(1);
				} catch (ModuleException e) {
					// TODO Auto-generated catch block
					hand.sendEmptyMessage(1);
					e.printStackTrace();
					LocalModuleInfoContainer.getInstance().remove(mi.getMac());
				}
			}
		}).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == 100&&resultCode!=100){
			icon.setImageResource(ImageContentor.getCloseImageRs(resultCode));
			KeyValueHelper.getInstence().get(mi.getMac()).setIndex(resultCode);
			KeyValueHelper.getInstence().save();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

}

package com.hfapp.activity;

import com.example.palytogether.R;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageContentor extends Activity{
	private GridView mGridView;
	private int imagIndex = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_content_layout);
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
		title.setText(R.string.setting_nav);
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(imagIndex);
				finish();
			}
		});
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(100);
				finish();
			}
		});
	}
	
	
	private BaseAdapter mgridViewadpt = new BaseAdapter() {
		private ImageView selectedView = null;
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = LayoutInflater.from(ImageContentor.this).inflate(R.layout.image_content_gridview_item_layout, null, true);
			ImageView image = (ImageView) v.findViewById(R.id.image_content_item_image);
			final ImageView flag = (ImageView) v.findViewById(R.id.image_content_item_select_flag);
			flag.setVisibility(View.GONE);
			image.setImageResource(ImageContentor.getCloseImageRs(position));
			image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(selectedView != null){
						selectedView.setVisibility(View.GONE);
					}
					selectedView = flag;
					selectedView.setVisibility(View.VISIBLE);
					imagIndex = position;
				}
			});
			return v;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imges.length;
		}
	};
	private void initView() {
		// TODO Auto-generated method stub
		mGridView = (GridView) findViewById(R.id.image_conten_gridview);
		mGridView.setAdapter(mgridViewadpt);
	}
	
	private  static  int[][] imges = {{R.drawable.icon_plug_outline,R.drawable.icon_plug_close,R.drawable.icon_plug_on},
		{R.drawable.icon_kt_outline,R.drawable.icon_kt_close,R.drawable.icon_kt_on},
		{R.drawable.icon_dfg_outline,R.drawable.icon_dfg_close,R.drawable.icon_dfg_on},
		{R.drawable.icon_tv_outline,R.drawable.icon_tv_close,R.drawable.icon_tv_on},
		{R.drawable.icon_pc_outline,R.drawable.icon_pc_close,R.drawable.icon_pc_on},
		{R.drawable.icon_rsh_outline,R.drawable.icon_rsh_close,R.drawable.icon_rsh_on},
		{R.drawable.icon_fan_outline,R.drawable.icon_fan_close,R.drawable.icon_fan_on},
		{R.drawable.icon_xyj_outline,R.drawable.icon_xyj_close,R.drawable.icon_xyj_on},
		{R.drawable.icon_wbl_outline,R.drawable.icon_wbl_close,R.drawable.icon_wbl_on},
		{R.drawable.icon_tablight_outline,R.drawable.icon_tablight_cloase,R.drawable.icon_tablight_on},
		//{R.drawable.zigbee_icon,R.drawable.zigbee_icon,R.drawable.zigbee_icon,}
	};
	
	public static  int getOpenImageRs(int index){
		return imges[index][2];
	}
	
	public static  int getCloseImageRs(int index){
		return imges[index][1];
	}
	
	public static  int getOutLineImageRs(int index){
		return imges[index][0];
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(100);
		finish();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
}

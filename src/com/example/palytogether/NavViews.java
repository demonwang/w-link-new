package com.example.palytogether;

import java.util.ArrayList;

import noting.ViewPagerAdapter;

import com.example.config.Userconfig;
import com.example.palytogether.R;
import com.hfapp.activity.Login;
import com.hfapp.activity.ModuleList;
import com.hfapp.activity.SmartCtrler;
import com.hfapp.view.ViewPager0;
import com.hfapp.view.ViewPager1;
import com.hfapp.view.ViewPager2;
import com.hfapp.view.ViewPager3;
import com.hfapp.view.ViewPager4;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NavViews extends Activity {
	
	//定义ViewPager对象
	private ViewPager viewPager;
	
	//定义ViewPager适配器
	private ViewPagerAdapter vpAdapter;
	
	//定义一个ArrayList来存放View
	private ArrayList<View> views;

	//引导图片资源
//    private static final int[] pics = {R.drawable.view0_bg,R.drawable.view1_bg,R.drawable.view2_bg,R.drawable.view3_bg,R.drawable.view4_bg};
    
    //底部小点的图片
    private ImageView[] points;
    
    //记录当前选中位置
    private int currentIndex;
    
    private Handler hand = new Handler(){
    	
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_nav_activity);
		
		initView();
		initData();
	}
	
	/**
	 * 初始化组件
	 */
	private void initView(){
		//实例化ArrayList对象
		views = new ArrayList<View>();
		
		//实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		
		//实例化ViewPager适配器
		vpAdapter = new ViewPagerAdapter(views);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
//        LayoutInflater inflater = getLayoutInflater();
//        
//        for(int i=0; i<pics.length; i++) {
//        	View v = inflater.inflate(R.layout.item_view, null);
//            ImageView image = (ImageView)v.findViewById(R.id.image);
//            image.setImageResource(pics[i]);
//            views.add(v);
//        }
		
		View view0 = new ViewPager0(this);
		View view1 = new ViewPager1(this);
		View view2 = new ViewPager2(this);
		View view3 = new ViewPager3(this);
		View view4 = new ViewPager4(this);
       
//		viewPager.addView(view0);
		views.add(view0);
//        viewPager.addView(view1);
        views.add(view1);
//        viewPager.addView(view2);
        views.add(view2);
//        viewPager.addView(view3);
        views.add(view3);
//        viewPager.addView(view4);
        views.add(view4);
        
        //设置数据
        viewPager.setAdapter(vpAdapter);
        //设置监听
        viewPager.setOnPageChangeListener(new pageListener());
        
        //初始化底部小点
        initPoint();
        
        
        Button btn = (Button) view4.findViewById(R.id.go);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Userconfig.isFristLogin()){
					Intent i = new Intent(NavViews.this,Login.class);
					startActivity(i);
					finish();
				}else{
					Intent i = new Intent(NavViews.this,ModuleList.class);
					startActivity(i);
					finish();
				}
			}
		});
	}
	
	/**
	 * 初始化底部小点
	 */
	private void initPoint(){
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);       
		
        points = new ImageView[views.size()];

        //循环取得小点图片
        for (int i = 0; i < views.size(); i++) {
        	//得到一个LinearLayout下面的每一个子元素
        	points[i] = (ImageView) linearLayout.getChildAt(i);
        	//默认都设为灰色
        	points[i].setEnabled(true);
        	//给每个小点设置监听
        	points[i].setOnClickListener(new pointListener());
        	//设置位置tag，方便取出与当前位置对应
        	points[i].setTag(i);
        }
        
        //设置当面默认的位置
        currentIndex = 0;
        //设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
	}

	private class pageListener implements OnPageChangeListener{

		/**
		 * 当滑动状态改变时调用
		 */
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
		/**
		 * 当当前页面被滑动时调用
		 */
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		/**
		 * 当新的页面被选中时调用
		 */
		@Override
		public void onPageSelected(int position) {
			// 设置底部小点选中状态
			setCurDot(position);
		}
		
	}
	
	private class pointListener implements OnClickListener{
		/**
		 * 通过点击事件来切换当前的页面
		 */
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			setCurView(position);
			setCurDot(position);
		}
		
	}
	/**
	 * 设置当前页面的位置
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= views.size()) {
			return;
		}
		viewPager.setCurrentItem(position);
	}
	
    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon){
         if (positon < 0 || positon > views.size() - 1 || currentIndex == positon) {
             return;
         }
         points[positon].setEnabled(false);
         points[currentIndex].setEnabled(true);
         currentIndex = positon;
     }

}


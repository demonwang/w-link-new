package com.hfapp.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.palytogether.R;
import com.hf.lib.util.HexBin;
import com.hf.module.IModuleManager;
import com.hf.module.ManagerFactory;
import com.hf.module.ModuleException;
import com.hf.module.info.DaysOfWeek;
import com.hf.module.info.TimerEachOne;
import com.hfapp.view.MySwitch;
import com.hfapp.view.MySwitch.OnSwitchChangedListener;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TimerAdder extends Activity implements OnCheckedChangeListener {
	private String mac;

	private RadioGroup mrg;
	private ArrayList<CheckBox> mcheckboxs = new ArrayList<CheckBox>();
	private final int[] checkboxids = { R.id.check1, R.id.check2, R.id.check3,
			R.id.check4, R.id.check5, R.id.check6, R.id.check7, R.id.check8 };
	
	private  String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	private View loops;
	private ImageView open_stat;
	private ImageView close_stat;
	private MySwitch openswc;
	private MySwitch closeswc;
	private EditText friMon;
	private EditText friDay;
	private EditText friHour;
	private EditText friMin;

	private EditText secMon;
	private EditText secDay;
	private EditText secHour;
	private EditText secMin;
	
	
	private ImageButton submit;
	
	private TimerEachOne tm = new TimerEachOne();
	private DaysOfWeek dw = new DaysOfWeek();
	private IModuleManager manager = ManagerFactory.getInstance().getManager();

	
	
	private boolean isDayTimer = true;
	private boolean doopen = true;
	private boolean docolse = true;
	
	
	private int timernum = 0;
	
	private Handler hand = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(TimerAdder.this, "input err", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				finish();
				submit.setEnabled(true);
				break;
			case 3:
				submit.setEnabled(true);
				Toast.makeText(TimerAdder.this, "add err", Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.timer_adder_layout);
		mac = getIntent().getStringExtra("mac");
		timernum = getIntent().getIntExtra("timenum", 0);
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

			}
		});
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView() {

		for (int i = 0; i < checkboxids.length; i++) {
			CheckBox check = (CheckBox) findViewById(checkboxids[i]);
			check.setOnCheckedChangeListener(this);
			check.setTextColor(getResources().getColor(R.color.gray));
			mcheckboxs.add(check);
			if(i==0){
				check.setBackgroundResource(R.drawable.loop_checkboxbguncheck);
			}
			
			if(getWeekOfDate()==i){
				check.setChecked(true);
			}
		}

		open_stat = (ImageView) findViewById(R.id.open_stat);
		friMon = (EditText) findViewById(R.id.fri_mon);
		friDay = (EditText) findViewById(R.id.fri_day);
		friHour = (EditText) findViewById(R.id.fri_hour);
		friMin = (EditText) findViewById(R.id.fri_min);
		openswc = (MySwitch) findViewById(R.id.open_switch);

		close_stat = (ImageView) findViewById(R.id.close_stat);
		secMon = (EditText) findViewById(R.id.sec_mon);
		secDay = (EditText) findViewById(R.id.sec_day);
		secHour = (EditText) findViewById(R.id.sec_hour);
		secMin = (EditText) findViewById(R.id.sec_min);
		closeswc = (MySwitch) findViewById(R.id.close_switch);
		mrg = (RadioGroup) findViewById(R.id.timer_adder_mode);
		loops = findViewById(R.id.loops);
		
		submit = (ImageButton) findViewById(R.id.submit_addtimer);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dosubtimer();
			}


		});
		
		mrg.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case 1:
					isDayTimer = true;
					loops.setVisibility(View.INVISIBLE);
					friMon.setVisibility(View.VISIBLE);
					friDay.setVisibility(View.VISIBLE);

					secMon.setVisibility(View.VISIBLE);
					secDay.setVisibility(View.VISIBLE);
					break;
				case 2:
					isDayTimer = false;
					loops.setVisibility(View.VISIBLE);
					friMon.setVisibility(View.GONE);
					friDay.setVisibility(View.GONE);

					secMon.setVisibility(View.GONE);
					secDay.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
		});
		mrg.check(1);
		openswc.setOnSwitchChangedListener(new OnSwitchChangedListener() {

			@Override
			public void onSwitchChanged(MySwitch obj, int status) {
				// TODO Auto-generated method stub
				if (status == MySwitch.SWITCH_ON) {
					open_stat.setImageResource(R.drawable.open_disable);
					doopen = false;
				} else if (status == MySwitch.SWITCH_OFF) {
					open_stat.setImageResource(R.drawable.open_time);
					doopen = true;
				}
			}
		});
		closeswc.setOnSwitchChangedListener(new OnSwitchChangedListener() {

			@Override
			public void onSwitchChanged(MySwitch obj, int status) {
				// TODO Auto-generated method stub
				if (status == MySwitch.SWITCH_ON) {
					close_stat.setImageResource(R.drawable.close_disable);
					docolse = true;
				} else if (status == MySwitch.SWITCH_OFF) {
					close_stat.setImageResource(R.drawable.close_timw);
					docolse = false;
				}
			}
		});

	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.check1:
			dw.isLoop = arg1;
			doselectloopday(0,arg1);
			break;
		case R.id.check2:
			dw.monday = arg1;
			doselectloopday(1,arg1);
			break;
		case R.id.check3:
			dw.tuesday = arg1;
			doselectloopday(2,arg1);
			break;
		case R.id.check4:
			dw.wednesday = arg1;
			doselectloopday(3,arg1);
			break;
		case R.id.check5:
			dw.thursday = arg1;
			doselectloopday(4,arg1);
			break;
		case R.id.check6:
			dw.friday = arg1;
			doselectloopday(5,arg1);
			break;
		case R.id.check7:
			dw.saturday = arg1;
			doselectloopday(6,arg1);
			break;
		case R.id.check8:
			dw.sunday = arg1;
			doselectloopday(7,arg1);
			break;

		default:
			break;
		}
	}
	
	private void doselectloopday(int pos,boolean sta){
		if(sta){
			if(pos == 0){
				mcheckboxs.get(pos).setBackgroundResource(R.drawable.loop_checkboxbg);
			}
			mcheckboxs.get(pos).setTextColor(getResources().getColor(R.color.blue_yellow));
			
		}else{
			if(pos == 0){
				mcheckboxs.get(pos).setBackgroundResource(R.drawable.loop_checkboxbguncheck);
			}
			mcheckboxs.get(pos).setTextColor(getResources().getColor(R.color.gray));
		}
	}
	
	public static int getWeekOfDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) -1;
        if (w < 0)
            w = 0;
        return w;
    }
	
	private void dosubtimer() {
		// TODO Auto-generated method stub
		tm.setNum((byte) timernum);
		if(isDayTimer){
			if(doopen){
				if(docolse){
					tm.setType((byte) 0x03);
					tm.setFriDo(HexBin.stringToBytes("FE010005016002FFFF"));
					tm.setSecDo(HexBin.stringToBytes("FE010005016001FFFF"));
					setBoth();
				}else{
					tm.setType((byte) 0x01);
					tm.setFriDo(HexBin.stringToBytes("FE010005016002FFFF"));
					setOnpen();
				}
			}else{
				if(docolse){
					tm.setType((byte) 0x01);
					tm.setFriDo(HexBin.stringToBytes("FE010005016001FFFF"));
					setclose();
				}
			}
		}else{
			tm.setFlag(dw.castToByte());
			if(doopen){
				if(docolse){
					tm.setType((byte) 0x04);
					tm.setFriDo(HexBin.stringToBytes("FE010005016002FFFF"));
					tm.setSecDo(HexBin.stringToBytes("FE010005016001FFFF"));
					setBoth();
				}else{
					tm.setType((byte) 0x02);
					tm.setFriDo(HexBin.stringToBytes("FE010005016002FFFF"));
					setOnpen();
				}
			}else{
				if(docolse){
					tm.setType((byte) 0x02);
					tm.setFriDo(HexBin.stringToBytes("FE010005016001FFFF"));
					setclose();
				}
			}
		}
		
		
		
		submit.setEnabled(false);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					manager.getHelper().setTimer(mac, tm);
					hand.sendEmptyMessage(2);
				} catch (ModuleException e) {
					// TODO Auto-generated catch block
					hand.sendEmptyMessage(3);
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	private void setclose(){
		int sm = isNumber(secMon.getText().toString().trim());
		int sd = isNumber(secDay.getText().toString().trim());
		int sh = isNumber(secHour.getText().toString().trim());
		int smin = isNumber(secMin.getText().toString().trim());
		if(sm<1||sm>12){
			hand.sendEmptyMessage(1);
			return ;
		}
		
		if(sd<1||sm>31){
			hand.sendEmptyMessage(1);
			return ;
		}
		
		if(sh<0||sh>23){
			hand.sendEmptyMessage(1);
			return ;
		}
		
		if(sm<0||sm>59){
			hand.sendEmptyMessage(1);
			return ;
		}
	}
	
	private void setOnpen(){
		int fm = isNumber(friMon.getText().toString().trim());
		int fd = isNumber(friDay.getText().toString().trim());
		int fh = isNumber(friHour.getText().toString().trim());
		int fmin = isNumber(friMin.getText().toString().trim());
		if(fm<1||fm>12){
			hand.sendEmptyMessage(1);
			return ;
		}
		
		if(fd<1||fm>31){
			hand.sendEmptyMessage(1);
			return ;
		}
		
		if(fh<0||fh>23){
			hand.sendEmptyMessage(1);
			return ;
		}
		
		if(fm<0||fm>59){
			hand.sendEmptyMessage(1);
			return ;
		}
	}
	
	private void setBoth(){
		setclose();setOnpen();
	}
	public int isNumber(String str)
    {
		if(str.length()<=0){
			return -10;
		}
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
             return -10;
        }
        else
        {
             return Integer.valueOf(str);
        }
    }
	
	/**
	 * 计算date之前n天的日期
	 */
	public static Date getDateBefore(Date date, int n) {
		Calendar now = Calendar.getInstance();  
        now.setTime(date);  
        now.set(Calendar.DATE, now.get(Calendar.DATE) - n);  
        return now.getTime();
	}
	
	/** 
     * 得到几天后的时间 
     */        
    public static Date getDateAfter(Date d, int day) {  
        Calendar now = Calendar.getInstance();  
        now.setTime(d);  
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);  
        return now.getTime();  
    }  
    
    /**
	 * 判断日期格式是否正确
	 */
	public static boolean IsDateFormat(String dataStr) {
		boolean state = false;
		try {
			java.text.SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd"); 
			dFormat.setLenient(false); 
			java.util.Date d = dFormat.parse(dataStr);
			state = true;
		} catch (ParseException e) {
			e.printStackTrace();
			state = false;
		} 
		return state;
	}
}

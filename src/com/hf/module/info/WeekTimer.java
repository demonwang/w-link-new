package com.hf.module.info;

import com.hf.lib.util.HexBin;

public class WeekTimer {
	private DaysOfWeek week;
	byte friHour;
	byte secHour;
	byte friMin;
	byte secMin;
	final byte[] friDo = HexBin.stringToBytes("FE01001101FFFFFFFFFFFFFFFFFFFFFFFF6002FFFF");
	final byte[] secDo = HexBin.stringToBytes("FE01001101FFFFFFFFFFFFFFFFFFFFFFFF6001FFFF");
}

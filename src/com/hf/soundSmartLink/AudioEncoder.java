package com.hf.soundSmartLink;

import java.nio.ByteBuffer;

public class AudioEncoder {
	private String sendData;
	private byte[] sendbytes;
	private double[] signal_real;

	public AudioEncoder(String ssid,String pswd) {
		// TODO Auto-generated constructor stub
		this.sendData ="123456789012345678901234567890123456789" ;
		sendbytes = this.getbytes(ssid, pswd);
		encode();
	}
	private byte[] getbytes(String ssid,String pswd){
		byte[] a = ssid.getBytes();
		byte[] b = pswd.getBytes();
		ByteBuffer bf = ByteBuffer.allocate(a.length+b.length+2);
		bf.put((byte) (0x80|(a.length+b.length+1)));
		bf.put(a);
		bf.put((byte) 0x1F);
		bf.put(b);
		//bf.put((byte) 0x01);
		byte[] c = bf.array();
//		int all = 0;
//		for (int i = 0; i < c.length; i++) {
//			all+=c[i];
//		}
//		c[c.length-1] = (byte)(all|0x20&0x7f);
		int frameNum = c.length/8;
		int lastFramnum = c.length%8;
		if(lastFramnum != 0){
			frameNum ++;
		}
		ByteBuffer realdata = ByteBuffer.allocate(frameNum*10);
		for (int i = 0; i < frameNum; i++) {
			realdata.put((byte)(i+1));
			byte com = (byte) (i+1);
			for (int j = 0; j < 8; j++) {
				if(i*8+j<c.length){
					com += c[i*8+j];
					realdata.put( c[i*8+j]);
				}else{
					realdata.put((byte) 0);
					com += 0;
				}
			}
			realdata.put((byte) (0x80|com));
		}
		return realdata.array();
	}
	public void encode() {
		double base = SmtlkFreq.SMTLK_FREQU_BASE0;
		signal_real = new double[sendbytes.length*SmtlkFreq.loopNum];
		int loop = 0;
		int index = 0;
		for (int i = 0; i < sendbytes.length*SmtlkFreq.loopNum; i++) {
			
			if(loop>SmtlkFreq.loopNum){
				loop = 0;
				index++;
				if(base == SmtlkFreq.SMTLK_FREQU_BASE0){
					base = SmtlkFreq.SMTLK_FREQU_BASE1;
				}else{
					base = SmtlkFreq.SMTLK_FREQU_BASE0;
				}
			}
			byte check = 0;
			signal_real[i] = 0.05 * getcos(i, base);
			byte bt = sendbytes[index];
			if ((bt & 0x01) != 0) {
				signal_real[i] += 0.1*getcos(i, SmtlkFreq.SMTLK_FREQU_BIT0);
				check++;
			}
			if ((bt & 0x02) != 0) {
				signal_real[i] += 0.1*getcos(i, SmtlkFreq.SMTLK_FREQU_BIT1);
				check++;
			}
			if ((bt & 0x04) != 0) {
				signal_real[i] += 0.1*getcos(i, SmtlkFreq.SMTLK_FREQU_BIT2);
				check++;
			}
			if ((bt & 0x08) != 0) {
				signal_real[i] += 0.1*getcos(i, SmtlkFreq.SMTLK_FREQU_BIT3);
				check++;
			}
			if ((bt & 0x10) != 0) {
				signal_real[i] += 0.1*getcos(i, SmtlkFreq.SMTLK_FREQU_BIT4);
				check++;
			}
			if ((bt & 0x20) != 0) {
				signal_real[i] += 0.1*getcos(i, SmtlkFreq.SMTLK_FREQU_BIT5);
				check++;
			}
			if ((bt & 0x40) != 0) {
				signal_real[i] += 0.1*getcos(i, SmtlkFreq.SMTLK_FREQU_BIT6);
				check++;
			}
			if ((bt & 0x80) != 0) {
				signal_real[i] += 0.1*getcos(i, SmtlkFreq.SMTLK_FREQU_BIT7);
				check++;
			}
			
			if((check&0x01) != 0){
				signal_real[i] += 0.1*getcos(i, SmtlkFreq.SMTLK_FREQU_CHECK);
			}
			loop++;
		}
	}
	
	private double getcos(int postion,double freq){
		return Math.cos(Math.PI * 2 * freq *SmtlkFreq.FREQ_STEP* postion / SmtlkFreq.FRAME_SAP);
	}
	
	public double[] getDouble(){
		return signal_real;
	}
}

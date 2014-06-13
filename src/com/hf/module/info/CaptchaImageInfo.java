package com.hf.module.info;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.hf.lib.util.Base64;



public class CaptchaImageInfo extends CaptchaInfo{
	private String captchaStubImage = null;

	public String getCaptchaStubImage() {
		return captchaStubImage;
	}

	public void setCaptchaStubImage(String captchaStubImage) {
		this.captchaStubImage = captchaStubImage;
	}
	
	public BufferedImage getCaptchaStubBufferedImage() throws IOException{
		BufferedImage bi = null;
		if(captchaStubImage != null){
        	byte[] imageByte = Base64.decode(captchaStubImage.getBytes());
			InputStream in = new ByteArrayInputStream(imageByte);
			bi = ImageIO.read(in);
		}
		return bi;
	}
	
	public Bitmap getBitmap(){
		byte[] img = android.util.Base64.decode(this.getCaptchaStubImage(),android.util.Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
		return bitmap;
	}
}

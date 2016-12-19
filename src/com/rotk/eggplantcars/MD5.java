package com.rotk.eggplantcars;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static String getMD5(String val){  
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes());  
			byte[] m = md5.digest();//����  
			return getString(m);  
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}  
	}  

	private static String getString(byte[] b){  
		StringBuffer sb = new StringBuffer();  
		for(int i = 0; i < b.length; i ++){  
			sb.append(b[i]);  
		}  
		return sb.toString();  
	}  

}

package com.example.applicationkotlinsample.utils;

import java.security.MessageDigest;

/**
 * Md5加密方法
 * 
 * @author admin
 */
public class Md5Utils {


	private static byte[] md5(String s, String charset) {
		MessageDigest algorithm;
		try {
			algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(s.getBytes(charset));
			byte[] messageDigest = algorithm.digest();
			return messageDigest;
		} catch (Exception e) {

		}
		return null;
	}

	private static final String toHex(byte hash[]) {
		if (hash == null) {
			return null;
		}
		StringBuffer buf = new StringBuffer(hash.length * 2);
		int i;

		for (i = 0; i < hash.length; i++) {
			if ((hash[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(hash[i] & 0xff, 16));
		}
		return buf.toString();
	}

	public static String hash(String s, String charset) {
		try {
			return new String(toHex(md5(s, charset)).getBytes(charset), charset);
		} catch (Exception e) {
			return s;
		}
	}

}

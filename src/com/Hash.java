package com;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Hash {
	public static String hash(String _input) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] hashbytes = digest.digest(_input.getBytes(StandardCharsets.UTF_8));
			return bytesToHex(hashbytes);
		} catch (Exception e) {
			System.out.println("Hash error: " + e);
			return "";
		}
	}
	
	private static String bytesToHex(byte[] hash) {
	    StringBuffer hexString = new StringBuffer();
	    for (int i = 0; i < hash.length; i++) {
		    String hex = Integer.toHexString(0xff & hash[i]);
		    if(hex.length() == 1) hexString.append('0');
		        hexString.append(hex);
	    }
	    return hexString.toString();
	}
}

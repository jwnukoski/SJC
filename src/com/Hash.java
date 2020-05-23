package com;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Hash {
	public static String hash(String _input) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA3_256");
			final byte[] hashbytes = digest.digest(_input.getBytes(StandardCharsets.UTF_8));
			return bytesToHex(hashbytes);
		} catch (Exception e) {
			return "";
		}
	}
	
	// _input must be raw and _answer hashed
	public static boolean checkHash(String _input, String _answer) {
		String hashedInput = hash(_input);
		if (hashedInput.contentEquals(_answer))
			return true;
		else
			return false;
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

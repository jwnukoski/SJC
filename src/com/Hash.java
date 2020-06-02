package com;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

// Only used for server password
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

/**
 * 
 */
package com.doitnext.trng;

import java.util.Base64;
import java.util.List;

import com.doitnext.trng.ComPortEnumerator.ComPortInfo;

/**
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class Main {

	private final static int KEYS_TO_PRINT = 100;
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	

	private static void defaultAction() {
		List<ComPortInfo> cpiList = ComPortEnumerator.list();
		System.out.println("Com ports found: ");
		int x = 1;
		for(ComPortInfo cpi : cpiList) {
			String tag = (cpi.getName().equals("STM32 Virtual ComPort")) ? "*"  : "";
			System.out.println(String.format("%s\t%d) %s", tag, x++, cpi.getName()));
		}

		Trng trng = new Trng();
		System.out.println(String.format("\nPrinting %d AES 256 keys", KEYS_TO_PRINT));
		try {
			for (x = 0; x < KEYS_TO_PRINT; x++) {
				if(x % 25 == 0) {
					System.out.println("\nKey Number\tHex\t\t\t\t\t\t\t\t\tBase64");
				}
				byte[] readBuffer = trng.getRandomBytes(32);
				System.out.println(String.format("%d\t\t%s\t%s", x+1, bytesToHex(readBuffer), new String(Base64.getEncoder().encode(readBuffer))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		System.out.println("\nFinished.");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		defaultAction();
	}

}

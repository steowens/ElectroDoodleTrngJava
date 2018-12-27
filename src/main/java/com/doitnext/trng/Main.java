/**
 * 
 */
package com.doitnext.trng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doitnext.trng.ComPortEnumerator.ComPortInfo;

/**
 * @author Steve Owens (steve@doitnext.com)
 *
 */
public class Main {

	private final static int KEYS_TO_PRINT = 100;
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String,String> cmdArgs = collectArgs(args);
		if(cmdArgs.size() == 0)
			defaultAction();
		if(cmdArgs.containsKey("--help")) {
			printUsage();
		} else if(cmdArgs.containsKey("--output-file")){
			makeRandomFile(cmdArgs);
		}
	}

	
	private static String bytesToHex(byte[] bytes) {
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

	private static String testForKey(String arg) {
		if("-h".equals(arg) || "--help".equals(arg))
			return "--help";
		if("-o".equals(arg) || "--output-file".equals(arg))
			return "--output-file";
		if("-l".equals(arg) || "--length".equals(arg))
			return "--length";
		return null;
	}
	
	private static boolean expectsArgument(String arg) {
		if(arg == null || "--help".equals(arg))
			return false;
		if("--length".equals(arg) || "--output-file".equals(arg))
			return true;
		else return false;
	}
	
	private static Map<String, String> collectArgs(String[] args) {
		Map<String,String> arguments = new HashMap<String,String>();
		for(int x = 0; x < args.length; x++) {
			String key = testForKey(args[x]);
			String value = null;
			if(key == null) {
				errorUnrecognizedOption(args[x]);
			} else {
				if(expectsArgument(key)) {
					if(x >= args.length - 1) {
						errorOptionMissingArgument(key);
					} else {
						value = args[++x];
					}
				} 
				arguments.put(key, value);
			}
		}
		return arguments;
	}

	private static void makeRandomFile(Map<String, String> cmdArgs) {
		String outputFile = cmdArgs.get("--output-file");
		System.out.println(String.format("Creating randomness file: %s", outputFile));
		String strLength = cmdArgs.get("--length");
		int length = 4096;
		
		if(strLength == null) {
			System.out.println(String.format("No --length option specified defaulting to %d", length));
		} else {
			try {
				length = Integer.parseInt(strLength);
			} catch(NumberFormatException e) {
				System.out.println(String.format("Unable to parse --length argument '%s' as an integer.", strLength));
				System.exit(2);
			}
		}
		
		File f = new File(outputFile);
		try(OutputStream os = new FileOutputStream(f)) {
			Trng trng = new Trng();
			int nWritten = 0;
			int numberLeft = length - nWritten;
			int numToRead = Math.min(1024, numberLeft);
			System.out.println("Progress indicator '.' == 1024 bytes:");
			while(numToRead > 0) {
				byte[] readBuffer = trng.getRandomBytes(numToRead);
				os.write(readBuffer);
				nWritten += numToRead;
				numberLeft = length - nWritten;
				numToRead = Math.min(1024, numberLeft);
				System.out.print(".");
			}
			System.out.println(String.format("\nWrote %d bytes of randomness to %s\nFinished.\n", length, outputFile));
		} catch (FileNotFoundException e) {
			System.out.println(String.format("Invalid --output-file argument: %s", e.getMessage()));
			System.exit(3);
		} catch (IOException e) {
			System.out.println(String.format("File IO error on %s: %s", outputFile, e.getMessage()));
			System.exit(3);
		} catch (InterruptedException e) {
			System.out.println(String.format("Unexpected thread interrupt: %s", e.getMessage()));
			System.exit(4);
		}
	}



	private static void errorUnrecognizedOption(String opt) {
		System.out.println(String.format("Unrecognized command line option: %s", opt));
		printUsage();
		System.exit(1);
	}

	private static void errorOptionMissingArgument(String opt) {
		System.out.println(String.format("Option %s is missing an argument.", opt));
		printUsage();
		System.exit(1);
	}

	private static void printUsage() {
		try(InputStream is= Main.class.getResourceAsStream("/usage.txt")) {
			byte[] buffer = new byte[1024];
			int nRead = is.read(buffer);
			while(nRead != -1) {
				System.out.write(buffer, 0, nRead);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}

package com.doitnext.trng;

import java.util.List;

import com.doitnext.trng.ComPortEnumerator.ComPortInfo;
import com.fazecast.jSerialComm.SerialPort;

public class Trng {
	private static final String portName = "STM32 Virtual ComPort";
	private final SerialPort spi;

	private static SerialPort getSpi(List<ComPortInfo> cpiList, String key) {
		for (ComPortInfo cpi : cpiList) {
			if (cpi.getName().equals(key)) {
				return cpi.getSpi();
			}
		}
		return null;
	}

	public Trng() {
		List<ComPortInfo> cpiList = ComPortEnumerator.list();
		spi = getSpi(cpiList, portName);
		spi.openPort();
		if (spi == null) {
			throw new IllegalStateException("Serial port: " + portName + " not found.");
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		spi.closePort();
	}

	public byte[] getRandomBytes(int count) throws InterruptedException {
		int numberGotten = 0;
		byte[] result = new byte[count];
		for (int x = 0; x < result.length; x++)
			result[x] = 0;

		int resultIndex = 0;

		while (numberGotten < count) {

			while (spi.bytesAvailable() <= 0)
				Thread.sleep(20);

			int bytesNeeded = count - numberGotten;
			int bytesAvail = spi.bytesAvailable();
			int numberToFetch = Math.min(bytesAvail, bytesNeeded);

			byte[] readBuffer = new byte[numberToFetch];
			int numRead = spi.readBytes(readBuffer, readBuffer.length);
			for (int x = 0; x < numRead; x++) {
				result[resultIndex++] = readBuffer[x];
			}
			numberGotten += numRead;
		}

		return result;
	}

}

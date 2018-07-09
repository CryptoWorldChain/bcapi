package org.fc.brewchain.bcapi;

import java.math.BigInteger;

public class UnitUtil {
	private static BigInteger unit = new BigInteger("1000000000000000000");

	public static BigInteger fromWei(String val) throws NumberFormatException {
		BigInteger biVal = new BigInteger(val);
		return fromWei(biVal);
	}

	public static BigInteger fromWei(BigInteger val) {
		return val.divide(unit);
	}

	public static BigInteger toWei(String val) throws NumberFormatException {
		BigInteger biVal = new BigInteger(val);
		return toWei(biVal);
	}

	public static BigInteger toWei(BigInteger val) {
		return val.multiply(unit);
	}
}

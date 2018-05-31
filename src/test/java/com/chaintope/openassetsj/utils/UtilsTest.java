package com.chaintope.openassetsj.utils;

import static org.junit.Assert.*;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Test;

public class UtilsTest {

	@Test
	/**
	 * Method to test the functionality of readVarInteger()
	 */	
	public void readVarIntegerTest() {

		int value;

		value = Integer.parseInt(Utils.readVarInteger("fd0000").get(0).toString());
		assertEquals(0, value);
		value = Integer.parseInt(Utils.readVarInteger("fd1100").get(0).toString());
		assertEquals(17, value);

		value = Integer.parseInt(Utils.readVarInteger("fe00000000").get(0).toString());
		assertEquals(0, value);
		value = Integer.parseInt(Utils.readVarInteger("fe11000000").get(0).toString());
		assertEquals(17, value);
		value = Integer.parseInt(Utils.readVarInteger("fe11220000").get(0).toString());
		assertEquals(8721, value);

		value = Integer.parseInt(Utils.readVarInteger("ff0000000000000000").get(0).toString());
		assertEquals(0, value);
		value = Integer.parseInt(Utils.readVarInteger("ff1100000000000000").get(0).toString());
		assertEquals(17, value);
		value = Integer.parseInt(Utils.readVarInteger("ff1122000000000000").get(0).toString());
		assertEquals(8721, value);
		value = Integer.parseInt(Utils.readVarInteger("ff1122334400000000").get(0).toString());
		assertEquals(1144201745, value);
	}
}

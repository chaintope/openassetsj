package com.chaintope.openassetsj.utils;

import static org.junit.Assert.*;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Test;

public class Leb128Test {

	@Test
	/**
	 * Method to test the functionality of encodeLeb128()
	 */	
	public void encodeLeb128Test() {

		String encodedStr = Leb128.encodeLeb128(300);
		assertEquals("ac02", encodedStr);
	}
}

package com.chaintope.openassetsj.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class Leb128Test {

	/**
	 * Method to test the functionality of encodeLeb128()
	 */	
	@Test
	public void encodeLeb128Test() {

		String encodedStr = Leb128.encodeLeb128(300);
		assertEquals("ac02", encodedStr);
	}
}

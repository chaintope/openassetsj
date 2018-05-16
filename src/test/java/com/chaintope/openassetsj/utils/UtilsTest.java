package com.chaintope.openassetsj.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilsTest {

	@Test
	/**
	 * Method to test the functionality of pubKeyHashToAssetID()
	 */	
	public void pubKeyHashToAssetIDTest() {

		String assetId = Utils.pubKeyHashToAssetID("081522820f2ccef873e47ee62b31cb9e9267e725");
		assertEquals("oWLkUn44E45cnQtsP6x1wrvJ2iRx9XyFny", assetId);
	}

	@Test
	/**
	 * Method to test the functionality of scriptToAssetID()
	 */	
	public void scriptToAssetIDTest() {

		String assetId = Utils.scriptToAssetID("a914f9d499817e88ef7b10a88673296c6d6df2f4292d87");
		assertEquals("oMb2yzA542yQgwn8XtmGefTzBv5NJ2nDjh", assetId);
	}

	@Test
	/**
	 * Method to test the functionality of encodeLeb128()
	 */	
	public void encodeLeb128Test() {

		String encodedStr = Utils.encodeLeb128(300);
		assertEquals("ac02", encodedStr);
	}

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

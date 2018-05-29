package com.chaintope.openassetsj.utils;

import static org.junit.Assert.*;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Test;

public class UtilsTest {
	
	NetworkParameters params = TestNet3Params.get();
//	NetworkParameters params = MainNetParams.get();

	@Test
	/**
	 * Method to test the functionality of addressToOaAddress()
	 */	
	public void addressToOaAddressTest() {

		String bitcoinAddress = null;
		String expectedOaAddress = null;

		if (params.equals(MainNetParams.get())) {
			
			bitcoinAddress = "1F2AQr6oqNtcJQ6p9SiCLQTrHuM9en44H8";
			expectedOaAddress = "akQz3f1v9JrnJAeGBC4pNzGNRdWXKan4U6E";
		}
		else if (params.equals(TestNet3Params.get())) {
			
			bitcoinAddress = "mo949sdMme4nk1QeNva37yTfiXLS1LYh9N";
			expectedOaAddress = "bWy6wQ3ShF7xUcFa1RYgDmqNF48Wc23wCPq";
		}

		String actualOaAddress = Utils.addressToOaAddress(Address.fromBase58(params, bitcoinAddress));
		assertEquals(expectedOaAddress, actualOaAddress);
	}

	@Test
	/**
	 * Method to test the functionality of oaAddressToAddress()
	 */	
	public void oaAddressToAddressTest() {
		
		String oaAddress = null;
		String expectedBitcoinAddress = null;

		if (params.equals(MainNetParams.get())) {
			
			oaAddress = "akQz3f1v9JrnJAeGBC4pNzGNRdWXKan4U6E";
			expectedBitcoinAddress = "1F2AQr6oqNtcJQ6p9SiCLQTrHuM9en44H8";
		}
		else if (params.equals(TestNet3Params.get())) {
			
			oaAddress = "bWy6wQ3ShF7xUcFa1RYgDmqNF48Wc23wCPq";
			expectedBitcoinAddress = "mo949sdMme4nk1QeNva37yTfiXLS1LYh9N";
		}
		
		String actualBitcoinAddress = Utils.oaAddressToAddress(oaAddress).toBase58();
		assertEquals(expectedBitcoinAddress, actualBitcoinAddress);
	}


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

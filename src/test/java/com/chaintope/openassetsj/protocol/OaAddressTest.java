package com.chaintope.openassetsj.protocol;

import static org.junit.Assert.*;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Test;

public class OaAddressTest {
	
	private NetworkParameters params = TestNet3Params.get();
	private OaAddress openAssetAddress = new OaAddress(params);

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

		String actualOaAddress = openAssetAddress.addressToOaAddress(Address.fromBase58(params, bitcoinAddress));
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
		
		String actualBitcoinAddress = openAssetAddress.oaAddressToAddress(oaAddress).toBase58();
		assertEquals(expectedBitcoinAddress, actualBitcoinAddress);
	}
}

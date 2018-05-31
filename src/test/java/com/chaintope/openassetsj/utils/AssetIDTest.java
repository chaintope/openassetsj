package com.chaintope.openassetsj.utils;

import static org.junit.Assert.*;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Test;

public class AssetIDTest {

	private NetworkParameters params = TestNet3Params.get();
	private AssetID assetID = new AssetID(params);

	@Test
	/**
	 * Method to test the functionality of pubKeyHashToAssetID()
	 */	
	public void pubKeyHashToAssetIDTest() {

		String assetId = assetID.pubKeyHashToAssetID("081522820f2ccef873e47ee62b31cb9e9267e725");
		assertEquals("oWLkUn44E45cnQtsP6x1wrvJ2iRx9XyFny", assetId);
	}

	@Test
	/**
	 * Method to test the functionality of scriptToAssetID()
	 */	
	public void scriptToAssetIDTest() {

		String assetId = assetID.scriptToAssetID("a914f9d499817e88ef7b10a88673296c6d6df2f4292d87");
		assertEquals("oMb2yzA542yQgwn8XtmGefTzBv5NJ2nDjh", assetId);
//		assertEquals(params.getId(), assetId);
	}
}

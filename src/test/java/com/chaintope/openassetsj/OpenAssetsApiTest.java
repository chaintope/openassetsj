package com.chaintope.openassetsj;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;

import org.junit.Test;

import com.chaintope.openassetsj.helper.WalletInitializer;
import com.chaintope.openassetsj.model.ColoredOutput;
import com.chaintope.openassetsj.model.OaConfig;
import com.chaintope.openassetsj.model.Rpc;
import com.chaintope.openassetsj.model.UnspentOutputs;

public class OpenAssetsApiTest {
	
	WalletAppKit walletAppKit;

  OpenAssetsApi oaApi;
	OaConfig oaConfig;

	public OpenAssetsApiTest() {

		NetworkParameters params = TestNet3Params.get();

		WalletInitializer walletInitializer = WalletInitializer.getInstance(params);
		walletAppKit = walletInitializer.getWalletAppKit();

		this.oaApi = new OpenAssetsApi(params, walletAppKit,
				10000, new Rpc("username", "password", 18333, "localhost"));
		
		oaConfig = oaApi.getConfiguration();
	}

	/**
	 * Method to test the functionality of issueAssets()
	 */
	@Test
	public void issueAssetsTest() {

		Transaction issuanceTransaction = null;
		
		List<Long> assetQuantities = new ArrayList<>();
		assetQuantities.add(8000L);
		assetQuantities.add(11000L);
		assetQuantities.add(6000L);

		issuanceTransaction = oaApi.issueAssets(
				walletAppKit.wallet().getIssuedReceiveKeys().get(0),
				assetQuantities,
				"u=https://cpr.sm/5YgSU1Pg-q");

		assertEquals(1, issuanceTransaction.getInputs().size());
		assertEquals(5, issuanceTransaction.getOutputs().size());

		try {

			assertEquals(1, issuanceTransaction.getInputs().size());
			assertEquals(5, issuanceTransaction.getOutputs().size());

			UnspentOutputs unspentOutputs = new UnspentOutputs(oaConfig);

			List<ColoredOutput> coloredOutputs = unspentOutputs.getColoredOutputs(issuanceTransaction);
			assertEquals(3, coloredOutputs.size());

			List<TransactionOutput> uncoloredOutputs = unspentOutputs.getUncoloredOutputs(issuanceTransaction);
			assertEquals(1, uncoloredOutputs.size());
		}
		catch(VerificationException ex) {

			fail("Exception occurred while verifying the issuance transaction");
		}
	}

	/**
	 * Method to test the functionality of getAssetsBalance()
	 */
	@Test
	public void getAssetsBalanceTest() {

		long assetsBalance = oaApi.getAssetsBalance();
		assertEquals(25000, assetsBalance);
	}	
}

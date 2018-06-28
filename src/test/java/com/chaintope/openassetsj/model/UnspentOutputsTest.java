package com.chaintope.openassetsj.model;

import static org.junit.Assert.*;

import java.util.List;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Test;

import com.chaintope.openassetsj.OpenAssetsApi;
import com.chaintope.openassetsj.helper.WalletInitializer;
import com.chaintope.openassetsj.utils.Utils;

public class UnspentOutputsTest {
	
	WalletAppKit walletAppKit;
	OpenAssetsApi oaApi;
	OaConfig oaConfig;
	
	public UnspentOutputsTest() {

		NetworkParameters params = TestNet3Params.get();

		WalletInitializer walletInitializer = WalletInitializer.getInstance(params);
		walletAppKit = walletInitializer.getWalletAppKit();

		this.oaApi = new OpenAssetsApi(params, walletAppKit,
				10000, new Rpc("username", "password", 18333, "localhost"));
		
		oaConfig = oaApi.getConfiguration();
	}
	
	/**
	 * Method to test the functionality of getColoredOutputs()
	 */
	@Test
	public void getColoredOutputsTest() {
		
		UnspentOutputs unspentOutputs;
		List<ColoredOutput> coloredOutputs;
		
		/* OA transaction having colored outputs */

		unspentOutputs = new UnspentOutputs(this.oaConfig);
		Transaction oaTransaction = new Transaction(oaConfig.params,
				Utils.packHexStringToBytes("0100000001a9e40894163fd5596846ab7155cb80f4d8f43dd7544705ee9e351f728b28bfc5040000006a47304402207a1b473aff68e7b8491c63fba8e93eee8472c73aa9769fb3f30634871cd957e102204fc95aad0014c1653cd8cd0fca1685180895e653d2c2c7b6eff4ff6bd2608e22012103ff23cf40fd0187aa4fd35fcad456237a2466d61fa5d5b97223b9851570a331a7ffffffff0522020000000000001976a914336eed1dddcc905ed80e446a40b4a0c273b5473488ac22020000000000001976a914336eed1dddcc905ed80e446a40b4a0c273b5473488ac22020000000000001976a914336eed1dddcc905ed80e446a40b4a0c273b5473488ac0000000000000000296a274f41010003c03ef855f02e1b753d68747470733a2f2f6370722e736d2f35596753553150672d710c36bf07000000001976a914336eed1dddcc905ed80e446a40b4a0c273b5473488ac00000000"));
		coloredOutputs = unspentOutputs.getColoredOutputs(oaTransaction);
		
		assertEquals(3, coloredOutputs.size());
		
		ColoredOutput coloredOutput = coloredOutputs.get(0);
		assertEquals(8000, coloredOutput.getAssetQuantity());
		assertEquals("u=https://cpr.sm/5YgSU1Pg-q", coloredOutput.getMetadata());
		assertEquals(0, coloredOutput.getTransactionOutput().getIndex());
		
		coloredOutput = coloredOutputs.get(1);
		assertEquals(11000, coloredOutput.getAssetQuantity());
		assertEquals("u=https://cpr.sm/5YgSU1Pg-q", coloredOutput.getMetadata());
		assertEquals(1, coloredOutput.getTransactionOutput().getIndex());
		
		coloredOutput = coloredOutputs.get(2);
		assertEquals(6000, coloredOutput.getAssetQuantity());
		assertEquals("u=https://cpr.sm/5YgSU1Pg-q", coloredOutput.getMetadata());
		assertEquals(2, coloredOutput.getTransactionOutput().getIndex());

		
		/* Normal transaction without colored outputs */
		
		unspentOutputs = new UnspentOutputs(oaConfig);
		coloredOutputs = null;

		Transaction normalTransaction = new Transaction(oaConfig.params,
				Utils.packHexStringToBytes("0200000001370fe6bca97bf3a39bf892dd2c057c723e27e20c03880298432b0325b61f1157010000006a473044022042cd3831046ac35cc6836c3bbb082a117d6d44ede445fc9b2a7d839021b57320022039358428ff59d1af1f59f9adb3ac04480f08f45aed88f4bb1304e7f3e60e2ff7012102c509e0dff008ba92b8e018881262b76e79697b3ebec08f4d61dfa43e9319ab39feffffff027a733805000000001976a914fccd4686c00cea3a11fa199f3fa2ae3b70ec60f988ace5ed559d060000001976a914faac464bbaf1f2189a110e30f26dc246765a712f88aca53a1400"));
		coloredOutputs = unspentOutputs.getColoredOutputs(normalTransaction);
		
		assertEquals(0, coloredOutputs.size());
	}
	
	/**
	 * Method to test the functionality of getUncoloredOutputs()
	 */
	@Test
	public void getUncoloredOutputsTest() {
		
		UnspentOutputs unspentOutputs;
		List<TransactionOutput> uncoloredOutputs;
		
		/* OA transaction having colored outputs */

		unspentOutputs = new UnspentOutputs(this.oaConfig);
		Transaction oaTransaction = new Transaction(oaConfig.params,
				Utils.packHexStringToBytes("0100000001a9e40894163fd5596846ab7155cb80f4d8f43dd7544705ee9e351f728b28bfc5040000006a47304402207a1b473aff68e7b8491c63fba8e93eee8472c73aa9769fb3f30634871cd957e102204fc95aad0014c1653cd8cd0fca1685180895e653d2c2c7b6eff4ff6bd2608e22012103ff23cf40fd0187aa4fd35fcad456237a2466d61fa5d5b97223b9851570a331a7ffffffff0522020000000000001976a914336eed1dddcc905ed80e446a40b4a0c273b5473488ac22020000000000001976a914336eed1dddcc905ed80e446a40b4a0c273b5473488ac22020000000000001976a914336eed1dddcc905ed80e446a40b4a0c273b5473488ac0000000000000000296a274f41010003c03ef855f02e1b753d68747470733a2f2f6370722e736d2f35596753553150672d710c36bf07000000001976a914336eed1dddcc905ed80e446a40b4a0c273b5473488ac00000000"));
		uncoloredOutputs = unspentOutputs.getUncoloredOutputs(oaTransaction);
		
		assertEquals(1, uncoloredOutputs.size());
		
		TransactionOutput uncoloredOutput = uncoloredOutputs.get(0);
		assertEquals(4, uncoloredOutput.getIndex());
		assertEquals(129971724, uncoloredOutput.getValue().value);
		assertEquals("mkCue8LwpiXQFhbNWDKhxdEzEs8GD2LbCg", uncoloredOutput.getAddressFromP2PKHScript(oaConfig.params).toBase58());
		assertEquals("76a914336eed1dddcc905ed80e446a40b4a0c273b5473488ac", Utils.packByteArrayToString(uncoloredOutput.getScriptBytes()));

		/* Normal transaction without colored outputs */

		unspentOutputs = new UnspentOutputs(oaConfig);
		uncoloredOutputs = null;

		Transaction normalTransaction = new Transaction(oaConfig.params,
				Utils.packHexStringToBytes("0200000001370fe6bca97bf3a39bf892dd2c057c723e27e20c03880298432b0325b61f1157010000006a473044022042cd3831046ac35cc6836c3bbb082a117d6d44ede445fc9b2a7d839021b57320022039358428ff59d1af1f59f9adb3ac04480f08f45aed88f4bb1304e7f3e60e2ff7012102c509e0dff008ba92b8e018881262b76e79697b3ebec08f4d61dfa43e9319ab39feffffff027a733805000000001976a914fccd4686c00cea3a11fa199f3fa2ae3b70ec60f988ace5ed559d060000001976a914faac464bbaf1f2189a110e30f26dc246765a712f88aca53a1400"));
		uncoloredOutputs = unspentOutputs.getUncoloredOutputs(normalTransaction);
		
		assertEquals(1, uncoloredOutputs.size());
		
		uncoloredOutput = uncoloredOutputs.get(0);
		assertEquals(0, uncoloredOutput.getIndex());
		assertEquals(87585658, uncoloredOutput.getValue().value);
		assertEquals("n4ZeVBn4u2jsEfekH2gkTQYCRoavUxMUwy", uncoloredOutput.getAddressFromP2PKHScript(oaConfig.params).toBase58());
		assertEquals("76a914fccd4686c00cea3a11fa199f3fa2ae3b70ec60f988ac", Utils.packByteArrayToString(uncoloredOutput.getScriptBytes()));
	}
}

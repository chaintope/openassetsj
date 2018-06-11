package com.chaintope.openassetsj;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import com.chaintope.openassetsj.helper.OpenAssetsHelper;
import com.chaintope.openassetsj.model.OaConfig;
import com.chaintope.openassetsj.model.Rpc;

/**
 * Instantiates an OpenAssetsJ API, and provides commonly used OpenAssets operations
 * 
 */
public class OpenAssetsApi {

	private OaConfig oaConfig;
	private OpenAssetsHelper oaHelper;

	public OpenAssetsApi(NetworkParameters params, WalletAppKit walletAppKit, long minTransactionFees,
				Rpc rpc) {

		this.oaConfig = new OaConfig(
			params,
			walletAppKit,
			rpc,
			minTransactionFees
		);

		this.oaHelper = new OpenAssetsHelper(oaConfig);
	}

	public OpenAssetsApi(String network, WalletAppKit walletAppKit, long minTransactionFees,
				String rpcUsername, String rpcPassword, int rpcPort, String rpcHost) {

		this.oaConfig = new OaConfig(
			getNetworkParameters(network),
			walletAppKit,
			new Rpc(rpcUsername, rpcPassword, rpcPort, rpcHost),
			minTransactionFees
		);

		this.oaHelper = new OpenAssetsHelper(oaConfig);
	}

	/**
	 * Sets appropriate network parameters according to the network name
	 */
	private NetworkParameters getNetworkParameters(String network) {
		
		switch(network) {
			case "mainnet":
				return MainNetParams.get();
			case "testnet":
				return TestNet3Params.get();
			default:
				return null;
//			TODO: Create new exception class for invalid network - InvalidNetworkException
//			default:
//				throw new InvalidNetworkException();
		}
	}
	
	/**
	 * Creates a transaction to issue assets
	 * @return Issuance transaction
	 */
	public Transaction issueAssets(ECKey fromKey, Coin amount, String to, int outputQty, String metadata) {
		// TODO: Create asset issuance transaction
		return oaHelper.issueAssets(fromKey, amount, to, outputQty, metadata);
	}

	/**
	 * Creates a transaction to transfer assets
	 * @return Asset transfer transaction
	 */
	public Transaction transferAssets() {
		// TODO: Create asset transfer transaction
		return new Transaction(oaConfig.params);
	}

	/**
	 * Creates a transaction to burn assets
	 * @return Asset transfer transaction
	 */
	public Transaction burnAssets() {
		// TODO: Create asset transfer transaction
		return new Transaction(oaConfig.params);
	}

	/**
	 * Gets assets balance for particular OA address
	 * @return Assets balance quantity
	 */
	public int getAssetsBalance(String oaAddress) {
		
		if (oaAddress == null) {
			// TODO: Calculate asset balance for all addresses
		}
		else {
			// TODO: Calculate asset balance for particular address
		}
		return 0;
	}

	/**
	 * Gets total assets balance
	 * @return Assets balance quantity
	 */
	public int getAssetsBalance() {
		// TODO: Calculate assets balance for all the address in a wallet
		return getAssetsBalance(null);
	}
	
}




























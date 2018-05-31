package com.chaintope.openassetsj;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import com.chaintope.openassetsj.model.Rpc;

/**
 * Instantiates an OpenAssetsJ API, and provides commonly used OpenAssets operations
 * 
 */
public class OpenAssetsApi {

	private NetworkParameters params;
	private WalletAppKit walletAppKit;
	private Rpc rpc;

	public OpenAssetsApi(String network, String rpcUsername, String rpcPassword, int rpcPort, String rpcHost, WalletAppKit walletAppKit) {

		this.walletAppKit = walletAppKit;
		rpc = new Rpc(rpcUsername, rpcPassword, rpcPort, rpcHost);
		setNetworkParameters(network);
	}

	/**
	 * Sets appropriate network parameters according to the network name
	 */
	private void setNetworkParameters(String network) {
		
		switch(network) {
			case "mainnet":
				params = MainNetParams.get();
				break;
			case "testnet":
				params = TestNet3Params.get();
				break;
//			TODO: Create new exception class for invalid network - InvalidNetworkException
//			default:
//				throw new InvalidNetworkException();
		}
	}
	
	/**
	 * Creates a transaction to issue assets
	 * @return Issuance transaction
	 */
	public Transaction issueAssets() {
		// TODO: Create asset issuance transaction
		return new Transaction(this.params);
	}

	/**
	 * Creates a transaction to transfer assets
	 * @return Asset transfer transaction
	 */
	public Transaction transferAssets() {
		// TODO: Create asset transfer transaction
		return new Transaction(this.params);
	}

	/**
	 * Creates a transaction to burn assets
	 * @return Asset transfer transaction
	 */
	public Transaction burnAssets() {
		// TODO: Create asset transfer transaction
		return new Transaction(this.params);
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




























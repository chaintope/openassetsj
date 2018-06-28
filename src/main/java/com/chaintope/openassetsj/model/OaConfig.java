package com.chaintope.openassetsj.model;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;

/**
 * Contains properties necessary to initialize OpenAssetsJ library
 */
public class OaConfig {

	public NetworkParameters params;
	public WalletAppKit walletAppKit;
	public Rpc rpc;
	public long minTransactionFees;

	public OaConfig(NetworkParameters params, WalletAppKit walletAppKit, Rpc rpc, long minTransactionFees) {

		this.params = params;
		this.walletAppKit = walletAppKit;
		this.rpc = rpc;
		this.minTransactionFees = minTransactionFees;
	}
}

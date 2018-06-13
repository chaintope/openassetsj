package com.chaintope.openassetsj;

import java.util.List;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import com.chaintope.openassetsj.helper.OpenAssetsHelper;
import com.chaintope.openassetsj.model.OaConfig;
import com.chaintope.openassetsj.model.Rpc;
import com.chaintope.openassetsj.model.TransferParameters;

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
	 * Returns appropriate network parameters according to the network name
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
	public Transaction issueAssets(
			ECKey fromKey,
			String toAddress,
			String changeAddress,
			long transactionAmount,
			long fees,
			long totalAssetQuantity,
			int noOfOutputs,
			String metadata) {

		noOfOutputs = (noOfOutputs <= 0 ? 1 : noOfOutputs);

		String fromAddress = fromKey.toAddress(oaConfig.params).toBase58();
		List<TransactionOutput> unspentOutputs = oaHelper.getUnspentOutputs(fromAddress);

		TransferParameters issueParams = new TransferParameters(unspentOutputs,
				fromKey, toAddress, changeAddress,
				totalAssetQuantity, noOfOutputs);
		
		return oaHelper.issueAssets(issueParams, metadata, fees);
	}

	/**
	 * Creates a transaction to issue assets
	 * @return Issuance transaction
	 */
	public Transaction issueAssets(
			ECKey fromKey,
			long totalAssetQuantity,
			int noOfOutputs,
			String metadata) {

		String fromAddress = fromKey.toAddress(oaConfig.params).toBase58();
		String toAddress = fromAddress;
		String changeAddress = fromAddress;

		noOfOutputs = (noOfOutputs <= 0 ? 1 : noOfOutputs);
		
		List<TransactionOutput> unspentOutputs = oaHelper.getUnspentOutputs(fromAddress);

		TransferParameters issueParams = new TransferParameters(unspentOutputs,
				fromKey, toAddress, changeAddress,
				totalAssetQuantity, noOfOutputs);
		
		return oaHelper.issueAssets(issueParams, metadata, oaConfig.minTransactionFees);
	}

	/**
	 * Creates a transaction to issue assets
	 * @return Issuance transaction
	 */
	public Transaction issueAssets(
			ECKey fromKey,
			List<Long> assetQuantities,
			String metadata) {

		String fromAddress = fromKey.toAddress(oaConfig.params).toBase58();
		String toAddress = fromAddress;
		String changeAddress = fromAddress;
		
		List<TransactionOutput> unspentOutputs = oaHelper.getUnspentOutputs(fromAddress);

		TransferParameters issueParams = new TransferParameters(unspentOutputs,
				fromKey, toAddress, changeAddress,
				assetQuantities);
		
		return oaHelper.issueAssets(issueParams, metadata, oaConfig.minTransactionFees);
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
		// TODO: Create asset burn transaction
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




























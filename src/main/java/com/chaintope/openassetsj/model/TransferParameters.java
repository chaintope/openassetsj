package com.chaintope.openassetsj.model;

import java.util.List;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.TransactionOutput;

import com.chaintope.openassetsj.utils.Utils;

public class TransferParameters {

    public UnspentOutputs unspentOutputs;
    public ECKey fromKey;
    public String receiversAddress;
    public String changeAddress;
    public List<Long> assetQuantityList;
    public long totalAssetQuantity;
    public int noOfOutputs;

    /**
     * Constructor to initialize TransferParameters using assetQuantityList
     * This allows to provide unequal asset quantities
     * @param unspentOutputs
     * @param fromKey
     * @param receiversAddress
     * @param changeAddress
     * @param transactionAmount
     * @param fees
     * @param assetQuantityList
     */
	public TransferParameters(
			UnspentOutputs unspentOutputs,
			ECKey fromKey,
			String receiversAddress,
			String changeAddress,
			List<Long> assetQuantityList) {

		this.unspentOutputs = unspentOutputs;
		this.fromKey = fromKey;
		this.receiversAddress = receiversAddress;
		this.changeAddress = changeAddress;
		this.assetQuantityList = assetQuantityList;
		this.noOfOutputs = assetQuantityList.size();
		this.totalAssetQuantity = 0;
		for(long assetQuantity : assetQuantityList) {
			this.totalAssetQuantity += assetQuantity;
		}
	}

	/**
     * Constructor to initialize TransferParameters using totalAssetQuantity and noOfOutputs
     * totalAssetQuantity will be equally divided into noOfOutputs
	 * @param unspentOutputs
	 * @param fromKey
	 * @param receiversAddress
	 * @param changeAddress
	 * @param totalAssetQuantity
	 * @param noOfOutputs
	 */
	public TransferParameters(
			UnspentOutputs unspentOutputs,
			ECKey fromKey,
			String receiversAddress,
			String changeAddress,
			long totalAssetQuantity,
			int noOfOutputs) {

		this.unspentOutputs = unspentOutputs;
		this.fromKey = fromKey;
		this.receiversAddress = receiversAddress;
		this.changeAddress = changeAddress;
		this.totalAssetQuantity = totalAssetQuantity;
		this.noOfOutputs = noOfOutputs;
		this.assetQuantityList = Utils.splitEqually(totalAssetQuantity, noOfOutputs);
	}
    
    
}

package com.chaintope.openassetsj.helper;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import com.chaintope.openassetsj.model.ColoredOutput;
import com.chaintope.openassetsj.model.OaConfig;
import com.chaintope.openassetsj.model.TransferParameters;
import com.chaintope.openassetsj.model.UnspentOutputs;
import com.chaintope.openassetsj.protocol.MarkerOutput;

public class OpenAssetsHelper {

    private OaConfig oaConfig;

    public OpenAssetsHelper(OaConfig oaConfig){

        this.oaConfig = oaConfig;
    }

    /**
     * Creates OpenAssets issuance transaction
     * @param issueParams Parameters required to issue an asset
     * @param metadata Metadata to be embedded in the issuance transaction
     * @param fees Fees to be included in transaction
     * @return Signed issuance transaction
     */
    public Transaction issueAssets(TransferParameters issueParams, String metadata, long fees) {

        Transaction issuanceTransaction = new Transaction(oaConfig.params);
        long amountRequired = issueParams.assetQuantityList.size() * Transaction.MIN_NONDUST_OUTPUT.value + fees; 

        List<TransactionOutput> uncoloredOutputs = issueParams.unspentOutputs.getUncoloredOutputs(amountRequired);
        long totalAmount = issueParams.unspentOutputs.getUncoloredOutputAmount();

        for (long assetQuantity :issueParams.assetQuantityList) {

        	issuanceTransaction.addOutput(
        			createColoredOutput(
        					Address.fromBase58(oaConfig.params, issueParams.receiversAddress),
        					Transaction.MIN_NONDUST_OUTPUT.value));
        }
        
        issuanceTransaction.addOutput(Coin.ZERO, createMarkerOutput(issueParams.assetQuantityList, metadata));

        long changeAmount = totalAmount -
        		(issueParams.assetQuantityList.size() * Transaction.MIN_NONDUST_OUTPUT.value + fees);

        if (changeAmount > 0) {

        	issuanceTransaction.addOutput(
        			Coin.valueOf(changeAmount),
        			ScriptBuilder.createOutputScript(
        					issueParams.fromKey.toAddress(oaConfig.params)));
        }

        for (TransactionOutput unspentTx :uncoloredOutputs) {

        	issuanceTransaction.addSignedInput(unspentTx, issueParams.fromKey);
        }

        return issuanceTransaction;
    }

    /**
     * Creates marker output
     * @param assetQuantityList list of asset quantities to issue
     * @param metadata metadata to pass through marker output
     * @return Script which contains marker output- op_return and data
     */
    private Script createMarkerOutput(List<Long> assetQuantityList, String metadata){

        MarkerOutput markerOutput = new MarkerOutput(assetQuantityList, metadata);
        return markerOutput.buildScript();
    }

    /**
     * create colored output
     * @param addr output bitcoin address
     * @param amount amount to send
     * @return TransactionOutput object
     */
    private TransactionOutput createColoredOutput(Address addr, long amount){

        TransactionOutput txout = new TransactionOutput(oaConfig.params, null, Coin.valueOf(amount), addr);
        return txout;
    }

    /**
     * get the unspent asset balance
     * @return
     */
    public long getAssetQuantityBalance() {

        long assetQuantities = 0L;
        UnspentOutputs unspentOutputs = new UnspentOutputs(oaConfig);
        List<ColoredOutput> unspentColoredOutputs = unspentOutputs.getColoredOutputs();

        for(ColoredOutput coloredOutput: unspentColoredOutputs) {

            assetQuantities += coloredOutput.getAssetQuantity();
        }

        return assetQuantities;
    }
}

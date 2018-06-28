package com.chaintope.openassetsj.model;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;

import com.chaintope.openassetsj.protocol.MarkerOutput;

public class UnspentOutputs {
	
	private OaConfig oaConfig;

	private List<TransactionOutput> unspentOutputs = null;
	private long unspentOutputAmount;

	private List<ColoredOutput> coloredOutputs;
	private long coloredOutputAmount;

	private List<TransactionOutput> uncoloredOutputs;
	private long uncoloredOutputAmount;

	public UnspentOutputs(OaConfig oaConfig) {

		this.oaConfig = oaConfig;
		this.unspentOutputs = new ArrayList<>();
		unspentOutputAmount = 0L;
		this.coloredOutputs = new ArrayList<>();
		coloredOutputAmount = 0L;
		this.uncoloredOutputs = new ArrayList<>();
		uncoloredOutputAmount = 0L;
	}
	
    public long getUnspentOutputAmount() {
		return unspentOutputAmount;
	}



	public long getColoredOutputAmount() {
		return coloredOutputAmount;
	}



	public long getUncoloredOutputAmount() {
		return uncoloredOutputAmount;
	}




	/**
     * Get all unspent outputs
     * @return - List of all unspent TransactionOutput
     */
    public List<TransactionOutput> getUnspentOutputs() {

        return getUnspentOutputs(null);
    }

    /**
     * Get all unspent outputs for particular address
     * @param address - Base58 encoded bitcoin address
     * @return - List of unspent TransactionOutput for that address
     */
    public List<TransactionOutput> getUnspentOutputs(String address) {

        List<TransactionOutput> allUnspentOutputs = oaConfig.walletAppKit.wallet().getUnspents();
        if (null == address || address.isEmpty()) {
            this.unspentOutputs = allUnspentOutputs;
        }
        else {
            for (TransactionOutput txOut : allUnspentOutputs) {
                Script script = txOut.getScriptPubKey();
                Address outputAddress = null;

                if (script.isSentToAddress()) {
                    outputAddress = txOut.getAddressFromP2PKHScript(oaConfig.params);
                }
                else if (script.isPayToScriptHash()) {
                    outputAddress = txOut.getAddressFromP2SH(oaConfig.params);
                }

                if (script.isSentToAddress() && (outputAddress != null) && (outputAddress.toBase58().equals(address))) {
                    this.unspentOutputs.add(txOut);
                }
            }
        }
        return this.unspentOutputs;
    }
    
    /**
     * Get uncolored outputs
     * @param amountRequired Total amount required for uncolored outputs
     * @return List of uncolored outputs
     */
    public List<TransactionOutput> getUncoloredOutputs(long amountRequired) {
    
    	this.unspentOutputs = getUnspentOutputs();
    	return getUncoloredOutputs(this.unspentOutputs, amountRequired);
    }

    /**
     * Get all uncolored outputs from a transaction
     * @param transaction Transaction from which uncolored outputs should be retrieved
     * @return List of uncolored outputs in that transaction
     */
    public List<TransactionOutput> getUncoloredOutputs(Transaction transaction) {
    
    	return getUncoloredOutputs(transaction, -1);
    }

    /**
     * Get uncolored outputs from the unspent outputs
     * @param unspentOutputs Unspent outputs
     * @param amountRequired Total amount required for uncolored outputs
     * @return List of uncolored outputs
     */
    public List<TransactionOutput> getUncoloredOutputs(List<TransactionOutput> unspentOutputs, long amountRequired) {

        List<String> transactionsVisited = new ArrayList<>();

        for (TransactionOutput tout : unspentOutputs) {
        	
        	Transaction tx = tout.getParentTransaction();
        	
        	// to make sure that we don't visit same transaction again for different TxOut
        	if (!transactionsVisited.contains(tx.getHashAsString())) {

        		transactionsVisited.add(tx.getHashAsString());
            	getUncoloredOutputs(tx, amountRequired);
        	}
        	
        	if (this.uncoloredOutputAmount >= amountRequired) {
        		break;
        	}
        }
        
        // return empty list if there is not sufficient balance in the wallet
        if (this.uncoloredOutputAmount < amountRequired) {
//        	TODO: throw exception - InsufficientMoneyException
//        	throw new InsufficientMoneyException(Coin.valueOf(amountRequired - amount),
//        			"Wallet has insufficient balance");
        	this.uncoloredOutputs = new ArrayList<>();
        	this.uncoloredOutputAmount = 0L;
        }

        return this.uncoloredOutputs;
    }

    /**
     * Get uncolored outputs in particular transaction
     * @param transaction Transaction to fetch the outputs from.
     * @param amountRequired Total amount required for uncolored outputs
     * @return List of all the unspent uncolored outputs in that transaction
     */
    public List<TransactionOutput> getUncoloredOutputs(Transaction transaction, long amountRequired) {

        // Get all transaction outputs
        List<TransactionOutput> transactionOutputs = transaction.getOutputs();

        TransactionOutput markerOutputTxOut = null;

        // Check if transaction contains marker output
        for (TransactionOutput output : transactionOutputs) {

            Script script = output.getScriptPubKey();

            if (script.isOpReturn()) {

                markerOutputTxOut = output;
                break;
            }
        }

        if ( null != markerOutputTxOut ) {

            // Deserialize marker output, and get the asset quantities

            MarkerOutput markerOutput = new MarkerOutput();
            String parsedScript = markerOutput.parseScript(markerOutputTxOut.getScriptBytes());

            markerOutput = markerOutput.deserializePayload(parsedScript);
            List<Long> assetQuantities = markerOutput.getAssetQuantities();
            int noOfAssetQuantities = assetQuantities.size();

            // Loop through all the transaction outputs, and get uncolored outputs out of it

            int outputIndex = 0;
            for (TransactionOutput output : transactionOutputs) {

                // Break if all the asset quantities are assigned to outputs
                if (outputIndex <= noOfAssetQuantities) {
                	outputIndex++;
                    continue;
                }

                // Skip the marker output
                Script script = output.getScriptPubKey();
                if (script.isOpReturn()) {
                    continue;
                }

                // Check if transaction output is mine
                if (output.isMine(oaConfig.walletAppKit.wallet())) {

                    // Check if the transaction output is spent
                    TransactionInput input = output.getSpentBy();
                    if (input == null) {

                        // Add unspent transaction output to the collection
                    	this.uncoloredOutputs.add(output);
                        this.uncoloredOutputAmount += output.getValue().value;
                    }
                }
                outputIndex++;
                
                if ((amountRequired > 0) && (this.uncoloredOutputAmount >= amountRequired)) {

                	break;
                }
            }
        }
        else {
            for (TransactionOutput output : transactionOutputs) {

                // Check if transaction output is mine
                if (output.isMine(oaConfig.walletAppKit.wallet())) {

                    // Check if the transaction output is spent
                    TransactionInput input = output.getSpentBy();
                    if (input == null) {

                        // Add unspent transaction output to the collection
                        this.uncoloredOutputs.add(output);
                        this.uncoloredOutputAmount += output.getValue().value;
                    }
                }
                
                if ((amountRequired > 0) && (this.uncoloredOutputAmount >= amountRequired)) {

                	break;
                }
            }
        }
    	
        return this.uncoloredOutputs;
    }

    /**
     * method to get all unspent transaction with op_return (OpenAsset marker output)
     * @return List of colored TransactionOutput
     */
    public List<ColoredOutput> getColoredOutputs() {

        for (Transaction tx : oaConfig.walletAppKit.wallet().getTransactions(false)) {

            getColoredOutputs(tx);
        }
        return this.coloredOutputs;
    }

    /**
     * Get colored outputs in particular transaction
     * @param transaction
     * @return List of all the unspent colored outputs
     */
    public List<ColoredOutput> getColoredOutputs(Transaction transaction) {

        // Get all transaction outputs
        List<TransactionOutput> transactionOutputs = transaction.getOutputs();

        TransactionOutput markerOutputTxOut = null;

        // Check if transaction contains marker output
        for (TransactionOutput output : transactionOutputs) {

            Script script = output.getScriptPubKey();

            if (script.isOpReturn()) {

                markerOutputTxOut = output;
                break;
            }
        }

        if ( null != markerOutputTxOut ) {

            // Deserialize marker output, and get the asset quantities

            MarkerOutput markerOutput = new MarkerOutput();
            String parsedScript = markerOutput.parseScript(markerOutputTxOut.getScriptBytes());

            markerOutput = markerOutput.deserializePayload(parsedScript);
            List<Long> assetQuantities = markerOutput.getAssetQuantities();
            int noOfAssetQuantities = assetQuantities.size();

            // Loop through all the transaction outputs, and get colored outputs out of it

            int assetQuantityIndex = 0;
            for (TransactionOutput output : transactionOutputs) {

                // Break if all the asset quantities are assigned to outputs
                if (assetQuantityIndex >= noOfAssetQuantities) {
                    break;
                }

                // Skip the marker output
                Script script = output.getScriptPubKey();
                if (script.isOpReturn()) {
                    continue;
                }

                // Check if transaction output is mine
                if (output.isMine(oaConfig.walletAppKit.wallet())) {

                    // Check if the transaction output is spent
                    TransactionInput input = output.getSpentBy();
                    if (input == null) {

                        // Add unspent transaction output to the collection
                        ColoredOutput coloredOutput = new ColoredOutput(output, assetQuantities.get(assetQuantityIndex), markerOutput.getMetadata());
                        this.coloredOutputs.add(coloredOutput);
                    }
                }
                assetQuantityIndex++;
            }
        }

        return this.coloredOutputs;
    }
}

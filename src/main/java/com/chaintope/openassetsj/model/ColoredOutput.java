package com.chaintope.openassetsj.model;

import org.bitcoinj.core.TransactionOutput;

public class ColoredOutput {

    TransactionOutput transactionOutput;
    long assetQuantity;
    String metadata;

    public ColoredOutput(TransactionOutput transactionOutput, long assetQuantity, String metadata) {
        this.transactionOutput = transactionOutput;
        this.assetQuantity = assetQuantity;
        this.metadata = metadata;
    }

    public TransactionOutput getTransactionOutput() {
        return transactionOutput;
    }

    public void setTransactionOutput(TransactionOutput transactionOutput) {
        this.transactionOutput = transactionOutput;
    }

    public long getAssetQuantity() {
        return assetQuantity;
    }

    public void setAssetQuantity(long assetQuantity) {
        this.assetQuantity = assetQuantity;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}

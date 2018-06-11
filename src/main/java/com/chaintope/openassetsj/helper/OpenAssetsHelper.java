package com.chaintope.openassetsj.helper;

import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import com.chaintope.openassetsj.model.ColoredOutput;
import com.chaintope.openassetsj.model.OaConfig;
import com.chaintope.openassetsj.protocol.MarkerOutput;

public class OpenAssetsHelper {

    private OaConfig oaConfig;

    public OpenAssetsHelper(OaConfig oaConfig){

        this.oaConfig = oaConfig;
    }

    /**
     * Creates OpenAssets issuance transaction
     * @param fromKey Issuer's key
     * @param amount btc amount, The amount of asset units to issue
     * @param to Receiver's bitcoin address, if unspecified, the assets are sent back to the issuer's address
     * @param outputQty Asset quantity to issue
     * @return
     */
    public Transaction issueAssets(ECKey fromKey, Coin amount, String to, int outputQty, String metadata){

        String from = fromKey.toAddress(oaConfig.params).toString();
        if (to == null || to.equals("")) {
            // if unspecified, the assets are sent back to the issuing address.
            to = from;
        }

        if(outputQty == 0){
            outputQty = 1;
        }

        // Getting all unspent outputs
        List<TransactionOutput> unspentOutputs = getUnspentOutputs(from);
        // List<TransactionOutput> coloredOutputs = collectUncoloredOutputs(unspentOutputs, (amount.getValue() + MIN_TRANSACTION_FEES));

        // TransferParameters issueParam = new TransferParameters(coloredOutputs, to, from, amount.value, outputQty);

        Transaction tx = new Transaction(oaConfig.params);
        long totalAmount = 0L;
        List<TransactionOutput> uncoloredOutputs = collectUncoloredOutputs(unspentOutputs, (amount.getValue() + oaConfig.minTransactionFees));

        //List<ECKey> issuedReceiveKeys = walletAppKit.wallet().getIssuedReceiveKeys();
        // System.out.println(issuedReceiveKeys);

        for (TransactionOutput unspentTx :uncoloredOutputs) {

            totalAmount += unspentTx.getValue().getValue();
        }

        Address issueAddress = Address.fromBase58(oaConfig.params, to);
        Address fromAddress = Address.fromBase58(oaConfig.params, from);

/*
        ArrayList<Long> asset_quantities = new ArrayList<>();
        for (long split_amt :issueParam.splitOutputAmount()) {

            asset_quantities.add(split_amt);
            tx.addOutput(createColoredOutput(issueAddress, split_amt));
        }

        ArrayList<Long> asset_quantity_list = issueParam.getAssetQuantities();
*/
        ArrayList<Long> asset_quantities = new ArrayList<>();
        asset_quantities.add((long)outputQty);
        tx.addOutput(createColoredOutput(issueAddress, amount.value));

        tx.addOutput(Coin.ZERO, createMarkerOutput(asset_quantities, metadata));

        long changeAmount = totalAmount - amount.value - oaConfig.minTransactionFees;
        if (changeAmount > 0) {

            tx.addOutput(Coin.valueOf(changeAmount), ScriptBuilder.createOutputScript(fromAddress));
        }

        for (TransactionOutput unspentTx :uncoloredOutputs) {

            tx.addSignedInput(unspentTx, fromKey);
        }

        return tx;
    }

    /**
     * Creates a transaction for sending an asset
     * @return
     */
    private Transaction transferAssets(){
        Transaction tx = new Transaction(oaConfig.params);
        return tx;
    }

    /**
     * create marker output
     * @param asset_quantities list of asset quantities to issue
     * @param metadata metadata to pass through marker output
     * @return Script which contains marker output- op_return and data
     */
    private Script createMarkerOutput(ArrayList<Long> asset_quantities, String metadata){

        MarkerOutput mrkObj = new MarkerOutput(asset_quantities, metadata);
        return mrkObj.buildScript();
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
     * Get all unspent outputs
     * @return - List of unspent TransactionOutput
     */
    private List<TransactionOutput> getUnspentOutputs() {
        return getUnspentOutputs(null);
    }

    /**
     * Get all unspent outputs for particular address
     * @param address - Base58 encoded address
     * @return - List of unspent TransactionOutput
     */
    public List<TransactionOutput> getUnspentOutputs(String address) {
        List<TransactionOutput> unspentOutputs = new ArrayList<TransactionOutput>();
        List<TransactionOutput> allUnspentOutputs = oaConfig.walletAppKit.wallet().getUnspents();
        if (null == address || address.isEmpty()) {
            unspentOutputs = allUnspentOutputs;
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
                    unspentOutputs.add(txOut);
                }
            }
        }
        return unspentOutputs;
    }

    /**
     * Filters uncolored outputs from the list of unspent outputs
     * @param unspentOutputs - List of unspent outputs
     * @return - List of uncolored TransactionOutput
     */
    private List<TransactionOutput> collectUncoloredOutputs(List<TransactionOutput> unspentOutputs) {
        return collectUncoloredOutputs(unspentOutputs, -1L);
    }

    /**
     * Filters uncolored outputs from the list of unspent outputs
     * @param unspentOutputs - List of unspent outputs
     * @param amount - Max transaction amount required, set -1 to remove this limit
     * @return - List of uncolored TransactionOutput
     */
    private List<TransactionOutput> collectUncoloredOutputs(List<TransactionOutput> unspentOutputs, long amount) {

        List<TransactionOutput> uncoloredOutputs = new ArrayList<TransactionOutput>();
        long totalAmount = 0L;
        for (TransactionOutput txOut : unspentOutputs) {
            Script script = txOut.getScriptPubKey();
            if (!script.isOpReturn()) {
                totalAmount += txOut.getValue().getValue();
                uncoloredOutputs.add(txOut);
                if ((-1 != amount) && (totalAmount >= amount)) {
                    break;
                }
            }
        }
        return uncoloredOutputs;
    }

    /**
     * Filters colored outputs from the list of unspent outputs
     * @param unspentOutputs List of unspent outputs
     * @return List of colored TransactionOutput
     */
    private List<TransactionOutput> collectColoredOutputs(List<TransactionOutput> unspentOutputs) {

        return collectColoredOutputs(unspentOutputs, -1L);
    }

    /**
     * Filters colored outputs from the list of unspent outputs
     * @param unspentOutputs List of unspent outputs
     * @param amount Max transaction amount required, set -1 to remove this limit
     * @return List of colored TransactionOutput
     */
    private List<TransactionOutput> collectColoredOutputs(List<TransactionOutput> unspentOutputs, long amount) {
        List<TransactionOutput> coloredOutputs = new ArrayList<TransactionOutput>();
        long totalAmount = 0L;
        for (TransactionOutput txOut : unspentOutputs) {
            Script script = txOut.getScriptPubKey();
            if (script.isOpReturn()) {
                totalAmount += txOut.getValue().getValue();
                coloredOutputs.add(txOut);
                if ((-1 != amount) && (totalAmount >= amount)) {

                    break;
                }
            }
        }
        return coloredOutputs;
    }

    /**
     * method to get all unspent transaction with op_return (OpenAsset marker output)
     * @return List of colored TransactionOutput
     */
    public List<ColoredOutput> getColoredOutputs() {

        List<ColoredOutput> coloredOutputs = new ArrayList<>();

        for (Transaction tx : oaConfig.walletAppKit.wallet().getTransactions(false)) {

            coloredOutputs.addAll ( getColoredOutputs(tx) );
        }
        return coloredOutputs;
    }

    /**
     * Get colored outputs in particular transaction
     * @param transaction
     * @return List of all the unspent colored outputs
     */
    public List<ColoredOutput> getColoredOutputs(Transaction transaction) {

        List<ColoredOutput> coloredOutputs = new ArrayList<>();

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
            // String markerOutputScript = Utils.encodeHexArray(markerOutputTxOut.getScriptBytes());
            // String parsedScript = markerOutput.parseScript(markerOutputScript);
            String parsedScript = markerOutput.parseScript(markerOutputTxOut.getScriptBytes());

            markerOutput = markerOutput.deserializePayload(parsedScript);
            List<Long> assetQuantities = markerOutput.getAssetQuantities();
            int noOfAssetQuantities = assetQuantities.size();
            System.out.println(assetQuantities);

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
                        coloredOutputs.add(coloredOutput);
                    }
                }
                assetQuantityIndex++;
            }
        }

        return coloredOutputs;
    }

    /**
     * get the unspent asset balance
     * @return
     */
    public long getAssetQuantityBalance() {

        long assetQuantities = 0L;
        List<ColoredOutput> unspentColoredOutputs = getColoredOutputs();

        for(ColoredOutput coloredOutput: unspentColoredOutputs) {

            assetQuantities += coloredOutput.getAssetQuantity();
        }

        return assetQuantities;
    }
}

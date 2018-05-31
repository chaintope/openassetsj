package com.chaintope.openassetsj.utils;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.script.ScriptOpCodes;

public class AssetID {

	private NetworkParameters params;

    private final int OA_VERSION_BYTE = 23;
    private final int OA_VERSION_BYTE_TESTNET = 115;

    /**
     * Initializes network parameters necessary to generate Asset ID
     * @param params Network to use while dealing with Asset ID
     */
	public AssetID(NetworkParameters params) {

		this.params = params;
	}

	/**
     * Generates OpenAsset ID from ECKey
     * @param ecKey Key used to derive asset id
     * @return Asset ID
     */
    public String generateAssetID(ECKey ecKey) {

        Address address = ecKey.decompress().toAddress(params);
        byte[] pubKeyHash = address.getHash160();
        String assetId = pubKeyHashToAssetID(pubKeyHash);
        return assetId;
    }

    /**
     * Generates OpenAsset ID from Public key hash
     * @param pubKeyHashStr Public key hash used to derive asset id
     * @return Asset ID
     */
    public String pubKeyHashToAssetID(String pubKeyHashStr) {

        byte[] pubKeyHash = Utils.packHexStringToBytes (pubKeyHashStr);
        String assetId = pubKeyHashToAssetID(pubKeyHash);
        return assetId;
    }

    /**
     * Generates OpenAsset ID from Public key hash bytes
     * @param pubKeyHash Public key hash used to derive asset id
     * @return Asset ID
     */
    public String pubKeyHashToAssetID(byte[] pubKeyHash) {

        byte[] script = new byte[5 + pubKeyHash.length];
        script[0] = (byte) ScriptOpCodes.OP_DUP;
        script[1] = (byte) ScriptOpCodes.OP_HASH160;
        script[2] = (byte) 0x14; // 20
        System.arraycopy(pubKeyHash, 0, script, 3, pubKeyHash.length);
        script[script.length - 2] = (byte) ScriptOpCodes.OP_EQUALVERIFY;
        script[script.length - 1] = (byte) ScriptOpCodes.OP_CHECKSIG;

        String assetId = scriptToAssetID(script);
        return assetId;
    }

    /**
     * Generates OpenAsset ID from script
     * @param script Script bytes used to derive asset id
     * @return Asset ID
     */
    public String scriptToAssetID(String scriptStr) {
    	
        byte[] script = Utils.packHexStringToBytes (scriptStr);
        String assetId = scriptToAssetID(script);
        return assetId;
    }
    
    /**
     * Generates OpenAsset ID from script bytes
     * @param script Script bytes used to derive asset id
     * @return Asset ID
     */
    public String scriptToAssetID(byte[] script) {

        byte[] scriptHash160 = Utils.hash160(script);

        byte[] scriptWithVersionByte = new byte[scriptHash160.length + 1];
        scriptWithVersionByte[0] = (byte) getVersionByte();
        System.arraycopy(scriptHash160, 0, scriptWithVersionByte, 1, scriptHash160.length);

        byte[] checksum = Utils.checksum(scriptWithVersionByte);

        byte[] assetIdBytes = new byte[scriptWithVersionByte.length + 4]; // 4 bytes for checksum
        System.arraycopy(scriptWithVersionByte, 0, assetIdBytes, 0, scriptWithVersionByte.length);
        System.arraycopy(checksum, 0, assetIdBytes, scriptWithVersionByte.length, 4);

        return Base58.encode(assetIdBytes);
    }
    
    /**
     * Gets the version byte according to the current network parameters
     */
    private int getVersionByte() {

    	int version = -1;
    	switch(params.getId()) {

	    	case NetworkParameters.ID_TESTNET:
	    		version = OA_VERSION_BYTE_TESTNET;
	    		break;
	    	case NetworkParameters.ID_MAINNET:
	    		version = OA_VERSION_BYTE;
	    		break;
		}    	
    	return version;
    }
}

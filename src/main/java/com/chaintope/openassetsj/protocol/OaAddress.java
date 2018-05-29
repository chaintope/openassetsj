package com.chaintope.openassetsj.protocol;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.VersionedChecksummedBytes;

public class OaAddress {

	private NetworkParameters params;

    private final int OA_NAMESPACE = 19;

    /**
     * Initializes network parameters necessary to generate OA address
     * @param params Network to use while dealing with OA address
     */
    public OaAddress(NetworkParameters params) {

		this.params = params;
	}

	/**
     * Generates OpenAsset address using EC key
     * @param ecKey ECKey from which you have to create OpenAsset Address
     * @return OpenAsset address
     */
    public String generateOaAddress(ECKey ecKey) {

        Address address = ecKey.decompress().toAddress(params);
        String oaAddress = addressToOaAddress(address);
        
        return oaAddress;
    }

    /**
     * Generates OpenAsset address using uncompressed address
     * @param address Address from which OpenAsset Address should be generated
     * @return OpenAsset address
     */
    public String addressToOaAddress(Address address) {

        String oaAddress = "";
        
        try {
            byte[] pubkeyHash = address.getHash160();
            int addrLen = 1 + 20;
            byte[] data = new byte[addrLen];
            System.arraycopy(pubkeyHash, 0, data, 1, 20);
            data[0] = (byte) address.getVersion();
            oaAddress = new VersionedChecksummedBytes(OA_NAMESPACE, data) { }.toString();
        }
        catch (Exception e) {

            e.printStackTrace();
        }
        return oaAddress;
    }
    
    /**
     * Converts OpenAsset address to normal bitcoin address
     * @param oaAddress openassets address
     * @return bitcoin address
     */
    public Address oaAddressToAddress(String oaAddress) {

        byte[] decodedAddress = Base58.decodeChecked(oaAddress);
        byte[] normalAddress = new byte[20];
        /* First 2 bytes - OAVersion byte and address version byte - skipped */
        System.arraycopy(decodedAddress, 2, normalAddress , 0, 20);
        Address newAddress = new Address(params, normalAddress);
        return newAddress;
    }
}

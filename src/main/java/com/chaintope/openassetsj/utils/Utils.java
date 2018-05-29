package com.chaintope.openassetsj.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.VersionedChecksummedBytes;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.ScriptOpCodes;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.digests.SHA256Digest;

import com.google.common.base.Joiner;
import com.google.common.primitives.Bytes;

/**
 * Contains all the common utility functions
 */
public class Utils {

	private static final String network = "testnet";
    private static NetworkParameters params = TestNet3Params.get();
//	private static final String network = "mainnet";
//    private static NetworkParameters params = MainNetParams.get();

    private static final int OA_NAMESPACE = 19;

    private static final int OA_VERSION_BYTE = 23;
    private static final int OA_VERSION_BYTE_TESTNET = 115;

    /**
     * Generates OpenAsset address using EC key
     * @param ecKey ECKey from which you have to create OpenAsset Address
     * @return OpenAsset address
     */
    public static String generateOaAddress(ECKey ecKey) {

        Address address = ecKey.decompress().toAddress(params);
        String oaAddress = addressToOaAddress(address);
        
        return oaAddress;
    }

    /**
     * Generates OpenAsset address using uncompressed address
     * @param address Address from which OpenAsset Address should be generated
     * @return OpenAsset address
     */
    public static String addressToOaAddress(Address address) {

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
    public static Address oaAddressToAddress(String oaAddress) {

        byte[] decodedAddress = Base58.decodeChecked(oaAddress);
        byte[] normalAddress = new byte[20];
        /* First 2 bytes - OAVersion byte and address version byte - skipped */
        System.arraycopy(decodedAddress, 2, normalAddress , 0, 20);
        Address newAddress = new Address(params, normalAddress);
        return newAddress;
    }

    /**
     * Generates OpenAsset ID from ECKey
     * @param ecKey Key used to derive asset id
     * @return Asset ID
     */
    public static String generateAssetID(ECKey ecKey) {

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
    public static String pubKeyHashToAssetID(String pubKeyHashStr) {

        byte[] pubKeyHash = packHexStringToBytes (pubKeyHashStr);
        String assetId = pubKeyHashToAssetID(pubKeyHash);
        return assetId;
    }

    /**
     * Generates OpenAsset ID from Public key hash bytes
     * @param pubKeyHash Public key hash used to derive asset id
     * @return Asset ID
     */
    public static String pubKeyHashToAssetID(byte[] pubKeyHash) {

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
    public static String scriptToAssetID(String scriptStr) {
    	
        byte[] script = packHexStringToBytes (scriptStr);
        String assetId = scriptToAssetID(script);
        return assetId;
    }
    
    /**
     * Generates OpenAsset ID from script bytes
     * @param script Script bytes used to derive asset id
     * @return Asset ID
     */
    public static String scriptToAssetID(byte[] script) {

        byte[] scriptHash160 = hash160(script);

        byte[] scriptWithVersionByte = new byte[scriptHash160.length + 1];
        scriptWithVersionByte[0] = (byte) getVersionByte();
        System.arraycopy(scriptHash160, 0, scriptWithVersionByte, 1, scriptHash160.length);

        byte[] checksum = checksum(scriptWithVersionByte);

        byte[] assetIdBytes = new byte[scriptWithVersionByte.length + 4]; // 4 bytes for checksum
        System.arraycopy(scriptWithVersionByte, 0, assetIdBytes, 0, scriptWithVersionByte.length);
        System.arraycopy(checksum, 0, assetIdBytes, scriptWithVersionByte.length, 4);

        return Base58.encode(assetIdBytes);
    }
    
    /**
     * Gets the version byte according to the current network parameters
     */
    private static int getVersionByte() {

    	int version;
    	switch(network) {

	    	case "testnet":
	    		version = OA_VERSION_BYTE_TESTNET;
	    		break;
	    	case "mainnet":
	    	default:
	    		version = OA_VERSION_BYTE;
	    		break;
		}    	
    	return version;
    }

    /**
     * Hash160 of the given input byte array
     * @param bytes Bytes to be hashed
     * @return Hashed bytes
     */
    private static byte[] hash160(byte[] bytes) {

        SHA256Digest firstDigest = new SHA256Digest();
        firstDigest.update(bytes, 0, bytes.length);
        byte[] firstHash = new byte[32];
        firstDigest.doFinal(firstHash, 0);
        RIPEMD160Digest secondDigest = new RIPEMD160Digest();
        secondDigest.update(firstHash, 0, firstHash.length);
        byte[] secondHash = new byte[20];
        secondDigest.doFinal(secondHash, 0);
        return secondHash;
    }

    /**
     * Calculates the checksum
     * @param arr Byte array to calculate checksum
     * @return Checksum bytes
     */
    private static byte[] checksum(byte[] arr) {

        byte[] checksum = null;
        try {

            checksum = Sha256Hash.hashTwice(arr, 0, arr.length);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return checksum;
    }

    /**
     * Converts hex string to byte array
     * @param unpackedStr
     * @return Byte array of the unpacked hex string
     */
    public static byte[] packHexStringToBytes(String unpackedStr) {

        int loopCount = unpackedStr.length() / 2;
        byte[] packedData = new byte[loopCount];
        for(int i=0;i<loopCount;i++) {

            packedData[i] = (byte) Integer.parseInt(unpackedStr.substring(i*2, (i+1)*2), 16);
        }
        return packedData;
    }

    /**
     * Converts hex string to char string
     * E.g Converts "464F4F" to "FOO"
     * @param unpackedStr
     * @return Byte array of the unpacked hex string
     */
    public static String packHexStringToCharString(String unpackedStr) {

        int loopCount = unpackedStr.length() / 2;
        String packedData = "";
        for(int i=0;i<loopCount;i++) {

            packedData += (char) Integer.parseInt(unpackedStr.substring(i*2, (i+1)*2), 16);
        }
        return packedData;
    }

    /**
     * Converts byte array into string
     * @param array byte array
     * @return String equivalent of byte array
     */
    public static String packByteArrayToString(byte array[]) {

        StringBuilder encoded = new StringBuilder();
        for (byte c : array) {

            String hexByte = Integer.toHexString((int)c);
            if (hexByte.length() < 2) {

                hexByte = "0" + hexByte;
            } else if (hexByte.length() > 2) {

                hexByte = hexByte.substring(hexByte.length() - 2, hexByte.length());
            }
            encoded.append(hexByte);
        }
        return encoded.toString();
    }

    /**
     * Converts hex string to char string
     * E.g Converts "FOO" to "464F4F"
     * @param packedStr
     * @return Hex string of the packed char string
     */
    public static String unpackCharStringToHexString(String packedStr) {

        int loopCount = packedStr.length();
        String unpackedData = "";
        for(int i=0; i<loopCount; i++) {

            unpackedData += Integer.toHexString((int) packedStr.charAt(i));
        }
        return unpackedData;
    }

    /**
     * Converts input value to unsigned leb128 equivalent
     * @param value integer value to convert into unsigned leb128
     * @return unsigned leb128
     */
    public static String encodeLeb128(int value) {

        String binaryStr = Integer.toString(value, 2);
        int strLength = (((int) Math.floor(binaryStr.length() / 7)) * 7);
        strLength += (strLength == binaryStr.length() ? 0 : 7);
        String paddedBinaryString = String.format("%" + strLength + "s", binaryStr).replace(' ', '0');
        int loopCount = paddedBinaryString.length() / 7;
        List<String> lebBytes = new ArrayList<String>();
        for (int i = 0; i < loopCount; i++) {

            String paddedStr = ( i == 0 ? "0" : "1") + paddedBinaryString.substring(i*7, (i+1)*7);
            int decimal = Integer.parseInt(paddedStr, 2);
            String hexStr = getHexString(decimal);
            lebBytes.add(hexStr);
        }
        Collections.reverse(lebBytes);
        String leb128Str = Joiner.on("").join(lebBytes.toArray());
        return leb128Str;
    }

    /**
     * Reads data from LEB128 encoded string
     * @param payload
     * @param offset
     * @return List containing quantity and length
     */
    public static List<Object> readLeb128(String payload, int offset) {

            List<Object> objList = new ArrayList<>();
        Object quantityObj = null;
        Object lengthObj = 0;

        byte[] data = Utils.packHexStringToBytes(payload);

        int result = 0;
        int shift = 0;

        while(true) {

            if (data.length < (1 + offset)) {

                quantityObj = null;
                lengthObj = offset;
            }

            byte byteToCheck = data[offset];
            result = result | ((byteToCheck & 0x7F) << shift);

            if (0 == (int)(byteToCheck & 0x80)) {

                break;
            }
            shift += 7;
            offset += 1;
        }

        quantityObj = result;
        lengthObj = offset + 1;

        objList.add(quantityObj);
        objList.add(lengthObj);

        return objList;
    }

    /**
     * Decodes the LEB128 encoded string
     * @param encodedString
     * @return LEB128 decoded number
     */
    public static long decodeLeb128(String encodedString) {

        byte[] data = Utils.packHexStringToBytes(encodedString);

        long result = 0;
        int shift = 0;
        int offset = 0;

        while (true) {

            byte byteToCheck = data[offset];

            result = result | ((byteToCheck & 0x7F) << shift);

            if (0 == (int) (byteToCheck & 0x80)) {

                break;
            }
            shift += 7;
            offset += 1;
        }

        return result;
    }

    /**
     * Packs var-int
     * @param value
     * @return Packed hex string
     */
    public static String packVarInteger(int value) {
    	
    	String hexString = "";

    	// 0xfd = 253
    	if ( value < 253) {
    		hexString = pack(value, "C");
    	}
    	// 0xffff = 65535
    	else if ( value <= 65535) {
    		hexString = "fd" + pack(value, "Cv");
    	}
    	// 0xffffffff = 4294967295
    	else if ( value <= 4294967295L) {
    		hexString = "fe" + pack(value, "CV");
    	}
    	// 0xffffffffffffffff = 18446744073709551615
    	else if ( value <= Long.parseUnsignedLong("18446744073709551615")) {
    		hexString = "ff" + pack(value, "CQ");
    	}
		return hexString;
    }
    
    /**
     * Packs integer based on the flag provided
     * @param value
     * @param flag
     * @return Packed hex string
     */
    public static String pack(int value, String flag) {
    	String packedStr = "";
    	
    	String hexString = getHexString(value);
    	
    	switch (flag) {
		case "C":
			// 8-bit unsigned, native-endian
			packedStr = hexString;
			break;
		case "Cv":
			// 16-bit unsigned, little-endian
			packedStr = hexString;
//			packedStr = convertToLittleEndian(hexString);
			packedStr = "0000".substring(packedStr.length()) + packedStr;
			break;
		case "CV":
			// 32-bit unsigned, little-endian
			packedStr = hexString;
//			packedStr = convertToLittleEndian(hexString);
			packedStr = "00000000".substring(packedStr.length()) + packedStr;
			break;
		case "CQ":
			// 64-bit unsigned, native-endian
			packedStr = hexString;
			packedStr = packedStr + "0000000000000000".substring(packedStr.length());
			break;
		default:
			break;
		}
    	
    	return packedStr;
    }
    
    /**
     * Converts string from Native-Endian to Little-Endian
     * @param str Native-Endian string
     * @return Little-Endian string
     */
    public static String convertToLittleEndian(String str) {
    	
    	String littleEndianStr = "";
    	int strLen = str.length();
    	
    	for(int i=0; i<strLen;i=i+2) {
    		littleEndianStr = str.substring(i, i+2) + littleEndianStr;
    	}
    	return littleEndianStr;
    }

    /**
     * Reads var-int
     * @param data
     * @return List containing count and the offset
     */
    public static List<Object> readVarInteger(String data) {
    	
    	return readVarInteger(data, 0);
    }

    /**
     * Reads var-int
     * @param data
     * @param offset
     * @return List containing count and the offset
     */
    public static List<Object> readVarInteger(String data, int offset) {

        List<Object> objList = new ArrayList<Object>();
        Object countObj = null;
        Object offsetObj = 0;

        byte[] packed = packHexStringToBytes(data);

        if (packed.length < (1 + offset)) {
            countObj = null;
            offsetObj = 0;
        }
        else {

            int elementsToCopy = packed.length - offset;
            byte[] bytes = new byte[elementsToCopy];
            System.arraycopy(packed, offset, bytes, 0, (elementsToCopy < 10) ? elementsToCopy : 10);

            int firstByte = (bytes[0] & 0xFF);

            // 0xFD = 253
            if (firstByte < 253) {

                countObj = firstByte;
                offsetObj = offset + 1;
            }
            // 0xFD = 253
            else if (firstByte == 253) {

                int noOfBytes = 2;
                byte[] temp = new byte[noOfBytes];
                System.arraycopy(bytes, 1, temp, 0, noOfBytes);
                countObj = calculateVarIntegerValue(temp);
                offsetObj = offset + noOfBytes + 1;
            }
            // 0xFE = 254
            else if (firstByte == 254) {

                int noOfBytes = 4;
                byte[] temp = new byte[noOfBytes];
                System.arraycopy(bytes, 1, temp, 0, noOfBytes);
                countObj = calculateVarIntegerValue(temp);
                offsetObj = offset + noOfBytes + 1;
            }
            // 0xFF = 255
            else if (firstByte == 255) {

                int noOfBytes = 8;
                byte[] temp = new byte[noOfBytes];
                System.arraycopy(bytes, 1, temp, 0, noOfBytes);
                countObj = calculateVarIntegerValue(temp);
                offsetObj = offset + noOfBytes + 1;
            }
        }
        objList.add(countObj);
        objList.add(offsetObj);
        return objList;
    }

    /**
     * Calculates var-int value
     * @param byteArr
     * @return Value of the var-int
     */
    public static long calculateVarIntegerValue(byte[] byteArr) {

        long sum = 0L, len = byteArr.length;
        sum = (byteArr[0] & 0xFF);
        for (int i = 1; i < len; i++ ) {

            int val = (byteArr[i] & 0xFF);
            sum += val * Math.pow(256, i);
        }
        return sum;
    }

    /**
     * Converts int to hex value
     * @param value integer value
     * @return converted hex string
     */
    public static String getHexString(int value) {
        String hexStr = Integer.toString(value, 16);
        hexStr = (hexStr.length() % 2 == 0) ? hexStr : "0" + hexStr;
        return hexStr;
    }
}

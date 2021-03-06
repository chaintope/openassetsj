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

    /**
     * Hash160 of the given input byte array
     * @param bytes Bytes to be hashed
     * @return Hashed bytes
     */
    public static byte[] hash160(byte[] bytes) {

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
    public static byte[] checksum(byte[] arr) {

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

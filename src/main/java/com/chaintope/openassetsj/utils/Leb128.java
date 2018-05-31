package com.chaintope.openassetsj.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

public class Leb128 {

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
            String hexStr = Utils.getHexString(decimal);
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
}

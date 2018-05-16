package com.chaintope.openassetsj.protocol;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;

import com.chaintope.openassetsj.utils.Utils;
import com.google.common.base.Joiner;

/**
 * Builds, validates and deserializes the marker output script
 */
public class MarkerOutput {

    private ArrayList<Long> assetQuantities;
    private String metadata;

    // A tag indicating that this transaction is an Open Assets transaction
    private final String OAP_MARKER = "4f41";

    // The major revision number of the Open Assets Protocol (1 = 0x0100)
    private final String VERSION = "0100";

    public MarkerOutput() {}

    public MarkerOutput(ArrayList<Long> assetQuantities, String metadata) {
        this.assetQuantities = assetQuantities;
        this.metadata = metadata;
    }

    public ArrayList<Long> getAssetQuantities() {
		return assetQuantities;
	}

	public void setAssetQuantities(ArrayList<Long> assetQuantities) {
		this.assetQuantities = assetQuantities;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	/**
     * Serialize the marker output into a Open Assets payload buffer.
     * @return Payload string for marker output
     */
    public String toPayload() {

        String payload;
        ArrayList<String> payloadArray = new ArrayList<>();
        payloadArray.add(OAP_MARKER);
        payloadArray.add(VERSION);
        String asset_quantity_count = Utils.packVarInteger(assetQuantities.size());

        payloadArray.add(asset_quantity_count);
        for (Long assetId: assetQuantities) {

            payloadArray.add(Utils.encodeLeb128(assetId.intValue()));
        }

        String metadata_length = Utils.packVarInteger(metadata.length());
        payloadArray.add(metadata_length);
            

        if(!metadata.isEmpty()) {
            try {

                payloadArray.add(String.format("%040x", new BigInteger(1, metadata.getBytes("utf8"))));
            } catch (UnsupportedEncodingException e){

                e.printStackTrace();
            }
        }
        payload = Joiner.on("").join(payloadArray).trim();
        return payload;
    }

    /**
     * Deserializes the input payload,
	 * and extracts the asset quantities and metadata
	 * from the given payload 
     * @param payload Payload string to deserialize
     * @return MarkerOutput if payload deserializes successfully, null otherwise
     */
    public MarkerOutput deserializePayload(String payload) {

        if(!validatePayload(payload)){
            return null;
        }

        payload = payload.substring(8, payload.length());

        List<Object> parsedPayload = parseAssetQuantity(payload);
        long assetQuantity = (long) parsedPayload.get(0);
        payload = parsedPayload.get(1).toString();

        int payloadLength = payload.length() / 2;
        String payloadBytes = "";
        String assetQuantityList = "";

        for (int i = 0; i < payloadLength; i++) {

            String payloadByte = payload.substring(i*2, (i+1)*2);
            payloadBytes += (Integer.parseInt(payloadByte, 16) >= 128 ? payloadByte : payloadByte + "|");
        }

        assetQuantityList = Joiner.on("").join(
                Arrays.copyOfRange(
                        payloadBytes.split("\\|"), 0, (int)(assetQuantity)));

        ArrayList<Long> assetQuantities = decodeLeb128(assetQuantityList);

        String metadataHex = payload.substring(assetQuantityList.length());
        String metadata = Utils.packHexStringToCharString(metadataHex.substring(2));

        MarkerOutput res = new MarkerOutput(assetQuantities, metadata);
        return res;
    }

    /**
     * Decodes the LEB128 encoded string of asset quantities 
     * @param assetQuantityList LEB128 encoded string of asset quantities
     * @return The decoded asset quantity list
     */
    private ArrayList<Long> decodeLeb128(String assetQuantityList) {

        ArrayList<Long> decodedAssetQuantities = new ArrayList<>();

        String assetQuantityBytes = "";
        int assetQuantityListLength = assetQuantityList.length() / 2;

        for (int i = 0; i < assetQuantityListLength; i++) {

            String assetQuantityByte = assetQuantityList.substring(i*2, (i+1)*2);

            if (Integer.parseInt(assetQuantityByte, 16) >= 128) {

                assetQuantityBytes += assetQuantityByte;
            }
            else {

                assetQuantityBytes += assetQuantityByte;
                decodedAssetQuantities.add(Utils.decodeLeb128(assetQuantityBytes));
                assetQuantityBytes = "";
            }
        }

        return decodedAssetQuantities;
    }

    /**
     * Parses asset quantities from the payload
     * @param payload Payload of marker output
     * @return A list containing parsed asset quantity and the payload string
     */
    private List<Object> parseAssetQuantity(String payload) {

        List<Object> parsedPayload = new ArrayList<Object>();
        long assetQuantity;
        String payloadStr;

        if (payload.startsWith(OAP_MARKER + VERSION)) {

            payload = payload.substring(8, payload.length());
        }

        int assetQuantityByte = Integer.parseInt(payload.substring(0, 2), 16);

        switch (assetQuantityByte) {
            // 0xFD = 253
            case 253:
                assetQuantity = Long.parseLong(payload.substring(2, 6), 16);
                payloadStr = payload.substring(6);
                break;
            // 0xFE = 254
            case 254:
                assetQuantity = Long.parseLong(payload.substring(2, 10), 16);
                payloadStr = payload.substring(10);
                break;
            default:
                assetQuantity = assetQuantityByte;
                payloadStr = payload.substring(2);
        }

        parsedPayload.add(assetQuantity);
        parsedPayload.add(payloadStr);

        return parsedPayload;
    }

    /**
     * Validates the payload data
     * @param payload Payload of marker output
     * @return true if payload is valid, false otherwise
     */
    private boolean validatePayload(String payload) {

        boolean res = false;
        if(payload.equals("")) {
            return res;
        }

        if(!payload.startsWith( OAP_MARKER + VERSION )) {
            return res;
        }

        int offset = (OAP_MARKER + VERSION).length() / 2;

        List<Object> varIntData = Utils.readVarInteger(payload, offset);
        int count = Integer.parseInt(varIntData.get(0).toString());
        offset = Integer.parseInt(varIntData.get(1).toString());

        if (count == 0) {

            return res;
        }

        for (int i = 0; i < count; i++) {

            List<Object> leb128Data = Utils.readLeb128(payload, offset);
            int quantity = Integer.parseInt(leb128Data.get(0).toString());
            int length = Integer.parseInt(leb128Data.get(1).toString());

            if ((quantity == 0) || (length - offset) > 9) {
                return res;
            }
            offset = length;
        }

        List<Object> varIntData2 = Utils.readVarInteger(payload, offset);

        if (varIntData2.get(0) == null) {
            return res;
        }

        int length = Integer.parseInt(varIntData2.get(0).toString());
        offset = Integer.parseInt(varIntData2.get(1).toString());

        if (Utils.packHexStringToCharString(payload).length() < (length + offset)) {
            return res;
        }
        res = true;
        return res;
    }

    /**
     * Parses the marker output script
     * @param outputScript Marker output script
     * @return Data if the given script is valid, empty string otherwise
     */
    public String parseScript(byte[] outputScript){
        Script script = new Script(outputScript);
        String scriptData = Utils.packByteArrayToString(script.getChunks().get(1).data);
        if (validatePayload(scriptData)) {
        	return scriptData;
        }
        return "";
    }

    /**
     * Builds the Marker output script from the payload
     * @return Marker output script
     */
    public Script buildScript(){

        return new ScriptBuilder()
                .op(ScriptOpCodes.OP_RETURN)
                .data(Utils.packHexStringToBytes(toPayload()))
                .build();
    }
}

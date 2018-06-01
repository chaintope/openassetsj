package com.chaintope.openassetsj.utils;

import static org.junit.Assert.*;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.VarInt;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Test;

public class UtilsTest {

	@Test
	public void varIntBitcoinjTest() {
		
		VarInt varInt = new VarInt(Utils.packHexStringToBytes("fd0000"), 0);
		assertEquals(0L, varInt.value);
		
		varInt = new VarInt(Utils.packHexStringToBytes("fd1100"), 0);
		assertEquals(17L, varInt.value);
		
		varInt = new VarInt(Utils.packHexStringToBytes("fe00000000"), 0);
		assertEquals(0L, varInt.value);
		
		varInt = new VarInt(Utils.packHexStringToBytes("fe11000000"), 0);
		assertEquals(17L, varInt.value);
		
		varInt = new VarInt(Utils.packHexStringToBytes("fe11220000"), 0);
		assertEquals(8721L, varInt.value);
		
		varInt = new VarInt(Utils.packHexStringToBytes("ff0000000000000000"), 0);
		assertEquals(0L, varInt.value);
		
		varInt = new VarInt(Utils.packHexStringToBytes("ff1100000000000000"), 0);
		assertEquals(17L, varInt.value);
		
		varInt = new VarInt(Utils.packHexStringToBytes("ff1122000000000000"), 0);
		assertEquals(8721L, varInt.value);
		
		varInt = new VarInt(Utils.packHexStringToBytes("ff1122334400000000"), 0);
		assertEquals(1144201745L, varInt.value);
	}
}

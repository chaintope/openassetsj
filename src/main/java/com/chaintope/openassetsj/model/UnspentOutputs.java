package com.chaintope.openassetsj.model;

import java.util.List;

import org.bitcoinj.core.TransactionOutput;

public class UnspentOutputs {

	public List<TransactionOutput> uncoloredOutputs;
	public List<ColoredOutput> coloredOutputs;
	public long amount;
}

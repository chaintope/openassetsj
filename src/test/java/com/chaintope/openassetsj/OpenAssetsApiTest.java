package com.chaintope.openassetsj;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicSeed;
import org.junit.Test;

import com.chaintope.openassetsj.model.Rpc;

public class OpenAssetsApiTest {
	
	WalletAppKit walletAppKit;
	NetworkParameters params = TestNet3Params.get();

	@Test
	/**
	 * Method to test the functionality of issueAssets()
	 */
	public void issueAssetsTest() {
		setupWallet(null);
		
		OpenAssetsApi oaApi = new OpenAssetsApi(params, walletAppKit,
				10000, new Rpc("username", "password", 18443, "localhost"));
		Transaction issuingTransaction = oaApi.issueAssets(walletAppKit.wallet().getIssuedReceiveKeys().get(0),
				Coin.valueOf(25000), "", 1, "");
		assertTrue(issuingTransaction.getInputs().size() > 0);
		assertTrue(issuingTransaction.getOutputs().size() > 0);
	}

    /**
     * method to configure the wallet and its event listeners
     * @param seed (optional) Deterministic seed object to load wallet from seed
     */
    private void setupWallet(DeterministicSeed seed) {

        walletAppKit = new WalletAppKit(params, new File("."), "OpenAssetsJKit") {

            @Override
            protected void onSetupCompleted() {

                if (wallet().getIssuedReceiveKeys().size() < 1) {

                	wallet().freshReceiveKey();
                }

                wallet().allowSpendingUnconfirmedTransactions();
//                setupWalletListeners(wallet());
            }
        };

        walletAppKit.setDownloadListener(new DownloadProgressTracker() {

            @Override
            protected void progress(double pct, int blocksSoFar, Date date) {

                super.progress(pct, blocksSoFar, date);
                int percentage = (int) pct;
                System.out.println("Percentage: " + percentage);
            }

            @Override
            protected void doneDownload() {

            	System.out.println("Blockchain download done");
                super.doneDownload();
                System.out.println(walletAppKit.wallet());
            }
        });

        walletAppKit.setAutoSave(true);
        walletAppKit.setBlockingStartup(false);
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
    }
}

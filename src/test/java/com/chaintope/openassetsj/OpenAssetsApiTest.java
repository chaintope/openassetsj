package com.chaintope.openassetsj;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.plaf.SliderUI;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.junit.Test;

import com.chaintope.openassetsj.model.OaConfig;
import com.chaintope.openassetsj.model.Rpc;
import com.chaintope.openassetsj.utils.Utils;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.subgraph.orchid.encoders.Hex;

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
				10000, new Rpc("username", "password", 18333, "localhost"));

		Transaction issuanceTransaction = null;
		
		List<Long> assetQuantities = new ArrayList<>();
		assetQuantities.add(8000L);
		assetQuantities.add(11000L);
		assetQuantities.add(6000L);

		issuanceTransaction = oaApi.issueAssets(
				walletAppKit.wallet().getIssuedReceiveKeys().get(0),
				assetQuantities,
				"u=https://cpr.sm/5YgSU1Pg-q");
		
		try {
			issuanceTransaction.verify();
		}
		catch(VerificationException ex) {
			ex.printStackTrace();
			fail("Exception occurred while verifying the issuance transaction");
		}
	}

    /**
     * method to configure the wallet and its event listeners
     * @param seed Deterministic seed object to load wallet from seed
     */
    private void setupWallet(DeterministicSeed seed) {

        walletAppKit = new WalletAppKit(params, new File("."), "OpenAssetsJKit") {

            @Override
            protected void onSetupCompleted() {

                if (wallet().getIssuedReceiveKeys().size() < 1) {

                	wallet().freshReceiveKey();
                }

                wallet().allowSpendingUnconfirmedTransactions();
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
        walletAppKit.setBlockingStartup(true);
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
    }
}

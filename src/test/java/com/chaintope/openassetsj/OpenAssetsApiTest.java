package com.chaintope.openassetsj;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicSeed;
import org.junit.Test;

import com.chaintope.openassetsj.model.Rpc;

public class OpenAssetsApiTest {
	
	WalletAppKit walletAppKit;
	NetworkParameters params;
	OpenAssetsApi oaApi;

	public OpenAssetsApiTest() {

		params = TestNet3Params.get();

		setupWallet(null);

		this.oaApi = new OpenAssetsApi(params, walletAppKit,
				10000, new Rpc("username", "password", 18333, "localhost"));
	}
	
	@Test
	/**
	 * Method to test the functionality of issueAssets()
	 */
	public void issueAssetsTest() {

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
//			TODO: Validate issuance transaction
//			 issuanceTransaction.verify();
		}
		catch(VerificationException ex) {
			fail("Exception occurred while verifying the issuance transaction");
		}
	}
	

//	@Test
	/**
	 * Method to test the functionality of getAssetsBalance()
	 */
	public void getAssetsBalanceTest() {

		long assetsBalance = oaApi.getAssetsBalance();
		assertEquals(25000, assetsBalance);
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
            }

            @Override
            protected void doneDownload() {

                super.doneDownload();
            }
        });

        walletAppKit.setAutoSave(true);
        walletAppKit.setBlockingStartup(true);
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
    }
}

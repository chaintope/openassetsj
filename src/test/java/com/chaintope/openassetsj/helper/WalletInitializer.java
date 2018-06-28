package com.chaintope.openassetsj.helper;

import java.io.File;
import java.util.Date;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;

import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

import com.google.common.base.Joiner;

/**
 * Class to setup wallet using WalletAppKit
 *
 */
public class WalletInitializer {
	
	static WalletAppKit walletAppKit;
	static NetworkParameters params;
	
	private static WalletInitializer walletInitializer = null;

	private WalletInitializer(NetworkParameters params) {

		this.params = params;
	}

	/**
	 * @return Instantiated WalletAppKit object
	 */
    public WalletAppKit getWalletAppKit() {

    	return this.walletAppKit;
    }
	
    /**
     * Get singleton instance of WalletInitializer class
     * @param params Network parameters to initialize wallet
     * @return Instance of WalletInitializer class
     */
	public static WalletInitializer getInstance(NetworkParameters params) {

		if (walletInitializer == null) {
			
			walletInitializer = new WalletInitializer(params);
			
            DeterministicSeed seed;
			try {

				seed = new DeterministicSeed(
						"nuclear scene frog height leg series anger rough loud sleep rookie winner", null, "", 1528695747L);
				setupWallet(seed);
			} catch (UnreadableWalletException e) {

				setupWallet(null);
			}
		}
		
		return walletInitializer;
	}

    /**
     * method to configure the wallet and its event listeners
     * @param seed Deterministic seed object to load wallet from seed
     */
    private static void setupWallet(DeterministicSeed seed) {

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
                System.out.println(pct);
            }

            @Override
            protected void doneDownload() {

                super.doneDownload();
            }
        });
        
        if(seed != null){

            System.out.println("Restoring wallet from seed");
            walletAppKit.restoreWalletFromSeed(seed);
        }

        walletAppKit.setAutoSave(true);
        walletAppKit.setBlockingStartup(true);
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
    }
}

package com.chaintope.openassetsj.helper;

import java.io.File;
import java.util.Date;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;

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
			setupWallet();
		}
		
		return walletInitializer;
	}

    /**
     * method to configure the wallet and its event listeners
     * @param seed Deterministic seed object to load wallet from seed
     */
    private static void setupWallet() {

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

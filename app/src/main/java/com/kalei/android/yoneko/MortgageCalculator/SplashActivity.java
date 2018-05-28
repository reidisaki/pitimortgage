package com.kalei.android.yoneko.MortgageCalculator;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.crashlytics.android.Crashlytics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends Activity {

    InterstitialAd mInterstitialAd;

    public static boolean hasInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        Fabric.with(this, new Crashlytics());

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(final int i) {
                super.onAdFailedToLoad(i);
                startActivity();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                startActivity();
            }
        });
    }

    private void startActivity() {
        Intent i = new Intent(SplashActivity.this, Main.class);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasInternet(this)) {
            startActivity();
        } else {
            requestNewInterstitial();
        }
    }

    // Create the interstitial.
//    final InterstitialAd interstitialAd = new InterstitialAd(this);
//    AdRegistration.setAppKey("ebbbcbf8ca734a10aa32cffb9f2c4971");
//    AdRegistration.enableLogging(true);

    // Set the listener to use the callbacks below.
//    interstitialAd.setListener(new AdListener() {
//        @Override
//        public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
//            interstitialAd.showAd();
//        }
//
//        @Override
//        public void onAdFailedToLoad(final Ad ad, final AdError adError) {
//            Log.i("mc", "ad failed: " + adError.getMessage());                
//        }
//
//        @Override
//        public void onAdExpanded(final Ad ad) {
//
//        }
//
//        @Override
//        public void onAdCollapsed(final Ad ad) {
//
//        }
//
//        @Override
//        public void onAdDismissed(final Ad ad) {
//        	Intent i = new Intent(SplashActivity.this, MapActivity.class);        	
//        	startActivity(i);
//        }
//    });
//
//    interstitialAd.loadAd();
//    
//    
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID").addTestDevice("F20E572DBF816C7D7BA7BAA63AF73CCC")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}

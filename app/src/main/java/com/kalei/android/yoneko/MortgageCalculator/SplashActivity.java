package com.kalei.android.yoneko.MortgageCalculator;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

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

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.setAdListener(new AdListener() {
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
//                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID").addTestDevice("1227AC999E49F1FE325D0EA5E2E4E604")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}

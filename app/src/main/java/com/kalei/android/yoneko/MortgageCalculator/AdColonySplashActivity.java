package com.kalei.android.yoneko.MortgageCalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAppOptions;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyUserMetadata;
import com.adcolony.sdk.AdColonyZone;
import com.kalei.android.yoneko.MortgageCalculator.R;

import static com.kalei.android.yoneko.MortgageCalculator.SplashActivity.hasInternet;

public class AdColonySplashActivity extends Activity {
    final private String APP_ID = "app85ecb10060fd487b82";
    final private String ZONE_ID = "vzdc4a5e9cbf72457ebc";
    final private String TAG = "AdColonyDemo";

    private ProgressBar progress;
    private AdColonyInterstitial ad;
    private AdColonyInterstitialListener listener;
    private AdColonyAdOptions adOptions;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.adcolony_activity_interstitial);
        progress = (ProgressBar) findViewById(R.id.progress);

        // Construct optional app options object to be sent with configure
        AdColonyAppOptions app_options = new AdColonyAppOptions().setUserID("unique_user_id");
//
//        // Configure AdColony in your launching Activity's onCreate() method so that cached ads can
//        // be available as soon as possible.
        AdColony.configure(this, app_options, APP_ID, ZONE_ID);
//
//        // Optional user metadata sent with the ad options in each request
//        AdColonyUserMetadata metadata = new AdColonyUserMetadata()
//                .setUserAge(26)
//                .setUserEducation(AdColonyUserMetadata.USER_EDUCATION_BACHELORS_DEGREE)
//                .setUserGender(AdColonyUserMetadata.USER_MALE);

        // Ad specific options to be sent with request
//        adOptions = new AdColonyAdOptions().setUserMetadata(metadata);

        // Set up listener for interstitial ad callbacks. You only need to implement the callbacks
        // that you care about. The only required callback is onRequestFilled, as this is the only
        // way to get an ad object.
        listener = new AdColonyInterstitialListener() {
            @Override
            public void onRequestFilled(AdColonyInterstitial ad) {
                // Ad passed back in request filled callback, ad can now be shown
                AdColonySplashActivity.this.ad = ad;
                progress.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onRequestFilled");
                ad.show();
            }

            @Override
            public void onRequestNotFilled(AdColonyZone zone) {
                // Ad request was not filled
                progress.setVisibility(View.GONE);
                Log.d(TAG, "onRequestNotFilled");
                if (ad != null) {
                    ad.show();
                }
            }

            @Override
            public void onOpened(AdColonyInterstitial ad) {
                // Ad opened, reset UI to reflect state change
                progress.setVisibility(View.VISIBLE);
                Log.d(TAG, "onOpened");
            }

            @Override
            public void onExpiring(AdColonyInterstitial ad) {
                // Request a new ad if ad is expiring
                progress.setVisibility(View.VISIBLE);
                AdColony.requestInterstitial(ZONE_ID, this, adOptions);
                Log.d(TAG, "onExpiring");
            }

            @Override
            public void onClosed(final AdColonyInterstitial ad) {
                super.onClosed(ad);
                Log.d(TAG, "onClosed");
                startActivity();
            }
        };

//        // Set up button to show an ad when clicked
//        showButton = (Button) findViewById(R.id.showbutton);
//        showButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!hasInternet(this) || BuildConfig.DEBUG) {
            startActivity();
        } else {

            // It's somewhat arbitrary when your ad request should be made. Here we are simply making
            // a request if there is no valid ad available onResume, but really this can be done at any
            // reasonable time before you plan on showing an ad.
            if (ad == null || ad.isExpired()) {
                // Optionally update location info in the ad options for each request:
                // LocationManager locationManager =
                //     (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                // Location location =
                //     locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                // adOptions.setUserMetadata(adOptions.getUserMetadata().setUserLocation(location));
                progress.setVisibility(View.VISIBLE);
                AdColony.requestInterstitial(ZONE_ID, listener, adOptions);
            }
        }
    }

    private void startActivity() {
        Intent i = new Intent(AdColonySplashActivity.this, Main.class);
        startActivity(i);
    }
}
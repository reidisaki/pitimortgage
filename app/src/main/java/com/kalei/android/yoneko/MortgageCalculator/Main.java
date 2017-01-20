package com.kalei.android.yoneko.MortgageCalculator;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.android.gms.location.LocationServices.API;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class Main extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private EditText mortAmountEditText, downPaymentEditText, percentageEditText, taxEditText, insuranceEditText, termsEditText, IREditText, HOA_EditText;
    private TextView principalTextView, taxesTextView, insuranceTextView, totalTextView;
    private Button calculateButton;
    //	private AdLayout adView;
    private AdView mAdView;
    private int mLastEdited = 0;
    private int mCount = 0;
    Handler handler = new Handler();
    private InterstitialAd mInterstitialAd;
    private LocationManager locationManager;
    private String mProvider;
    private RequestQueue mQueue;
    private String TAG = "MortgageCalculator";
    private Map<String, String> states;

    private static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int UPDATE_INTERVAL_IN_SECONDS = 600;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    private String mStateName = "";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    LocationRequest mLocationRequest;



    /* Your ad unit id. Replace with your actual ad unit id. */

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dont show the top application name, saves space
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        if (ContextCompat.checkSelfPermission(this,
                permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission.ACCESS_FINE_LOCATION,
                    permission.INTERNET}, 0);
        } else {

            initOnCreate();

            //		// Add the AdView to the view hierarchy. The view will have no size
            //		// until the ad is loaded.
            //		RelativeLayout rLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
            //		rLayout.addView(adView);
            //
            //		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) adView.getLayoutParams();
            //		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            //		adView.setLayoutParams(params);
            //		// Create an ad request. Check logcat output for the hashed device ID to
            //		// get test ads on a physical device.
            //
            //		final TelephonyManager tm =(TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            //		String deviceid = tm.getDeviceId();
            //		Log.i("REID",deviceid);
        }
    }

    private void initOnCreate() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(API)
                    .build();

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        }
        //		AdRegistration.setAppKey("ebbbcbf8ca734a10aa32cffb9f2c4971");

        initViews();
        validateValues();
        attachTextChangeHandlers();
        attachButtonHandlers();
    }

    private void attachButtonHandlers() {
        calculateButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (validateValues()) {
                    //set totals of each edit text value
                    calculateValues();
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Error with values";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        IREditText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                IREditText.setText("");
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(IREditText, 0);

                return false;
            }
        });

        insuranceEditText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                insuranceEditText.setText("");
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(insuranceEditText, 0);
                return false;
            }
        });

        HOA_EditText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HOA_EditText.setText("");
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(HOA_EditText, 0);
                return false;
            }
        });
        termsEditText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                termsEditText.setText("");
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(termsEditText, 0);
                return false;
            }
        });
        mortAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                handleDollarFormatting(this, mortAmountEditText, s.toString());
            }
        });
        mortAmountEditText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mortAmountEditText.setText("");
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mortAmountEditText, 0);
                return false;
            }
        });

        downPaymentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                handleDollarFormatting(this, downPaymentEditText, s.toString());
            }
        });
        downPaymentEditText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLastEdited = downPaymentEditText.getId();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(downPaymentEditText, 0);
                downPaymentEditText.setText("");
                return false;
            }
        });
        percentageEditText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLastEdited = percentageEditText.getId();
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(percentageEditText, 0);
                percentageEditText.setText("");
                return false;
            }
        });
    }

    protected void handleDollarFormatting(final TextWatcher textWatcher, EditText editText, String stringValue) {
        editText.removeTextChangedListener(textWatcher);
        try {
            Long mortAmount = InputUtils.convertStringToLong(stringValue);
            String displayAmount = InputUtils.formatDisplayNumberWithCommas(mortAmount);
            editText.setText(displayAmount);
            editText.setSelection(displayAmount.length());
        } catch (NumberFormatException ex) {

        }
        editText.addTextChangedListener(textWatcher);
    }

    protected void calculateValues() {
        double mi = Double.valueOf(IREditText.getText().toString()) / 1200;
        double base = 1;
        double mbase = 1 + mi;
        double HOA = 0;
        for (int i = 0; i < Integer.valueOf(termsEditText.getText().toString()) * 12; i++) {
            base = base * mbase;
        }

        float loanAmount = (float) InputUtils.convertStringToLong(mortAmountEditText.getText().toString()) -
                (float) InputUtils.convertStringToLong(downPaymentEditText.getText().toString());
        float annualTax =
                (Float.valueOf(taxEditText.getText().toString()) * (float) InputUtils.convertStringToLong(mortAmountEditText.getText().toString())) /
                        100;
        float annualInsurance = Float.valueOf(insuranceEditText.getText().toString());

        principalTextView.setText(String.format(" %,.2f", loanAmount * mi / (1 - (1 / base))));
        taxesTextView.setText(String.format("%,.2f", annualTax / 12));
        insuranceTextView.setText(String.format("%,.2f", annualInsurance / 12));
        HOA = Double.valueOf(HOA_EditText.getText().toString());
        totalTextView.setText(String.format("%,.2f", loanAmount * mi / (1 - (1 / base)) + annualTax / 12 + annualInsurance / 12 + HOA));
        if (SplashActivity.hasInternet(this)) {
            //dont' request new ad every time they click
            //            requestNewAd();
        }

        //		try {
        //			handler.post(
        //			new Runnable() {
        //
        //				@Override
        //				public void run() {
        //					boolean adLoaded = adView.loadAd();
        //					Log.i("Reid","LOADING AD now! : " + adLoaded);
        //				}
        //			});
        //
        //
        //		} catch (NullPointerException e) {
        //			Log.i("mc","error from amazon");
        //		}

    }

    private void requestNewAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mAdView.loadAd(adRequest);
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
//already showing an interstitial on splash page
//        mInterstitialAd.loadAd(adRequest);
    }

    protected boolean validateValues() {
        boolean isValid = true;
        // Check each editText and if it's blank set it to 0 or it's defaulted value.
        if (taxEditText.getText().toString().equalsIgnoreCase("")) {
            taxEditText.setText("1.1");//default to Los Angeles
        }
        if (insuranceEditText.getText().toString().equalsIgnoreCase("")) {
            insuranceEditText.setText("1200"); //default to cheap LA insruance
        }

        if (HOA_EditText.getText().toString().equalsIgnoreCase("")) {
            HOA_EditText.setText("0"); //default to NO HOA
        }
        if (termsEditText.getText().toString().equalsIgnoreCase("")) {
            termsEditText.setText("30"); //default to 30  year loan
        }
        if (IREditText.getText().toString().equalsIgnoreCase("")) {
            IREditText.setText("4");
        }

        if (percentageEditText.getText().length() > 0 && mLastEdited == percentageEditText.getId()) {
            recalculate(1);//recalculate the downpayment term
        } else if (downPaymentEditText.getText().length() > 0 && mLastEdited == downPaymentEditText.getId()) {
            recalculate(0);
        }
        try {
            if (InputUtils.convertStringToLong(mortAmountEditText.getText().toString()) < 0) {
                isValid = false;
            }
            if (InputUtils.convertStringToLong(downPaymentEditText.getText().toString()) < 0) {
                isValid = false;
            }
        } catch (Exception e) {
            isValid = false;
        }
        return isValid;
    }

    private void attachTextChangeHandlers() {
        mortAmountEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.MortgateAmountEditText) {
                    if (mortAmountEditText.getText().toString().length() > 0) {
                        mortAmountEditText.setText(InputUtils
                                .formatDisplayNumberWithCommas(InputUtils.convertStringToLong(mortAmountEditText.getText().toString())));
                    }
                }
            }
        });
        downPaymentEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && v.getId() == R.id.DownPaymentEditText) {
                    recalculate(0);
                }
            }
        });

        percentageEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && v.getId() == R.id.PercentageEditText) {
                    recalculate(1);
                }
            }
        });
    }

    protected void recalculate(Integer type) {
        //check the view if it's id is downpayment, then convert percentage and vice versa
        try {

            if (!mortAmountEditText.getText().toString().equalsIgnoreCase("") && InputUtils.convertStringToLong(mortAmountEditText.getText().toString()) > 0) {
                long mortAmount = (Long) NumberFormat.getNumberInstance(java.util.Locale.US).parse(mortAmountEditText.getText().toString());
                long downPayment = 0;
                double percent = 0;
                boolean isValid = true;
                if (type.equals(0)) {
                    try {
                        downPayment = InputUtils.convertStringToLong(downPaymentEditText.getText().toString());
                    } catch (Exception e) {
                        isValid = false;
                    }
                } else {
                    try {
                        percent = Double.valueOf(percentageEditText.getText().toString().equals("0.0") ? "0" : percentageEditText.getText().toString());
                    } catch (Exception e) {
                        isValid = false;
                    }
                }

                if (isValid) {
                    if (type.equals(0)) {
                        double newPercent = ((double) downPayment / mortAmount) * 100;
                        percentageEditText.setText(String.format("%.2f", newPercent));
                    } else {
                        int newMort = (int) ((percent / 100) * mortAmount);
                        downPaymentEditText.setText(String.valueOf(newMort));
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //amazon BS

    //	public void showInterstitial() {
    //		try {
    //		final InterstitialAd interstitialAd = new InterstitialAd(this);
    //
    //		// Set the listener to use the callbacks below.
    //		interstitialAd.setListener(new AdListener() {
    //			@Override
    //			public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
    //				interstitialAd.showAd();
    //			}
    //
    //			@Override
    //			public void onAdFailedToLoad(final Ad ad, final AdError adError) {
    //				Log.i("mc", "ad failed: " + adError.getMessage());
    //			}
    //
    //			@Override
    //			public void onAdExpanded(final Ad ad) {
    //
    //			}
    //
    //			@Override
    //			public void onAdCollapsed(final Ad ad) {
    //
    //			}
    //
    //			@Override
    //			public void onAdDismissed(final Ad ad) {
    //			}
    //		});
    //
    //		// Load the interstitial.
    //		handler.post(
    //		new Runnable() {
    //
    //			@Override
    //			public void run() {
    //				interstitialAd.loadAd();
    //			}
    //		});
    //
    //		} catch (NullPointerException e) {
    //			Log.i("mc", "null pointer from Amazon: " + e.getMessage());
    //		}
    //	}

    @Override
    public void onResume() {
        super.onResume();

        // Create the interstitial.

        //amazon bs
        //		Log.i("mc","mCount: " + mCount);
        //		try {
        //			handler.post(
        //			new Runnable() {
        //
        //				@Override
        //				public void run() {
        //					adView.loadAd();
        //				}
        //			});
        //
        //		} catch(NullPointerException e) {
        //			Log.i("mc", "null pointer from Amazon: " + e.getMessage());
        //		}

        if (SplashActivity.hasInternet(this) && ContextCompat.checkSelfPermission(this,
                permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("deviceid")
                    .build();
            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
            if (mAdView != null) {
                mAdView.resume();
            }
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial));

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mInterstitialAd.show();
                }

                @Override
                public void onAdFailedToLoad(final int errorCode) {
                    super.onAdFailedToLoad(errorCode);
                }

                @Override
                public void onAdClosed() {

                }
            });
            if (mCount % 2 == 0) {
                requestNewInterstitial();
                Log.i("mc", "LOADING AD now!");
            }
            mCount++;
        }
    }

    @Override
    public void onPause() {
        //		if (adView != null) {
        //			adView.pause();
        //		}
        super.onPause();

//        if (locationManager != null) {
//            locationManager.removeUpdates(this);
//        }
    }

    /**
     * Called before the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void initViews() {
        loadStateMap();

        mQueue = Volley.newRequestQueue(this);
        mortAmountEditText = (EditText) findViewById(R.id.MortgateAmountEditText);

        downPaymentEditText = (EditText) findViewById(R.id.DownPaymentEditText);
        percentageEditText = (EditText) findViewById(R.id.PercentageEditText);
        taxEditText = (EditText) findViewById(R.id.TaxEditText);
        insuranceEditText = (EditText) findViewById(R.id.InsuranceEditText);
        termsEditText = (EditText) findViewById(R.id.TermsEditText);
        IREditText = (EditText) findViewById(R.id.IREditText);
        calculateButton = (Button) findViewById(R.id.CalculateButton);
        HOA_EditText = (EditText) findViewById(R.id.HOAEditText);
        principalTextView = (TextView) findViewById(R.id.PrincipalValueText);
        insuranceTextView = (TextView) findViewById(R.id.InsuranceMonthValueText);
        taxesTextView = (TextView) findViewById(R.id.TaxesMonthValueText);
        totalTextView = (TextView) findViewById(R.id.TotalValueText);

        mAdView = (AdView) findViewById(R.id.adView);

//		Log.i("Reid","LOADING AD now!");
//		try {
//			handler.post(
//					new Runnable() {
//
//						@Override
//						public void run() {
//							AdTargetingOptions adOptions = new AdTargetingOptions();
//							adView.loadAd(adOptions);			
//						}
//					});
//
//		} catch(NullPointerException e) {
//			Log.i("mc", "null pointer from Amazon: " + e.getMessage());
//		}
    }

    private void setupLocation() {
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mProvider = locationManager.getBestProvider(criteria, false);
    }

    private float getMortgageRate(String location) {
        float rate = 0;

        // Instantiate the RequestQueue.
//        String url = "http://www.zillow.com/webservice/GetRateSummary.htm?zws-id=" + getString(R.string.zillow_id) + "&state=" + state + "&output=json";
        String url = "https://mortgageapi.zillow.com/getRates?partnerId=" + getString(R.string.zillow_id) +
                "&includeCurrentRate=true&queries.rateData.location.zipCode=" + location;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject rootObject = new JSONObject(response);
                            String thirtyYearFixed = rootObject.getJSONObject("rates").getJSONObject("rateData").getJSONObject("currentRate").getString("rate");
                            IREditText.setText(thirtyYearFixed);

                            Animation fadeIn = AnimationUtils.loadAnimation(Main.this, R.anim.fade_in);
                            IREditText.startAnimation(fadeIn);

                            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    IREditText.setTextColor(Color.GREEN);
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    Animation fadeOut = AnimationUtils.loadAnimation(Main.this, R.anim.fade_out);
                                    IREditText.startAnimation(fadeOut);
                                    IREditText.setTextColor(Color.WHITE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("mc", "error: " + error.getMessage());
            }
        });
        stringRequest.setTag(TAG);
// Add the request to the RequestQueue.
        mQueue.add(stringRequest);

        return rate;
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChanged(final Location location) {
        mLastLocation = location;
        processLocation(location);
    }

    private void processLocation(final Location location) {
        try {
            if (!mStateName.equals(getStateName(location))) {
                //This is actually zip code for now.
                mStateName = getStateName(location);
                getMortgageRate(mStateName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStateName(final Location location) throws IOException {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        String address = "";

        try {
            if (addresses != null && addresses.size() >= 0) {
                return addresses.get(0).getPostalCode();
//                return states.get(addresses.get(0).getAdminArea());
            }
        } catch (Exception e) {
            //dont fail on international users.
        }
        return "";
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(TAG);
        }
        if (ContextCompat.checkSelfPermission(this,
                permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mGoogleApiClient.disconnect();
        }
    }

    private void loadStateMap() {

        states = new HashMap<>();
        states.put("Alabama", "AL");
        states.put("Alaska", "AK");
        states.put("Alberta", "AB");
        states.put("American Samoa", "AS");
        states.put("Arizona", "AZ");
        states.put("Arkansas", "AR");
        states.put("Armed Forces (AE)", "AE");
        states.put("Armed Forces Americas", "AA");
        states.put("Armed Forces Pacific", "AP");
        states.put("British Columbia", "BC");
        states.put("California", "CA");
        states.put("Colorado", "CO");
        states.put("Connecticut", "CT");
        states.put("Delaware", "DE");
        states.put("District Of Columbia", "DC");
        states.put("Florida", "FL");
        states.put("Georgia", "GA");
        states.put("Guam", "GU");
        states.put("Hawaii", "HI");
        states.put("Idaho", "ID");
        states.put("Illinois", "IL");
        states.put("Indiana", "IN");
        states.put("Iowa", "IA");
        states.put("Kansas", "KS");
        states.put("Kentucky", "KY");
        states.put("Louisiana", "LA");
        states.put("Maine", "ME");
        states.put("Manitoba", "MB");
        states.put("Maryland", "MD");
        states.put("Massachusetts", "MA");
        states.put("Michigan", "MI");
        states.put("Minnesota", "MN");
        states.put("Mississippi", "MS");
        states.put("Missouri", "MO");
        states.put("Montana", "MT");
        states.put("Nebraska", "NE");
        states.put("Nevada", "NV");
        states.put("New Brunswick", "NB");
        states.put("New Hampshire", "NH");
        states.put("New Jersey", "NJ");
        states.put("New Mexico", "NM");
        states.put("New York", "NY");
        states.put("Newfoundland", "NF");
        states.put("North Carolina", "NC");
        states.put("North Dakota", "ND");
        states.put("Northwest Territories", "NT");
        states.put("Nova Scotia", "NS");
        states.put("Nunavut", "NU");
        states.put("Ohio", "OH");
        states.put("Oklahoma", "OK");
        states.put("Ontario", "ON");
        states.put("Oregon", "OR");
        states.put("Pennsylvania", "PA");
        states.put("Prince Edward Island", "PE");
        states.put("Puerto Rico", "PR");
        states.put("Quebec", "QC");
        states.put("Rhode Island", "RI");
        states.put("Saskatchewan", "SK");
        states.put("South Carolina", "SC");
        states.put("South Dakota", "SD");
        states.put("Tennessee", "TN");
        states.put("Texas", "TX");
        states.put("Utah", "UT");
        states.put("Vermont", "VT");
        states.put("Virgin Islands", "VI");
        states.put("Virginia", "VA");
        states.put("Washington", "WA");
        states.put("West Virginia", "WV");
        states.put("Wisconsin", "WI");
        states.put("Wyoming", "WY");
        states.put("Yukon Territory", "YT");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 0 && grantResults.length > 0) {
            initOnCreate();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,
                permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            finish();
            return;
        }
        mLastLocation = FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            processLocation(mLastLocation);
        }
        FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(final int i) {

    }

    protected void onStart() {
        if (ContextCompat.checkSelfPermission(this,
                permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {

    }

//    private void requestPermissions() {
//        final ArrayList<String> permissionsMissing = new ArrayList<>();
//        ArrayList<String> permissionsNeeded = new ArrayList<>();
//        permissionsNeeded.add(permission.ACCESS_COARSE_LOCATION);
//        permissionsNeeded.add(permission.ACCESS_FINE_LOCATION);
//        for (final String permission : permissionsNeeded) {
//            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                permissionsMissing.add(permission);
//            }
//        }
//
//        if (!permissionsMissing.isEmpty()) {
//            final String[] array = new String[permissionsMissing.size()];
//            permissionsMissing.toArray(array);
//
//            //We will get the result asynchronously in onRequestPermissionsResult().
//            ActivityCompat.requestPermissions(this, array, 10001);
//        }
//    }
}
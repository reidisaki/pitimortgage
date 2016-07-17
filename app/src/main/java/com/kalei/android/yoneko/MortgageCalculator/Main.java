package com.kalei.android.yoneko.MortgageCalculator;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;

public class Main extends Activity {
    private EditText mortAmountEditText, downPaymentEditText, percentageEditText, taxEditText, insuranceEditText, termsEditText, IREditText, HOA_EditText;
    private TextView principalTextView, taxesTextView, insuranceTextView, totalTextView;
    private Button calculateButton;
    //	private AdLayout adView;
    private AdView mAdView;
    private int mLastEdited = 0;
    private int mCount = 0;
    Handler handler = new Handler();
    private InterstitialAd mInterstitialAd;
    /* Your ad unit id. Replace with your actual ad unit id. */

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //		AdRegistration.setAppKey("ebbbcbf8ca734a10aa32cffb9f2c4971");

        initViews();
        validateValues();
        attachTextChangeHandlers();
        attachButtonHandlers();

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

        float loanAmount = Float.valueOf(InputUtils.convertStringToLong(mortAmountEditText.getText().toString())) -
                Float.valueOf(InputUtils.convertStringToLong(downPaymentEditText.getText().toString()));
        float annualTax =
                (Float.valueOf(taxEditText.getText().toString()) * Float.valueOf(InputUtils.convertStringToLong(mortAmountEditText.getText().toString()))) /
                        100;
        float annualInsurance = Float.valueOf(insuranceEditText.getText().toString());

        principalTextView.setText(String.format(" %,.2f", loanAmount * mi / (1 - (1 / base))));
        taxesTextView.setText(String.format("%,.2f", annualTax / 12));
        insuranceTextView.setText(String.format("%,.2f", annualInsurance / 12));
        HOA = Double.valueOf(HOA_EditText.getText().toString());
        totalTextView.setText(String.format("%,.2f", loanAmount * mi / (1 - (1 / base)) + annualTax / 12 + annualInsurance / 12 + HOA));
        requestNewAd();

        //		try {
        //			handler.post(
        //			new Runnable() {
        //
        //				@Override
        //				public void run() {
        //					// TODO Auto-generated method stub
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
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
//already showing an interstitial on splash page
//		mInterstitialAd.loadAd(adRequest);
    }

    protected boolean validateValues() {
        boolean isValid = true;
        // Check each editText and if it's blank set it to 0 or it's defaulted value.
        if (taxEditText.getText().toString().equalsIgnoreCase("")) {
            taxEditText.setText("1.08");//default to Los Angeles
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
                        downPayment = Long.valueOf(InputUtils.convertStringToLong(downPaymentEditText.getText().toString()));
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
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
    //				// TODO Auto-generated method stub
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
        //					// TODO Auto-generated method stub
        //					adView.loadAd();
        //				}
        //			});
        //
        //		} catch(NullPointerException e) {
        //			Log.i("mc", "null pointer from Amazon: " + e.getMessage());
        //		}

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

    @Override
    public void onPause() {
        //		if (adView != null) {
        //			adView.pause();
        //		}
        super.onPause();
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
//							// TODO Auto-generated method stub
//							AdTargetingOptions adOptions = new AdTargetingOptions();
//							adView.loadAd(adOptions);			
//						}
//					});
//
//		} catch(NullPointerException e) {
//			Log.i("mc", "null pointer from Amazon: " + e.getMessage());
//		}
    }
}
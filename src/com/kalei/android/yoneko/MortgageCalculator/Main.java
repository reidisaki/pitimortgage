package com.kalei.android.yoneko.MortgageCalculator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
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

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;
import com.amazon.device.ads.InterstitialAd;




public class Main extends Activity {
	private EditText mortAmountEditText, downPaymentEditText, percentageEditText, taxEditText, insuranceEditText, termsEditText,IREditText, HOA_EditText; 
	private TextView principalTextView, taxesTextView, insuranceTextView, totalTextView;
	private Button calculateButton;
	private AdLayout adView;
	private int mLastEdited =0;
	private int mCount = 0;
	Handler handler = new Handler();
	/* Your ad unit id. Replace with your actual ad unit id. */
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);	

		AdRegistration.setAppKey("ebbbcbf8ca734a10aa32cffb9f2c4971");
		
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
				if(validateValues()) {
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
		mortAmountEditText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mortAmountEditText.setText("");
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mortAmountEditText, 0);
				return false;
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

	protected void calculateValues() {
		// TODO Auto-generated method stub
		double mi = Double.valueOf(IREditText.getText().toString()) / 1200;
		double base = 1;
		double mbase = 1 + mi;
		double HOA =0;
		for (int i=0; i<Integer.valueOf(termsEditText.getText().toString()) * 12; i++)
		{
			base = base * mbase;
		}

		float loanAmount = Float.valueOf(mortAmountEditText.getText().toString()) - Float.valueOf(downPaymentEditText.getText().toString());
		float annualTax = (Float.valueOf(taxEditText.getText().toString()) * Float.valueOf(mortAmountEditText.getText().toString()))/100;
		float annualInsurance = Float.valueOf(insuranceEditText.getText().toString());

		
		
		principalTextView.setText(String.format(" %,.2f", loanAmount * mi / ( 1 - (1/base))));
		taxesTextView.setText(String.format("%,.2f",annualTax/12));
		insuranceTextView.setText(String.format("%,.2f",annualInsurance / 12));
		HOA = Double.valueOf(HOA_EditText.getText().toString());
		totalTextView.setText(String.format("%,.2f",loanAmount * mi / ( 1 - (1/base)) + annualTax /12 + annualInsurance /12 + HOA));
		
		try {
			handler.post(
			new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					boolean adLoaded = adView.loadAd();
					Log.i("Reid","LOADING AD now! : " + adLoaded);
				}
			});
		
		
		} catch (NullPointerException e) {
			Log.i("mc","error from amazon");
		}
		
	}

	protected boolean validateValues() {
		boolean isValid = true;
		// Check each editText and if it's blank set it to 0 or it's defaulted value.
		if(taxEditText.getText().toString().equalsIgnoreCase("")){
			taxEditText.setText("1.08");//default to Los Angeles			
		}
		if(insuranceEditText.getText().toString().equalsIgnoreCase("")){
			insuranceEditText.setText("1200"); //default to cheap LA insruance
		}

		if(HOA_EditText.getText().toString().equalsIgnoreCase("")){
			HOA_EditText.setText("0"); //default to NO HOA
		}
		if(termsEditText.getText().toString().equalsIgnoreCase("")){			
			termsEditText.setText("30"); //default to 30  year loan
		}
		if(IREditText.getText().toString().equalsIgnoreCase("")){
			IREditText.setText("4");
		}

		if(percentageEditText.getText().length() > 0 && mLastEdited == percentageEditText.getId()) {
			recalculate(1);//recalculate the downpayment term
		} else if(downPaymentEditText.getText().length() > 0 && mLastEdited == downPaymentEditText.getId()) {
			recalculate(0);
		}
		try {
			if(Double.parseDouble(mortAmountEditText.getText().toString()) < 0 ) {
				isValid = false;
			}
			if(Double.parseDouble(downPaymentEditText.getText().toString()) < 0 ) {
				isValid = false;
			}
		} catch(Exception e) {
			isValid = false;
		}
		return isValid;
	}

	private void attachTextChangeHandlers() {
		downPaymentEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus && v.getId() == R.id.DownPaymentEditText) {
					recalculate(0);
				} 
			}
		});

		percentageEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus && v.getId() == R.id.PercentageEditText) {
					recalculate(1);
				}
			}
		});

	}
	protected void recalculate(Integer type) {
		//check the view if it's id is downpayment, then convert percentage and vice versa
		if(!mortAmountEditText.getText().toString().equalsIgnoreCase("") && Double.valueOf(mortAmountEditText.getText().toString()) > 0){			
			double downPayment =0;
			double percent = 0;
			boolean isValid = true;
			if(type.equals(0)){
				try {
					downPayment = Double.valueOf(downPaymentEditText.getText().toString());
				} catch(Exception e) {
					isValid = false;
				}	
			} else {
				try {					
					percent = Double.valueOf(percentageEditText.getText().toString());
				} catch(Exception e) {
					isValid = false;
				}					
			}

			if(isValid) {
				double mortgage =  Double.valueOf(mortAmountEditText.getText().toString());
				if(type.equals(0)){
					double newPercent = ((double)downPayment/mortgage)*100;
					percentageEditText.setText(String.format("%.2f", newPercent));
				} else {
					double newMort = (double)((percent/100) * mortgage);
					downPaymentEditText.setText(String.format("%.2f", newMort));
				}
			}
		}	

	}

	public void showInterstitial() {
		try {
		final InterstitialAd interstitialAd = new InterstitialAd(this);

		// Set the listener to use the callbacks below.
		interstitialAd.setListener(new AdListener() {
			@Override
			public void onAdLoaded(final Ad ad, final AdProperties adProperties) {
				interstitialAd.showAd();
			}

			@Override
			public void onAdFailedToLoad(final Ad ad, final AdError adError) {
				Log.i("mc", "ad failed: " + adError.getMessage());                
			}

			@Override
			public void onAdExpanded(final Ad ad) {

			}

			@Override
			public void onAdCollapsed(final Ad ad) {

			}

			@Override
			public void onAdDismissed(final Ad ad) {                
			}
		});

		// Load the interstitial.
		handler.post(
		new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				interstitialAd.loadAd();		
			}
		});
		
		} catch (NullPointerException e) {
			Log.i("mc", "null pointer from Amazon: " + e.getMessage());
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		// Create the interstitial.
		if(mCount % 2 == 0) {			
			showInterstitial();
			Log.i("mc","LOADING AD now!");
		}
		mCount++;
		Log.i("mc","mCount: " + mCount);
		try {
			handler.post(
			new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					adView.loadAd();			
				}
			});
		
		} catch(NullPointerException e) {
			Log.i("mc", "null pointer from Amazon: " + e.getMessage());
		}

		//		AdRequest adRequest = new AdRequest.Builder()
		//		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		//		.addTestDevice("deviceid")
		//		.build();
		//		// Start loading the ad in the background.
		//		adView.loadAd(adRequest);
		//		if (adView != null) {
		//			adView.resume();
		//		}
	}

	@Override
	public void onPause() {
		//		if (adView != null) {
		//			adView.pause();
		//		}
		super.onPause();
	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() {
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}
	private void initViews() {

		mortAmountEditText = (EditText)findViewById(R.id.MortgateAmountEditText);

		downPaymentEditText = (EditText)findViewById(R.id.DownPaymentEditText);
		percentageEditText = (EditText)findViewById(R.id.PercentageEditText);
		taxEditText = (EditText)findViewById(R.id.TaxEditText);				
		insuranceEditText = (EditText)findViewById(R.id.InsuranceEditText);		
		termsEditText = (EditText)findViewById(R.id.TermsEditText);				
		IREditText = (EditText)findViewById(R.id.IREditText);
		calculateButton = (Button)findViewById(R.id.CalculateButton);
		HOA_EditText = (EditText)findViewById(R.id.HOAEditText);		
		principalTextView = (TextView)findViewById(R.id.PrincipalValueText);
		insuranceTextView = (TextView)findViewById(R.id.InsuranceMonthValueText);
		taxesTextView = (TextView)findViewById(R.id.TaxesMonthValueText);
		totalTextView = (TextView)findViewById(R.id.TotalValueText);

		adView = (AdLayout)findViewById(R.id.adView);
		
		Log.i("Reid","LOADING AD now!");
		try {
			handler.post(
			new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					AdTargetingOptions adOptions = new AdTargetingOptions();
					adView.loadAd(adOptions);			
				}
			});
		
		} catch(NullPointerException e) {
			Log.i("mc", "null pointer from Amazon: " + e.getMessage());
		}
	}
}
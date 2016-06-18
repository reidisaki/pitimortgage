package com.kalei.android.yoneko.MortgageCalculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class InputUtils {

	public static long convertStringToLong(String value) {
		try {
			return (Long)NumberFormat.getNumberInstance(java.util.Locale.US).parse(value);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static String formatDisplayNumberWithCommas(long value){
		DecimalFormat formatter = new DecimalFormat("#,###");
		return formatter.format(value).toString();
	}
}

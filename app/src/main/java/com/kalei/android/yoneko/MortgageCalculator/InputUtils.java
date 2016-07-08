package com.kalei.android.yoneko.MortgageCalculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class InputUtils {

    public static long convertStringToLong(String value) {
        if (value.length() > 0) {
            try {
                value = value.replace(",", "");
                return (Long) NumberFormat.getNumberInstance(java.util.Locale.US).parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static String formatDisplayNumberWithCommas(long value) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
//        return NumberFormat.getInstance(java.util.Locale.US).format(value);
        return formatter.format(value).toString();
    }
}

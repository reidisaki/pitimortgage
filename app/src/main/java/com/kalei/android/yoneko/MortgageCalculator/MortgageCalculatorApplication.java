package com.kalei.android.yoneko.MortgageCalculator;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by risaki on 5/4/17.
 */

public class MortgageCalculatorApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
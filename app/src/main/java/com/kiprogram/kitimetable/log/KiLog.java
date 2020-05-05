package com.kiprogram.kitimetable.log;

import android.util.Log;

public final class KiLog {
    private KiLog() {}
    private static final String TAG = "kilog";
    public static void i(CharSequence cs) {
        Log.i(TAG, cs.toString());
    }
    public static void e(CharSequence cs) {
        Log.e(TAG, cs.toString());
    }
}

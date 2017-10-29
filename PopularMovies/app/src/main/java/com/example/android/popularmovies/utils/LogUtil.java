package com.example.android.popularmovies.utils;

import android.util.Log;

/**
 * Created by PrajktiK on Oct/26/2017.
 */

public final class LogUtil{

    public static final boolean DEBUG = true;

    private static final String APP_TAG = "PopularMoviesApp :: ";

    public static void error(String logTag, String message){
        Log.e(APP_TAG + logTag, message);
    }

    public static void info(String logTag, String message){
        Log.i(APP_TAG + logTag, message);
    }

    public static void debug(String logTag, String message){
        Log.d(APP_TAG + logTag, message);
    }

    public static void verbose(String logTag, String message){
        Log.v(APP_TAG + logTag, message);
    }

}

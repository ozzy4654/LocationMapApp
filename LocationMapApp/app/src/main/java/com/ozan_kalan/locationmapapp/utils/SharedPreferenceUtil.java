package com.ozan_kalan.locationmapapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ozan.kalan on 6/30/17.
 *
 * To be used for future extensibility as app
 * needs to access SharedPrefs to store or grab data
 *
 */

public class SharedPreferenceUtil {

    private static final String LOCATION_MAP_APP = "lma_prefs";

    private static SharedPreferences sPrefs;
    private static SharedPreferenceUtil sSharedPreferenceUtil;

    public SharedPreferenceUtil(Context mContext) {
        if (sPrefs == null) {
            sPrefs = mContext.getSharedPreferences(LOCATION_MAP_APP, Context.MODE_PRIVATE);
        }
    }

    public static SharedPreferenceUtil getInstance(Context mContext) {
        if (sSharedPreferenceUtil == null) {
            sSharedPreferenceUtil = new SharedPreferenceUtil(mContext);
        }
        return sSharedPreferenceUtil;
    }

    public static SharedPreferenceUtil getInstance() {
        return sSharedPreferenceUtil;
    }

    public String getString(String mKey) {
        return sPrefs.getString(mKey, "");
    }

    public void setString(String mKey, String mValue) {
        sPrefs.edit().putString(mKey, mValue).commit();
    }

    public long getLong(String aKey) {
        return sPrefs.getLong(aKey, -1);
    }

    public void setLong(String mKey, long mValue) {
        sPrefs.edit().putLong(mKey, mValue).commit();
    }

    public void clear() {
        sPrefs.edit().clear().commit();
    }


}

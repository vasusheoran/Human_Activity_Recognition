package com.bits.har.entities;

import android.content.SharedPreferences;

import com.bits.har.main.MainActivity;

public class BaseActivity  {
    protected SharedPreferences getDefaultSharedPreferences() {
        return MainActivity.preferences;
    }


}

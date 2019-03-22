package com.bits.har;

import android.content.SharedPreferences;

public class BaseActivity  {
    protected SharedPreferences getDefaultSharedPreferences() {
        return MainActivity.preferences;
    }


}

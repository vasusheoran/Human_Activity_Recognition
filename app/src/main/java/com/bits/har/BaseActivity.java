package com.bits.har;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.bits.har.MainActivity.activity;

public class BaseActivity  {
    protected SharedPreferences getDefaultSharedPreferences() {
        return MainActivity.preferences;
    }


}

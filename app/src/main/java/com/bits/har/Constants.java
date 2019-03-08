package com.bits.har;

import android.Manifest;

public class Constants {

    public static final int GYROSCOPE = 1;
    public static final int ACCELEROMETER = 2;
    public static final int MAGNETOMETER = 3;
    public static final int FUSEDORIENTATION = 4;

    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.BODY_SENSORS
    };

}

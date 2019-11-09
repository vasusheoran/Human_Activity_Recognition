package com.bits.har.metadata;

import android.Manifest;
import android.os.Environment;

public class Constants {

    public static final int GYROSCOPE = 1;
    public static final int ACCELEROMETER = 2;
    public static final int MAGNETOMETER = 3;
    public static final int FUSEDORIENTATION = 4;



    public static final String[] LABELS = {"Jogging", "Walking", "Upstairs","Running","Downstairs","Standing"};

    public static final int YIELD = 5;              // A new classification every 5 sec.

    public static final int N_SAMPLES = 50;
    public static final int BATCH_SIZE = N_SAMPLES * 6;
    public static final int N_FEATURES = 6;

    public static final int OUTPUT_SIZE = 6;

    public static final String DATA_PATH = Environment.getExternalStorageDirectory() + "/track/dataset/";
    public static final String RESULT_PATH = Environment.getExternalStorageDirectory() + "/track/results/";

    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.BODY_SENSORS
    };

    public static final String[] ORIENTATION = {"Towards_Up",
            "Towards_Down",
            "Away_Up",
            "Away_Down"};


    public static final String[] ACTIVITY_TYPE = {"WALKING",
            "RUNNING",
            "STANDING",
            "JOGGING",
            "STAIRS_UP",
            "STAIRS_DN"};
}

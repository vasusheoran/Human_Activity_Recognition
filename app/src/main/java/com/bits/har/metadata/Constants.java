package com.bits.har.metadata;

import android.Manifest;
import android.os.Environment;

public class Constants {

    public static final int GYROSCOPE = 1;
    public static final int ACCELEROMETER = 2;
    public static final int MAGNETOMETER = 3;
    public static final int FUSEDORIENTATION = 4;

    public static final  float[] MU = new float[]{0.197048f,0.276003f,0.534628f, -0.056207f, -0.240377f,-0.186459f};
    public static final  float[] SIGMA = new float[]{5.454867f,12.333439f, 6.947743f, 5.347857f, 7.901437f, 7.002565f};



    public static final String[] LABELS = {"JOGGING", "RUNNING", "STAIRS_DN", "STAIRS_UP", "STANDING","WALKING"};
    //public static final String[] LABELS = {"Jogging", "Walking", "Upstairs","Running","Downstairs","Standing"};

    public static final int YIELD = 5;              // A new classification every 5 sec.

    public static final int N_SAMPLES = 80;
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

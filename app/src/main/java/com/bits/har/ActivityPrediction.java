package com.bits.har;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ActivityPrediction {

    private static final String TAG = "MyActivity";

    private static final int N_SAMPLES = 100;
    public static Queue<Float> accX;
    public static Queue<Float> accY;
    public static Queue<Float> accZ;
    public static Queue<Float> gyroX;
    public static Queue<Float> gyroY;
    public static Queue<Float> gyroZ;
    public static Queue<Float> accMagOrientationX;
    public static Queue<Float> accMagOrientationY;
    public static Queue<Float> accMagOrientationZ;
    public static Queue<Float> fusedOrientationX;
    public static Queue<Float> fusedOrientationY;
    public static Queue<Float> fusedOrientationZ;

    private String previousResult;
    private TensorFlowClassifier classifier;
    public List<Float> data = new ArrayList<>();


    private void activityPrediction() {
        Log.v(TAG, "Activity Prediction");
    }
}

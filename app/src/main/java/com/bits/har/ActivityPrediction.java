package com.bits.har;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ActivityPrediction {

    private static final String TAG = "MyActivity";

    private static final int N_SAMPLES = 100;
    public static Queue<Float> accX = new LinkedList<>();
    public static Queue<Float> accY = new LinkedList<>();
    public static Queue<Float> accZ = new LinkedList<>();
    public static Queue<Float> gyroX = new LinkedList<>();
    public static Queue<Float> gyroY = new LinkedList<>();
    public static Queue<Float> gyroZ = new LinkedList<>();
    public static Queue<Float> gyroOrientationX = new LinkedList<>();
    public static Queue<Float> gyroOrientationY = new LinkedList<>();
    public static Queue<Float> gyroOrientationZ = new LinkedList<>();
    public static Queue<Float> fusedOrientationX = new LinkedList<>();
    public static Queue<Float> fusedOrientationY = new LinkedList<>();
    public static Queue<Float> fusedOrientationZ = new LinkedList<>();

    private String previousResult;
    private TensorFlowClassifier classifier;
    public List<Float> data = new ArrayList<>();


    public static void activityPrediction() {
        Log.v(TAG, "Activity Prediction");
    }
}

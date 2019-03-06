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
    public static Queue<Float> ax;
    public static Queue<Float> ay;
    public static Queue<Float> az;
    public static Queue<Float> gx;
    public static Queue<Float> gy;
    public static Queue<Float> gz;
    public static Queue<Float> ox;

    private String previousResult;
    private TensorFlowClassifier classifier;
    public List<Float> data = new ArrayList<>();


    private void activityPrediction() {
        Log.v(TAG, "Activity Prediction");
    }
}

package com.bits.har;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private static final int N_SAMPLES = 100;
    private static Queue<Float> ax;
    private static Queue<Float> ay;
    private static Queue<Float> az;
    private static Queue<Float> gx;
    private static Queue<Float> gy;
    private static Queue<Float> gz;
    private TextView downstairsTextView;

    private TextView joggingTextView;
    private TextView sittingTextView;
    private TextView standingTextView;
    private TextView upstairsTextView;
    private TextView walkingTextView;
    private TextToSpeech textToSpeech;
    private float[] results;
    private String previousResult;
    private TensorFlowClassifier classifier;

    private String[] labels = {"Jogging", "Standing","Walking"};
//    private String[] labels = {"Downstairs", "Jogging", "Sitting", "Standing", "Upstairs", "Walking"};
    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ax = new LinkedList<>();
        ay = new LinkedList<>();
        az = new LinkedList<>();
        gx = new LinkedList<>();
        gy = new LinkedList<>();
        gz = new LinkedList<>();

//        downstairsTextView = (TextView) findViewById(R.id.downstairs_prob);
        joggingTextView = (TextView) findViewById(R.id.jogging_prob);
//        sittingTextView = (TextView) findViewById(R.id.sitting_prob);
        standingTextView = (TextView) findViewById(R.id.standing_prob);
//        upstairsTextView = (TextView) findViewById(R.id.upstairs_prob);
        walkingTextView = (TextView) findViewById(R.id.walking_prob);

        classifier = new TensorFlowClassifier(getApplicationContext());

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);
        previousResult = "";
    }

    @Override
    public void onInit(int status) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (results == null || results.length == 0) {
                    return;
                }
                float max = -1;
                int idx = -1;
                for (int i = 0; i < results.length; i++) {
                    if (results[i] > max) {
                        idx = i;
                        max = results[i];
                    }
                }

                //if(previousResult!=labels[idx]){
                    textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
                    previousResult = labels[idx];
                    Log.v(TAG, "Activity Prediction : ax - " + ax.size() + " | gxsize - " + gx.size());
                /*}else{
                    Log.v(TAG, "Activity Unchanged");
                }*/
            }
        }, 2000, 5000);
    }

    protected void onPause() {
        getSensorManager().unregisterListener(this);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        activityPrediction();
        final int type = event.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            //Smoothing the sensor data a bit
            ax.add(event.values[0]);
            ay.add(event.values[1]);
            az.add(event.values[2]);

        }
        if (type == Sensor.TYPE_GYROSCOPE) {
            //Smoothing the sensor data a bit
            gx.add(event.values[0]);
            gy.add(event.values[1]);
            gz.add(event.values[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void activityPrediction() {
//
        if (ax.size() >= N_SAMPLES && ay.size() >= N_SAMPLES && az.size() >= N_SAMPLES && gx.size() >= N_SAMPLES && gy.size() >= N_SAMPLES && gz.size() >= N_SAMPLES) {

//            Log.v(TAG, "Inside Activity Prediction : ax - " + ax.size() + " | gxsize - " + gx.size());
            List<Float> data = new ArrayList<>();
            data.addAll(ax);
            data.addAll(ay);
            data.addAll(az);
            data.addAll(gx);
            data.addAll(gy);
            data.addAll(gz);

            results = classifier.predictProbabilities(toFloatArray(data));
            //Log.v(TAG, results.toString());
/*
            downstairsTextView.setText(Float.toString(round(results[0], 2)));
            joggingTextView.setText(Float.toString(round(results[1], 2)));
            sittingTextView.setText(Float.toString(round(results[2], 2)));
            standingTextView.setText(Float.toString(round(results[3], 2)));
            upstairsTextView.setText(Float.toString(round(results[4], 2)));
            walkingTextView.setText(Float.toString(round(results[5], 2)));
            */

            joggingTextView.setText(Float.toString(round(results[0], 2)));
            standingTextView.setText(Float.toString(round(results[1], 2)));
            walkingTextView.setText(Float.toString(round(results[2], 2)));

            ax.clear();
            ay.clear();
            az.clear();
            gx.clear();
            gy.clear();
            gz.clear();
        }
        if(ax.size() == 101){
            ax.remove();
            ay.remove();
            az.remove();
        }
        if(gx.size() == 101){
            gx.remove();
            gy.remove();
            gz.remove();
        }
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }

}

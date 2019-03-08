package com.bits.har;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class MainActivity extends BaseActivity implements TextToSpeech.OnInitListener {
    private static final String TAG = "MyActivity";

    public static Activity activity;

    /*private static final int N_SAMPLES = 100;
    public static Queue<Float> ax;
    public static Queue<Float> ay;
    public static Queue<Float> az;
    public static Queue<Float> gx;
    public static Queue<Float> gy;
    public static Queue<Float> gz;*/

    private TextView downstairsTextView;
    public static FilterSensorData mFilterSensorData = null;

    public static FileWrite fw = null;
    public static String accValue = "";
    public static String gyroValue = "";
    public static String kalmanValue = "";
    public static boolean newIMUValues;

    public static String Qangle = "";
    public static String Qbias = "";
    public static String Rmeasure = "";
    public static boolean newKalmanValues;

    public static String pValue = "";
    public static String iValue = "";
    public static String dValue = "";
    public static String targetAngleValue = "";
    public static boolean newPIDValues;

    public static boolean backToSpot;
    public static int maxAngle = 8; // Eight is the default value
    public static int maxTurning = 20; // Twenty is the default value

    private TextView joggingTextView;
//    private TextView sittingTextView;
    private TextView standingTextView;
//    private TextView upstairsTextView;
    private TextView walkingTextView;
    private TextToSpeech textToSpeech;

    public float[] results;
    private String previousResult;
    private TensorFlowClassifier classifier;
    public static SharedPreferences preferences = null;

    private String[] labels = {"Jogging", "Standing","Walking"};
//    private String[] labels = {"Downstairs", "Jogging", "Sitting", "Standing", "Upstairs", "Walking"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
       /* FileWrite.FOLDER = new File(Environment.getExternalStorageDirectory()
                + "/har");*/
//        FileWrite.readFromFile(activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Set portrait mode only - for small screens like phones
        setContentView(R.layout.activity_main);
        /*ax = new LinkedList<>();
        ay = new LinkedList<>();
        az = new LinkedList<>();
        gx = new LinkedList<>();
        gy = new LinkedList<>();
        gz = new LinkedList<>();*/

        startSession();
        setSensorManager(getApplicationContext());

//        downstairsTextView = (TextView) findViewById(R.id.downstairs_prob);
        joggingTextView = (TextView) findViewById(R.id.jogging_prob);
//        sittingTextView = (TextView) findViewById(R.id.sitting_prob);
        standingTextView = (TextView) findViewById(R.id.standing_prob);
//        upstairsTextView = (TextView) findViewById(R.id.upstairs_prob);
        walkingTextView = (TextView) findViewById(R.id.walking_prob);

        classifier = new TensorFlowClassifier(getApplicationContext());

        /*textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);
        previousResult = "";*/
    }

    private void setSensorManager(Context applicationContext) {

        SensorManager mSensorManger = (SensorManager) getSystemService(SENSOR_SERVICE);
        mFilterSensorData = new FilterSensorData(getApplicationContext(),mSensorManger);

        preferences = PreferenceManager.getDefaultSharedPreferences(this); // Create SharedPreferences instance
        String filterCoefficient = preferences.getString("filterCoefficient", null); // Read the stored value for filter coefficient
        if (filterCoefficient != null) {
            mFilterSensorData.filter_coefficient = Float.parseFloat(filterCoefficient);
            mFilterSensorData.tempFilter_coefficient = mFilterSensorData.filter_coefficient;
        }
        // Read the previous back to spot value
        backToSpot = preferences.getBoolean("backToSpot", true); // Back to spot is true by default
        // Read the previous max angle
        maxAngle = preferences.getInt("maxAngle", 8); // Eight is the default value
        // Read the previous max turning value
        maxTurning = preferences.getInt("maxTurning", 20); // Twenty is the default value
    }

    @Override
    public void onInit(int status) {
        startSession();
        /*Timer timer = new Timer();
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
                *//*}else{
                    Log.v(TAG, "Activity Unchanged");
                }*//*
            }
        }, 2000, 5000);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFilterSensorData.unregisterListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFilterSensorData.initListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mFilterSensorData.unregisterListeners();

        // Store the value for FILTER_COEFFICIENT and max angle at shutdown
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("filterCoefficient", Float.toString(mFilterSensorData.filter_coefficient));
        edit.putBoolean("backToSpot", backToSpot);
        edit.putInt("maxAngle", maxAngle);
        edit.putInt("maxTurning", maxTurning);
        edit.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFilterSensorData.unregisterListeners();
        endSession();
    }

    public boolean startSession() {
        fw = new FileWrite();
        String fileName = FileWrite.getFileName();
        if (!checkPermissions()) return false;
        try {

            String filepath = Environment.getExternalStorageDirectory() + "/track/";
            File directory = new File(filepath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File csvFile = new File(directory, fileName);
            FileWriter writer = new FileWriter(csvFile);

            writer.append("time,ax,ay,az,gx,gy,gz,mx,my,mz,gox,goy,goz,fox,foy,foz\n");
            writer.flush();

            fw.setFile(csvFile);
            fw.setCsvFile(csvFile);
            fw.setFileName(fileName);
            fw.setWriter(writer);

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void endSession() {
        try {
            fw.getWriter().flush();
            fw.getWriter().close();

            Log.d(TAG, "Session over. ");
            Toast.makeText(this, "Sending data to phone!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Session ending error. Writer issue? or csvFile missing?");
            fw = null;
            startSession();
        }
    }

    public static int getRotation() {
        return activity.getWindowManager().getDefaultDisplay().getRotation();
    }

    private void setTextToSpeech() {
            joggingTextView.setText(Float.toString(round(results[0], 2)));
            standingTextView.setText(Float.toString(round(results[1], 2)));
            walkingTextView.setText(Float.toString(round(results[2], 2)));    }

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

}

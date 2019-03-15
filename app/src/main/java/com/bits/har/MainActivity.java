package com.bits.har;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final String TAG = "MyActivity";

    public Activity activity;
    public static FileWrite fw =null;
    protected static List<Float> trainData;

    public FilterSensorData mFilterSensorData;

    public static boolean backToSpot;
    public static int maxAngle = 8; // Eight is the default value
    public static int maxTurning = 20; // Twenty is the default value
    ActivityPrediction activityPrediction;
    private static final int TIME_CONSTANT = 2000;
    private final int N_SAMPLES = Constants.N_SAMPLES;

    public TextView walkingSlowTextView;
    public TextView walkingFastTextView;
    private TextToSpeech textToSpeech;
    public static boolean isVoiceEnabled;

    public static float[] results;
    public static SharedPreferences preferences = null;

    private String[] labels = {"Fast", "Slow","Walking"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Set portrait mode only - for small screens like phones
        setContentView(R.layout.activity_main);
        activityPrediction = new ActivityPrediction(this);

        walkingSlowTextView = findViewById(R.id.walking_prob_slow);
        walkingFastTextView = findViewById(R.id.walking_prob_fast);
        Switch recordingSwitchtView = findViewById(R.id.record_data);
        Switch predictActivitySwitchtView = findViewById(R.id.enable_voice);

        recordingSwitchtView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(activity, "Recording Data!", Toast.LENGTH_SHORT)
                            .show();
                    startSession();
                }else {
                    Toast.makeText(activity, "Saving Data...", Toast.LENGTH_SHORT)
                            .show();
                    endSession();
                }

            }
        });

        predictActivitySwitchtView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(activity, "Voice Enabled", Toast.LENGTH_SHORT)
                            .show();
                    isVoiceEnabled = true;
                    new Timer().scheduleAtFixedRate(new updateActivity(), 2000, 3000);
                }else {
                    Toast.makeText(activity, "...", Toast.LENGTH_SHORT)
                            .show();
                    isVoiceEnabled = false;
                }

            }
        });

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);
        setSensorManager();
    }

    private void setSensorManager() {

        SensorManager mSensorManger = (SensorManager) getSystemService(SENSOR_SERVICE);
        mFilterSensorData = new FilterSensorData(mSensorManger, activityPrediction, this ,fw);

        preferences = PreferenceManager.getDefaultSharedPreferences(this); // Create SharedPreferences instance
        String filterCoefficient = preferences.getString("filterCoefficient", null); // Read the stored value for filter coefficient
        if (filterCoefficient != null) {
            mFilterSensorData.setFilter_coefficient(Float.parseFloat(filterCoefficient));
            mFilterSensorData.setTempFilter_coefficient(mFilterSensorData.getFilter_coefficient());
        }

        backToSpot = preferences.getBoolean("backToSpot", true); // Back to spot is true by default
        maxAngle = preferences.getInt("maxAngle", 8); // Eight is the default value
        maxTurning = preferences.getInt("maxTurning", 20); // Twenty is the default value

        //Start Predictions

        new Timer().scheduleAtFixedRate(new CalculateProbabilty(), 1000, TIME_CONSTANT);
    }


    @Override
    public void onInit(int status) {
//        startTimerThread();
    }

    public class updateActivity extends TimerTask {

        public void run(){


            if(!isVoiceEnabled){
                this.cancel();
                return;
            }

            if(results == null)
                return;
            float max = -1;
            int idx = -1;
            for (int i = 0; i < results.length; i++) {
                if (results[i] > max) {
                    idx = i;
                    max = results[i];
                }
            }

                textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
//        mFilterSensorData.unregisterListeners();
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

        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("filterCoefficient", Float.toString(mFilterSensorData.getFilter_coefficient()));
        edit.putBoolean("backToSpot", backToSpot);
        edit.putInt("maxAngle", maxAngle);
        edit.putInt("maxTurning", maxTurning);
        edit.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFilterSensorData.unregisterListeners();
        endSession();
    }

    public void startSession() {
        fw = new FileWrite();
        String fileName = FileWrite.getFileName();
        if (!checkPermissions()) return;
        try {

            String filepath = Environment.getExternalStorageDirectory() + "/track/";
            File directory = new File(filepath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File csvFileFused = new File(directory,"fused_" +  fileName);
            FileWriter writerFused = new FileWriter(csvFileFused);
            writerFused.append("time,ax,ay,az,gx,gy,gz,mx,my,mz,fox,foy,foz\n");
            writerFused.flush();

            FileWriter fwArray [] = {null,null,writerFused};
            File fileArray [] = {null,null,csvFileFused};

            fw.setCsvFile(fileArray);
            fw.setWriterArray(fwArray);

            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }


    public void endSession() {
        try {

            if(fw!=null)
                fw.closeFileWriter();

            Log.d(TAG, "Session over. ");
            Toast.makeText(this, "Sending data to phone!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Session ending error. Writer issue? or csvFile missing?");
            fw = null;
        }
    }

    public void setPrediction() {
        if(results == null)
            return;
        walkingFastTextView.setText(Float.toString(round(results[0], 2)));
        walkingSlowTextView.setText(Float.toString(round(results[1], 2)));
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    public boolean checkPermissions() {
        int result;
        final List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : Constants.PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat
                    .requestPermissions(this,
                            listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                            100);
            return false;
        } else {
            return true;
        }
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String permissions[],
                                           @NonNull final int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted! Press Start!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    class CalculateProbabilty extends TimerTask {
        public void run() {
            if(ActivityPrediction.accX.size() < N_SAMPLES)
                return;

                List<Float> data = new ArrayList<>();
                ActivityPrediction.isPredicting = true;
                data.addAll(ActivityPrediction.accX);
                data.addAll(ActivityPrediction.accY);
                data.addAll(ActivityPrediction.accZ);
                data.addAll(ActivityPrediction.gyroX);
                data.addAll(ActivityPrediction.gyroY);
                data.addAll(ActivityPrediction.gyroZ);
                data.addAll(ActivityPrediction.fusedOrientationX);
                data.addAll(ActivityPrediction.fusedOrientationY);
                data.addAll(ActivityPrediction.fusedOrientationZ);
                ActivityPrediction.isPredicting = false;
                if(data.size() == Constants.BATCH_SIZE){
                    results = activityPrediction.classifier.predictProbabilities(toFloatArray(data));
                    Log.d(TAG, "Results : " + results[0] + " , " +  results[1]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setPrediction();
                        }
                    });
                    Log.d(TAG, "Updating UI ");
                }
        }
    }

}

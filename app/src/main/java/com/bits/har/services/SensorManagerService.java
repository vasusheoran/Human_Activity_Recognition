package com.bits.har.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.bits.har.entities.ActivityPrediction;
import com.bits.har.metadata.Constants;
import com.bits.har.main.MainTabActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SensorManagerService extends Service implements SensorEventListener {
    private static final String TAG = "SensorManagerService";

    // angular speeds from gyro
    private float[] gyro = new float[3];
    // accelerometer vector
    private float[] accel = new float[3];
    // linear accelerometer vector
    private float[] linear = new float[3];
    private SensorManager mSensorManager;
    private long lastUpdate;
    private static final int TIME_CONSTANT = 30;


    private float filter_coefficient = 0.90f;
    private final float alpha = 0.9f;
    private ActivityPrediction activityPrediction;
    public UpdateWindow myUpdateWindow;
    public Vibrator v;
//    public static FileWrite fw;

    public SensorManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getApplicationContext(), "Started Sensor Service",  Toast.LENGTH_SHORT).show();
        this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.activityPrediction = MainTabActivity.activityPrediction;

        v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
//        this.fw = new FileWrite();

        initListeners();

        this.myUpdateWindow = new UpdateWindow();
        new Timer().scheduleAtFixedRate(this.myUpdateWindow, 1000, TIME_CONSTANT);

    }




    // This function registers sensor listeners for the accelerometer, magnetometer and gyroscope.
    public void initListeners() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
//        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null)
//            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getApplicationContext()
                .getSystemService(SENSOR_SERVICE);
        return super.onStartCommand(intent, flags, startId);

    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        super.onDestroy();
        myUpdateWindow.cancel();
        unregisterListeners();
        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
// Vibrate for 500 milliseconds
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //ActivityPrediction.activityPrediction();
//        long curTimestamp = System.currentTimeMillis();
        //long stamp = System.currentTimeMillis();
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // Copy new accelerometer data into accel array and calculate orientation
                accel = activityPrediction.normaliseAccelerometerValues(accel);
                System.arraycopy(sensorEvent.values, 0, accel, 0, 3);
//                calculateAccMagOrientation();
                break;

            case Sensor.TYPE_GYROSCOPE:
                // Process gyro data
                System.arraycopy(sensorEvent.values, 0, gyro, 0, 3);
//                gyroFunction(event);
                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:
                // Copy new magnetometer data into magnet array
                linear = activityPrediction.normaliseLinaerValues(linear);
                System.arraycopy(sensorEvent.values, 0, linear, 0, 3);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void unregisterListeners() {
        mSensorManager.unregisterListener(this);
    }

    class UpdateWindow extends TimerTask {
        public void run() {

            //Log.d(TAG,"Before Normalization : " + (System.currentTimeMillis()/1000L) + "," +  accel[0] + "," + accel[1] + "," + accel[2] + "," + gyro[0] + "," + gyro[1]  + "," + gyro[2] + "," + linear[0] + ","  + linear[1] + ","  + linear[2]);
            String result = activityPrediction.normaliseValues(accel, linear, gyro);
            //String result = (System.currentTimeMillis()/1000L) + "," +  accel[0] + "," + accel[1] + "," +
              //      accel[2] + "," + gyro[0] + "," + gyro[1]  + "," + gyro[2] + "," + linear[0] + ","  + linear[1] + ","  + linear[2];
            Log.d(TAG,"After  Normalization : " + result + "\n");
            if(FileWriterService.fw!=null)
                FileWriterService.fw.addValues(result, Constants.FUSEDORIENTATION);
            activityPrediction.updateSensorValues(accel,gyro,linear);
        }
    }
}

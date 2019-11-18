package com.bits.har.entities;

import android.app.Activity;
import android.util.Log;

import com.bits.har.metadata.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ActivityPrediction{

    public static boolean isPredicting;
    private  final String TAG = "ActivityPrediction";
    private final int N_SAMPLES = Constants.N_SAMPLES;
    public  static Queue<Float> accX = new LinkedList<>();
    public  static Queue<Float> accY = new LinkedList<>();
    public  static Queue<Float> accZ = new LinkedList<>();
    public  static Queue<Float> gyroX = new LinkedList<>();
    public  static Queue<Float> gyroY = new LinkedList<>();
    public  static Queue<Float> gyroZ = new LinkedList<>();
    public  static Queue<Float> fusedOrientationX = new LinkedList<>();
    public  static Queue<Float> fusedOrientationY = new LinkedList<>();
    public  static Queue<Float> fusedOrientationZ = new LinkedList<>();

    public static List<Float> reshapedData = new ArrayList<>();
    public  int size=0;


    private String previousResult;
    public TensorFlowClassifier classifier;

    public String normaliseValues(float [] acc, float [] linear, float[] gyro){
        acc[0] = (acc[0] - Constants.MU[0])/Constants.SIGMA[0];
        acc[1] = (acc[1] - Constants.MU[1])/Constants.SIGMA[1];
        acc[2] = (acc[2] - Constants.MU[2])/Constants.SIGMA[2];
        linear[0] = (linear[0] - Constants.MU[3])/Constants.SIGMA[3];
        linear[1] = (linear[1] - Constants.MU[4])/Constants.SIGMA[4];
        linear[2] = (linear[2] - Constants.MU[5])/Constants.SIGMA[5];

        String result =(System.currentTimeMillis()/1000L) + "," +  acc[0] + "," + acc[1] + "," + acc[2] + "," + gyro[0] + "," + gyro[1]  + "," + gyro[2] + "," + linear[0] + ","  + linear[1] + ","  + linear[2];
        return result;
    }

    public float[] normaliseAccelerometerValues(float [] acc){
        acc[0] = (acc[0] - Constants.MU[0])/Constants.SIGMA[0];
        acc[1] = (acc[1] - Constants.MU[1])/Constants.SIGMA[1];
        acc[2] = (acc[2] - Constants.MU[2])/Constants.SIGMA[2];

        return acc;
    }

    public float[] normaliseLinaerValues(float [] linear){
        linear[0] = (linear[0] - Constants.MU[3])/Constants.SIGMA[3];
        linear[1] = (linear[1] - Constants.MU[4])/Constants.SIGMA[4];
        linear[2] = (linear[2] - Constants.MU[5])/Constants.SIGMA[5];
        return linear;
    }


    public  void updateSensorValues(float[] accel, float[] gyro, float[] fusedOrientation){
        if(isPredicting)
            return ;
        if(accX.size() >= N_SAMPLES ){
            accX.remove();
            accY.remove();
            accZ.remove();
        }
        if(gyroX.size() >= N_SAMPLES){
            gyroX.remove();
            gyroY.remove();
            gyroZ.remove();
        }
        if(fusedOrientationX.size() >= N_SAMPLES){

            fusedOrientationX.remove();
            fusedOrientationY.remove();
            fusedOrientationZ.remove();
        }

        accX.add(accel[0]);
        accY.add(accel[1]);
        accZ.add(accel[2]);
        gyroX.add(gyro[0]);
        gyroY.add(gyro[1]);
        gyroZ.add(gyro[2]);
        fusedOrientationX.add(fusedOrientation[0]);
        fusedOrientationY.add(fusedOrientation[1]);
        fusedOrientationZ.add(fusedOrientation[2]);
    }

    public static void clearQueues(){
        accX.clear();
        accY.clear();
        accZ.clear();
        gyroX.clear();
        gyroY.clear();
        gyroZ.clear();
    }

    public static void addQueueToResaphedData(){
        reshapedData.addAll(accX);
        reshapedData.addAll(accY);
        reshapedData.addAll(accZ);
        reshapedData.addAll(gyroX);
        reshapedData.addAll(gyroY);
        reshapedData.addAll(gyroZ);
    }

    public ActivityPrediction() {
//        this.classifier = new TensorFlowClassifier(activity);
    }

/*    private  float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


    private  float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }*/

}

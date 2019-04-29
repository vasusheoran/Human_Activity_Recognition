package com.bits.har.entities;

import android.content.Context;

import com.bits.har.metadata.Constants;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


public class TensorFlowClassifier {
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/frozen_v4_har.pb";
    private static final String INPUT_NODE = "input";
    private static final String[] OUTPUT_NODES = {"y_"};
    private static final String OUTPUT_NODE = "y_";
    private static long[] INPUT_SIZE = {1, 200, 6};
    private static final int OUTPUT_SIZE = 3;

    public TensorFlowClassifier(final Context context) {
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
    }

    private float[][] reshapeResult (float[] result, int l, int w){

        float[][] finalResult = new float[l][w];
        for (int i = 0, a=-1, b=0; i < result.length; i++) {
            if(i%w == 0){
                a++;
                b=0;
            }

            finalResult[a][b] = result[i];
            b++;
        }

        return finalResult;
    }

    public float[][] predictProbabilities(float[] data, int length) {

        float [] result = new float[(length* Constants.N_FEATURES)];
        INPUT_SIZE[0] = length;
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);

        return reshapeResult(result, length, Constants.N_FEATURES);
    }
}

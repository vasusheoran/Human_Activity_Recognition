package com.bits.har;

import android.content.Context;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


public class TensorFlowClassifier {
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/tf_model_1.pb";
//    private static final String MODEL_FILE = "file:///android_asset/frozen_har.pb";
    private static final String INPUT_NODE = "reshape_1_input_5";
    private static final String[] OUTPUT_NODES = {"dense_4_5/BiasAdd"};
    private static final String OUTPUT_NODE = "dense_4_5/BiasAdd";
    private static final long[] INPUT_SIZE = {1, 10, 3};
    private static final int OUTPUT_SIZE = 3;

    public TensorFlowClassifier(final Context context) {
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
    }

    public float[] predictProbabilities(float[] data) {
        float[] result = new float[OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);

        //Downstairs	Jogging	  Sitting	Standing	Upstairs	Walking
        return result;
    }
}

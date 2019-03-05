package com.bits.har;

import android.content.Context;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


class TensorFlowClassifier {
    private static final String TAG = "TensorFlowClassifier";
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/fina_graph.tflite";
    private static final String INPUT_NODE = "Placeholder";
    private static final String[] OUTPUT_NODES = {"dense_3/Softmax"};
    private static final String OUTPUT_NODE = "dense_3/Softmax";
    private static final long[] INPUT_SIZE = {1, 100, 6, 1};
    private static final int OUTPUT_SIZE = 3;

    TensorFlowClassifier(final Context context) {
        Log.v(TAG, MODEL_FILE);
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
    }

    float[] predictProbabilities(float[] data) {
        Log.v(TAG, INPUT_NODE);
        float[] result = new float[OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);

        //Downstairs    Jogging   Sitting   Standing    Upstairs    Walking
        return result;
    }
}

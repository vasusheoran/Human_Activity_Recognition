package com.bits.har;

import android.content.Context;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


class TensorFlowClassifier {
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/cnn/v2/best_model.0.95-1.00-07-0.21.tflite";
    private static final String INPUT_NODE = "conv2d_1_input_1";
    private static final String[] OUTPUT_NODES = {"dense_3_1/Softmax"};
    private static final String OUTPUT_NODE = "dense_3_1/Softmax";
    private static final long[] INPUT_SIZE = {1, 100, 11, 1};
    private static final int OUTPUT_SIZE = 3;

    TensorFlowClassifier(final Context context) {
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
    }

    float[] predictProbabilities(float[] data) {
        float[] result = new float[OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);

        //Downstairs	Jogging	  Sitting	Standing	Upstairs	Walking
        return result;
    }
}

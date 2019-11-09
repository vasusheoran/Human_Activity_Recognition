package com.bits.har.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.bits.har.main.GraphPlotActivity;
import com.bits.har.main.MainTabActivity;
import com.bits.har.metadata.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ClassificationService extends IntentService {
    private static final String TAG = "ClassificationService";

    List<Float> data;
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CLASSIFY = "com.bits.har.services.action.CLASSIFY";
    private static final String FILE_NAME = "com.bits.har.services.extra.FILE_NAME";
    private String[] labels = Constants.LABELS;

    public ClassificationService() {
        super("ClassificationService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionClassify(Context context, String name) {
        Intent intent = new Intent(context, ClassificationService.class);//Creates intent of the same class
        intent.setAction(ACTION_CLASSIFY);
        intent.putExtra(FILE_NAME,name);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {  //handles intent created above
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CLASSIFY.equals(action)) {
//                final List<Float> list = FileWriterService.reshapedData;
//                final List<String> ts = FileWriterService.timestamps;
                final String file_name = intent.getStringExtra(FILE_NAME);
                float[][] result = classify();
                handleActionWrite(result, file_name);

                startGraphView(intent);
                stopSelf();
            }
        }
    }

    private void startGraphView(Intent intent){

        String FILE_PATH = "com.bits.har.main.GraphPlotActivity";
        Intent graphViewIntent = new Intent(this, GraphPlotActivity.class);
        final String file_name = intent.getStringExtra(FILE_NAME);
        graphViewIntent.putExtra(FILE_PATH,Constants.RESULT_PATH + file_name);
        startActivity(graphViewIntent);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private float[][] classify() {
        final List<Float> list = FileWriterService.reshapedData;
        if(list == null)
            throw new UnsupportedOperationException("Not yet implemented");
        else
            this.data = list;
//        list.
        return MainTabActivity.tensorFlowClassifier.predictProbabilities(toFloatArray(list), list.size() / Constants.BATCH_SIZE);

    }

    private void handleActionWrite(float[][] result, String fileName) {
        final List<String> timeStamp = FileWriterService.timestamps;
        if (!MainTabActivity.checkPermissions()) return;
        try {
            String filePath = Constants.RESULT_PATH;
//            MainTabActivity.resultFileName = filePath;

            File directory = new File(filePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File csvFileFused = new File(directory, fileName);
            FileWriter writerFused = new FileWriter(csvFileFused);
            writerFused.append("time,activity,index,confidence\n");

            for(int i=0; i<result.length;i++){


                float max = -1;
                int idx = -1;
                for (int j = 0; j < result[i].length; j++) {
                    if (result[i][j] > max) {
                        idx = j;
                        max = result[i][j];
                    }
                }

                // (idx + 1) is done for better graph views
                String res =  timeStamp.get(i) + "," + labels[idx] + "," + (idx + 1)+  "," + max + "\n";
                writerFused.append(res);
            }
            writerFused.flush();
            writerFused.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
}

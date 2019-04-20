package com.bits.har.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.bits.har.entities.FileWrite;
import com.bits.har.fragments.TabFragment1;
import com.bits.har.main.MainTabActivity;
import com.bits.har.metadata.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ClassificationService extends IntentService {

    List<Float> data;
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CLASSIFY = "com.bits.har.services.action.CLASSIFY";
    private static final String ACTION_BAZ = "com.bits.har.services.action.BAZ";

    private static final String LIST_DATA = "com.bits.har.services.extra.LIST_DATA";
    private static final String LIST_TIMESTAMP = "com.bits.har.services.extra.LIST_TIMESTAMP";
    private static final String FILE_NAME = "com.bits.har.services.extra.FILE_NAME";

    public ClassificationService() {
        super("ClassificationService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionClassify(Context context,String name) {
        Intent intent = new Intent(context, ClassificationService.class);
        intent.setAction(ACTION_CLASSIFY);
        intent.putExtra(FILE_NAME,name);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ClassificationService.class);
        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM_LIST, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CLASSIFY.equals(action)) {
//                final List<Float> list = FileWriterService.reshapedData;
//                final List<String> ts = FileWriterService.timestamps;
                final String file_name = intent.getStringExtra(FILE_NAME);
                float[][] result = handleActionFoo();
                handleActionWrite(result, file_name);
                stopSelf();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private float[][] handleActionFoo() {
        final List<Float> list = FileWriterService.reshapedData;
        if(list == null)
            throw new UnsupportedOperationException("Not yet implemented");
        else
            this.data = list;
//        list.
        return MainTabActivity.tensorFlowClassifier.predictProbabilities(toFloatArray(list), list.size() / Constants.BATCH_SIZE);
//        Toast.makeText(this, result.toString(), Toast.LENGTH_SHORT).show();

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
            writerFused.append("time,Fast,Normal,Slow\n");

            for(int i=0; i<result.length;i++){
                String res =  timeStamp.get(i) + "," + Float.toString(result[i][0]) + "," + Float.toString(result[i][1]) + "," + Float.toString(result[i][2]) + "\n";
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

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1) {
        throw new UnsupportedOperationException("Not yet implemented");
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

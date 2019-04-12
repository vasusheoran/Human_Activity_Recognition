package com.bits.har.services;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.bits.har.entities.ActivityPrediction;
import com.bits.har.entities.FileWrite;
import com.bits.har.fragments.TabFragment1;
import com.bits.har.main.MainTabActivity;
import com.bits.har.metadata.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class FileWriterService extends Service {

    private static final String TAG = "FileWriterService";

    private static List<Float> reshapedData;
    public static List<String> timestamps;

    private ActivityPrediction activityPrediction;
    public static FileWrite fw;
    public static String filePathHome = Constants.DATA_PATH;

    public FileWriterService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        endSession();
        this.fw = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.fw = new FileWrite();
        startSession();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void startSession() {
        fw = new FileWrite();
        String fileName = FileWrite.getFileName();
        if (!MainTabActivity.checkPermissions()) return;
        try {
            String filePath = filePathHome + TabFragment1.activityType ;

            File directory = new File(filePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File csvFileFused = new File(directory,"fused_" +  fileName);
            FileWriter writerFused = new FileWriter(csvFileFused);
            writerFused.append("time,ax,ay,az,gx,gy,gz,la_x,la_y,la_z,fox,foy,foz\n");
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<Path> getFileList() throws IOException {
        List<Path> files;
        Stream<Path> stream = Files.find(Paths.get(filePathHome),
                Integer.MAX_VALUE,(filePath, fileAttr) -> fileAttr.isRegularFile());
        Path[] arrayOfPaths = stream.toArray(Path[]::new);
        files = Arrays.asList(arrayOfPaths);

        return files;
    }

    public static void addQueuesToReshapedData(){
        reshapedData.addAll(ActivityPrediction.accX);
        reshapedData.addAll(ActivityPrediction.accY);
        reshapedData.addAll(ActivityPrediction.accZ);
        reshapedData.addAll(ActivityPrediction.gyroX);
        reshapedData.addAll(ActivityPrediction.gyroY);
        reshapedData.addAll(ActivityPrediction.gyroZ);
    }

    public static void reshape(List<List<String>> list){
        reshapedData = new ArrayList<>();
        timestamps = new ArrayList<>();
        boolean flag;
        for ( List<String> batch : list) {

            if(batch.size() < Constants.N_SAMPLES){
                break;
            }

            flag =true;
            int index = 0;
            for ( String row : batch) {
                String[] values  = row.split(",");

                ActivityPrediction.accX.add(Float.parseFloat(values[1]));
                ActivityPrediction.accY.add(Float.parseFloat(values[2]));
                ActivityPrediction.accZ.add(Float.parseFloat(values[3]));
                ActivityPrediction.gyroX.add(Float.parseFloat(values[4]));
                ActivityPrediction.gyroY.add(Float.parseFloat(values[5]));
                ActivityPrediction.gyroZ.add(Float.parseFloat(values[6]));


                if(flag){
                    timestamps.add(values[0]);
                    flag = false;
                }
            }

            addQueuesToReshapedData();
            ActivityPrediction.clearQueues();

        }
    }

    public static List<Float> getFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
        List<List<String>> list = FileWrite.readBatch(reader, Constants.N_SAMPLES);
        reshape(list);
        return reshapedData;
    }

}

package com.bits.har.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.bits.har.entities.ActivityPrediction;
import com.bits.har.entities.FileWrite;
import com.bits.har.main.MainTabActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterService extends Service {

    private static final String TAG = "FileWriterService";

    private ActivityPrediction activityPrediction;
    public static FileWrite fw;

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

            String filepath = Environment.getExternalStorageDirectory() + "/track/" + MainTabActivity.activityType ;
            File directory = new File(filepath);
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

}

package com.bits.har.entities;

import android.util.Log;

import com.bits.har.metadata.Constants;
import com.bits.har.main.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FileWrite extends BaseActivity {

    private static final String TAG = "FilterSensorData";
    private File file;

    public FileWriter[] getWriterArray() {
        return writerArray;
    }

    public void setWriterArray(FileWriter[] writerArray) {
        this.writerArray = writerArray;
    }

    public File[] getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(File[] csvFile) {
        this.csvFile = csvFile;
    }

    public static void setFileName(String[] fileName) {
        FileWrite.fileName = fileName;
    }

    private static FileWriter writerArray[] = new FileWriter[3];
    private static File csvFile []= new File[3];
    private static String fileName[] = new String[3];

    public void setFile(File file) {
        this.file = file;
    }

    public static String getFileName() {
        final Calendar date = Calendar.getInstance();
        final StringBuilder sb = new StringBuilder();


        final int year = date.get(Calendar.YEAR);
        final int month = 1 + date.get(Calendar.MONTH);
        final int day = date.get(Calendar.DAY_OF_MONTH);
        final int hour = date.get(Calendar.HOUR_OF_DAY);
        final int minute = date.get(Calendar.MINUTE);
        final int second = date.get(Calendar.SECOND);

        sb.append(year);
        sb.append('_');
        if (month < 10) sb.append(0);
        sb.append(month);
        sb.append('_');
        if (day < 10) sb.append(0);
        sb.append(day);
        sb.append('_');
        if (hour < 10) sb.append(0);
        sb.append(hour);
        sb.append('_');
        if (minute < 10) sb.append(0);
        sb.append(minute);
        sb.append('_');
        if (second < 10) sb.append(0);
        sb.append(second);
        sb.append(".csv");

        return sb.toString();
    }


    public static File getFile(String path) {
        File file = new File(path);
        return file;
    }

    public static List<List<String>> readBatch(BufferedReader reader, int batchSize) throws IOException {
        List<List<String>> list = new ArrayList<>();
        boolean flag = true;
        boolean isValid = false;
        reader.readLine();      //Dummy line for labels
        while(flag){
            List<String> result = new ArrayList<>();

            for (int i = 0; i < batchSize; i++) {
                String line = reader.readLine();
                if (line != null ) {
                    result.add(line);
                } else{
                    flag = false;
                    if(!isValid)
                        list=null;
                    break;
                }
            }

            list.add(result);
            isValid=true;

        }
        return list;
    }

    public void addValues(String data,int value) {
        try {
            switch (value){
                case Constants.ACCELEROMETER:
                    if(this.writerArray !=null && this.writerArray[0] !=null){
                        this.writerArray[0].append(data + "\n");
                        this.writerArray[0].flush();
                    }
                    break;

                case Constants.GYROSCOPE:
                    if(this.writerArray !=null && this.writerArray[1] !=null){
                        this.writerArray[1].append(data + "\n");
                        this.writerArray[1].flush();
                    }
                    break;

                case Constants.FUSEDORIENTATION:
                    if(this.writerArray !=null && this.writerArray[2] !=null){
                        this.writerArray[2].append(data + "\n");
                        this.writerArray[2].flush();
                    }
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Writing value error. Writer issue? or csvFile missing?");
        }
    }


    public void closeFileWriter() throws IOException {

        if(this.writerArray[0]!=null)
            this.writerArray[0].close();
        if(this.writerArray[1]!=null)
            this.writerArray[1].close();
        if(this.writerArray[2]!=null)
            this.writerArray[2].close();

        this.writerArray = null;
        this.csvFile = null;
        MainActivity.fw = null;
    }
}

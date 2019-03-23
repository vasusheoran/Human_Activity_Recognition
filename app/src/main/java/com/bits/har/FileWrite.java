package com.bits.har;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;

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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
/*
    public static void writeToTextFile(String data, Context context) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/myData");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();
            final String filename = folder.toString() + "/" + "Test.csv";
            File file = new File(filename);

            Log.d(TAG, "" + var);
//            FileWriter fw = new FileWriter(filename);
            PrintWriter p = new PrintWriter(new FileOutputStream(file, true));
            p.println("Hello");

            *//*OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();*//*
        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }*/

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
/*
    public void writeData(String data,String strFilePath)
    {

        PrintWriter csvWriter;
        try
        {

            File file = new File(strFilePath);
            if(!file.exists()){
                file = new File(strFilePath);
            }
            csvWriter = new  PrintWriter(new FileWriter(file,true));


            csvWriter.print(data+","+"hello");
            csvWriter.append('\n');
            csvWriter.print("world");


            csvWriter.close();


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

/*
    public static String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read FOLDER: " + e.toString());
        }

        return ret;
    }*/
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

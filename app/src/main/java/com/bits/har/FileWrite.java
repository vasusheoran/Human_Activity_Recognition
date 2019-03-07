package com.bits.har;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FileWrite {

    private static final String TAG = "FilterSensorData";
    public static File file = null;

    public static void writeToTextFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }
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
    }
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
    }
}

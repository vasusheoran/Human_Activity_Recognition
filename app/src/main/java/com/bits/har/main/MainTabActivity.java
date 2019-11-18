package com.bits.har.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bits.har.entities.TensorFlowClassifier;
import com.bits.har.fragments.ItemFragment;
import com.bits.har.metadata.Constants;
import com.bits.har.R;
import com.bits.har.entities.ActivityPrediction;
import com.bits.har.fragments.TabFragmentDataCollection;
import com.bits.har.services.ClassificationService;
import com.bits.har.services.FileWriterService;
import com.bits.har.services.SensorManagerService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainTabActivity extends AppCompatActivity
        implements TabFragmentDataCollection.OnFragmentInteractionListener, ItemFragment.OnListFragmentInteractionListener {
    private static final String TAG = "MainTabActivity";

    public  static Activity activity;
    public static ActivityPrediction activityPrediction;
    public static TensorFlowClassifier tensorFlowClassifier;
    //    private static final String[] labels = {"Fast", "Normal", "Slow"};//never used
    public static Fragment itemFragment;


    public static float[][] results;

    private static TextToSpeech textToSpeech;
    public static boolean isVoiceEnabled;

    public static Intent serviceManagerIntent;


    private static final String[] labels = {"JOGGING", "RUNNING", "DOWN", "UP", "STANDING","WALKING"};

    //UI for TabFragmentDataCollection

//    public TextView walkingSlowTextView;//never used anywhere
//    public TextView walkingFastTextView;//same
//    public TextView walkingNormalTextView;//same


//    Sequence {"Jogging", "Walking", "Upstairs","Running","Downstairs","Standing"};

    public TextView Jogging_tv;
    public TextView Walking_tv;
    public TextView Upstairs_tv;
    public TextView Running_tv;
    public TextView Downstairs_tv;
    public TextView Standing_tv;


//    private TextToSpeech textToSpeech;
//    public static boolean isVoiceEnabled;

    //Activity Prediction

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;


    Switch enable_voice_switch;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        this.activityPrediction = new ActivityPrediction();//entities
        this.tensorFlowClassifier = new TensorFlowClassifier(activity);//entities

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);
        textToSpeech = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            textToSpeech.setLanguage(Locale.UK);
                        }
                    }
                });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        fragmentManager = getSupportFragmentManager();

        results = new float[1][6];

    }


    private void setSensorManager() {


        if (serviceManagerIntent == null) {
            serviceManagerIntent = new Intent(this, SensorManagerService.class);
            Log.v(TAG, "Created Sensor Manager Service. ");

        }

        this.startService(serviceManagerIntent);
        Log.v(TAG, "Started Sensor");

        //new Timer().scheduleAtFixedRate(new CalculateProbabilty(), 1000, 1000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabFragment1Interaction(int position) {

    }

    public void sendMessage(String path) {

        String FILE_PATH = "com.bits.har.main.GraphPlotActivity";
        Intent intent = new Intent(this, GraphPlotActivity.class);
        intent.putExtra(FILE_PATH, path);
//        editText.setText("asdfasf");
        startActivity(intent);
    }


    public void setPrediction() {
        if (results == null)
            return;
        Jogging_tv.setText(Float.toString(round(results[0][0], 2)));
        Walking_tv.setText(Float.toString(round(results[0][1], 2)));
        Upstairs_tv.setText(Float.toString(round(results[0][2], 2)));
        Downstairs_tv.setText(Float.toString(round(results[0][4], 2)));
        Running_tv.setText(Float.toString(round(results[0][3], 2)));
        Standing_tv.setText(Float.toString(round(results[0][5], 2)));


    }

    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


    static private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onListFragmentInteraction(Path item) {

        Toast.makeText(this, item.getFileName().toString() + " selected", Toast.LENGTH_SHORT).show();//when user selects a ..csv file from the list of csvs

        try {
            //setSensorManager();
            //this.stopService(serviceManagerIntent);
            //List<Float> l1 = FileWriterService.getReshapedData(item.toAbsolutePath().toString());
            //ClassificationService.startActionClassify(this, item.getFileName().toString());


//            List<Float> list = FileWriterService.getReshapedData( item.toAbsolutePath().toString());
//
//            if(list == null || list.size() == 0){
//                Toast.makeText(this, "Please record data for at least 10 secs ... ", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            ClassificationService.startActionClassify(this, item.getFileName().toString());
            File f = new File(Constants.RESULT_PATH + item.getFileName().toString());
            if (f.exists() && !f.isDirectory()) {
                // do something
                sendMessage(Constants.RESULT_PATH + item.getFileName().toString());
            } else {

                List<Float> list = FileWriterService.getReshapedData(item.toAbsolutePath().toString());

                if (list == null || list.size() == 0) {
                    Toast.makeText(this, "Please record data for at least 10 secs ... ", Toast.LENGTH_SHORT).show();
                    return;
                }
                ClassificationService.startActionClassify(this, item.getFileName().toString());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new TabFragmentDataCollection();
                    break;
                case 1:
//                    fragment = new Graph();
//                    MainTabActivity.itemFragment = fragment;
                    ItemFragment itemFragment = (ItemFragment) ItemFragment.newInstance();

                    if (ItemFragment.isViewUpdated)
                        itemFragment.updateView();

                    fragment = itemFragment;
                    MainTabActivity.itemFragment = fragment;

                    /*fragment = Graph.newInstance();
                     */
                    break;
            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }


    public static boolean checkPermissions() {
        int result;
        final List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : Constants.PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(activity, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat
                    .requestPermissions(activity,
                            listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                            100);
            return false;
        } else {
            return true;
        }
    }

    static boolean isSensorManagerServiceRunning = false;

    public static class CalculateProbabilty extends TimerTask {
        public void run() {

            if (!isVoiceEnabled) {
                this.cancel();
                return;
            }
            if (ActivityPrediction.accX.size() < Constants.N_SAMPLES)
                return;
            List<Float> data = new ArrayList<>();
            ActivityPrediction.isPredicting = true;
            data.addAll(ActivityPrediction.accX);
            data.addAll(ActivityPrediction.accY);
            data.addAll(ActivityPrediction.accZ);
//                data.addAll(ActivityPrediction.gyroX);
//                data.addAll(ActivityPrediction.gyroY);
//                data.addAll(ActivityPrediction.gyroZ);
            data.addAll(ActivityPrediction.gyroX);
            data.addAll(ActivityPrediction.gyroY);
            data.addAll(ActivityPrediction.gyroZ);
            ActivityPrediction.isPredicting = false;
            results = tensorFlowClassifier.predictProbabilities(toFloatArray(data), data.size() / Constants.BATCH_SIZE);
            //results = activityPrediction.classifier.predictProbabilities(toFloatArray(data));

            Log.d(TAG, "Results : "+ Arrays.toString(results[0]));
            //TODO : Get index of max label.
        }




    }



    static public class updateActivity extends TimerTask {

        public void run() {


            if (!isVoiceEnabled) {
                this.cancel();
                return;
            }

            if (results == null)
                return;
            float max = -1;
            int idx = -1;
            for (int i = 0; i < results[0].length; i++) {
                if (results[0][i] > max) {
                    idx = i;
                    max = results[0][i];
                }
            }
            Log.d(TAG, "Index : " + idx  + " , Label : "  + labels[idx]);
            textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));

        }



    }
}

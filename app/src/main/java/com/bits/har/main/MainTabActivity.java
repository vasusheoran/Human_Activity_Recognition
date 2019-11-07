package com.bits.har.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import android.view.Menu;
import android.view.MenuItem;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainTabActivity extends AppCompatActivity
        implements TabFragmentDataCollection.OnFragmentInteractionListener, ItemFragment.OnListFragmentInteractionListener {
    private static final String TAG = "MainTabActivity";

    private static Activity activity;
    public static ActivityPrediction activityPrediction;
    public static TensorFlowClassifier tensorFlowClassifier;
    private static final String[] labels = {"Fast", "Normal", "Slow"};
    public static Fragment itemFragment;

    //UI for TabFragmentDataCollection

    public TextView walkingSlowTextView;
    public TextView walkingFastTextView;
    public TextView walkingNormalTextView;
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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        this.activityPrediction = new ActivityPrediction();
        this.tensorFlowClassifier = new TensorFlowClassifier(activity);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onListFragmentInteraction(Path item) {
        Toast.makeText(activity, item.getFileName().toString() + " selected", Toast.LENGTH_SHORT).show();

        try {


//            List<Float> list = FileWriterService.getReshapedData( item.toAbsolutePath().toString());
//
//            if(list == null || list.size() == 0){
//                Toast.makeText(this, "Please record data for at least 10 secs ... ", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            ClassificationService.startActionClassify(this, item.getFileName().toString());
            File f = new File(Constants.RESULT_PATH + item.getFileName().toString());
            if(f.exists() && !f.isDirectory()) {
                // do something
                sendMessage(Constants.RESULT_PATH + item.getFileName().toString());
            }else{

                List<Float> list = FileWriterService.getReshapedData( item.toAbsolutePath().toString());

                if(list == null || list.size() == 0){
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

            switch(position) {
                case 0:
                    fragment = new TabFragmentDataCollection();
                    break;
                case 1:
//                    fragment = new Graph();
//                    MainTabActivity.itemFragment = fragment;
                    ItemFragment itemFragment = (ItemFragment) ItemFragment.newInstance();

                    if(ItemFragment.isViewUpdated)
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
}

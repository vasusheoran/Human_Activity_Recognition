package com.bits.har.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bits.har.R;
import com.bits.har.entities.ResultViewDummy;
import com.bits.har.metadata.Constants;
import com.bits.har.services.FileWriterService;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.TimeZone;

public class GraphPlotActivity extends AppCompatActivity {
    private static final String TAG = "GraphPlotActivity";
    private GraphView graph;
    List<Date> date;
    List<String> dateList;
    List<Integer> label=  new ArrayList<>();
    List<String> durationLabel=  new ArrayList<>();
    List<Integer> time=  new ArrayList<>();
    List<DataPoint> dp = new ArrayList<>();
    public  Activity graphPlotActivity=null;
    long slow, normal, fast;

    private static final String FILE_PATH = "com.bits.har.main.GraphPlotActivity";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        graphPlotActivity = this;


        setContentView(R.layout.activity_graph_plot);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        String path = i.getStringExtra(FILE_PATH);


        plotGraph(path);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readCVS(String path){
        List<ResultViewDummy> resultViewDummyList = new ArrayList<>();

        String[] content = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileWriterService.getFile(path))));
            String line = "";
            br.readLine();

            for(int i=0; (line = br.readLine()) != null ; i++){

                content = line.split(",");
                dp.add(new DataPoint(i, Integer.parseInt(content[2])));

                if(content[1].equals("Slow")){
                    this.slow += 5;
                }else if(content[1].equals("Normal")){
                    this.normal += 5;
                }else if(content[1].equals("Fast")){
                    this.fast += 5;
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String splitToComponentTimes(long longVal)
    {
        int hours = (int) longVal / 3600;
        int remainder = (int) longVal - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
        String result= null;
        if(hours == 0){
                if(mins == 0)
                    result = secs + " s";
                else
                    result = mins + "." + secs + " m";
        }else{
            if(mins == 0){
                result = hours + " h";
            }else
                result = hours + "." + mins + " h";
        }

        return result;
    }

    public DataPoint[] getDataPoints(){
        DataPoint[] dataPoints = new DataPoint[label.size() +1];
        int i;
        for ( i = 0; i < label.size(); i++){
            dataPoints[i] = new DataPoint(time.get(i) , Integer.parseInt(String.valueOf(label.get(i))));
        }

//        if(dataPoints.length <2)
        dataPoints[i] = new DataPoint(time.get(i-1)+1,0);
        return dataPoints;
    }

    public DataPoint[] getDataPoints_dp(){
        DataPoint[] dataPoints = dp.toArray(new DataPoint[dp.size()]);
        return dataPoints;
    }

    public void defaultGraph(){
        TextView walkingFast = findViewById(R.id.graph_view_walking_prob_fast);
        TextView walkingNormal = findViewById(R.id.graph_view_walking_prob_normal);
        TextView walkingSlow = findViewById(R.id.graph_view_walking_prob_slow);
        walkingSlow.setText(splitToComponentTimes(this.slow));
        walkingNormal.setText(splitToComponentTimes(this.normal));
        walkingFast.setText(splitToComponentTimes(this.fast));

        graph = findViewById(R.id.graph);
//        graph.getGridLabelRenderer().setVerticalAxisTitle("Labels");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time (s)");

//        DataPoint[] points = getDataPoints();
        DataPoint[] points = getDataPoints_dp();

        Log.d(TAG, Arrays.toString(points));


//        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

//        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
//        staticLabelsFormatter.setVerticalLabels(new String[] {"Slow", "Normal", "Fast"});

//        String[] strListString = dateList.toString().replaceAll("\\[|\\]", "").split(",");


        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
//
//        String[] horizontalLabels = durationLabel.toArray(new String[durationLabel.size()]);
//        staticLabelsFormatter.setHorizontalLabels(horizontalLabels);
        staticLabelsFormatter.setVerticalLabels(new String[] {"", "S", "N", "F"});
//        Log.v(TAG, Arrays.toString(horizontalLabels));


        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
//        graph.getGridLabelRenderer().setHorizontalLabelsAngle(20);
//        graph.getGridLabelRenderer().setNumHorizontalLabels(5);
        // set manual X bounds
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(3);
        graph.getViewport().setYAxisBoundsManual(true);


//        graph.getViewport().setMinX(-5);
        graph.getViewport().setMaxX(dp.size() + 5);
        graph.getViewport().setXAxisBoundsManual(true);

//         enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
//        series.setValueDependentColor(data -> Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100));

//        series.setSpacing(5);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Double d  = dataPoint.getX();
                int length = d.intValue() * Constants.YIELD;
                Log.d(TAG, length + "");
                if(length<60)
                    Toast.makeText(graphPlotActivity, "Time: "+ (length) + " secs" , Toast.LENGTH_SHORT).show();
                else if(length < 3600)
                    Toast.makeText(graphPlotActivity, "Time: "+ (length / 60) + " mins and " + (length % 60) + " secs", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(graphPlotActivity, "Time: "+ (length / 3600) + " hrs and " + (length % 3600) + " mins" , Toast.LENGTH_SHORT).show();


            }
        });
        graph.addSeries(series);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void plotGraph(String path){
        readCVS(path);
//        createBarChartGraph();
        defaultGraph();
    }

}

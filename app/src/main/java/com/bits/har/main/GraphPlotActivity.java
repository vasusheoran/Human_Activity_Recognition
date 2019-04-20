package com.bits.har.main;

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
import android.view.View;

import com.bits.har.R;
import com.bits.har.metadata.Constants;
import com.bits.har.services.FileWriterService;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class GraphPlotActivity extends AppCompatActivity {
    private GraphView graph;
    List<Float> results;
    List<Date> date;
    List<String> dateList;
    List<Integer> label;
    private static final String[] labels = {"Fast", "Normal", "Slow"};
    private static final String FILE_PATH = "com.bits.har.main.GraphPlotActivity";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_plot);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        String path = i.getStringExtra(FILE_PATH);


        plotGraph(path);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readCVS(String path){
        label = new ArrayList<>();
        date = new ArrayList<>();
        dateList = new ArrayList<>();
        String[] content = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FileWriterService.getFile(path))));
            String line = "";
            String tempLabels = br.readLine();

            int j=0;

            while((line = br.readLine()) != null){
                results = new ArrayList<>();
                content = line.split(",");
                int index = 0;
                for (String item: content) {
                    if(item== null)
                        continue;

                    if(index==0){
                        Timestamp stamp = new Timestamp(Long.parseLong(item));
                        Date d = new Date(stamp.getTime());
 /*                       String dateString = String.valueOf(d.getHours()) + ":" + String.valueOf(d.getMinutes());
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss a", Locale.US);
                        String str = format.format(d);
                        dateList.add(dateString);*/
                        date.add(d);
                    }
                    else
                        results.add(Float.parseFloat(item));

                    index++;
                }
                float max = -1;
                int idx = -1;
                for (int i = 0; i < results.size(); i++) {
                    if (results.get(i)> max) {
                        idx = i;
                        max = results.get(i);
                    }
                }

                label.add(Constants.N_FEATURES - idx );

            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createBarChartGraph(){
       /* DataPoint[] dataPoints = new DataPoint[label.size()];
        for (int i = 0; i < label.size(); i++){
            dataPoints[i] = new DataPoint(Integer.parseInt(String.valueOf(date.get(i))), label.get(i));
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(dataPoints);
        mGraph.addSeries(series);
       *//* series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });*//*
        series.setSpacing(50);*/
        DataPoint[] dataPoints = new DataPoint[label.size()];
        for (int i = 0; i < label.size(); i++){
            dataPoints[i] = new DataPoint(date.get(i), label.get(i));
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);

        graph.addSeries(series);

// set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps
//        graph.getViewport().setXAxisBoundsManual(true);
//        graph.getViewport().setMinX(date.get(0).getTime());
//        graph.getViewport().setMaxX(date.get(date.size()-1).getTime());


        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(3);

        graph.getViewport().setScrollable(true); // enables horizontal scrolling
//        graph.getViewport().setScrollableY(true); // enables vertical scrolling

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(50);

// draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    public DataPoint[] getDataPoints(){
        DataPoint[] dataPoints = new DataPoint[label.size()];
        for (int i = 0; i < label.size(); i++){
            dataPoints[i] = new DataPoint(date.get(i), Integer.parseInt(String.valueOf(label.get(i))));
        }
        return dataPoints;
    }

    public void defaultGraph(){


        graph = findViewById(R.id.graph);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Labels");
//        graph.getGridLabelRenderer().setHorizontalAxisTitle("Labels");

        DataPoint[] points = getDataPoints();

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);

//        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
//        staticLabelsFormatter.setVerticalLabels(new String[] {"Slow", "Normal", "Fast"});

//        String[] strListString = dateList.toString().replaceAll("\\[|\\]", "").split(",");

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss a", Locale.US);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this,format));
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(20);
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        // set manual X bounds

        graph.getViewport().setMinX(date.get(0).getTime());
        graph.getViewport().setMaxX(date.get(date.size()-1).getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(3);
        graph.getViewport().setYAxisBoundsManual(true);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        series.setValueDependentColor(data -> Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100));

        series.setSpacing(10);

        graph.addSeries(series);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void plotGraph(String path){
        readCVS(path);
//        createBarChartGraph();
        defaultGraph();
    }

}

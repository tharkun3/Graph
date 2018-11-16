package com.supprt.jarek.graph;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity {
    Graph graph;
    int viewLenght;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         graph = findViewById(R.id.main_graph);

         //here we can use both graphDataSet or list of GraphItem

        //GraphDataSet  data = new GraphDataSet();
         //data.add(10,"12");
        // graph.setData(data);

        final List<GraphItem> graphData = new ArrayList<>();
        graphData.add(new GraphItem(10,"2000-10-01", Color.BLUE));
        graphData.add(new GraphItem(12,"2000-10-02", Color.YELLOW));
        graphData.add(new GraphItem(14,"2000-10-03"));
        graphData.add(new GraphItem(11,"2000-10-04"));
        graphData.add(new GraphItem(17,"2000-10-05"));
        graphData.add(new GraphItem(16.123,"2000-10-06"));
        graphData.add(new GraphItem(18,"2000-10-07"));
        graphData.add(new GraphItem(10,"2000-10-08"));
        graphData.add(new GraphItem(12,"2000-10-09"));
        graphData.add(new GraphItem(14,"2000-11-01"));
        graphData.add(new GraphItem(11,"2000-11-02"));
        graphData.add(new GraphItem(17,"2000-11-03"));
        graphData.add(new GraphItem(16,"2000-11-04"));
        graphData.add(new GraphItem(18,"2000-11-05"));
        graphData.add(new GraphItem(10,"2000-11-06"));
        graphData.add(new GraphItem(31,"2000-11-07"));
        graphData.add(new GraphItem(14,"2000-11-08"));
        graphData.add(new GraphItem(11,"2000-11-09"));
        graphData.add(new GraphItem(17,"2000-12-01"));
        graphData.add(new GraphItem(16,"2000-12-02"));
        graphData.add(new GraphItem(18,"2000-12-03"));
        graphData.add(new GraphItem(10,"2000-12-04"));
        graphData.add(new GraphItem(20,"2000-12-05"));
        graphData.add(new GraphItem(14,"2000-12-06"));
        graphData.add(new GraphItem(11,"2001-01-01"));
        graphData.add(new GraphItem(4,"2001-01-02"));
        graphData.add(new GraphItem(16,"2001-01-03"));
        graphData.add(new GraphItem(18,"2001-01-04"));
        graphData.add(new GraphItem(29.6,"2001-10-01"));
        graphData.add(new GraphItem(30,"2001-10-02"));
        graphData.add(new GraphItem(30.5,"2001-10-03"));
        graphData.add(new GraphItem(11,"2002-10-04"));
        graphData.add(new GraphItem(17,"2002-10-05"));
        graphData.add(new GraphItem(16,"2002-10-06"));
        graphData.add(new GraphItem(18,"2002-10-07"));
        graphData.add(new GraphItem(10,"2002-10-08"));
        graphData.add(new GraphItem(12,"2002-10-09"));
        graphData.add(new GraphItem(14,"2002-11-01"));
        graphData.add(new GraphItem(11,"2002-11-02"));
        graphData.add(new GraphItem(33.7,"2002-11-03"));
        graphData.add(new GraphItem(33,"2002-11-04"));
        graphData.add(new GraphItem(34.5,"2002-11-05"));
        graphData.add(new GraphItem(34,"2002-11-06"));
        graphData.add(new GraphItem(35,"2002-11-07"));
        graphData.add(new GraphItem(14,"2002-11-08"));
        graphData.add(new GraphItem(9,"2002-11-09"));
        graphData.add(new GraphItem(5,"2002-12-01"));
        graphData.add(new GraphItem(8,"2002-12-02"));
        graphData.add(new GraphItem(9,"2002-12-03"));
        graphData.add(new GraphItem(6,"2002-12-04"));
        graphData.add(new GraphItem(20,"2002-12-05"));
        graphData.add(new GraphItem(5,"2002-12-06"));
        graphData.add(new GraphItem(11,"2002-12-07"));
        graphData.add(new GraphItem(4,"2002-12-08"));
        graphData.add(new GraphItem(3,"2002-12-09"));
        graphData.add(new GraphItem(18,"2002-12-10"));

        viewLenght=graphData.size();

        graph.setData(graphData);
        graph.setYDataDescrCount(5);
        graph.setXDataDescrCount(6);

        graph.setDescription0Y("0Y Description");
        graph.setDescription0X("0X Description");

        graph.setValueFormat("%.2f");
        graph.setValueFormatOnData("%.6f");

        //String[] miesiace={"Sty.","Lut.","Mar.","Kwi.","Maj","Cze.","Lip.","Sie.","Wrz.","Paź.","Lis.","Gru."};
       // graph.setMonthsNames(miesiace);

        graph.setMarkColor(Color.GREEN);
        graph.setTransverseLines(true);
        graph.setRegression(true);
        graph.setFixedMaxX(GraphItem.getMaxValue(GraphItem.toDouble(graphData)));
        graph.setFixedMinX(GraphItem.getMinValue(GraphItem.toDouble(graphData)));
        graph.setSmartDate(true);
        //graph.setViewData(2,10);
        //graph.setViewDataLast(2);
        graph.setGraphType(GraphType.GRAPH);
        graph.setGraphColor(Color.RED);
        graph.setValuesColor(Color.BLACK);
        graph.setMarkedDataBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
        graph.setDescriptionTextSize(16);
        graph.setValuesTextSize(16);

        graph.setErrorMessage("Error");
        graph.setDataPolicyShow(ShowMarkedDataPolicy.SHOW_MARKED);
        graph.show();

        graph.setOnGraphTouchedListener(new Graph.GraphTouched() {
            @Override
            public void onGraphTouched(double value, int id, String description) {
                Toast.makeText(ActivityMain.this,description+" "+String.valueOf(value)+" "+String.valueOf(id),Toast.LENGTH_SHORT).show();
            }
        });


        Button left = findViewById(R.id.left_button);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(graph.getDataViewStart()>0){
                    graph.setViewData(graph.getDataViewStart()-1,graph.getDataViewEnd()-1);
                    graph.show();
                }
            }
        });
        Button right = findViewById(R.id.right_button);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(graph.getDataViewEnd()<graphData.size()-1){
                    graph.setViewData(graph.getDataViewStart()+1,graph.getDataViewEnd()+1);
                    graph.show();
                }
            }
        });
        Button zoomIn = findViewById(R.id.zoom_in_button);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    viewLenght = graph.getDataViewLenght();
                if(viewLenght>0){
                    viewLenght-=4;
                    graph.setViewDataLenght(viewLenght);
                    graph.show();
                }
            }
        });
        Button zoomOut = findViewById(R.id.zoom_out_button);
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //graph.setViewData();
                viewLenght = graph.getDataViewLenght();
                if(viewLenght<graphData.size()){
                    viewLenght+=2;
                    graph.setViewDataLenght(viewLenght);
                    graph.show();
                }
            }
        });
        Button graphButton = findViewById(R.id.graph_button);
        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graph.setGraphType(GraphType.GRAPH);
                    graph.show();

            }
        });
        Button barButton = findViewById(R.id.bar_button);
        barButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graph.setGraphType(GraphType.BAR_CHART);
                graph.show();
            }
        });
    }
}

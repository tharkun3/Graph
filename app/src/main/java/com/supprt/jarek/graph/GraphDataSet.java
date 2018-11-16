package com.supprt.jarek.graph;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class GraphDataSet {
    private List<GraphItem> graphData;

    public GraphDataSet() {
        graphData = new ArrayList<>();
    }
    public GraphDataSet(List<GraphItem> graphData) {
        this.graphData = graphData;
    }

    public void add(double value,String description){
        graphData.add(new GraphItem(value,description));
    }

    public void add(double value,String description,int color){
        graphData.add(new GraphItem(value,description,color));
    }

    public List<GraphItem> getGraphDataList(){
        return graphData;
    }
}

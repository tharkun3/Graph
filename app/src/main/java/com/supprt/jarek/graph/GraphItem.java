package com.supprt.jarek.graph;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class GraphItem {
    private double value;
    private String description;
    private int orginalId;
    private int color;

    public GraphItem(double value, String description) {
        this.value = value;
        this.description = description;
    }
    public GraphItem(double value, String description,int color) {
        this.value = value;
        this.description = description;
        this.color=color;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setOrginalId(int orginalId) {
        this.orginalId = orginalId;
    }

    public int getOrginalId() {
        return orginalId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public static double getMaxValueFromData(List<GraphItem> graphItems)
    {if(graphItems!=null){
        List<Double> values = toDouble(graphItems);
      return  getMaxValue(values);
    }
        return 0;
    }
    public static double getMinValueFromData(List<GraphItem> graphItems)
    {if(graphItems!=null){
        List<Double> values = toDouble(graphItems);
        return  getMinValue(values);
    }
        return 0;
    }

    public static double getMaxValue(List<Double> values) {
        if (values != null) {

            double maxData = 0.0;
            if (values.size() > 0) {
                maxData = values.get(values.size() - 1);
            }
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) > maxData) {
                    maxData = values.get(i);
                }
            }
            return maxData;
        }
        return 0;
    }

    public static double getMinValue(List<Double> values) {
        if (values != null) {

            double minData = 0.0;
            if (values.size() > 0) {
                minData = values.get(values.size() - 1);
            }
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) < minData) {
                    minData = values.get(i);
                }
            }
        return minData;
        }
        return 0;
    }

        public static List<Double> toDouble(List<GraphItem> data){
       List<Double> values = new ArrayList<>();
        for(GraphItem item:data){
            values.add(item.getValue());
        }
        return values;
    }
}

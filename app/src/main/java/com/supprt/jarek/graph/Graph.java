package com.supprt.jarek.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Graph extends View implements GestureDetector.OnGestureListener
{

    private int defaultColor[]={Color.YELLOW,Color.RED,Color.GREEN,Color.BLUE,Color.DKGRAY,Color.LTGRAY,Color.MAGENTA,Color.GRAY};

    private GestureDetectorCompat mDetector;

    public static final int DATA_LENGHT_MAX=-5000;

    private List<GraphItem> graphData;
    private List<GraphItem> viewData;

    private DisplayMetrics metrics;

    private Paint axis, mainGraph,reg1, dexcrY, descrX, transverse,errorMessagePaint,mark,markedDataBackground,markedDataBorder;
    private float descrXheight, descrYheight,barWidth, barChartDistanceRatio;

    private float LEFT_PADDING = 0.12f;
    private float RIGHT_PADDING = 0.05f;
    private float TOP_PADDING = 0.05f;
    private float BOTTOM_PADDING = 0.1f;

    private final float textSize = 12;
    private final float strokeWidth = 2;

    private double fixedMinX,fixedMaxX;

    private TranslateXY translate;

    private int transverseCount,descrCount;

    private int markedId;
    private String markedDate;

    private String errorMessage;

    private Regression regression;
    private SmartDate smartDate;

    GraphTouched listener;

    String description0X,description0Y;
    String valueFormat,valueFormatOnData;

    private boolean isRegression;
    private boolean isTransverseLines;
    private boolean isSmartDate;
    private boolean isSmallMark;

    private GraphType graphType;
    private ShowMarkedDataPolicy showPolicy;

    private String[] monthsName;

    public Graph(Context context){
        super(context);
    }

    public Graph(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);

        String[] month={"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};
        setMonthsNames(month);

        mDetector = new GestureDetectorCompat(context,this);
        //mScaleGestureDetector = new ScaleGestureDetector(context, this);

        if(context instanceof GraphTouched)
            listener = (GraphTouched) context;

        barWidth =0.1f;
        barChartDistanceRatio =0.2f;

        transverseCount =5;
        descrCount=7;
        markedId=-1;

        valueFormat="%.3f";
        fixedMaxX=0;
        fixedMinX=0;
        isRegression=true;
        isTransverseLines =true;
        isSmartDate=false;
        isSmallMark=true;

        graphType=GraphType.GRAPH;
        showPolicy=ShowMarkedDataPolicy.SHOW_MARKED;

        metrics = context.getResources().getDisplayMetrics();

        axis = new Paint();
        mark = new Paint();
        mark.setStyle(Paint.Style.STROKE);

        mainGraph = new Paint();
        reg1 = new Paint();
        transverse = new Paint();
        dexcrY = new Paint();
        descrX = new Paint();
        errorMessagePaint= new Paint();

        markedDataBackground = new Paint();
        markedDataBorder = new Paint();
        markedDataBorder.setStyle(Paint.Style.STROKE);



      setAxisColor(Color.BLACK);
      setMarkColor(Color.BLUE);
      setGraphColor(Color.CYAN);
      setRegresionColor(Color.DKGRAY);
      setTransverseColor(Color.LTGRAY);
      setValuesColor(Color.CYAN);
      setDescriptionColor(Color.DKGRAY);
      setErrorColor(Color.RED);
      setMarkedDataBackgroundColor(Color.WHITE);
      setMarkedDataBorderColor(Color.BLACK);


       setGraphStrokeWidth(strokeWidth);
       setRegresionStrokeWidth(2);
       setTransverseStrokeWidth(3);
        setMarkStrokeWidth(2);
        setAxisStrokeWidth(2);
        setMarkedDataBorderStroke(1);

       setValuesTextSize(textSize);
       setDescriptionTextSize(textSize);
       setErrorFontSize(20);

        Paint.FontMetrics fm = dexcrY.getFontMetrics();
        descrYheight = fm.descent - fm.ascent;

        fm = descrX.getFontMetrics();
        descrXheight = fm.descent - fm.ascent;

        translate = new TranslateXY();

        smartDate = new SmartDate();

        regression =new Regression();

    }

    public Graph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public interface GraphTouched {
        void onGraphTouched(double value,int id,String description);
    }

    public void setOnGraphTouchedListener(GraphTouched listener){
        this.listener=listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (this.mDetector.onTouchEvent(event)) {
            return true;
        } else
       // if(event.getAction()==ACTION_DOWN) return true;
       // else
            return super.onTouchEvent(event);

    }

    @Override
    public boolean onDown(MotionEvent event) {
        // Log.d("onDown","onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        float oneItemDistance = (translate.getDataX(translate.getDataLenght())-translate.getDataX(0))/translate.getDataLenght();

        int dataLenght = getDataViewEnd()-getDataViewStart();

        int start = getDataViewStart()+Math.round(distanceX/oneItemDistance);
        int end = getDataViewEnd()+Math.round(distanceX/oneItemDistance);

        start = start<0?0:start;
        end = end<=start+dataLenght?start+dataLenght:end;

        end = end>graphData.size()?graphData.size()-1:end;
        start= start>= graphData.size()-dataLenght? graphData.size()-dataLenght-1:start;
        setViewData(start,end);
        show();
        return true;
    }
    @Override
    public boolean onSingleTapUp(MotionEvent event) {

     //   if(event.getAction()==MotionEvent.ACTION_UP) {
            int touchedId = -1;
            float distanceMin = 0.0f;
            if (graphData != null && graphData.size() > 0) {
                switch (graphType) {
                    case GRAPH:
                        float[] distance = new float[viewData.size()];
                        for (int i = 0; i < translate.getDataLenght(); i++) {

                            distance[i] = Math.abs(translate.getDataX(i) - event.getX()) + Math.abs(translate.getDataY(i) - event.getY());
                            if (i == 0) {
                                touchedId = 0;
                                distanceMin = distance[0];
                            } else {
                                if (distanceMin > distance[i]) {
                                    touchedId = i;
                                    distanceMin = distance[i];
                                }
                            }

                        }
                        if (distanceMin < getRootView().getHeight() * 0.05f) {
                            if (markedId != (viewData.get(touchedId).getOrginalId())) {
                                markedId = viewData.get(touchedId).getOrginalId();
                                if (listener != null)
                                    listener.onGraphTouched(viewData.get(touchedId).getValue(), markedId, viewData.get(touchedId).getDescription());
                                show();
                            } else eraseMark();
                        }
                        break;

                    case BAR_CHART:
                        float x0,x1,y0,y1;
                        for (int i = 0; i < translate.getDataLenght(); i++){
                            x0=translate.getDataX(i)-barWidth/2+barWidth* barChartDistanceRatio /2;
                            x1=translate.getDataX(i)+barWidth/2-barWidth* barChartDistanceRatio /2;
                            y0=translate.getDataY(i)-getHeight()*0.1f;// need to have some extra poll to recognize touch on particular bar
                            y1=translate.get0Y();

                            if(event.getX()>x0 && event.getX()<x1 && event.getY()>y0 && event.getY()<y1){
                                touchedId=i;
                            }
                        }
                        if(touchedId!=-1)
                            if (markedId != (viewData.get(touchedId).getOrginalId())) {
                                markedId = viewData.get(touchedId).getOrginalId();
                                if (listener != null)
                                    listener.onGraphTouched(viewData.get(touchedId).getValue(), markedId, viewData.get(touchedId).getDescription());
                                show();
                            } else eraseMark();
                        break;
                }
            }
        //}
        return true;
    }
    @Override
    public void onShowPress(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        //Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }
    @Override
    public void onLongPress(MotionEvent event) {
        // Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//calculating left padding depended on viewed values
        if (viewData != null) {
            float step = (float) (translate.getMaxData() - translate.getMinData()) / (transverseCount - 1);
            float left_padding=0.0f;
            for (int i = 0; i < transverseCount; i++) {
                String text = String.format(Locale.US, valueFormat, translate.getMinData() + step * i);
                left_padding= dexcrY.measureText(text)>left_padding? dexcrY.measureText(text):left_padding;
            }
            if (left_padding!=0) {LEFT_PADDING=left_padding/getWidth()+0.02f;}
//calculating bottom padding depended on viewed values
            float bottom_padding;
            bottom_padding= descrXheight *2.6f;
            if(bottom_padding!=0) {BOTTOM_PADDING= bottom_padding / getHeight()+0.03f ;}

            float top_padding;
            top_padding= descrYheight *2f;
            if(top_padding!=0) {TOP_PADDING= top_padding/getHeight() ;}

        }

        float chartWidth = getWidth()*(1-LEFT_PADDING-RIGHT_PADDING);
        float leftPaddnig= LEFT_PADDING*getWidth();
        float topPaddnig= TOP_PADDING*getHeight();
        float chartHeight = getHeight()*(1-TOP_PADDING-BOTTOM_PADDING);
        translate.setMargins(leftPaddnig,chartWidth,topPaddnig,chartHeight);

        if(!getRootView().isInEditMode()) {

            //making necessary transitions before drawing graphs, transitions are dependant of graph type
            switch (graphType) {
                case GRAPH:
                 translate.setTransition(0);
                    translate.setTransitionY(0);
                    break;
                case BAR_CHART:
                    translate.setTransition(0);
                    translate.setTransitionY(0);
                    if(translate.getDataLenght()>1) {
                        barWidth = (translate.getDataX(1) - translate.getDataX(0))-(translate.getDataX(1) - translate.getDataX(0))/translate.getDataLenght();
                        translate.setTransition(barWidth/2);
                        translate.setTransitionY((translate.get0X()-translate.getMaxY())*0.5f);
                    }
                    else {
                        barWidth=(translate.getMaxX()-translate.get0X())/2;
                        translate.setTransition(0);
                        translate.setTransitionY(0);
                    }


                    break;
            }

            if(description0Y!=null){ // setting 0Y description
                float x = translate.get0X() - dexcrY.measureText(description0Y) / 2f;
                if (x < 0.0f) x = 0.0f;
                canvas.drawText(description0Y, x, translate.getTopY()- descrYheight *0.5f , dexcrY);//0.03f * getHeight()
            }

            if(description0X!=null){ // setting 0X description
                float x = translate.getTopX() - descrX.measureText(description0X) / 2f;
                if (x+ descrX.measureText(description0X) > getWidth()) x = getWidth()- descrX.measureText(description0X);
                canvas.drawText(description0X, x, translate.get0Y() + descrXheight *1.5f, descrX);
            }

            if (viewData != null) {

                // setting 0Y values nad lines
                float step = (float) (translate.getMaxData() - translate.getMinData()) / (transverseCount - 1);
                for (int i = 0; i < transverseCount; i++) {

                    String text= String.format(Locale.US,valueFormat, translate.getMinData() + step * i);
                    canvas.drawText(text, (translate.get0X() - 0.02f * getHeight())/2- dexcrY.measureText(text)/2, translate.getDataY(translate.getMinData() + step * i)+ descrYheight /3, dexcrY);
                    if (i > 0) {
                        if(isTransverseLines)  canvas.drawLine(translate.get0X(), translate.getDataY(translate.getMinData() + step * i), translate.getTopX(), translate.getDataY(translate.getMinData() + step * i), transverse);
                    }
                    canvas.drawLine(translate.get0X() - 0.02f * getHeight(), translate.getDataY(translate.getMinData() + step * i), translate.get0X(), translate.getDataY(translate.getMinData() + step * i), axis);

                }
                //drawing regression line
                if(isRegression)
                    canvas.drawLine(translate.getDataX(0), translate.getDataY(regression.getRegression(0)), translate.getDataX(translate.getDataLenght() - 1), translate.getDataY(regression.getRegression(translate.getDataLenght() - 1)), reg1);


                  // here we draw our graph based on graph type
                    switch (graphType){
                        case GRAPH:
                            drawGraph(canvas);
                            break;
                        case BAR_CHART:
                            drawBarChart(canvas);
                            break;

                }

                //setting 0X description
                for (int i = 0; i < descrCount; i++){
                    int stepX = Math.round(i*translate.getDataLenght() / (descrCount));

                    if(stepX<translate.getDataLenght()) {

                        if(!isSmartDate) {//no smart date activated
                            String text = viewData.get(stepX).getDescription();
                            canvas.drawText(text, translate.getDataX(stepX) - dexcrY.measureText(text) / 2f, translate.get0Y() +  descrXheight *1.3f, descrX);

                        }
                        else
                        {//smart date activated draw 1st line (smart date is divided into 2 lines)
                            String text1 = smartDate.getFirstLineDescription(stepX);
                            canvas.drawText(text1, translate.getDataX(stepX) - dexcrY.measureText(text1) / 2f, translate.get0Y() +  descrXheight *1.3f, descrX);
                        }
                        canvas.drawLine(translate.getDataX(stepX ), translate.get0Y(), translate.getDataX(stepX ), translate.get0Y() + getHeight() * 0.02f, axis);
                    }
                }
                if(isSmartDate){//smart date activated draw 2nd line

                    Integer[] indexes = smartDate.getSecondLineIndexes();
                    for(Integer index:indexes){
                        String text;
                        if(smartDate.DATA_NUMBER_EDGE>viewData.size()){
                            text =  MonthDescr(smartDate.getDate(index,SmartDate.M));
                        } else {
                            text =  smartDate.getDate(index,SmartDate.Y);
                        }

                        canvas.drawText(text, translate.getDataX(index) - dexcrY.measureText(text) / 2f, translate.get0Y() +  descrXheight *2.6f, descrX);

                    }

                }

                //erasing mark if -1 else setting mark
                if (markedId != -1) {
                    int idToShow=-1;
                    for(int i=0;i<viewData.size();i++){
                        if(viewData.get(i).getOrginalId()==markedId)
                            idToShow=i;
                    }
                    if (idToShow >= 0) {

                        switch (graphType) {
                            case GRAPH:
                            canvas.drawCircle(translate.getDataX(idToShow), translate.getDataY(idToShow), getHeight() * 0.015f, mark);
                            canvas.drawLine(translate.getDataX(idToShow), translate.getDataY(idToShow), translate.get0X(), translate.getDataY(idToShow), mark);
                            canvas.drawLine(translate.getDataX(idToShow), translate.getDataY(idToShow), translate.getDataX(idToShow), translate.get0Y(), mark);
                        break;
                            case BAR_CHART:
                                canvas.drawRect(translate.getDataX(idToShow)-barWidth/2+barWidth* barChartDistanceRatio /2, translate.getDataY(idToShow),translate.getDataX(idToShow)+barWidth/2-barWidth* barChartDistanceRatio /2,translate.get0Y(),mark);

                                break;
                        }

                    }
                }

                //setting marked window
                switch (showPolicy){
                    case SHOW_NEVER:
                        break;
                    case SHOW_ALWAYS:
                        for(int i=0;i<translate.getDataLenght();i++){
                            drawData(canvas,i);
                        }
                        break;
                    case SHOW_MARKED:
                        if (markedId != -1){
                            int idToShow=-1;
                            for(int i=0;i<viewData.size();i++){
                                if(viewData.get(i).getOrginalId()==markedId)
                                    idToShow=i;
                            }
                            if (idToShow >= 0) {
                            drawData(canvas,idToShow);
                            }
                        }
                        break;
                }
            }
        }
//finally here we draw 0X and 0Y
        canvas.drawLine(translate.get0X(),translate.get0Y(),translate.getTopX(),translate.get0Y(), axis);//oś ox
        canvas.drawLine(translate.getTopX(),translate.get0Y(),translate.getTopX()-0.02f*getHeight(),translate.get0Y()-0.02f*getHeight(), axis);//oś ox
        canvas.drawLine(translate.getTopX(),translate.get0Y(),translate.getTopX()-0.02f*getHeight(),translate.get0Y()+0.02f*getHeight(), axis);//oś ox

        canvas.drawLine(translate.get0X(),translate.get0Y(),translate.get0X(),translate.getTopY(), axis);//oś oy
        canvas.drawLine(translate.get0X(),translate.getTopY(),translate.get0X()+0.02f*getHeight(),translate.getTopY()+0.02f*getHeight(), axis);//oś oy
        canvas.drawLine(translate.get0X(),translate.getTopY(),translate.get0X()-0.02f*getHeight(),translate.getTopY()+0.02f*getHeight(), axis);//oś oy

        if(errorMessage!=null && viewData==null){
            String[] errorMessageSplit = errorMessage.split(" - ");
            for(int i=0;i<errorMessageSplit.length;i++)
                canvas.drawText(errorMessageSplit[i],  (translate.getTopX()-translate.get0X())/2- dexcrY.measureText(errorMessageSplit[i])/2f,(translate.get0Y()-translate.getTopY())/2+getHeight()*0.07f+getHeight()*0.07f*i, errorMessagePaint);
        }
    }

    private void drawData(Canvas canvas,int id){

        float baseX = translate.getDataX(id);
        float baseY = translate.getDataY(id);

        String value;
        if(valueFormatOnData==null)
        value = String.valueOf(viewData.get(id).getValue());
        else   value = String.format(Locale.US,valueFormatOnData,viewData.get(id).getValue());

        float x1 = baseX- dexcrY.measureText(value)/2f;
        float y1 = baseY- descrYheight *1.3f;

        String description = viewData.get(id).getDescription();

        float x2= baseX- descrX.measureText(description)/2f;
        float y2 =baseY- descrXheight *1.3f- descrYheight *1.3f;

        if(x1<x2) {if(x1<translate.get0X()) {x1=translate.get0X(); x2=x1- descrX.measureText(description)/2f+ dexcrY.measureText(value)/2f;}}//+dexcrY.measureText(description)/2f
        else
        if(x2<x1) {if(x2<translate.get0X()) {x2=translate.get0X(); x1=x2- dexcrY.measureText(value)/2f+ descrX.measureText(description)/2f;}}//+descrX.measureText(value)/2f

        if(x1<x2) {
           if(x1+ dexcrY.measureText(value)>getWidth()) {x1=getWidth()- dexcrY.measureText(value); x2=x1+ dexcrY.measureText(value)/2- descrX.measureText(description)/2f;}}
        else
        if(x2<x1) {
           if(x2+ descrX.measureText(description)>getWidth()) {x2=getWidth()- descrX.measureText(description); x1=x2+ descrX.measureText(description)/2- dexcrY.measureText(value)/2f;}}

        if(y2- descrYheight *1.3f<0) {y2= descrYheight *1.3f;y1=y2+ descrXheight *1.3f;}


        canvas.drawRect(Math.min(x2,x1),y2- descrYheight *1.3f,Math.max(x1+ dexcrY.measureText(value),x2+ descrX.measureText(description)),y1+0.3f* descrXheight,markedDataBackground);
        canvas.drawRect(Math.min(x2,x1),y2- descrYheight *1.3f,Math.max(x1+ dexcrY.measureText(value),x2+ descrX.measureText(description)),y1+0.3f* descrXheight,markedDataBorder);

        canvas.drawText(value, x1,y1, dexcrY);
        canvas.drawText(description, x2,y2, descrX);

    }
    private void drawGraph(Canvas canvas){
        if(translate.getDataLenght()==1){
            if (isSmallMark) {
                canvas.drawCircle(translate.getDataX(0), translate.getDataY(0), getHeight() * 0.006f, mark);
            }
        }

        for (int i = 0; i < translate.getDataLenght() - 1; i++) {
            canvas.drawLine(translate.getDataX(i), translate.getDataY(i), translate.getDataX(i + 1), translate.getDataY(i + 1), mainGraph);


            if (isSmallMark) {
                canvas.drawCircle(translate.getDataX(i), translate.getDataY(i), getHeight() * 0.006f, mark);

                if (i == translate.getDataLenght() - 2) {
                    canvas.drawCircle(translate.getDataX(i + 1), translate.getDataY(i + 1), getHeight() * 0.006f, mark);
                }
            }
        }
    }

    private void drawBarChart(Canvas canvas){

        for (int i = 0; i < translate.getDataLenght(); i++) {
            Paint barChartPaint = new Paint();
            if(viewData.get(i).getColor()==0)
            barChartPaint.setColor(mainGraph.getColor());
            else  barChartPaint.setColor(viewData.get(i).getColor());
            canvas.drawRect(translate.getDataX(i)-barWidth/2+barWidth* barChartDistanceRatio /2, translate.getDataY(i),translate.getDataX(i)+barWidth/2-barWidth* barChartDistanceRatio /2,translate.get0Y(),barChartPaint);
        }

    }

    public void setMarkedDataBackgroundColor(int graphColor) {
        markedDataBackground.setColor(graphColor);
    }
    public void setMarkedDataBorderColor(int graphColor) {
        markedDataBorder.setColor(graphColor);
    }
    public void setMarkedDataBorderStroke(float stroke) {
        markedDataBorder.setStrokeWidth(stroke);
    }

    public void setGraphColor(int graphColor) {
        mainGraph.setColor(graphColor);
    }
    public void setGraphStrokeWidth(float width) {
        mainGraph.setStrokeWidth(width*metrics.density);
    }

    public void setRegresionColor(int regresionColor) {
        reg1.setColor(regresionColor);
    }
    public void setRegresionStrokeWidth(float width) {
        reg1.setStrokeWidth(width*metrics.density);
    }
    public void setAxisColor(int axisColor) {
        axis.setColor(axisColor);
    }
    public void setAxisStrokeWidth(float width) {
        axis.setStrokeWidth(width*metrics.density);
    }

    public void setValuesColor(int valuesColor) {
        dexcrY.setColor(valuesColor);
    }
    public void setValuesTextSize(float size) {
        dexcrY.setTextSize(size*metrics.density);
    }

    public void setDescriptionColor(int descriptionColor) {
        descrX.setColor(descriptionColor);
    }
    public void setDescriptionTextSize(float size) {
        descrX.setTextSize(size*metrics.density);
    }

    public void setMarkColor(int markColor) {
        mark.setColor(markColor);
    }
    public void setMarkStrokeWidth(int width) {
        mark.setStrokeWidth(width*metrics.density);
    }

    public void setTransverseColor(int poprzeczneColor) {
        transverse.setColor(poprzeczneColor);
    }

    public void setTransverseStrokeWidth(float width){
        transverse.setStrokeWidth(width*metrics.density);
    }

    public void setErrorColor(int errorColor) {
        errorMessagePaint.setColor(errorColor);
    }

    public void setErrorFontSize(float size){
        errorMessagePaint.setTextSize(size*metrics.density);
    }

    public void setData(GraphDataSet graphDataSet){
        setData(graphDataSet.getGraphDataList());
    }

    public void setData(List<GraphItem> graphData){
        this.graphData=graphData;
        this.viewData=new ArrayList<>();
        for(int i=0;i<graphData.size();i++){
            viewData.add(new GraphItem(graphData.get(i).getValue(),graphData.get(i).getDescription(),graphData.get(i).getColor()));
            viewData.get(i).setOrginalId(i);
        }
        regression.setGraphData(viewData);

        double min,max;
        if(fixedMinX==0) min=GraphItem.getMinValue(GraphItem.toDouble(viewData));
        else min = fixedMinX;
        if(fixedMaxX==0)max=GraphItem.getMaxValue(GraphItem.toDouble(viewData));
        else max=fixedMaxX;
        translate.setDataFrame(min,max );
        translate.setDataY(GraphItem.toDouble(viewData));
        smartDate.setData(viewData);
    }

    public void clearData(){
        graphData.clear();
        viewData.clear();
    }

    public void setViewData(int start,int end){
        if(viewData!=null) {
            viewData.clear();
            start = start >= 0 ? start : 0;
            end = end < graphData.size() ? end : graphData.size() - 1;
            for (int i = start; i <= end; i++) {
                viewData.add(graphData.get(i));
                viewData.get(viewData.size() - 1).setOrginalId(i);
            }
            regression.setGraphData(viewData);
            double min, max;
            if (fixedMinX == 0) min = GraphItem.getMinValue(GraphItem.toDouble(viewData));
            else min = fixedMinX;
            if (fixedMaxX == 0) max = GraphItem.getMaxValue(GraphItem.toDouble(viewData));
            else max = fixedMaxX;
            translate.setDataFrame(min, max);
            translate.setDataY(GraphItem.toDouble(viewData));
            smartDate.setData(viewData);
        }
    }
    public void setViewDataLast(int lenght){
        if(viewData!=null) {
            viewData.clear();

            lenght = lenght <= graphData.size() ? lenght : graphData.size();
            if (lenght == DATA_LENGHT_MAX) {
                lenght = graphData.size();
            } else if(lenght<1) lenght=1;
            //Log.d("viewData", String.valueOf(graphData.size()));
            for (int i = (graphData.size() - lenght); i < graphData.size(); i++) {
                viewData.add(graphData.get(i));
                viewData.get(viewData.size() - 1).setOrginalId(i);
                //  Log.d("viewData",String.valueOf(i));
            }
            regression.setGraphData(viewData);
            double min, max;
            if (fixedMinX == 0) min = GraphItem.getMinValue(GraphItem.toDouble(viewData));
            else min = fixedMinX;
            if (fixedMaxX == 0) max = GraphItem.getMaxValue(GraphItem.toDouble(viewData));
            else max = fixedMaxX;
            translate.setDataFrame(min, max);
            translate.setDataY(GraphItem.toDouble(viewData));
            smartDate.setData(viewData);
        }
    }
    public void setViewDataLenght(int lenght){
        if(viewData!=null) {
            int start = getDataViewStart();
            int end = getDataViewEnd();
            int presentLenght = end-start;

            lenght = lenght <= graphData.size() ? lenght : graphData.size();
            if (lenght == DATA_LENGHT_MAX) {
                lenght = graphData.size();
            } else if(lenght<1) lenght=1;

            int differenceLenght = lenght-presentLenght;
            start -= differenceLenght/2;
            end += differenceLenght/2;

            setViewData(start,end);
        }
    }

    public void show(){
        postInvalidate();
    }

    public void setGraphType(GraphType graphType) {
        this.graphType = graphType;
    }

    public void setDataPolicyShow(ShowMarkedDataPolicy policy){
        this.showPolicy=policy;
    }

    public void setYDataDescrCount(int count){this.transverseCount =count;}

    public void setXDataDescrCount(int count){this.descrCount=count;}

    public void setFixedMaxX(double fixedMaxX) {
        this.fixedMaxX = fixedMaxX;
        double min=fixedMinX,max;
        if(fixedMaxX==0)max=GraphItem.getMaxValue(GraphItem.toDouble(viewData));
        else max=fixedMaxX;
        translate.setDataFrame(min,max );
    }

    public void setFixedMinX(double fixedMinX) {
        this.fixedMinX = fixedMinX;
        double min,max=fixedMaxX;
        if(fixedMinX==0) min=GraphItem.getMinValue(GraphItem.toDouble(viewData));
        else min = fixedMinX;
        translate.setDataFrame(min,max );
    }

    public void setDescription0Y(String description0Y) {
        this.description0Y = description0Y;
    }

    public void setDescription0X(String description0X) {
        this.description0X = description0X;
    }

    public void setValueFormat(String valueFormat) {
        this.valueFormat = valueFormat;
    }

    public void setValueFormatOnData(String valueFormatOnData) {
        this.valueFormatOnData = valueFormatOnData;
    }

    public void setErrorMessage(String msg){
        errorMessage=msg;
    }

    public int getDataViewStart(){
        if(viewData.size()>0)
        return viewData.get(0).getOrginalId();
        else return 0;
    }
    public int getDataViewEnd(){
        if(viewData.size()>0)
        return viewData.get(viewData.size()-1).getOrginalId();
        else return 0;
    }

    public int getDataViewLenght(){
        return viewData.size();
    }

    public void setRegression(boolean isRegression){
        this.isRegression=isRegression;
    }
    public void setTransverseLines(boolean transverseLines){
        this.isTransverseLines =transverseLines;
    }
    public void setSmallMark(boolean isSmallMark){
        this.isSmallMark=isSmallMark;
    }

    public void setSmartDate(boolean isSmartDate) {
        this.isSmartDate=isSmartDate;
    }

    public void setSmartDateEdge(int count){
        smartDate.setChangeDescriptionEdge(count);
    }

    public void markDate(String date){

        String[] dateCut = date.split("-");

        if(graphData!=null)

            for(int i=0;i< graphData.size();i++) {
                String[] dateShowCut =graphData.get(i).getDescription().split("-");
                try{
                    int dateInt[] = new int[dateCut.length];
                    int dateShowInt[] = new int[dateShowCut.length];
                    if(dateInt.length==3 && dateShowInt.length==3)
                    {
                        dateInt[0]=Integer.parseInt(dateCut[0]);
                        dateInt[1]=Integer.parseInt(dateCut[1]);
                        dateInt[2]=Integer.parseInt(dateCut[2]);

                        dateShowInt[0]=Integer.parseInt(dateShowCut[0]);
                        dateShowInt[1]=Integer.parseInt(dateShowCut[1]);
                        dateShowInt[2]=Integer.parseInt(dateShowCut[2]);

                        if(dateInt[0]==dateShowInt[0] && dateInt[1]==dateShowInt[1] && dateInt[2]==dateShowInt[2] ) {
                            markedId=i;
                            this.markedDate=date;
                            show();
                        }
                    }
                } catch (NumberFormatException e){}
            }

    }

    public void setMarkedId(int markedId){
        this.markedId=markedId;
        show();
    }

    public void eraseMark(){
        markedId=-1;
        show();
    }

    private class TranslateXY{

        private float left,right,top,bottom,width,height;
        private float transition,transitionY;
        private double minData,maxData;

        private List<Double> dataY;

        private  TranslateXY(float minData,float maxData){

            this.minData=minData;
            this.maxData=maxData;
            this.transition=0;
            this.transitionY=0;
        }

        private  TranslateXY(){
            this.transition=0;
        }

        private void setDataFrame(double minData,double maxData){

            this.minData=minData;
            this.maxData=maxData;
        }


        private void setMargins(float left, float width,float top, float height){
            this.left=left;
            this.right=left+width;
            this.width=width;
            this.height=height;
            this.bottom=top+height;
            this.top = top;
        }

        private void setTransition(float transition) {
            this.transition = transition;
        }

        private void setTransitionY(float transitionY) {
            this.transitionY = transitionY;
        }

        private void setDataY(List<Double> dataY){
            this.dataY=dataY;
        }

        private float getDataY(int next){
            double value = dataY.get(next);

            if(getDataLenght()>1)
            return  -transitionY+get0Y()-(get0Y()-getMaxY())*((float)value-(float)minData)/((float)maxData-(float)minData);
            else   if(getDataLenght()==1) {
                return (get0Y()-getMaxY())/2;
            }
            return 0;
        }

        private float getDataY(double value){
            if(getDataLenght()>1)
                return  -transitionY+get0Y()-(get0Y()-getMaxY())*((float)value-(float)minData)/((float)maxData-(float)minData);
            else   if(getDataLenght()==1) {
                return (get0Y()-getMaxY())/2;
            }
            return 0; }

        private float getDataX(int next){

            if(getDataLenght()>1)
            return transition+get0X()+(getMaxX()-get0X())*next/(dataY.size()-1);
            else   if(getDataLenght()==1) {
                return (getMaxX()-get0X())/2;
            }
            return 0; }


        private float get0X(){
            return left;
        }

        private float get0Y(){
            return bottom;
        }

        private float getMaxX(){
            return right-width*0.04f-transition*2;
        }

        private float getTopX(){
            return right;
        }

        private float getMaxY(){
            return top+height*0.04f+transitionY*2;
        }

        private float getTopY(){
            return top;
        }

        private int getDataLenght(){
            return dataY.size();
        }

        private double getMaxData() {
            return maxData;
        }

        private double getMinData() {
            return minData;
        }
    }

    private class SmartDate{

        private static final int YMD=0;
        private static final int Y=1;
        private static final int M=2;
        private static final int D=3;
        private static final int NONE=4;

        private int DATA_NUMBER_EDGE;

        private ArrayList<String> dates;


        private SmartDate(){
            DATA_NUMBER_EDGE=160;
        }

        private SmartDate(int number){
            DATA_NUMBER_EDGE=number;
        }

        private SmartDate(List<GraphItem> graphData){
            DATA_NUMBER_EDGE=160;
            setData(graphData);
        }

        private void setChangeDescriptionEdge(int count){
            DATA_NUMBER_EDGE=count;
        }

        private void setData(List<GraphItem> graphData){
            dates = new ArrayList<>();
            if(graphData!=null){
                for(GraphItem item:graphData){
                    dates.add(item.getDescription());
                }
            }

        }

        private String getFirstLineDescription(int index){
            if(DATA_NUMBER_EDGE>dates.size()){//in this case we show days
                return getDate(index,D);
            }
            else{//in this case we show months
                return MonthDescr(getDate(index,M));
            }
        }

        private Integer[] getFirstLineIndexes(){
            if(DATA_NUMBER_EDGE>dates.size()){//in this case we show monthsFirstDay

                List<Integer> indexes = new ArrayList<>();
                for(int i=0;i<dates.size();i++){
                    if(i==0) indexes.add(i);
                    else
                    {
                        if(!getDate(i,D).equals(getDate(i-1,D))) indexes.add(i);
                    }
                }
                Integer[] array=new Integer[indexes.size()];
                indexes.toArray(array);
                return array;
            }
            else{//in this case we show yearsFirstDay

                List<Integer> indexes = new ArrayList<>();
                for(int i=0;i<dates.size();i++){
                    if(i==0) indexes.add(i);
                    else
                    {
                        if(!getDate(i,M).equals(getDate(i-1,M))) indexes.add(i);
                    }
                }
                Integer[] array=new Integer[indexes.size()];
                indexes.toArray(array);
                return array;
            }
        }

        private Integer[] getSecondLineIndexes(){
            if(DATA_NUMBER_EDGE>dates.size()){//in this case we show monthsFirstDay

                List<Integer> indexes = new ArrayList<>();
                for(int i=0;i<dates.size();i++){
                    if(i==0) indexes.add(i);
                    else
                    {
                        if(!getDate(i,M).equals(getDate(i-1,M))) indexes.add(i);
                    }
                }
                Integer[] array=new Integer[indexes.size()];
                indexes.toArray(array);
                return array;
            }
            else{//in this case we show yearsFirstDay

                List<Integer> indexes = new ArrayList<>();
                for(int i=0;i<dates.size();i++){
                    if(i==0) indexes.add(i);
                    else
                    {
                        if(!getDate(i,Y).equals(getDate(i-1,Y))) indexes.add(i);
                    }
                }
                Integer[] array=new Integer[indexes.size()];
                indexes.toArray(array);
                return array;
            }
        }

        private String getDate(int index,int type){

            String input;
            if(index>=dates.size()) {input=dates.get(dates.size()-1);}
            else input=dates.get(index);

            String output="";
            if(!input.equals(""))
                switch (type)
                {
                    case YMD:output=input;
                        break;
                    case Y:output=input.split("-")[0];
                        break;
                    case M:output=input.split("-")[1];
                        break;
                    case D:output=input.split("-")[2];
                        break;
                    case NONE:
                        break;
                }

            return output;
        }

        private int getMonth1StDayIndex(int index){
            String monthS;
            if(index< dates.size()-1)
                monthS=getDate(index,M);
            else monthS=getDate(dates.size()-1,M);
            for(int i=index;i>0;i--){
                if(!monthS.equals(getDate(i-1,M))){return i;}
            }
            return 0;
        }

        private int getYear1StDayIndex(int index){
            String year;
            if(index< dates.size())
                year=getDate(index,Y);
            else year=getDate(dates.size()-1,Y);
            for(int i=index;i>0;i--){
                if(!year.equals(getDate(i-1,Y))){return i;}
            }
            return 0;
        }

    }

    private String MonthDescr(String monthS){
        int month=Integer.parseInt(monthS);
        if(month>12 || month<1) return "Error number of months exceeded, type 1 to 12";
        return monthsName[month-1];
    }

    private void setMonthsNames(String[] monthsNames){
        this.monthsName = monthsNames;
    }

  private class Regression {

        private List<Double> values;
        private double averageX, averageY;
        private double a,b;

        Regression(){
            averageY =0;
            averageX =0;
            a=0;
            b=0;
        }

      private void setGraphData(List<GraphItem> data){
            values = new ArrayList<>();
            for(GraphItem item:data){
                values.add(item.getValue());
            }
            calculate();
        }

      private void setData(List<Double> values){
            this.values=values;
            calculate();
        }

      private void calculate(){
            int count=0;
            averageY =0;
            averageX =0;
            for(int i=0;i<values.size();i++){
                averageX = averageX +(i);
                averageY = averageY +values.get(i);
                count+=1;
            }
            if(count>0) {
                averageX /=count;
                averageY /=(count);
           }
            double up=0,down=0;
            for(int i=0;i<values.size();i++){
                up+=(((i)- averageX)*(values.get(i)- averageY));
                down+=(((i)- averageX)*((i)- averageX));
            }
            a=(up)/down;
            b= averageY -a* averageX;
        }

      private float getRegression(int index){
            return (float) a*index+(float) b;
        }


    }


}


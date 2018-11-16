# Graph
This is my first push to GitHub.

I have wrote my first let's say library. This library shows data on graph paired value-description.

** This library is included in:
Graph.java GraphDataSet.java GraphItem.java GraphType.java ShowMarkedPlicy.java

** In ActivityMain you can see how to use this library.

** First in activity.xml create:

        <com.supprt.jarek.graph.Graph
        android:id="@+id/main_graph"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:background="@color/colorPrimary">
        
** than bound it in your java or kotlin activity class:
Graph graph = findViewById(R.id.main_graph);

//prepeare data and set this data to graph:
GraphDataSet  data = new GraphDataSet();

data.add(10,"12");

data.add(14,""2000-10-01");

data.add(12.6,""2000-10-01",Color.RED);//this sets this data to red color only in barChart

graph.setData(data);
        
** after that just type:
graph.show();

** and will create graph!

** aditionaly you can customize your graph with followed commands:

 graph.setGraphType(GraphType.GRAPH); // or GraphType.BAR_CHART

 graph.setDescription0Y("0Y Description"); //>> this sets the name of the 0Y axis
 
 graph.setDescription0X("0X Description"); //>> this sets the name of the 0X axis

 graph.setYDataDescrCount(5); //>> this sets the number of descriptions on the 0Y axis
 
 graph.setXDataDescrCount(6); //>> this sets the number of descriptions on the 0X axis
 
 graph.setValuesTextSize(16); //>> text size of values
 
 graph.setDescriptionTextSize(16); //>> text size of descriptions
 
 graph.setValueFormat("%.2f"); //>> how to format values
 
 graph.setTransverseLines(true); //>> setting background transverse lines to show or not
 
 graph.setRegression(true);  //>> show regression line
 
 ** bottom lines must be described more:
 if you don't set them or set them to 0 the data in graphView will be always displayed from max to min value DISPLAYED. 
 To prevent this set this fixed max min values.
 
 graph.setFixedMaxX(GraphItem.getMaxValue(GraphItem.toDouble(graphData)));
 
 graph.setFixedMinX(GraphItem.getMinValue(GraphItem.toDouble(graphData)));
 
 ** if you set descripions of your values as: YYYY-MM-DD you can set smart date to true.
 //this feature shows data in 2 lines divided to day/month or if there is to much data it changes to month/year.
 
 graph.setSmartDate(true);
 
 graph.setSmartDateEdge(150); //sets the number for viewed data to change smart data to month/year
 
 // use bottom to change description on months
 
 String[] month={"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};
 graph.setMonthsNames(months); 
 
 ** setting colors:
  
  graph.setGraphColor(Color.RED); //>> main graph color both graph and bar
  
  graph.setValuesColor(Color.BLACK); //>> all values color
  
  graph.setDescriptionColor(Color.BLACK); //>> all description color
  
  graph.setMarkedDataBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));  //>> background color for marked data view
  
  graph.setMarkedDataBorderColor(Color.BLACK); //>>border color for marked data view
  
  graph.setMarkColor(Color.BLUE); //>> sets the color when you mark particular value
  
  graph.setErrorColor(Color.RED); //>> sets the color for the text thata shows when no data is loaded
  
  graph.setErrorMessage("Error"); //>> if no data loaded graph will show custom text
 
  graph.setDataPolicyShow(ShowMarkedDataPolicy.SHOW_MARKED);//>> how to show small view that shows detailed info if show marked it 
  shows only when you mark value, you can use: SHOW_NEVER or SHOW_ALWAYS - usefull only with few data number.
 
  ** graph has its own GraphTouched listener
  
         graph.setOnGraphTouchedListener(new Graph.GraphTouched() {
            @Override
            public void onGraphTouched(double value, int id, String description) {
          }
        });
        
     **   you can also manipulate what data grap shows:
        
         graph.setViewDataLast(count);//>> sets the viewData to last 'count' values
         graph.setViewDataLenght(count); //>>sets the viewData 'count' values (like zoom in and zoom out)
         graph.setViewData(start,end); //>>sets the viewData start and end index
         
        int graph.getDataViewStart();
        int graph.getDataViewEnd();
        
      **  after manipulating always use 
        graph.show();
      

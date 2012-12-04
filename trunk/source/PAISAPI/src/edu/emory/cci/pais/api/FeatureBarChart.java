package edu.emory.cci.pais.api;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

import com.sun.java.swing.Painter;


/**
 * A simple demonstration application showing how to create a bar chart.
 */
public class FeatureBarChart {
	
	public FeatureBarChart(){}

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     * @param width TODO
     * @param height TODO
     * @param matrix 
     * @param format Export image format
     */
    public static File getFeatureBarChart(String title, String subtitle, int width, int height, double [][] fm, String format) {
/*    	System.out.println("fm length: " + fm.length); 
		for (int i = 0; i < fm.length; i ++ )
			for (int j = 0; j < fm[i].length; j ++ )
				System.out.println("i:j: " + fm[i][j]); */
        
        CategoryDataset dataset = createDataset(fm);
        JFreeChart chart = createChart(dataset, title);
       Title secondTitle = new TextTitle(subtitle);
        chart.addSubtitle(secondTitle); 
        
        File temp = null;
        //ChartPanel chartPanel = new ChartPanel(chart, false);
        //chartPanel.setPreferredSize(new Dimension(width, height));
        //setContentPane(chartPanel);
		try {
			temp = File.createTempFile("_" + System.currentTimeMillis(), "." +  format);

		    // Delete temp file when program exits.
		    temp.deleteOnExit();
	    	FileOutputStream out  = new FileOutputStream(temp);
			if ("PNG".equalsIgnoreCase(format)){
				ChartUtilities.writeChartAsPNG(out, chart, width, height);
			} 
			else if ("JPG".equalsIgnoreCase(format) || "JPEG".equalsIgnoreCase(format)){
				ChartUtilities.writeChartAsJPEG(out, chart,	width, height);
			}
			
			else	if ("EPS".equalsIgnoreCase(format) ){
				//batik lib needed
				//jlibeps needed
		        EpsGraphics2D g = new EpsGraphics2D();
		        //g.setColor(Color.blue);
		        //chart.draw(g2, area, info)
		        chart.draw(g, new Rectangle(width, height - 20));
		         Writer writer= new FileWriter(temp);
		         writer.write(g.toString());
		         writer.close();
			}
			else	if ("svg".equalsIgnoreCase(format) ){
				//batik lib needed
			/*								
				DOMImplementation domImpl =
						SVGDOMImplementation.getDOMImplementation();
				Document document = domImpl.createDocument(null, "svg", null);
				SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
				chart.draw(svgGenerator,new Rectangle(width, height));
				boolean useCSS = true; // we want to use CSS style attribute

				Writer out = new OutputStreamWriter(new    FileOutputStream(name), "UTF-8");
				svgGenerator.stream(out, useCSS);
				out.close();
				*/
				
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return temp;

    }
    
    
  private static CategoryDataset createDataset ( double [][] fm) {
        //FREQUENCY   BINSTART    BINEND 
	    //148868           0         110 
        // row keys...
        String series = "Area";


        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int length = fm.length;
        for (int i = 0; i < length; i++ ){
        	String category = null;
        	if (fm[length-1][2] > 100) 
        		category = (long) fm[i][1] + "-" + (long) fm[i][2];
        	else if (fm[length-1][2] > 10){
    			DecimalFormat dFormat = new DecimalFormat("0.0");
        		category = dFormat.format( fm[i][1]) + "-" + dFormat.format(  fm[i][2] );
        	}
        	else {
    			DecimalFormat dFormat = new DecimalFormat("0.00");
        		category = dFormat.format( fm[i][1]) + "-" + dFormat.format(  fm[i][2] );
        	}
        	 dataset.addValue( fm[i][0], series, category);
        }

        
        return dataset;
        
    }    

 
  
/*  private static CategoryDataset createHistogramDataset ( double [][] fm, String seriesName) {
      //FREQUENCY   BINSTART    BINEND 
	    //148868           0         110 
      // row keys...
	  HistogramDataset dataset = new HistogramDataset();
	  dataset.setType(HistogramType.RELATIVE_FREQUENCY);
	  dataset.addSeries(key, values, bins)


      // create the dataset...
      //DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      for (int i = 0; i < fm.length; i++ ){
      	String category = null;
      	if (fm[i][2] > 0) 
      		category = (long) fm[i][1] + "-" + (long) fm[i][2];
      	else category = fm[i][1] + "-" + fm[i][2];
      	 dataset.addValue( fm[i][0], seriesName, category);
      }

      
      return dataset;
      
  }  */    
    
    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset.
     * @param title TODO
     * 
     * @return The chart.
     */
    private static JFreeChart createChart(CategoryDataset dataset, String title) {
        
        // create the chart...
        JFreeChart chart = ChartFactory.createBarChart(
            title,         // chart title
            "Range",               // domain axis label
            "Count",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            false,                     // include legend
            false,                     // tooltips?
            false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(250,250,250));
        plot.setDomainGridlinePaint(Color.lightGray );
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoTickUnitSelection(true);
        rangeAxis.setAutoRange(true);
/*        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberTickUnit unit = new  NumberTickUnit(0.001);
        rangeAxis.setTickUnit(unit);
*/     
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        CategoryAxis xAxis = (CategoryAxis)plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        //rangeAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.blue);

        
        renderer.setDrawBarOutline(false);
        
/*        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.blue, 
            0.0f, 0.0f, new Color(0, 0, 64)
        );
        GradientPaint gp1 = new GradientPaint(
            0.0f, 0.0f, Color.green, 
            0.0f, 0.0f, new Color(0, 64, 0)
        );
        GradientPaint gp2 = new GradientPaint(
            0.0f, 0.0f, Color.red, 
            0.0f, 0.0f, new Color(64, 0, 0)
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);*/

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
        // OPTIONAL CUSTOMISATION COMPLETED.
        
        return chart;
        
    }
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

    	double [][] fm = { 
    			{148868, 0, 110}, 
    			{43579, 110, 220},
    			{8879,         220,         330},
    			{3170,         330,         440 },
    			{1377,         440,         550},
    			{657,         550,         660 },
    			{385,         660,         770},
    			{ 241,         770,         880 },
    			{136,         880,         990 },
    			{19,         990,        1100 }    			
    	};
    	
    	
    	double [][] fm2 = { 
    			{148868, 0, 0.110}, 
    			{43579, 0.110, 0.220},
    			{8879,         0.220,         0.330},
    			{3170,         0.330,         0.440 },
    			{1377,         0.440,         0.550},
    			{657,         0.550,         0.660 },
    			{385,         0.660,         0.770},
    			{ 241,         0.770,         0.880 },
    			{136,        0.880,         0.990 },
    			{19,         0.990,       0.1100 }    			
    	};
    	
       File outFile = FeatureBarChart.getFeatureBarChart("Feature Histogram Demo", "Second title", 600, 400, fm, "png");
       System.out.println(outFile.getAbsolutePath());
        
/*        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);*/

    }

}

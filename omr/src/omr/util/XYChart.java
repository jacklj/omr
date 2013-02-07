
package omr.util;

import java.awt.image.BufferedImage;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;



// USAGE ////////
//int[] data = {4,7,6,2,1,0,3,5,6,7,8,12,11,9,6}; //int array
//int size = 10; // what data from int array to use, range index 0 -> (size - 1)
//String name = "TestChart";
//
//XYChart myChart = new XYChart(data, size, name);
//myChart.display("Test Chart 1", 400, 150);



public class XYChart
{

	public static void main(String[] args) {
		// for testing

		int[] data = {4,7,6,2,1,0,3,5,6,7,8,12,11,9,6};
		int size = 10;
		String name = "TestChart";

		XYChart myChart = new XYChart(data, size, name);
		myChart.display(400, 150);
		
	}

	
	//////////////////////////////////////////////////////////////////////////////////////////////////

	private JFreeChart chart;
	private String name;
	
	public XYChart(int data[], int size, String name)
	{
		this.name = name;
		XYSeries series = new XYSeries(name);
		for (int i=0; i<size; i+=1)
			series.add(i, data[i]);
		XYDataset xyDataset = new XYSeriesCollection(series);

		chart = ChartFactory.createXYAreaChart(name, "width", "# Pixels", xyDataset, PlotOrientation.VERTICAL, true, false, false);

	}

	public XYChart(int size, int data[], String name)
	{		
		this.name = name;
		XYSeries series = new XYSeries(name);
		for (int i=0; i<size; i+=1)
			series.add(i, data[i]);
		XYDataset xyDataset = new XYSeriesCollection(series);

		chart = ChartFactory.createXYAreaChart(name, "width", "# Pixels", xyDataset, PlotOrientation.HORIZONTAL, true, false, false);

	}
	
	

	public BufferedImage getChart(int x, int y)
	{
		return chart.createBufferedImage(x, y);
	}
	
	
	public void display(int width, int height) {
		BufferedImage chartImage = chart.createBufferedImage(width, height);
		
		DisplayImage di = new DisplayImage(chartImage, name);
		di.display();
		
	}
}

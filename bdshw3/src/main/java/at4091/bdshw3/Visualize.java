package at4091.bdshw3;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

// For PCA
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;


// For Graph
import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Visualize extends JFrame {
	
	private static final long serialVersionUID = 1L;
	//ArrayList<TreeSet<Integer>> kSets;
	Matrix principalComponents;
	
	public Visualize(double[][] input, ArrayList<TreeSet<Integer>> kSets, String title) {  
		  
		//this.kSets = kSets;
		
		double[][] normMatrix = normalize(input);
		double[][] mtm = matrixMatrixTranspose(normMatrix);
		
		this.principalComponents = getPrincipalComponents(mtm);
		
		// Create dataset
		XYDataset dataset = getDataset(kSets);  
	  
	    // Create chart  
	    JFreeChart chart = ChartFactory.createScatterPlot(title, "X", "Y", dataset, PlotOrientation.HORIZONTAL, true, false, false);
	  
	    //Changes background color  
	    XYPlot plot = (XYPlot)chart.getPlot();  
	    plot.setBackgroundPaint(new Color(255,255,255));  
	      
	    // Create Panel  
	    ChartPanel panel = new ChartPanel(chart);  
	    setContentPane(panel);  
	    
	}  
	
	public Visualize(double[][] input, String title) {  
		  
		//this.kSets = kSets;
		
		double[][] normMatrix = normalize(input);
		double[][] mtm = matrixMatrixTranspose(normMatrix);
		
		this.principalComponents = getPrincipalComponents(mtm);
		
		// Create dataset
		XYDataset dataset = getDataset();  
	  
	    // Create chart  
	    JFreeChart chart = ChartFactory.createScatterPlot(title, "X", "Y", dataset, PlotOrientation.HORIZONTAL, true, false, false);
	  
	    //Changes background color  
	    XYPlot plot = (XYPlot)chart.getPlot();  
	    plot.setBackgroundPaint(new Color(255,255,255));  
	      
	    // Create Panel  
	    ChartPanel panel = new ChartPanel(chart);  
	    setContentPane(panel);  
	    
	} 
	  
	
	private Matrix getPrincipalComponents(double[][] input) {
		
		// Get Principal Components
		SparkConf conf = new SparkConf().setMaster("local").setAppName("app");
		JavaSparkContext sc = new JavaSparkContext(conf);
		
		List<Vector> data = new ArrayList<Vector>();
		for (int i = 0; i < input.length; i++ ) {	
			data.add(new DenseVector(input[i]));
		}
		
		JavaRDD<Vector> rows = sc.parallelize(data);

		// Create a RowMatrix from JavaRDD<Vector>.
		RowMatrix mat = new RowMatrix(rows.rdd());

		// Compute the top 2 principal components.
		Matrix pc = mat.computePrincipalComponents(2);
		
		sc.close();
		return pc;
		
	}

	public XYDataset getDataset(ArrayList<TreeSet<Integer>> kSets) {
			
		// Add Principal Components to XYSeries
		XYSeriesCollection dataset = new XYSeriesCollection();  
		
		for (int j = 0; j < kSets.size(); j++) {
			
			String title = "series " + String.valueOf(j + 1);
			XYSeries series = new XYSeries(title); 
			
			for (int i = 0; i < kSets.get(j).size(); i++){
				
				for(Integer val: kSets.get(j)) {
					int nextval = val.intValue();
					series.add(principalComponents.apply(nextval, 0), principalComponents.apply(nextval, 1)); 
				}	
				
			}
			
			dataset.addSeries(series); 
		}
		  
		return dataset;
	}
	
	public XYDataset getDataset() {
		
		// Add Principal Components to XYSeries
		XYSeriesCollection dataset = new XYSeriesCollection();  
			
		XYSeries series = new XYSeries("data"); 
		
		//System.out.println(principalComponents.numRows());
		
		for (int i = 0; i < 24; i++){
			
			series.add(principalComponents.apply(i, 0), principalComponents.apply(i, 1)); 
					
		}
			
		dataset.addSeries(series);
		  
		return dataset;
	}
	

	
	// Normalize vector to unit vector
	static double[][] normalize(double[][] input) {
		
		double[][] outputMatrix = new double[input.length][input[0].length];
		
		// Iterate through Rows
		for (int i = 0; i < input.length; i++) {
			
			double sumOfSquares = 0;
			
			for (int j = 0; j < input[i].length; j++) {
				sumOfSquares += input[i][j] * input[i][j];
			}
			
			double length = Math.sqrt(sumOfSquares);
			
			for (int j = 0; j < input[i].length; j++) {
				outputMatrix[i][j] = input[i][j] / length;
			}
		}
		
		return outputMatrix;
		
	}
	
	// NOT USED, this normalizes by column which is not what I want
	// Normalize by Scaling All Features to be [0,1] 
	static double[][] featureScaling(double[][] input) {
		
		double[][] outputMatrix = new double[input.length][input[0].length];
		
		// Iterate through Rows
		for (int i = 0; i < input[0].length; i++) {
			
			double max = 0;
			double min = 0;
			
			// Iterate through Columns to find Min and Max
			for (int j = 0; j < input.length; j++) {
			
				max = Math.max(max, input[j][i]);
				min = Math.min(min, input[j][i]);
			}
			
			// Calculate Scaled Values 
			double range = max - min;
			for (int j = 0; j < input.length; j++) {
				outputMatrix[j][i] = (input[j][i] - min) / range;
			}
		}
		
		return outputMatrix;
		
	}
	
	// NOT USED
	// Compute M^T * M 
	static double[][] matrixMatrixTranspose(double[][] input) {
		
		double[][] outputMatrix = new double[input.length][input.length];
		
		// Iterate through Rows
		for (int i = 0; i < input.length; i++) {
			
			for (int j = i; j < input.length; j++) {
			
				double sumOfProducts = 0;
				
				for (int k = 0; k < input[0].length; k++) {
					
					sumOfProducts += input[i][k] * input[j][k];
				}

				outputMatrix[i][j] = sumOfProducts;
				outputMatrix[j][i] = sumOfProducts;
			}
				
		}
		return outputMatrix;
	}
	
}

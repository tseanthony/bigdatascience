package at4091.bdshw3;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;



public class App 
{
   
	static String simOption;
	
	public static void main( String[] args ) throws URISyntaxException, IOException
    {
		
		simOption = "";
    	try {
    		switch (args[0]) {
    		case "-e": 
    			simOption = args[0];
    			break;
    		case "-c" : 
    			simOption = args[0];
    			break;
    		default: 
    			throw new Exception();
    		}
    	} catch(Exception e) {
    		System.out.println("Invalid Similarity Measure");
    	}
		
		String testFolderName = args[1];
    	ArrayList<String> KNNFileList = new ArrayList<String>();
   
    	try {
    		KNNFileList = getFileList(testFolderName);
    	} catch(Exception e) {
    		System.out.println("Could Not Open File");
    	}
    	
    	// Get Directory for Input and Output
    	File currFile = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    	File TestPath = currFile.getParentFile().getParentFile();

    	File inputDirectory = new File(TestPath, "data");
    	String inputPath = inputDirectory.toString();
    	
    	File outputDirectory = new File(TestPath, "output");
    	String outputPath = outputDirectory.toString();
    	
    	// Delete previous Files
    	for(File outputFolder: outputDirectory.listFiles()) {
    		if (outputFolder.isDirectory()) {
    			for (File stopWordFiles: outputFolder.listFiles()) {
    				stopWordFiles.delete();
    			}
    			outputFolder.delete();
    		} else {
    			outputFolder.delete();
    		}
    	} 
    	
    	File stopWordFile = new File(TestPath, "CoreNLP_StopWords.txt");
    	
    	ArrayList<String> inputFileList = getFileListFromFolder(inputPath);
    	
    	// Instance of StopWordFilter Class with Name of Stop File Text File
    	StopWordFilter swf = new StopWordFilter(stopWordFile);
    	
    	// Create Stop Word Removed Files for Topic Generation
    	for (int i = 0; i < inputFileList.size(); i++) {
    		File currInput = new File(inputFileList.get(i));
        	File currOutput = createOutputTree(inputFileList.get(i), outputPath);
        	swf.remove(currInput, currOutput);
    	}
        	
    	// Get List of Stop-Word-Removed Documents
    	ArrayList<String> outFileList = getFileListFromFolder(outputPath);
    	
    	// Pre-Process Document (Token, Pos ,Lemma, NER) and add to TreeMap for TOPICS ONLY
    	Preprocessor pp = new Preprocessor();
    	TreeMap<String, HashMap<String, Integer>> dtmArrayModel = new TreeMap<String, HashMap<String, Integer>>();
    	
    	for (int k = 0; k < outFileList.size(); k++) { 
    		pp.process(outFileList.get(k));
    		HashMap<String, Integer> nGramMapforModel = pp.createNGramNoNER(3, 1, 2);
    		dtmArrayModel.put(outFileList.get(k) , nGramMapforModel);
    	}
    	    	
    	// Create TF-IDF
    	DocumentTermMatrix modelTermsDTM = new DocumentTermMatrix(dtmArrayModel);
    	double[][] tfidf = modelTermsDTM.termFreqInvDocFreq();
    	
    	// Delete Stop-Word Files
    	for(File outputFolder: outputDirectory.listFiles()) {
    		if (outputFolder.isDirectory()) {
    			for (File stopWordFiles: outputFolder.listFiles()) {
    				stopWordFiles.delete();
    			}
    			outputFolder.delete();
    		}
    	} 
    	
    	/* 
    	 * END OF HW1
    	 */
    	
    	ArrayList<TreeSet<Integer>> cluster = new ArrayList<TreeSet<Integer>>();
    	switch(simOption) {
			case "-e": 
				cluster = Cluster.kMeansEuclidean(3, tfidf);   
				break;
			case "-c" : 
				cluster = Cluster.kMeansCosine(3, tfidf);
				break;
			default: 
				break;
		}
    	
    	
    	    	
    	// Create Stop Word Removed Files for KNN input
    	for (int i = 0; i < KNNFileList.size(); i++) {
    		File currInput = new File(KNNFileList.get(i));
        	File currOutput = createOutputTree(KNNFileList.get(i), outputPath);
        	swf.remove(currInput, currOutput);
    	}
    	
    	// document word vectors
    	TreeMap<String, HashMap<String, Integer>> dtmforKNN = new TreeMap<String, HashMap<String, Integer>>();
    	outFileList = getFileListFromFolder(outputPath);
    	for (int k = 0; k < outFileList.size(); k++) { 
    		pp.process(outFileList.get(k));
    		HashMap<String, Integer> nGramMapforModel = pp.createNGramNoNER(3, 1, 2);
    		dtmforKNN.put(outFileList.get(k), nGramMapforModel);
    	}
    	
    	// create dtm matrix for input files.
    	double[][] inputTFIDF = modelTermsDTM.KNNInputTFIDF(dtmforKNN);
    	
    	knearestneighbor(3, inputTFIDF, tfidf, cluster);
    	
    	// Delete Stop-Word Files
    	for(File outputFolder: outputDirectory.listFiles()) {
    		if (outputFolder.isDirectory()) {
    			for (File stopWordFiles: outputFolder.listFiles()) {
    				stopWordFiles.delete();
    			}
    			outputFolder.delete();
    		} else {
    			outputFolder.delete();
    		}
    	} 
    	
    
	}
	
	static void knearestneighbor(int kneighbors, double[][] inputTFIDF, double[][] clustertfidf, ArrayList<TreeSet<Integer>> label2doc) {
		
		int[] inputclassification = new int[inputTFIDF.length];
		HashMap <Integer, Integer> res = clusterReverseMap(label2doc);
		
		for (int i = 0; i < inputTFIDF.length; i++) {
			
			HashMap<Integer, Double>  docdistancearr = new HashMap <Integer,Double>();
			
			// get distance/similarity of all clustered document to document to be classified
			for (int j = 0; j < clustertfidf.length; j++) {
				
				double dist = 0;
				if(simOption.equals("-e")) {
					dist = Cluster.euclideanDistance(inputTFIDF[i], clustertfidf[j]);		
				} else if (simOption.equals("-c")) {
					dist = Cluster.cosineSimilarity(inputTFIDF[i], clustertfidf[j]);
				} 
				docdistancearr.put(new Integer(j), new Double(dist));	
				
			}
			
			// sort distances by distance/similarity
			ArrayList<Entry<Integer,Double>> sortedList = new ArrayList<Entry<Integer,Double>>(docdistancearr.entrySet());
			
			Comparator<Double> revCompare = new Comparator<Double>() {
				@Override
				public int compare(Double o1, Double o2) {
					return o2.compareTo(o1);
				}
	        };
	        			
			switch(simOption) {
			case("-e"):
				sortedList.sort(Entry.comparingByValue());
				break;
			case("-c"):
				sortedList.sort(Entry.comparingByValue(revCompare));
				break;
			default:
				break;
			}
			
			// count votes, index = class
			int[] kneighborvotes = new int[label2doc.size()];
			for (int k = 0; k < kneighbors; k++) {
				kneighborvotes[ res.get( sortedList.get(k).getKey() )] += 1; 
			}
			
			// classify document, index = class
			int maxindex = 0;
			int maxcount = 0;
			for (int q = 0; q < kneighborvotes.length; q++) {
				if (kneighborvotes[q] > maxcount) {
					maxindex = q;
					maxcount = kneighborvotes[q];
				}
			}
			
			inputclassification[i] = maxindex;
						
		}
		
		for (int i = 0; i < inputclassification.length; i++) {
			System.out.println(inputclassification[i] + 1);
		}

	}
	
	// create dictionary to find cluster by document
	static HashMap <Integer, Integer> clusterReverseMap(ArrayList<TreeSet<Integer>> cluster){
    	
		HashMap <Integer, Integer> res = new HashMap <Integer, Integer>();

		for (int i = 0; i < cluster.size(); i++ ) {
    		for (Integer doc: cluster.get(i)) {
    			res.put(doc, new Integer(i));
    		}
    	}
    	
    	return res;
    	
    	
	}
    
    static void printDoubleMatrix(double[][] input) {
    	for (int i = 0; i < input.length; i++) {
    		Formatter out = new Formatter(new StringBuilder(), Locale.US);
    		for (int j = 0; j < input[0].length; j++) {    			
    			out.format("%5.3f", input[i][j]);

    		}
    		System.out.println(out);
    		out.close();
    	}
    	
    	System.out.println();		 		
    }
    

    // Returns file array of text files.
	static ArrayList<String> getFileList(String inputPath) throws IOException 
	{
		
		ArrayList<String> fileList = new ArrayList<String>();;
		File dataFile = new File(inputPath);
			
		File[] dataTextFiles = dataFile.listFiles();
		for (int j = 0; j < dataTextFiles.length; j++) {
			if (!dataTextFiles[j].equals(".DS_Store")) {
				fileList.add(dataTextFiles[j].toURI().toString().substring(5));
			}
		} 
    	
	    return fileList;
	}
	
	static ArrayList<String> getFileListFromFolder(String inputPath) throws IOException 
	{
		
		ArrayList<String> fileList = new ArrayList<String>();;
		File dataFile = new File(inputPath);
		
		File[] dataSubDir = dataFile.listFiles();
	    for (int i = 0; i < dataSubDir.length; i++) {
	    	
	    	String dataSubDirName = dataSubDir[i].getName();
	    	if (!dataSubDirName.equals(".DS_Store") && dataSubDir[i].isDirectory()) {
	    			
				File[] dataTextFiles = dataSubDir[i].listFiles();
				for (int j = 0; j < dataTextFiles.length; j++) {
					if (!dataSubDirName.equals(".DS_Store")) {
						
						fileList.add(dataTextFiles[j].toURI().toString().substring(5));
							
					}
				} 
	    	}
	    }
	    return fileList;
	}
	
	// Returns a output File
	static File createOutputTree(String inputPath, String outputPath) throws IOException 
	{
    	File currInput = new File(inputPath);
    	String outputFolder = currInput.getParentFile().getName();
    	File outputDir = new File(outputPath, outputFolder);
    	outputDir.mkdir();
    	File outputTextFile = new File(outputDir, currInput.getName());
    	outputTextFile.delete();
    	outputTextFile.createNewFile();
    	
    	return outputTextFile;
	}
	
   

    	
    
}

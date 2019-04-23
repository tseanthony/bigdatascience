package at4091.bdshw1;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class App 
{
	
    public static void main( String[] args ) throws IOException, URISyntaxException 
    {
    	// Get Directory for Input and Output
    	File currFile = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    	File TestPath = currFile.getParentFile().getParentFile();

    	File inputDirectory = new File(TestPath, "data");
    	String inputPath = inputDirectory.toString();
    	
    	File outputDirectory = new File(TestPath, "output");
    	String outputPath = outputDirectory.toString();
    	 	
    	File stopWordFile = new File(TestPath, "CoreNLP_StopWords.txt");
    	
    	ArrayList<String> inputFileList = getFileList(inputPath);
    	
    	// Instance of StopWordFilter Class with Name of Stop File Text File
    	StopWordFilter swf = new StopWordFilter(stopWordFile);
    	
    	// Create Stop Word Files
    	for (int i = 0; i < inputFileList.size(); i++) {
    		File currInput = new File(inputFileList.get(i));
        	File currOutput = createOutputFile(inputFileList.get(i), outputPath);
        	swf.remove(currInput, currOutput);
    	}
    	
    	// Get List of Stop-Word-Removed Documents
    	ArrayList<String> outFileList = getFileList(outputPath);
    	
    	// Pre-Process Document (Token, Pos ,Lemma, NER) and add to TreeMap for TOPICS ONLY
    	Preprocessor pp = new Preprocessor();
    	
    	TreeMap<String, HashMap<String, Integer>> dtmArrayTopics = new TreeMap<String, HashMap<String, Integer>>();
    	TreeMap<String, HashMap<String, Integer>> dtmArrayModel = new TreeMap<String, HashMap<String, Integer>>();
    	
    	for (int k = 0; k < outFileList.size(); k++) { 
    		pp.process(outFileList.get(k));
    		HashMap<String, Integer> nGramMapforTopics = pp.createNGram(4, 2, 2);
    		HashMap<String, Integer> nGramMapforModel = pp.createNGram(3, 1, 2);
    		
//    		HashMap<String, Integer> nGramMapforTopics = pp.createNGramNoNER(4, 2, 2);
//    		HashMap<String, Integer> nGramMapforModel = pp.createNGramNoNER(3, 1, 2);
    	
    		dtmArrayTopics.put(outFileList.get(k) , nGramMapforTopics);
    		dtmArrayModel.put(outFileList.get(k) , nGramMapforModel);
    	}
    	
    	// Print Topics from Stop Word and Pre-Process (NER + n-gram)
    	DocumentTermMatrix topicsDTM = new DocumentTermMatrix(dtmArrayTopics);
    	topicsDTM.printTopics(new File(outputPath));
    	
    	System.out.println("-----------------------------------");
    	
    	// Perform LDA to get topics
    	MalletLDA lda = new MalletLDA(stopWordFile);
    	lda.printTopics(new File(outputPath));
    	
    	System.out.println("-----------------------------------");
    	
    	// Create TF-IDF
    	DocumentTermMatrix modelTermsDTM = new DocumentTermMatrix(dtmArrayModel);
    	double[][] tfidf = modelTermsDTM.termFreqInvDocFreq();
    	
    	
    	// UNCOMMENT TO PRINT SCATTER GRAPH - ALL DATA
    	SwingUtilities.invokeLater(() -> {  
  		  Visualize graph = new Visualize(tfidf, "No Clusters"); 
  	      graph.setSize(800, 400);  
  	      graph.setLocationRelativeTo(null);  
  	      graph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
  	      graph.setVisible(true);  
  	    });

    	System.out.println("-----------------------------------");
    	
    	
    	ArrayList<TreeSet<Integer>> clustersE = Cluster.kMeansEuclidean(3, tfidf);   	
    	// UNCOMMENT TO PRINT SCATTER GRAPH - Euclidean Distance
    	SwingUtilities.invokeLater(() -> {  
    		  Visualize graph = new Visualize(tfidf, clustersE, "PCA, Cluster by Euclidean"); 
    	      graph.setSize(800, 400);  
    	      graph.setLocationRelativeTo(null);  
    	      graph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
    	      graph.setVisible(true);  
    	    }); 
    	
    	// Constructor prints confusion matrix
    	@SuppressWarnings("unused")
    	Evaluation euclideanConfusionMatrix = new Evaluation(clustersE);
    	
    	System.out.println("-----------------------------------");
    	
    	ArrayList<TreeSet<Integer>> clustersC = Cluster.kMeansCosine(3, tfidf);
    	// UNCOMMENT TO PRINT SCATTER GRAPH - Cosine Similarity
    	SwingUtilities.invokeLater(() -> {  
  		  Visualize graph = new Visualize(tfidf, clustersC, "PCA, Cluster by Cosine"); 
  	      graph.setSize(800, 400);  
  	      graph.setLocationRelativeTo(null);  
  	      graph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
  	      graph.setVisible(true);  
  	    }); 
    	
    	// Constructor prints confusion matrix
    	@SuppressWarnings("unused")
		Evaluation cosineConfusionMatrix = new Evaluation(clustersC);

    	System.out.println("-----------------------------------");
    	 
    	// Delete Stop-Word Folders
    	for(File outputFolder: outputDirectory.listFiles()) {
    		if (outputFolder.isDirectory()) {
    			for (File stopWordFiles: outputFolder.listFiles()) {
    				stopWordFiles.delete();
    			}
    			outputFolder.delete();
    		}
    	}
    	
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
	static File createOutputFile(String inputPath, String outputPath) throws IOException 
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
	    	
 


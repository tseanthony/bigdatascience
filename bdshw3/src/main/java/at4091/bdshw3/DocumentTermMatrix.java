package at4091.bdshw3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;



public class DocumentTermMatrix {
	
	TreeMap<String, HashMap<String, Integer>> localTermFreq;		// input from preprocessor
	HashMap<String, Double> globalTermFreq;							// number of times word appears in a Doc
	
	ArrayList<String> fileList;										
	ArrayList<String> wordList;										
	double[] docWordTotal;
	double[] invDocFreq;								

											
	public DocumentTermMatrix(TreeMap<String, HashMap<String, Integer>> inputArray) {
		
		this.localTermFreq = inputArray;
	
		this.fileList = new ArrayList<String>(inputArray.keySet());
		Collections.sort(fileList); 
		
		this.docWordTotal = new double[fileList.size()];
		Arrays.fill(this.docWordTotal, 0.0);
		
		this.globalTermFreq = this.makeGlobalTermFreq(inputArray);
		
		this.globalTermFreq.entrySet().removeIf(entries->entries.getValue() <= 1);
		
		this.wordList = new ArrayList<String>(this.globalTermFreq.keySet());
		Collections.sort(wordList);
		
		this.invDocFreq = this.makeInvDocFreq();;
		
				
	}

	//PRIVATE METHODS
	
	//Print Topics. sorts number of words from highest to lowest appearance.
	public void printTopics(File outputDirectory) throws IOException {
		printTopicsHelper(this.globalTermFreq, outputDirectory);
		//System.out.format("\n%35s  %5d\n", "NUMBER_OF_TERMS", this.globalTermFreq.size());
		
	}
	
	private void printTopicsHelper(HashMap<String, Double> input, File outputDirectory) throws IOException {
		
		ArrayList<Entry<String, Double>> sortedList = new ArrayList<Entry<String, Double>>(input.entrySet());
		
		Comparator<Double> revCompare = new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return (int) (o2 - o1);
			}
        };
		
		sortedList.sort(Entry.comparingByValue(revCompare));
		
		File outputFile = new File(outputDirectory, "topics.txt");
		outputFile.delete();
		outputFile.createNewFile();
		
		FileWriter f = new FileWriter(outputFile, true);
		
		System.out.format("%35s      %s\n\n", "TERMS", "DOC_FREQUENCY");
		
		Formatter titleFormatter = new Formatter(new StringBuilder(), Locale.US);
		titleFormatter.format("%35s      %s\n\n", "TERMS", "DOC_FREQUENCY");
		f.write(titleFormatter.toString());
		
		
		for(int i = 0; i < sortedList.size(); i++) {
			
			String word = sortedList.get(i).getKey();
			Double count = sortedList.get(i).getValue();
			
			// Print to Terminal
			System.out.format("%35s  %5.0f\n", word, count);
			
			// Write to File
			Formatter out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%35s  %5.0f\n", word, count);
			f.write(out.toString());
			out.close();
			
		}
		
		titleFormatter.close();
		f.close();
	}
	
	public void printTopics() {
		printTopicsHelper(this.globalTermFreq);
		System.out.format("\n%35s  %5d\n", "NUMBER_OF_TERMS", this.globalTermFreq.size());
	}
	
	private void printTopicsHelper(HashMap<String, Double> input) {

		ArrayList<Entry<String, Double>> sortedList = new ArrayList<Entry<String, Double>>(input.entrySet());
		
		Comparator<Double> revCompare = new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return (int) (o2 - o1);
			}
        };
		
		sortedList.sort(Entry.comparingByValue(revCompare));
		
		System.out.format("%35s      %s\n\n", "TERMS", "DOC_FREQUENCY");
		for(int i = 0; i < sortedList.size(); i++) {
			String word = sortedList.get(i).getKey();
			Double count = sortedList.get(i).getValue();
			System.out.format("%35s  %5.0f\n", word, count);
		}
	}
	
	
	// Construct Dictionary, Count Number of Words in Each Document
	private HashMap<String, Double> makeGlobalTermFreq(TreeMap<String, HashMap<String, Integer>> inputMapMap) {
		
		HashMap<String, Double> outputMap = new HashMap<String, Double>();
		
		for (int i = 0; i < this.fileList.size(); i++) {
		
			HashMap<String, Integer> termFreq = inputMapMap.get(fileList.get(i));
			HashSet<String> wordList = new HashSet<String>(termFreq.keySet());
			
			for (String key: wordList) {
			
				// Count number of words in each document.
				this.docWordTotal[i] += (double) termFreq.get(key);
				
				// Update global word count.
				if (outputMap.containsKey(key)) {
					
					Double count = outputMap.get(key);
					outputMap.put(key, new Double(count + 1.0));

				} else {
			
					Double count = new Double(1);	
					outputMap.put(key, count);		
				
				}
			}
		} 
		return outputMap;	
	}
	
	
	
	// Compute Inverse Document Frequency (Log (Total # Documents / # Documents word appears in))
	private double[] makeInvDocFreq() {
		
		double[] output = new double[this.wordList.size()];
		Arrays.fill(output, 0.0);
		
		//Double Loop to fill values
		for(int i = 0; i < this.wordList.size(); i++) {
					
			double docCountDoub = (double) fileList.size();
			Double wordDocCount = this.globalTermFreq.get(wordList.get(i));
			output[i] =  Math.log(docCountDoub/wordDocCount.doubleValue());
		}
		
		
		
		return output;
	}
	
	//PUBLIC METHODS

	// Returns Matrix with Word Counts within document
	public double[][] docTermMatrix() {
		
		double[][] output = new double[this.fileList.size()][this.wordList.size()];
		
		for(double[] rows : output) {
			Arrays.fill(rows, 0.0);
		}
		
		for(int i = 0; i < output.length; i++) {
			
			String docName = this.fileList.get(i);
			Map<String, Integer> currDoc = this.localTermFreq.get(docName);
			
			for (int j = 0; j < output[i].length; j++ ) {
				
				if (currDoc.containsKey(this.wordList.get(j))) {
					
					Integer countInDoc = currDoc.get(wordList.get(j));
					output[i][j] = countInDoc.doubleValue(); 
					
				}
			}
		}	
		
		return output;
	}
	
	// Return Matrix with Term Frequency (# of Occurrences within Document / Number of Words in Document) 
	public double[][] docTermFreqMatrix() {
		
		double[][] output = new double[this.fileList.size()][this.wordList.size()];
		for(double[] rows : output) {
			Arrays.fill(rows, 0.0);
		}
		
		for(int i = 0; i < output.length; i++) {
			
			String docName = this.fileList.get(i);
			Map<String, Integer> currDoc = this.localTermFreq.get(docName);
			
			for (int j = 0; j < output[i].length; j++ ) {
				
				if (currDoc.containsKey(this.wordList.get(j))) {
					
					double countInDoc = currDoc.get(wordList.get(j)).doubleValue();
					double termFreq = countInDoc / this.docWordTotal[i];
					output[i][j] = termFreq;	
				}		
			}
		}	
		
		return output;
	}
	
	// Returns TF-IDF
	public double[][] termFreqInvDocFreq() {
		
		double[][] output = new double[this.fileList.size()][this.wordList.size()];
		for(double[] rows : output) {
			Arrays.fill(rows, 0.0);
		}

		for(int i = 0; i < output.length; i++) {
			
			String docName = this.fileList.get(i);
			Map<String, Integer> currDoc = this.localTermFreq.get(docName);
			
			for (int j = 0; j < output[i].length; j++ ) {
				
				if (currDoc.containsKey(this.wordList.get(j))) {
					
					double countInDoc = currDoc.get(wordList.get(j)).doubleValue();
					double termFreq = countInDoc / this.docWordTotal[i];
					
					double termFreqInvDocFreq = termFreq * this.invDocFreq[j];
					output[i][j] = termFreqInvDocFreq;
						
				}		
			}
		}
		
		return output;
	}
	
	// Returns TF-IDF
	public double[][] KNNInputTFIDF(TreeMap<String, HashMap<String, Integer>> dtmforKNN) {
		
		ArrayList<String> KNNfileList = new ArrayList<String>(dtmforKNN.keySet());
		double[][] output = new double[KNNfileList.size()][this.wordList.size()];
		
		for(double[] rows : output) {
			Arrays.fill(rows, 0.0);
		}

		for(int i = 0; i < output.length; i++) {
			
			String docName = KNNfileList.get(i);
			Map<String, Integer> currDoc = dtmforKNN.get(docName);
			
			for (int j = 0; j < output[i].length; j++ ) {
				
				if (currDoc.containsKey(this.wordList.get(j))) {
					
					double countInDoc = currDoc.get(wordList.get(j)).doubleValue();
					double termFreq = countInDoc / countDocumentWords(currDoc);
					
					double termFreqInvDocFreq = termFreq * this.invDocFreq[j];
					output[i][j] = termFreqInvDocFreq;
						
				}		
			}
		}
		
		return output;
	}
	
	private double countDocumentWords (Map<String, Integer> document) {
		double res = 0;
		
		HashSet<String> wordList = new HashSet<String>(document.keySet());
		for (String key: wordList) {
			// Count number of words in document.
			res += (double) document.get(key);
			
		}
		return res;
	}
	
	
}



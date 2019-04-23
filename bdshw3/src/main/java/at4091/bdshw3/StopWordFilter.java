package at4091.bdshw3;

import java.util.HashSet;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;


public class StopWordFilter {
	
	HashSet<String> stopWords;
	
	public StopWordFilter (File stopWordFile) throws URISyntaxException {
		this.stopWords = getStopWords(stopWordFile);
	}
	
	private HashSet<String> getStopWords(File stopWordFile) throws URISyntaxException {
		
    	File dataFile = stopWordFile;
		
		HashSet<String> stopWords = new HashSet<String>();
		
		try {
			Scanner sc = new Scanner(dataFile);
			while (sc.hasNext()) {
				stopWords.add(sc.next());
		    }
			sc.close();
			
		} catch (IOException e) {
			System.out.println("StopWordFilter: Stop Word File Not Found");
			e.printStackTrace();
		}	
		
		return stopWords;
		
	}
	
		
	
	public void remove(File inputFile, File outputFile) {
		
		outputFile.delete();
		
		try {
			
			outputFile.createNewFile();
			Scanner sc = new Scanner(new File(inputFile.toString()));
			FileWriter f = new FileWriter(outputFile);
			
			while (sc.hasNext()) {
				String nextWord = sc.next();
				
				//This will remove all numbers as well.
				String cleanNextWord = nextWord.replaceAll("[\\[\\]]*[\\d]*[\"\']*[,:;-]*[\\)\\(]*", "");
				
				if (!this.stopWords.contains(cleanNextWord.toLowerCase())) {
					f.write(cleanNextWord + " ");
				}
			}
			f.close(); sc.close();
			
		} catch (Exception e) {
			System.out.println("StopWordFilter: File Not Found");
			e.printStackTrace();
		}	
	}
	
}

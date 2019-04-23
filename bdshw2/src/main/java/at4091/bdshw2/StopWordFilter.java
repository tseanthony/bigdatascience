package at4091.bdshw2;

import java.util.HashSet;
import java.util.Scanner;
import java.io.File;
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
	
		
	public String remove(String sentence) {
		
		StringBuilder clean = new StringBuilder();
		
		Scanner sc = new Scanner(sentence);
		
		while (sc.hasNext()) {
			String nextWord = sc.next();
			
			// Remove Unicode
			String nextWord1 = nextWord.replaceAll("[^\\x00-\\x7F]", "");
			
			//This will remove all numbers as well.
			String cleanNextWord = nextWord1.replaceAll("[\\[\\]]*[\\d]*[\"\']*[,:;-]*[\\)\\(]*", "");
			
			if (!this.stopWords.contains(cleanNextWord.toLowerCase())) {
				clean.append(cleanNextWord + " ");
			}
		}
		
		sc.close();
			
		return clean.toString();
		
	}

}

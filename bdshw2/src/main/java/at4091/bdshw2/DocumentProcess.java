package at4091.bdshw2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.EnglishGrammaticalRelations;

enum Sentiment{
	POSITIVE, NEGATIVE; 
}

public class DocumentProcess {

	HashMap<String, Integer> negFrequency;
	HashMap<String, Integer> posFrequency;
	
	HashMap<String, Integer> negFrequency2;
	HashMap<String, Integer> posFrequency2;
	
	HashMap<String, Integer> negFrequency3;
	HashMap<String, Integer> posFrequency3;
	
	HashMap<String, Integer> negFrequency4;
	HashMap<String, Integer> posFrequency4;
	
	public DocumentProcess() {
		
	    negFrequency = new HashMap<String, Integer>();
	    posFrequency = new HashMap<String, Integer>();
	    
	    negFrequency2 = new HashMap<String, Integer>();
	    posFrequency2 = new HashMap<String, Integer>();
	    
	    negFrequency3 = new HashMap<String, Integer>();
	    posFrequency3 = new HashMap<String, Integer>();
	    
	    negFrequency4 = new HashMap<String, Integer>();
	    posFrequency4 = new HashMap<String, Integer>();
	    
	    	
	}
	

	public void processMentions(CoreDocument document, Sentiment type, char character) {
		
		switch (type) 
		{
		case NEGATIVE:
			processMentionsHelper(document, negFrequency, character);
			break;
		default:
			processMentionsHelper(document, posFrequency, character);
			break;
		}
	}
	
	public void processMentionsHelper(CoreDocument document, HashMap<String, Integer> mentionFreq, char character) {
		
		List<CoreLabel> SentenceTokens = document.sentences().get(0).tokens();
		for (int i = 0; i < SentenceTokens.size(); i++) {
			String word = SentenceTokens.get(i).word();

			if (word.charAt(0) == character) {
	
				mentionFreq.merge(word, 1, Integer::sum);
			} 	
		}
				
	}
	
	public void processNGramNoun(CoreDocument document, Sentiment type, boolean noun) {
		
		switch (type) 
		{
		case NEGATIVE:
			processNGramNounHelper(document, negFrequency,negFrequency2,negFrequency3,negFrequency4, 4, noun);
			break;
		default:
			processNGramNounHelper(document, posFrequency,posFrequency2,posFrequency3,posFrequency4, 4, noun);
			break;
		}	
	}
	
	public void processNGramNounHelper(CoreDocument document
										, HashMap<String, Integer> frequencyMap 
										, HashMap<String, Integer> frequencyMap1
										, HashMap<String, Integer> frequencyMap2
										, HashMap<String, Integer> frequencyMap3
										, int maxSize, boolean noun) {
		
		int dynamicMaxSize = maxSize;			// To prevent array index errors
			
		// Store Nouns in List
		List<CoreLabel> SentenceTokens = document.sentences().get(0).tokens();
		List<String> partOfSpeechTags = document.sentences().get(0).posTags();
		List<String> nounArray = new ArrayList<String>();
		

		for (int k = 0; k < partOfSpeechTags.size(); k++) {
			String pos = partOfSpeechTags.get(k);
			
			// Inefficient but I don't have time.
			if (noun) {
				if (pos != null && (pos.equals("NN") || pos.equals("NNS"))) {
					nounArray.add(SentenceTokens.get(k).word());
				} 
			} else {
				nounArray.add(SentenceTokens.get(k).word());
			}
			
		}
		
		
		// Token in Noun Array
		for (int j = 0; j < nounArray.size(); j++) {
			
			StringBuilder word = new StringBuilder(32);
			
			// Prevent indexing out of bounds
			if (j + dynamicMaxSize >= nounArray.size()) {
				dynamicMaxSize = nounArray.size() - j;
			}
			
			// n gram size
			for (int k = 0; k < dynamicMaxSize; k++) {
				
				String nextWord = nounArray.get(j + k).toLowerCase();
									
				if (nextWord.equals(".") 
						|| nextWord.equals("...") 
						|| nextWord.equals("/") 
						|| nextWord.equals("!")
						|| nextWord.equals("?")
						|| nextWord.equals("&")
						|| nextWord.equals("*")) {
					break;
				}
				
				word.append(nextWord + " ");

				String wordStr = word.toString();
				
				switch (k) {
				case 0:
					frequencyMap.merge(wordStr, 1, Integer::sum);
					break;
				case 1:
					frequencyMap1.merge(wordStr, 1, Integer::sum);
					break;
				case 2:
					frequencyMap2.merge(wordStr, 1, Integer::sum);
					break;
				case 3: 
					frequencyMap3.merge(wordStr, 1, Integer::sum);
					break;
				default:
					break;
				}
					
			} // Inner Loop	
		} // Center Loop
		dynamicMaxSize = maxSize;

				
	}
	
	public void processNGramPatternPOS(CoreDocument document, Sentiment type, ArrayList<String> pattern) {
		
		switch (type) 
		{
		case NEGATIVE:
			createNGramPatternPOShelper(document, negFrequency, pattern);
			break;
		default:
			createNGramPatternPOShelper(document, posFrequency, pattern);
			break;

		}	
	}
	
	public void createNGramPatternPOShelper(CoreDocument document, HashMap<String, Integer> frequencyMap, ArrayList<String> pattern) {
		
		int dynamicMaxSize = pattern.size();			// To prevent array index errors
		
		List<CoreLabel> SentenceTokens = document.sentences().get(0).tokens();
		List<String> partOfSpeechTags = document.sentences().get(0).posTags();
		
		// Token in CoreSentence
		for (int j = 0; j < SentenceTokens.size(); j++) {
			
			StringBuilder word = new StringBuilder(32);
			int wordCount = 0;
	
			// Prevent indexing out of bounds
			if (j + dynamicMaxSize >= SentenceTokens.size()) {
				dynamicMaxSize = SentenceTokens.size() - j;
			}
			
			// n gram size
			for (int k = 0; k < dynamicMaxSize; k++) {
				
				String nextWord = SentenceTokens.get(j + k).word();
				
				// Update Named Entity, Break out of loop if k > 0 && n-grams do not match
				String currWordPOS = partOfSpeechTags.get(j + k);
				if ( !currWordPOS.contentEquals(pattern.get(k)) ) {
					break;
				} 
				
				word.append(nextWord);
				word.append(" ");
				wordCount++;
				
				String wordStr = word.toString().toLowerCase();
				
				if (wordCount == pattern.size()) {
					frequencyMap.merge(wordStr, 1, Integer::sum);
				}			
			} 
		}
	}
	
	public void processNGramDependencyParser(CoreDocument document, Sentiment type) {
		
		switch (type) 
		{
		case NEGATIVE:
			processNGramDependencyParseHelper(document, negFrequency);
			break;
		default:
			processNGramDependencyParseHelper(document, posFrequency);
			break;
		}	
	}
	
	public void processNGramDependencyParseHelper(CoreDocument document, 
										HashMap<String, Integer> frequencyMap) {
		
		SemanticGraph semGraph = document.sentences().get(0).dependencyParse();
		Collection<IndexedWord> rootCollection = semGraph.getRoots();
		
		for (IndexedWord root : rootCollection) {
			
			IndexedWord nsubj = semGraph.getChildWithReln(root, EnglishGrammaticalRelations.NOMINAL_SUBJECT);
			IndexedWord dobj = semGraph.getChildWithReln(root, EnglishGrammaticalRelations.DIRECT_OBJECT);
			
			if (nsubj != null && dobj != null) {
				
				String phrase = nsubj.word().toLowerCase() + " " + root.word().toLowerCase() + " " + dobj.word().toLowerCase();
				
				frequencyMap.merge(phrase, 1, Integer::sum);
				
			}
		}
	}
	
	public void processNGramNER(CoreDocument document, Sentiment type) {
		
		switch (type) 
		{
		case NEGATIVE:
			processNGramNERHelper(document, negFrequency,negFrequency2,negFrequency3,negFrequency4);
			break;
		case POSITIVE:
			processNGramNERHelper(document, posFrequency,posFrequency2,posFrequency3,posFrequency4);
			break;
		}
	}
	
	public void processNGramNERHelper(CoreDocument document, HashMap<String, Integer> frequencyMap,
			HashMap<String, Integer> frequencyMap1,
			HashMap<String, Integer> frequencyMap2,
			HashMap<String, Integer> frequencyMap3) {
		

		int maxsize = 4;
		int dynamicMaxSize = maxsize;			// To prevent array index errors
		
		List<CoreLabel> SentenceTokens = document.sentences().get(0).tokens();
	
		// Token in CoreSentence
		for (int j = 0; j < SentenceTokens.size(); j++) {
			
			StringBuilder word = new StringBuilder(32);
			
			// Prevent indexing out of bounds
			if (j + dynamicMaxSize >= SentenceTokens.size()) {
				dynamicMaxSize = SentenceTokens.size() - j;
			}
			
			// n gram size
			for (int k = 0; k < dynamicMaxSize; k++) {
				
				String nextWord = SentenceTokens.get(j + k).word();
				
				if (nextWord.equals(".") 
						|| nextWord.equals("...") 
						|| nextWord.equals("/") 
						|| nextWord.equals("!")
						|| nextWord.equals("?")
						|| nextWord.equals("&")) {
					break;
				}
				
				String currNamedEntity = SentenceTokens.get(j + k).ner();
				
				if (currNamedEntity.length() == 1) { break;}
								
				word.append(nextWord);
				word.append(" ");
				
				String wordStr = word.toString().toLowerCase();
				
				switch (k) {
				case 0:
					frequencyMap.merge(wordStr, 1, Integer::sum);
					break;
				case 1:
					frequencyMap1.merge(wordStr, 1, Integer::sum);
					break;
				case 2:
					frequencyMap2.merge(wordStr, 1, Integer::sum);
					break;
				case 3: 
					frequencyMap3.merge(wordStr, 1, Integer::sum);
					break;
				default:
					break;
				}		
			} 
		}			
	}
		
	public void print(String title, int printCount,HashMap<String, Integer> negMap, HashMap<String, Integer> posMap ) {
			
		posMap.remove("$");
		posMap.remove("#");
		posMap.remove("&");
		
		negMap.remove("$");
		negMap.remove("#");
		negMap.remove("&");
		
		Comparator<Integer> countCompare = new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return (int) (o2 - o1);
			}
        };
		
        // Sort All Lists by Frequency
		ArrayList<Entry<String, Integer>> negList = new ArrayList<Entry<String, Integer>>(negMap.entrySet());
		ArrayList<Entry<String, Integer>> posList = new ArrayList<Entry<String, Integer>>(posMap.entrySet());
        
		negList.sort(Entry.comparingByValue(countCompare));
		posList.sort(Entry.comparingByValue(countCompare));
		
		System.out.printf("\n%s: Negative\n", title);
		
		if (!negList.isEmpty()) {
			int maxcountneg = (printCount < negList.size()) ? printCount : negList.size();
			for (int i = 0; i < maxcountneg; i++) {
				System.out.printf("%-40s%d\n", negList.get(i).getKey(), negList.get(i).getValue());
			} 
		}
		System.out.printf("\n%s: Positive\n", title);
		if (!posList.isEmpty()) {
			int maxcountpos = (printCount < posList.size()) ? printCount : posList.size();
			for (int i = 0; i < maxcountpos; i++) {
				System.out.printf("%-40s%d\n", posList.get(i).getKey(), posList.get(i).getValue());
			} 
		}	
	}
	
	
}

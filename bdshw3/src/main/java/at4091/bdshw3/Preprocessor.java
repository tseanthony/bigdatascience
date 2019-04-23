package at4091.bdshw3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.lang.Integer;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Preprocessor {

	StanfordCoreNLP pipeline;
	CoreDocument document; 
	
	public Preprocessor() {
	
		// set up pipeline properties / pipeline
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma"); //,ner
	    
////	    //NER properties. Simplifies model to enhance speed, avoid OOM error
//	    props.setProperty("ner.applyFineGrained", "false");
//	    props.setProperty("ner.applyNumericClassifiers", "false");
//	    props.setProperty("ner.useSUTime", "false");
	     
	    this.pipeline = new StanfordCoreNLP(props);	 
	    this.document = null; 
	     
	}
	
	public void process(String inputPath) throws IOException {
		
		// Loads .txt document to string
		byte[] fileArray;
		fileArray = Files.readAllBytes(Paths.get(inputPath));
		String input = new String(fileArray, "US-ASCII");

		// Create CoreDocument and Annotate
	    CoreDocument exampleDocument = new CoreDocument(input);
	    pipeline.annotate(exampleDocument);

	    this.document = exampleDocument;
	  	    
	}
	
	// N-grams from words with the same named entity class.
	public HashMap<String, Integer> createNGram(int maxSize, int minSize, int minWordFrequency) {
		
		CoreDocument currDoc = this.document;
		
		HashMap<String, Integer> wordFreq = getWordFrequency(currDoc);
		HashMap<String, Integer> nGramMap = new HashMap<String, Integer>();  
		
		int dynamicMaxSize = maxSize;			// To prevent array index errors
		
		// for CoreSentences in CoreDocument
		for (int i = 0; i < currDoc.sentences().size(); i++) {
			List<CoreLabel> SentenceTokens = currDoc.sentences().get(i).tokens();
	
			// Token in CoreSentence
			for (int j = 0; j < SentenceTokens.size(); j++) {
				
				StringBuilder word = new StringBuilder(32);
				int wordCount = 0;
				String namedEntity = "0";
				
				// Prevent indexing out of bounds
				if (j + dynamicMaxSize >= SentenceTokens.size()) {
					dynamicMaxSize = SentenceTokens.size() - j;
				}
				
				// n gram size
				for (int k = 0; k < dynamicMaxSize; k++) {
					
					String nextWord = SentenceTokens.get(j + k).lemma();
					
					// Update Named Entity, Break out of loop if k > 0 && n-grams do not match
					String currNamedEntity = SentenceTokens.get(j + k).ner();
					if (k == 0) {
						namedEntity = currNamedEntity;
					} else if (!namedEntity.equals(currNamedEntity)) {
						 break;
					}
					
					// Prevent adding words under minimum word frequency
					if (wordFreq.get(nextWord) < minWordFrequency) {
						break;
					}
					
					// Prevent adding "." to n-gram
					if (nextWord.equals(".") || nextWord.equals("...") || nextWord.equals("?")) {
						break;
					}
					
					if (!namedEntity.equals("0") && (k > 0)) {
						
					}
					
					word.append(nextWord);
					wordCount++;
					
					String wordStr = word.toString().toLowerCase();
					
					// Add word if not in the map. Else increment count
					if (nGramMap.containsKey(wordStr)) {
						
						int count = nGramMap.get(wordStr).intValue() + 1;
						nGramMap.put(wordStr, new Integer(count));
						
					} else {
						
						// Add to dictionary if n-gram size is greater the minSize
						if (wordCount >= minSize) nGramMap.put(wordStr, new Integer(1));	
						
					}		
				} 
			}
			dynamicMaxSize = maxSize;
		}
		
		//Remove Nuisance Tokens
		nGramMap.remove("...");
		nGramMap.remove(".");
		
		return nGramMap;
		
	}
	
	// N-grams from words with the same named entity class.
	public HashMap<String, Integer> createNGramNoNER(int maxSize, int minSize, int minWordFrequency) {
				
		CoreDocument currDoc = this.document;
		
		HashMap<String, Integer> wordFreq = getWordFrequency(currDoc);
		HashMap<String, Integer> nGramMap = new HashMap<String, Integer>();  
		
		int dynamicMaxSize = maxSize;			// To prevent array index errors
		
		// for CoreSentences in CoreDocument
		for (int i = 0; i < currDoc.sentences().size(); i++) {
			List<CoreLabel> SentenceTokens = currDoc.sentences().get(i).tokens();
	
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
					
					String nextWord = SentenceTokens.get(j + k).lemma();
					
					// Prevent adding words under minimum word frequency
					if (wordFreq.get(nextWord) < minWordFrequency) {
						break;
					}
					
					// Prevent adding "." to n-gram
					if (nextWord.equals(".") || nextWord.equals("...") || nextWord.equals("?")) {
						break;
					}
					
					word.append(nextWord);
					wordCount++;
					
					String wordStr = word.toString().toLowerCase();
					
					// Add word if not in the map
					if (nGramMap.containsKey(wordStr)) {
						
						int count = nGramMap.get(wordStr).intValue() + 1;
						nGramMap.put(wordStr, new Integer(count));
						
					} else {
		
						// Add to dictionary if n-gram size is greater the minSize 
						if (wordCount >= minSize) nGramMap.put(wordStr, new Integer(1));	
						
					}		
				} 
			}
			dynamicMaxSize = maxSize;
		}
		
		//Remove Nuisance Tokens
		nGramMap.remove("...");
		nGramMap.remove(".");
		
		return nGramMap;
		
	}
	
	static private HashMap<String, Integer> getWordFrequency(CoreDocument currDoc){
		
		HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>(); 
		
		for (int i = 0; i < currDoc.sentences().size(); i++) {
			
			List<CoreLabel> SentenceTokens = currDoc.sentences().get(i).tokens();
			
			for (int j = 0; j < SentenceTokens.size(); j++) {
				
				String key = SentenceTokens.get(j).lemma();

				if (wordCountMap.containsKey(key)) {
					
					Integer count = wordCountMap.get(key);
					wordCountMap.put(key, new Integer(count + 1));

				} else {
					wordCountMap.put(key, new Integer(1));		
				}
			}	
		}
		return wordCountMap;
		
	}
		
}

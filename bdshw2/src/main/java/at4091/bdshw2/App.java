package at4091.bdshw2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import edu.stanford.nlp.pipeline.CoreDocument;

public class App 
{
	
	static CoreDocument annotatedTweet;
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		
		
		// Get Directory for Input and Output
    	File currFile = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    	File TestPath = currFile.getParentFile().getParentFile();
		
		File twitterCSV = new File(TestPath,"sentiment140.csv");
		
		CSVParser parser = CSVParser.parse(twitterCSV, Charset.forName("US-ASCII"), CSVFormat.RFC4180);
		Iterator<CSVRecord> fileIterator = parser.iterator();
		
		CoreNLPAnalysis nlpProcessor = new CoreNLPAnalysis();
		
		File stopWordFile = new File(TestPath, "CoreNLP_StopWords.txt");
		StopWordFilter swf = new StopWordFilter(stopWordFile);
		
		ArrayList<String> patternone = new ArrayList<String>();
		patternone.add("JJ");		// Adjective
		patternone.add("NN");		// Noun, singular or mass
		
		ArrayList<String> patterntwo = new ArrayList<String>();
		patterntwo.add("PRP"); 		// Personal Pronoun
		patterntwo.add("VBD");		// Verb, past tense
		
		ArrayList<String> patternthr = new ArrayList<String>();
		patternthr.add("PRP");		// Personal pronoun
		patternthr.add("VBG");		// Verb, gerund or present participle
		
		DocumentProcess mentions = new DocumentProcess();
		DocumentProcess hashtags = new DocumentProcess();
		DocumentProcess ngrams14 = new DocumentProcess();
		DocumentProcess nounonly = new DocumentProcess();
		DocumentProcess pattern1 = new DocumentProcess();
		DocumentProcess pattern2 = new DocumentProcess();
		DocumentProcess pattern3 = new DocumentProcess();
		DocumentProcess depparse = new DocumentProcess();
		DocumentProcess namedent = new DocumentProcess();
		
		int j = 0;
		
		while(fileIterator.hasNext()){
//		for (int i = 0; i < 3000; i++){
			
			if (++j % 50000 == 0) { System.out.println(j); }
			
			CSVRecord twitterPost = fileIterator.next();
			
			int sentimentVal = Integer.parseInt(twitterPost.get(0));
			Sentiment tweetSentiment;
			switch(sentimentVal)
			{
			case(0):
				tweetSentiment = Sentiment.NEGATIVE;
				break;
			case(4):
				tweetSentiment = Sentiment.POSITIVE;
				break;
			default:
				continue;
			}
			
			
			String text = twitterPost.get(5);
			String cleantext = swf.remove(text);
			CoreDocument annotatedTweet = nlpProcessor.annotate(cleantext);
			
			if (text != null && annotatedTweet != null && !annotatedTweet.sentences().isEmpty()) { 
					
				mentions.processMentions(annotatedTweet,tweetSentiment, '@');
				hashtags.processMentions(annotatedTweet,tweetSentiment, '#');
				ngrams14.processNGramNoun(annotatedTweet,tweetSentiment, false);
				nounonly.processNGramNoun(annotatedTweet,tweetSentiment, true);
				pattern1.processNGramPatternPOS(annotatedTweet,tweetSentiment, patternone);
				pattern2.processNGramPatternPOS(annotatedTweet,tweetSentiment, patterntwo);
				pattern3.processNGramPatternPOS(annotatedTweet,tweetSentiment, patternthr);
				depparse.processNGramDependencyParser(annotatedTweet,tweetSentiment);
				namedent.processNGramNER(annotatedTweet,tweetSentiment);

			}	
		}

		
		mentions.print("20 Most Frequent Mentions", 20, mentions.negFrequency, mentions.posFrequency);
		hashtags.print("20 Most Frequent Hashtags", 20, hashtags.negFrequency, hashtags.posFrequency);
		
		ngrams14.print("1-Grams", 20, ngrams14.negFrequency, ngrams14.posFrequency);
		ngrams14.print("2-Grams", 20, ngrams14.negFrequency2, ngrams14.posFrequency2);
		ngrams14.print("3-Grams", 20, ngrams14.negFrequency3, ngrams14.posFrequency3);
		ngrams14.print("4-Grams,", 20, ngrams14.negFrequency4, ngrams14.posFrequency4);
		
		nounonly.print("1-Grams, Nouns Only", 20, nounonly.negFrequency, nounonly.posFrequency);
		nounonly.print("2-Grams, Nouns Only", 20, nounonly.negFrequency2, nounonly.posFrequency2);
		nounonly.print("3-Grams, Nouns Only", 20, nounonly.negFrequency3, nounonly.posFrequency3);
		nounonly.print("4-Grams, Nouns Only", 20, nounonly.negFrequency4, nounonly.posFrequency4);

		pattern1.print("Adjective, Noun (singular or Mass)", 10, pattern1.negFrequency, pattern1.posFrequency);
		pattern2.print("Personal Pronoun, Verb (past tense)", 10, pattern2.negFrequency,pattern2.posFrequency);
		pattern3.print("Personal Pronoun, Verb (gerund or present participle)", 10, pattern3.negFrequency, pattern3.posFrequency);
		depparse.print("20 Most Frequent Dependency Parser Results", 20, depparse.negFrequency, depparse.posFrequency);
		
		namedent.print("20 Most Frequent NER Results, 1-grams", 20, namedent.negFrequency, namedent.posFrequency);
		namedent.print("20 Most Frequent NER Results, 2-grams", 20, namedent.negFrequency2, namedent.posFrequency2);
		namedent.print("20 Most Frequent NER Results, 3-grams", 20, namedent.negFrequency3, namedent.posFrequency3);
		namedent.print("20 Most Frequent NER Results, 4-grams", 20, namedent.negFrequency4, namedent.posFrequency4);
		
			
	}
		
}





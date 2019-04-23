package at4091.bdshw2;


import java.util.Properties;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;


public class CoreNLPAnalysis {

	StanfordCoreNLP pipeline;
		
	public CoreNLPAnalysis() {
	
		// set up pipeline properties / pipeline
	    Properties props = new Properties();
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse"); //
	    
	    //NER properties. Simplifies model to enhance speed, avoid OOM error
	    props.setProperty("ner.applyFineGrained", "false");
	    props.setProperty("ner.applyNumericClassifiers", "false");
	    props.setProperty("ner.useSUTime", "false");
	    //props.setProperty("ner.model","edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
	    props.setProperty("parse.originalDependencies", "true");
	     
	    this.pipeline = new StanfordCoreNLP(props); 
	}
	
	public CoreDocument annotate(String tweet) {
		CoreDocument tweetDocument = new CoreDocument(tweet);
		pipeline.annotate(tweetDocument);
		return tweetDocument;
		
	}
	
}


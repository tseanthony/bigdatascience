package at4091.bdshw1;

import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

/*
 * MalletLDA
 * Most code from examples given in URLS:
 * Mallet Data Import Guide:
 * http://mallet.cs.umass.edu/import-devel.php
 * Mallet Topic Modeling Guide
 * http://mallet.cs.umass.edu/import-devel.php
 */

public class MalletLDA {
	
	Pipe pipe;
	ParallelTopicModel model;
	File stopWordFile;
	
	public MalletLDA(File stopWordFile) throws IOException{ 
		
		this.stopWordFile = stopWordFile;
		
		pipe = buildPipe();
		
    	String TestPath = System.getProperty("user.dir");
    	File inputDirectory = new File(TestPath, "data");
    	InstanceList instances = readDirectory(inputDirectory);
        
        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        int numTopics = 10;
        model = new ParallelTopicModel(numTopics, 1.0, 0.01);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(2);

        // Run the model for 50 iterations and stop (this is for testing only, 
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(50);
        model.estimate();
        
        /** Code Below is to infer topics for individual documents 	*/
        /** From example on mallet.cu.umass.edu						*/
        
//        // Show the words and topics in the first instance
//
//        // The data alphabet maps word IDs to strings
//        Alphabet dataAlphabet = instances.getDataAlphabet();
//        
//        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
//        LabelSequence topics = model.getData().get(0).topicSequence;
//        
//        Formatter out = new Formatter(new StringBuilder(), Locale.US);
//        
//        // Prints Alphabet
//        for (int position = 0; position < tokens.getLength(); position++) {
//            out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
//        }
//        System.out.println(out);
//        
//        // Estimate the topic distribution of the first instance, 
//        //  given the current Gibbs state.
//        
//        double[] topicDistribution = model.getTopicProbabilities(0);
//
//        // Get an array of sorted sets of word ID/count pairs
//        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
//        
//        // Show top 5 words in topics with proportions for the first document
//        for (int topic = 0; topic < numTopics; topic++) {
//            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
//            
//            out = new Formatter(new StringBuilder(), Locale.US);
//            out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
//            int rank = 0;
//            while (iterator.hasNext() && rank < 5) {
//                IDSorter idCountPair = iterator.next();
//                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
//                rank++;
//            }
//            System.out.println(out);
//        }
//        
//        // Create a new instance with high probability of topic 0
//        StringBuilder topicZeroText = new StringBuilder();
//        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();
//
//        int rank = 0;
//        while (iterator.hasNext() && rank < 5) {
//            IDSorter idCountPair = iterator.next();
//            topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
//            rank++;
//        }
//
//        // Create a new instance named "test instance" with empty target and source fields.
//        InstanceList testing = new InstanceList(instances.getPipe());
//        testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));
//
//        TopicInferencer inferencer = model.getInferencer();
//        double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
//        System.out.println("0\t" + testProbabilities[0]);
		
	}
	
	public Pipe buildPipe() {
		
        
		// Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: tokenize, lowercase, remove stopwords, map to features
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add(new TokenSequenceLowercase());
        pipeList.add( new TokenSequenceRemoveStopwords(this.stopWordFile, "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        //pipeList.add(new Target2Label());
        //pipeList.add(new FeatureSequence2FeatureVector());
        //pipeList.add(new PrintInputAndTarget());
        
        return new SerialPipes(pipeList);
		
	}
	
    public InstanceList readDirectory(File directory) {
        return readDirectories(new File[] {directory});
    }

    public InstanceList readDirectories(File[] directories) {
        
        // Construct a file iterator, starting with the 
        //  specified directories, and recursing through subdirectories.
        // The second argument specifies a FileFilter to use to select
        //  files within a directory.
        // The third argument is a Pattern that is applied to the 
        //   filename to produce a class label. In this case, I've 
        //   asked it to use the last directory name in the path.
        FileIterator iterator =
            new FileIterator(directories,
                             new TxtFilter(),
                             FileIterator.LAST_DIRECTORY);

        // Construct a new instance list, passing it the pipe
        //  we want to use to process instances.
        InstanceList instances = new InstanceList(pipe);

        // Now process each instance provided by the iterator.
        instances.addThruPipe(iterator);

        return instances;
    }

	public void printTopics(File outputDirectory) throws IOException {
		
		File outputFile = new File(outputDirectory, "LDATopicList.txt");
		outputFile.delete();
		outputFile.createNewFile();
		
		FileWriter f = new FileWriter(outputFile, true);
		
		f.write("LDA Topics\n");
		System.out.format("\n\nLDA TOPICS\n\n");
		
		
	    Object[][] topWords = model.getTopWords(1);
	    for (int i = 0; i < topWords.length; i++) {
	    	
	    	Formatter out = new Formatter(new StringBuilder(), Locale.US);
	    	
	    	for (int j = 0; j < topWords[i].length; j++) {
	    		out.format("%s ", topWords[i][j].toString()); 
	    	}
	    	System.out.println(out);
	    	f.write(out.toString() + "\n");
	    	
	    }
	    
	    f.close();
		
	}
	
	public void printTopics() {
		
		System.out.format("\n\nLDA TOPICS\n");
		
	    Object[][] topWords = model.getTopWords(1);
	    for (int i = 0; i < topWords.length; i++) {
	    	
	    	Formatter out = new Formatter(new StringBuilder(), Locale.US);
	    	
	    	for (int j = 0; j < topWords[i].length; j++) {
	    		out.format("%s ", topWords[i][j].toString()); 
	    	}
	    	System.out.println(out);
	    }
		
	}
}

/** This class illustrates how to build a simple file filter */
class TxtFilter implements FileFilter {

    /** Test whether the string representation of the file 
     *   ends with the correct extension. Note that {@ref FileIterator}
     *   will only call this filter if the file is not a directory,
     *   so we do not need to test that it is a file.
     */
    public boolean accept(File file) {
        return file.toString().endsWith(".txt");
    }
}

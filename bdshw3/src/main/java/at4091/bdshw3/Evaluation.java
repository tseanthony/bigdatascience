package at4091.bdshw3;

import java.util.ArrayList;
import java.util.TreeSet;

public class Evaluation {

	public Evaluation(ArrayList<TreeSet<Integer>> inputKSets) {
		
		// Initialize Truth Array
		int[] truthArray = new int[24];
		for (int i = 0; i< 8; i++) {
			truthArray[i] = 1;
		}
		for (int i = 8; i< 16; i++) {
			truthArray[i] = 2;
		}
		for (int i = 16; i< 24; i++) {
			truthArray[i] = 3;
		}
		
		// Determine Consensus of each bin
		int[] kSetLabel = new int[3];
		
		for (int i = 0; i < inputKSets.size(); i++) {
			int countOne = 0;
			int countTwo = 0;
			int countThree = 0;
			
			for (Integer dataRowIndex:  inputKSets.get(i)) {
				
				int rowIndex = dataRowIndex.intValue();
				if (rowIndex < 8) {
					countOne++;
				} else if (rowIndex < 16) {
					countTwo++;
				} else {
					countThree++;
				}
				
				if (countOne > countTwo && countOne > countThree) {
					kSetLabel[i]  = 1;
				} else if (countTwo > countOne && countTwo > countThree) {
					kSetLabel[i]  = 2;
				} else {
					kSetLabel[i]  = 3;
				}
				
			}	
		}
		
		int[] predictArray = new int[24];
		for (int i = 0; i < inputKSets.size(); i++) {
			
			for (Integer dataRowIndex:  inputKSets.get(i)) {
				
				int rowIndex = dataRowIndex.intValue();
				predictArray[rowIndex] = kSetLabel[i];
				
			}	
		}
		
		int truePositiveTotal = 0;
		int trueNegativeTotal = 0;
		int falsePostiveTotal = 0;
		int falseNegativeTotal = 0;
		
		for (int i = 0; i < 3; i++) {
			
			int tp= 0;
			int tn= 0;
			int fp= 0;
			int fn= 0;
			
			int startIndex = 0;
			
			for (int endIndex = startIndex + 8; startIndex < endIndex; startIndex++) {
				
				if (truthArray[i] == predictArray[i]) {
					tp++;
				} else {
					fp++;
				}
			}
			
			tn = 16 - fp;
			fn = 8 - tp;
			
			truePositiveTotal += tp;
			trueNegativeTotal += tn;
			falsePostiveTotal += fp;
			falseNegativeTotal += fn;
			
		}
		
		System.out.println("\nConfusion Matrix (Overall)\n");
		System.out.format("True Positive: %5s\n",truePositiveTotal);
		System.out.format("True Negative: %5s\n",trueNegativeTotal);
		System.out.format("False Positive: %5s\n",falsePostiveTotal);
		System.out.format("False Negative: %5s\n",falseNegativeTotal);
		
		double fscore = (2 * truePositiveTotal) / (2 * truePositiveTotal + falsePostiveTotal + falseNegativeTotal);
		System.out.format("F1 Score: %5f\n",fscore);
	}
	
}

package at4091.bdshw3;

import java.util.ArrayList;
//import java.util.Random;
import java.util.TreeSet;

public class Cluster {
	
	
	public static ArrayList<TreeSet<Integer>> kMeansEuclidean(int k, double[][] input1) {
		
		double[][] input = Visualize.normalize(input1);
		
		TreeSet<Integer> centroidIndices = new TreeSet<Integer>();
		double[][] centroids = new double[k][input[k].length];
		ArrayList<TreeSet<Integer>> kSets = new ArrayList<TreeSet<Integer>>(k);
		for (int i = 0; i < k; i++ ) {
			kSets.add(new TreeSet<Integer>());
		}
		
//		// Randomly Choose First Centroid
//		Random r = new Random();
//		int firstCentroid = r.nextInt(input.length); 
		
		int firstCentroid = 1;
		centroidIndices.add(new Integer(firstCentroid));
		kSets.get(0).add(new Integer(firstCentroid));
		
		// Copy First Centroid to Array
		System.arraycopy(input[firstCentroid], 0, centroids[0], 0, input[firstCentroid].length);
		
		// Find k - 1 Next Centroids
		for (int i = 1; i < k; i++) {
			
			int maxIndex = 0;
			double maxDistance = 0;
			
			for (int j = 0; j < input.length; j++) {
				
				Integer test = new Integer(j);
				if (centroidIndices.contains(test)) {
					continue;
				}
				
				double minDistance = Integer.MAX_VALUE;
				
				// Finds Closest Distance From Current Centroids
				for (int q = 0; q < i; q++) {
					double currDistance = euclideanDistance(centroids[q], input[j]);
					minDistance = Math.min(minDistance, currDistance);		
				}
				
				if (minDistance > maxDistance) {
					maxDistance = minDistance;
					maxIndex = j;
				}			
			}
			
			System.arraycopy(input[maxIndex], 0, centroids[i], 0, input[maxIndex].length);
			centroidIndices.add(new Integer(maxIndex));
			kSets.get(i).add(new Integer(maxIndex));
			
		}
		
		// Split Rest of Data Into Closest Set
		for (int i = 0; i < input.length; i++) {
			
			Integer currIndex = new Integer(i);
			if (centroidIndices.contains(currIndex)) {
				continue;
			}
			
			// Find Closest Centroid
			int closestCentroid = -1;
			double minDistance = Integer.MAX_VALUE;
			
			for (int j = 0; j < k; j++) {
				double currDistance = euclideanDistance(centroids[j], input[i]);
				
				if (currDistance < minDistance) {
					closestCentroid = j;
					minDistance = currDistance;	
				}
			}
			// Add to Sets
			kSets.get(closestCentroid).add(new Integer(i));
			
			//Update Centroid with Average value
			for (int c = 0; c < centroids[closestCentroid].length; c++) {
				centroids[closestCentroid][c] = (centroids[closestCentroid][c] + input[i][c])/2;		
			}	
		}
		
		// Update Sets Until No Changes are Needed or Max Amount of Rounds have Occurred
 		for (int maxRounds = 0; maxRounds < 100; maxRounds++) {
 			
 			boolean changeMadeInCurrentRound = false;
 			
 			
 			// Bandage Fix, Wrong DS Used.
 			ArrayList<TreeSet<Integer>> kSetsTemp = new ArrayList<TreeSet<Integer>>(k);
 			for (int i = 0; i < k; i++ ) {
 				kSetsTemp.add(new TreeSet<Integer>());
 			}
 			
 			// Start
 			for (int setNum = 0; setNum < k; setNum++) {
 				
 				for (Integer data: kSets.get(setNum)) {
 					
 					int closestSet = -1;
 					int dataRow = data.intValue();
 					double distanceToBestCentroid = Integer.MAX_VALUE;
 					
 					// Check if Other Centroids are Closer
 					for (int centroidIndex = 0; centroidIndex < k; centroidIndex++) {
 						
 						double distToCentroid = euclideanDistance(centroids[centroidIndex], input[dataRow]);
 						
 						if (distToCentroid < distanceToBestCentroid) {
 							closestSet = centroidIndex;
 							distanceToBestCentroid = distToCentroid;
 						}
 					}
 					
 					// If Change is Required
 					if (closestSet != setNum) {
 						
 						// Mark that a Move is Required
 						changeMadeInCurrentRound = true;
 						
 						// Update Centroid with Average value
 						for (int c = 0; c < centroids[closestSet].length; c++) {
 							centroids[closestSet][c] = (centroids[closestSet][c] + input[dataRow][c])/2;		
 						}
 					}	
 					
 					kSetsTemp.get(closestSet).add(data);
 					
 				}	
 			}
 			
 			// Bandage Fix, Wrong DS Used.
 			kSets = kSetsTemp;
 			
 			// If No Changes Were Made, Break Out of Loop.
 			if (changeMadeInCurrentRound == false) {
 				//System.out.println("Early Break");
 				break;
 				
 			}
 			
 		}
		
		//Prints Sets
		int i = 0;
		System.out.println("\nkMeans: Euclidean Distance");
		System.out.println("(Documents Listed By Index)");
		for (TreeSet<Integer> asdf: kSets) {
			System.out.format("\nGroup #%d\n",++i);
			for (Integer val: asdf) {
				System.out.println(val);
			}
		}
		
		return kSets;
		
	}
	
	public static ArrayList<TreeSet<Integer>> kMeansCosine(int k, double[][] input1) {
		
		double[][] input = Visualize.normalize(input1);
		
		TreeSet<Integer> centroidIndices = new TreeSet<Integer>();
		double[][] centroids = new double[k][input[k].length];
		ArrayList<TreeSet<Integer>> kSets = new ArrayList<TreeSet<Integer>>(k);
		for (int i = 0; i < k; i++ ) {
			kSets.add(new TreeSet<Integer>());
		}
		
		// Randomly Choose First Centroid
		
//		Random r = new Random();
//		int firstCentroid = r.nextInt(input.length); 
		
//		// Hard Code first Centroid
		int firstCentroid = 1;
		
		centroidIndices.add(new Integer(firstCentroid));
		kSets.get(0).add(new Integer(firstCentroid));
		
		// Copy First Centroid to Array
		System.arraycopy(input[firstCentroid], 0, centroids[0], 0, input[firstCentroid].length);
		
		// Find k - 1 Next Centroids
		for (int i = 1; i < k; i++) {
			
			int maxIndex = 0;
			double minSimilarity = Integer.MAX_VALUE;
			
			for (int j = 0; j < input.length; j++) {
				
				Integer test = new Integer(j);
				if (centroidIndices.contains(test)) {
					continue;
				}
				
				double maxSimilarity = 0;
				
				// Finds Closest Similarity From Current Centroids
				for (int q = 0; q < i; q++) {
					double currSimilarity = cosineSimilarity(centroids[q], input[j]);
					maxSimilarity = Math.max(maxSimilarity, currSimilarity);		
				}
				
				if (maxSimilarity < minSimilarity) {
					minSimilarity = maxSimilarity;
					maxIndex = j;
				}			
			}
			
			System.arraycopy(input[maxIndex], 0, centroids[i], 0, input[maxIndex].length);
			centroidIndices.add(new Integer(maxIndex));
			kSets.get(i).add(new Integer(maxIndex));
			
		}
		
		// Split Rest of Data Into Closest Set
		for (int i = 0; i < input.length; i++) {
			
			Integer currIndex = new Integer(i);
			if (centroidIndices.contains(currIndex)) {
				continue;
			}
			
			// Find Closest Centroid
			int closestCentroid = -1;
			double minSimilarity = 0;
			
			for (int j = 0; j < k; j++) {
				double currSimilarity = cosineSimilarity(centroids[j], input[i]);
				
				if (currSimilarity > minSimilarity) {
					closestCentroid = j;
					minSimilarity = currSimilarity;	
				}
			}
			// Add to Sets
			kSets.get(closestCentroid).add(new Integer(i));
			
			//Update Centroid with Average value
			for (int c = 0; c < centroids[closestCentroid].length; c++) {
				centroids[closestCentroid][c] = (centroids[closestCentroid][c] + input[i][c])/2;	
			}
			// Find Vector Length
			double sumOfSquares = 0;
			for (int c = 0; c < centroids[closestCentroid].length; c++) {
				sumOfSquares += Math.pow(centroids[closestCentroid][c], 2);	
			}
			double length = Math.sqrt(sumOfSquares);
			// Update Vector Value
			for (int c = 0; c < centroids[closestCentroid].length; c++) {
				centroids[closestCentroid][c] = centroids[closestCentroid][c] / length;	
			}
			
		}
		
		// Update Sets Until No Changes are Needed or Max Amount of Rounds have Occurred
 		for (int maxRounds = 0; maxRounds < 1000; maxRounds++) {
 			
 			boolean changeMadeInCurrentRound = false;
 			
 			// Bandage Fix, Wrong DS Used.
 			ArrayList<TreeSet<Integer>> kSetsTemp = new ArrayList<TreeSet<Integer>>(k);
 			for (int i = 0; i < k; i++ ) {
 				kSetsTemp.add(new TreeSet<Integer>());
 			}
 			
 			// Start
 			for (int setNum = 0; setNum < k; setNum++) {
 				
 				for (Integer data: kSets.get(setNum)) {
 					
 					int closestSet = -1;
 					int dataRow = data.intValue();
 					double similarityToBestCentroid = 0;
 					
 					// Check if Other Centroids are Closer
 					for (int centroidIndex = 0; centroidIndex < k; centroidIndex++) {
 						
 						double distToCentroid = cosineSimilarity(centroids[centroidIndex], input[dataRow]);
 						
 						if (distToCentroid > similarityToBestCentroid) {
 							closestSet = centroidIndex;
 							similarityToBestCentroid = distToCentroid;
 						}
 					}
 					
 					// If Change is Required
 					if (closestSet != setNum) {
 						
 						// Mark that a Move is Required
 						changeMadeInCurrentRound = true;
 						
 						// Update Centroid with Average value
 						for (int c = 0; c < centroids[closestSet].length; c++) {
 							centroids[closestSet][c] = (centroids[closestSet][c] + input[dataRow][c])/2;		
 						}
 						// Find Vector Length
 						double sumOfSquares = 0;
 						for (int c = 0; c < centroids[closestSet].length; c++) {
 							sumOfSquares += Math.pow(centroids[closestSet][c], 2);	
 						}
 						double length = Math.sqrt(sumOfSquares);
 						// Update Vector Value
 						for (int c = 0; c < centroids[closestSet].length; c++) {
 							centroids[closestSet][c] = centroids[closestSet][c] / length;	
 						}
 					}	
 					
 					kSetsTemp.get(closestSet).add(data);
 					
 				}	
 			}
 			
 			// Bandage Fix, Wrong DS Used.
 			kSets = kSetsTemp;
 			
 			// If No Changes Were Made, Break Out of Loop.
 			if (changeMadeInCurrentRound == false) {
 				//System.out.println("Early Break");
 				break;
 				
 			}
 			
 		}
		
		//Prints Sets
		int i = 0;
		System.out.println("\nkMeans: Cosine Similarity");
		System.out.println("(Documents Listed By Index)");
		for (TreeSet<Integer> asdf: kSets) {
			System.out.format("\nGroup #%d\n",++i);
			for (Integer val: asdf) {
				System.out.println(val);
			}
		}
		
		return kSets;
		
	}
	
	// Returns Cosine Between Two Points
	static double cosineSimilarity(double[] centroid, double[] input) {
				
		double inputLenSquare = 0;	
		double centroidLenSquare = 0;
		double dotProd = 0;
		
		// Calculate Length of Each Vector and Dot Product
		for (int i = 0; i < input.length; i++) {
			inputLenSquare += Math.pow(input[i], 2);
			centroidLenSquare += Math.pow(input[i], 2);
			dotProd += input[i] * centroid[i];
		}
		
		double inputLen = Math.sqrt(inputLenSquare);
		double centroidLen = Math.sqrt(centroidLenSquare);
		
		return dotProd / (inputLen * centroidLen);
		
	}
	
	
	// Returns EuclideanDistance Between Two Points
	public static double euclideanDistance(double[] centroid, double[] input) {
		
		double sumDiffSquare = 0;
		
		for (int k = 0; k < input.length; k++) {
			sumDiffSquare += Math.pow(input[k] - centroid[k], 2);
		}
		
		return Math.sqrt(sumDiffSquare);
		
	}
	
	// NOT USED
	static double[][] cosineSimilarityMatrix(double[][] input) {
		
		double[] vecLenArray = new double[input.length];
		double[][] cosineSimMatrix = new double[input.length][input.length];
		
		
		//Calculate Length of Each Vector
		for (int i = 0; i < input.length; i++) {
			
			double vecLenSquare = 0;	
			
			for (int j = 0; j < input[i].length; j++) {
				
				vecLenSquare += Math.pow(input[i][j], 2);
				
			}
			vecLenArray[i] = Math.sqrt(vecLenSquare);
		}

		
		// Fill Output Diagonal with Zeros
		for (int i = 0; i < input.length; i++) {
			cosineSimMatrix[i][i] = 0.0;
		}
		
		
		// Fill Rest
		for (int i = 0; i < input.length - 1; i++) {
			for (int j = i + 1; j < input.length; j++) {
				
				double dotProd = 0;
				
				for (int k = 0; k < input[i].length; k++) {
					dotProd += input[i][k] * input[j][k];
				}
				
				double cosineAngle = dotProd / (vecLenArray[i] * vecLenArray[j]);
				cosineSimMatrix[i][j] = cosineAngle;
				cosineSimMatrix[j][i] = cosineAngle;
				
			}
		}
		
		return cosineSimMatrix;
		
	}
	
	
	// NOT USED
	static double[][] euclideanDistanceMatrix(double[][] input) {
		
		double[][] euclideanMatrix = new double[input.length][input.length];
		
		// Fill Diagonal with Zeros
		for (int i = 0; i < input.length; i++) {
			euclideanMatrix[i][i] = 0.0;
		}
		
		// Fill Rest
		for (int i = 0; i < input.length - 1; i++) {
			for (int j = i + 1; j < input.length; j++) {
				
				double sumDiffSquare = 0;
				
				for (int k = 0; k < input[i].length; k++) {
					sumDiffSquare += Math.pow(input[i][k] - input[j][k], 2);
				}
				
				double distance = Math.sqrt(sumDiffSquare);
				euclideanMatrix[i][j] = distance;
				euclideanMatrix[j][i] = distance;
				
			}
		}
		
		return euclideanMatrix;
		
	}

}

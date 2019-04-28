Anthony Tse 
Big Data Science and Machine Learning Algorithms
Spring 2019
Homework 4

NOTE 1: PLEASE DO NOT MOVE FILES

HOW TO EXECUTE FROM TERMINAL 

1) In your terminal, navigate to the main at4091 folder.

2) Then navigate to the BDS.HW1 directory.

	cd BDS.HW1/

In the BDS.HW1 directory, enter the command:

	mvn clean dependency:copy-dependencies package

Run the executable:

	java -cp target/BDS.HW1-0.0.1-SNAPSHOT-jar-with-dependencies.jar AT4091.bdshw3.App [-c/-e] [test document directory]
 
options:

-c: cosine similarity (for kmeans and knn)
-e: euclidean distance (for kmeans and knn)

Groups 1: Aviation


Discussion:
Documents 9 and 10 are combined articles of topics "Hoof and Mouth" and "Airlines". I will automatically marks these as incorrect and that they do not belong to any class.

Results (pasted from terminal):
KNN: Document, Classification
/Users/anthonytse/NYU/BigDataScience/Homework/HW4/datahw/unknown01.txt: group C1
/Users/anthonytse/NYU/BigDataScience/Homework/HW4/datahw/unknown02.txt: group C1
/Users/anthonytse/NYU/BigDataScience/Homework/HW4/datahw/unknown03.txt: group C1
/Users/anthonytse/NYU/BigDataScience/Homework/HW4/datahw/unknown04.txt: group C1
/Users/anthonytse/NYU/BigDataScience/Homework/HW4/datahw/unknown05.txt: group C4
/Users/anthonytse/NYU/BigDataScience/Homework/HW4/datahw/unknown06.txt: group C4
/Users/anthonytse/NYU/BigDataScience/Homework/HW4/datahw/unknown07.txt: group C7
/Users/anthonytse/NYU/BigDataScience/Homework/HW4/datahw/unknown08.txt: group C7
/Users/anthonytse/NYU/BigDataScience/Homework/HW4/datahw/unknown09.txt: group C4
/Users/anthonytse/NYU/BigDataScience/Homework/HW4/datahw/unknown10.txt: group C1

Confusion Matrix(euclidean, k = 3):

C1: 	C4:	C7:	Overall:
TP: 4	TP: 2	TP: 2	TP: 2.67
TN: 5	TN: 7	TN: 8	TN: 6.67
FP: 1	FP: 1	FP: 0	FP: 0.67
FN: 0	FN: 0	FN: 0	FN: 0.00

NOTE: I did the confusion matrix incorrectly for the HW1.

F Score (euclidean, k = 3): 0.89

Different k's:
k = 1	=>	Same as k = 3
k = 5	=>	Same as k = 3
k = 10	=>	Worse
k = 21	=>	Doesn't work. All classified as same topic.


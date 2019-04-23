Anthony Tse 

Big Data Science 
Spring 2019
Homework 2


NOTE 1: PLEASE DO NOT MOVE FILES


HOW TO EXECUTE FROM TERMINAL 

1) In your terminal, navigate to the main at4091 folder.

2) Then navigate to the bdshw2 directory.

	cd bdshw2

In the BDS.HW1 directory, enter the command:

	mvn clean dependency:copy-dependencies package

Run the executable:

	
java -cp bdshw2/target/bdshw2-0.0.1-SNAPSHOT-jar-with-dependencies.jar AT4091.bdshw2.App

NOTES FROM ANTHONY

Output generated from Prince HPC is in text file labeled output.

Late because I did not recieve email. 


Analysis of my results: For all ngrams: people hate work and getting sick, some longer ngrams do not give much insight because the are repeated words.  For nouns: people hate work, school, bad weather, and positive words are associated with positive tweets. Would like to remove hashtags next time around. For NER, people like hannah montana. The time NER Class are more frequent than other NER classes. If I were to redo this, I'd separate the NER's by their classification, remove hashtags, and repeated words. 




 


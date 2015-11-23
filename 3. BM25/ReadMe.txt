Indexer.java
	Input
		- tccorpus.txt
	Steps to Run
		- In terminal cd to the project directory
		- cd src/
		- javac com/Indexer.java
		- java com.Indexer tccorpus.txt index.out
	Output
		- index.out

Bm25.java
	Input
		- index.out
		- queries.txt
	Steps to Run
		- javac com/Bm25.java
		- java com.Bm25 index.out queries.txt 100 > results.eval
	Output
		- results.eval

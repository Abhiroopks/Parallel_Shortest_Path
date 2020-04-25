graphs:
	gcc -o generator.o graphGenerator.c
	./genscript
testsC:
	
	gcc -fopenmp FloydWarshallParallel.c -o FloydWarshallParallel.o
	gcc -fopenmp FloydWarshallSequential.c -o FloydWarshallSequential.o
	gcc -fopenmp FloydWarshallRecursiveParallel.c -o FloydWarshallRecursiveParallel.o
	gcc -fopenmp DijkstraSeq.c -o DijkstraSequential.o
	gcc -fopenmp DijkstraParallel.c -o DijkstraParallel.o
	gcc -fopenmp BellmanFordSeq.c -o BellmanFordSequential.o
	gcc -fopenmp BellmanFordParallel.c -o BellmanFordParallel.o

testsJ:
	javac -cp .:junit-4.12.jar:hamcrest-core-1.3.jar *.java

clean:
	rm *.class *.o *.txt

runtimesJ:
	java -cp .:junit-4.12.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore Timings
runtestsJ:
	java -cp .:junit-4.12.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore Tests

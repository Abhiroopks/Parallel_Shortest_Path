graphs:
	gcc -o bin/generator.o test/graphGenerator.c
	sh  test/genscript
testsC:
	
	gcc -fopenmp src/floydwarshall/FloydWarshallParallel.c -o bin/FloydWarshallParallel.o
	gcc -fopenmp src/floydwarshall/FloydWarshallSequential.c -o bin/FloydWarshallSequential.o
	gcc -fopenmp src/floydwarshall/FloydWarshallRecursiveParallel.c -o bin/FloydWarshallRecursiveParallel.o
	gcc -fopenmp src/dijkstra/DijkstraSeq.c -o bin/DijkstraSequential.o
	gcc -fopenmp src/dijkstra/DijkstraParallel.c -o bin/DijkstraParallel.o
	gcc -fopenmp src/bellmanford/BellmanFordSeq.c -o bin/BellmanFordSequential.o
	gcc -fopenmp src/bellmanford/BellmanFordParallel.c -o bin/BellmanFordParallel.o

testsJ:
	javac -cp .:test/junit-4.12.jar:test/hamcrest-core-1.3.jar:src/dijkstra/:src/deltastepping:src/bellmanford test/*.java -d bin/

clean:
	rm  bin/*

runtimesJ:
	java -cp .:test/junit-4.12.jar:test/hamcrest-core-1.3.jar:bin/ org.junit.runner.JUnitCore Timings
runtestsJ:
	java -cp .:test/junit-4.12.jar:test/hamcrest-core-1.3.jar:bin/ org.junit.runner.JUnitCore Tests

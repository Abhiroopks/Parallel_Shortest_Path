tests:
	gcc -o generator graphGenerator.c
	javac -cp .:junit-4.12.jar:hamcrest-core-1.3.jar *.java
	./genscript

clean:
	rm *.class matrix* generator FWS FWP

runtimes:
	java -cp .:junit-4.12.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore Timings
runtests:
	java -cp .:junit-4.12.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore Tests
fwp:
	gcc -fopenmp FloydWarshallParallel.c -o FWP
	./fwpscript
fws:
	gcc -fopenmp FloydWarshallSequential.c -o FWS
	./fwsscript

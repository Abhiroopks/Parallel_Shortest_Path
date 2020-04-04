# EE382C_Term_Project
## Graph Generator Instructions
- Will generator a random symmetric (assuming numRows == numCols, for undirected weighted graphs) randomly sparsified adjacency matrix  
- Compile: 
`gcc -o generator graphGenerator.c`
- Run:
`./generator [# vertices] [output file]`

## JUnit Tests
- Verifies correctness of **Delta Stepping**,**Dijkstra**, and **Bellman-Ford** parallel algorithms
- Prints timing information for [2-256] cores and [100-1000] sized graphs
- Compile:
`Make tests`
- Check correctness:
`make runtests` 
- Print timing info:
`make runtimes`
- Clean directory:
`make clean`



## Floyd-Warshall 
	gcc -fopenmp FloydWarshallSequential.c -o FWS  
	./FWS <inputfile> <outputfile> <numVertices>  
	gcc -fopenmp FloydWarshallParallel.c -o FWP  
	./FWP <inputfile> <outputfile> <numVertices> 


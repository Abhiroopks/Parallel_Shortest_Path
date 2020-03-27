# EE382C_Term_Project
Graph Generator Instructions:  
Will generator a random symmetric (assuming numRows == numCols, for undirected weighted graphs) randomly sparsified adjacency matrix  
Compilation: gcc -o generator graphGenerator.c  
Run: ./generator <outputfile> <numRows> <numCols> 

Floyd-Warshall:  
gcc -fopenmp FloydWarshallSequential.c -o FWS  
./FWS <inputfile> <outputfile> <numVertices>  

gcc -fopenmp FloydWarshallParallel.c -o FWP  
./FWP <inputfile> <outputfile> <numVertices>   

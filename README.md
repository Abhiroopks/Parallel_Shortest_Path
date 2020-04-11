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
`make tests`
- Check correctness:
`make runtests` 
- Print timing info:
`make runtimes`
- Clean directory:
`make clean`



## Floyd-Warshall 
- Compile and print timing info:
`make fws`
`make fwp`
- Will print times to "fwsnums.csv" and "fwpnums.csv"

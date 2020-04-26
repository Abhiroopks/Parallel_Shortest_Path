# EE382C_Term_Project
## Graph Generator Instructions
- Will generate a random symmetric (assuming numRows == numCols, for undirected weighted graphs) adjacency matrix
- To create all graphs necessary for testing:
`make graphs`

- To make custom graph:

`gcc -o generator test/graphGenerator.c`

`sh bin/generator [# vertices] [output file]`

## JUnit Tests
- Verifies correctness of **Delta Stepping**,**Dijkstra**, and **Bellman-Ford** parallel algorithms
- Prints timing information for [2-256] cores and [100-1000] sized graphs
- Compile:
`make testsJ`
- Check correctness:
`make runtestsJ` 
- Print timing info:
`make runtimesJ > [output file]`

## Delete binaries,graphs:
`make clean`

## C Tests
- Verifies correctness of **Dijkstra**, **Bellman-Ford**, and **Floyd-Warshall** parallel algorithms
- Prints timing information for [2-256] cores and [100-1000] sizes graphs
- Note that Floyd-Warshall Recursive Parallel only works with graphs that have V = Power of 2
- Compile:
`make testsC`
- Check correctness: (Will save to results/checkres.txt)
`sh test/checkC`
- Timing info: (Will save to individual csv files in results/times directory)

`sh test/timesC`

`sh test/timesFWR` (Separate script for Floyd-Warshall Recursive Parallel due to extremely long time to complete)


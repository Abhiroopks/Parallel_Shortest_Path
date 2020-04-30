# EE382C_Term_Project
Authors: Abhiroop Kodandapursanjeeva and Muhammed Mohaimin Sadiq

## Project Description
- Several minimum path finding algorithms for graphs are parallelized and tested for correctness and time performance
- Implemented in C only:
  - Standard Floyd-Warshall and Recursive Floyd-Warshall
- Implemented in Java only:
  - Delta Stepping
- Implemented in C and Java:
  - Dijkstra's
  - Bellman-Ford

## Graph Generator Instructions
- Will generate a random symmetric adjacency matrix of specified size
  - Nonnegative weights only
  - Dense graph (# Edges ~= 0.5V<sup>2</sup>)
- To create all graphs necessary for testing:
  - `make graphs`
  - This will create {100, 200, 300...1000} sized graphs, as well as {128, 256, 512...2048} sized graphs

- To make custom graph:
  - `gcc -o generator test/graphGenerator.c`
  - `sh bin/generator [# vertices] [output file]`

## JUnit Tests
- Verifies correctness of **Delta Stepping**,**Dijkstra**, and **Bellman-Ford** parallel algorithms
- Prints timing information for {2-256} cores and {100-1000} sized graphs
- Compile:
  - `make testsJ`
- Check correctness:
  - `make runtestsJ` 
- Print timing info:
  - `make runtimesJ > [output file]`

## Delete binaries,graphs:
  - `make clean`

## C Tests
- Verifies correctness of **Dijkstra**, **Bellman-Ford**, and **Floyd-Warshall** parallel algorithms
- Prints timing information for [2-256] cores and [100-1000] sizes graphs
- Note that Floyd-Warshall Recursive Parallel only works with graphs that have V = Power of 2
- Compile:
  - `make testsC`
- Check correctness: (Will save to results/checkres.txt)
  - `sh test/checkC`
- Timing info: (Will save to individual csv files in results/times directory)
  - `sh test/timesC`
  - `sh test/timesFWR` (Separate script for Floyd-Warshall Recursive Parallel due to extremely long time to complete)


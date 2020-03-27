#include <stdio.h>
#include <omp.h>
#include <stdlib.h>
#include <string.h>

void findShortestPath(char ifile[], char ofile[], int n){
    FILE* infile = NULL;
    FILE* outfile = NULL;

    infile = fopen(ifile, "r");
    if (!infile) {
        printf("Error: Cannot open file %s\n", ifile);
        exit(1);
    }
    outfile = fopen(ofile, "w");

    double start, end;
    start = omp_get_wtime();
    //set up the graph
    int V = n;
    //fscanf(infile1, "%d %d", &row, &col); //get the dimensions
    int graph[V][V];

    for(int i = 0; i < V; i++) {
        for(int j = 0; j < V; j++)
            fscanf(infile, "%d", &graph[i][j]);
    }

    //initialize distances
    int dist[V][V];
#pragma omp parallel for shared(graph, dist)
    for (int i = 0; i < V; i++) {
        for (int j = 0; j < V; j++) {
            if (graph[i][j] != 0) {
                dist[i][j] = graph[i][j];
            } else {
                dist[i][j] = 10000000; //let's just say this is large enough
            }
        }
    }

#pragma omp parallel for shared(dist)
    for (int i = 0; i < V; i++) {
        dist[i][i] = 0;
    }

    //find shortest paths
//#pragma omp parallel shared(dist){
    for (int k = 0; k < V; k++) {
//#pragma omp barrier
#pragma omp parallel for
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (dist[i][j] > dist[i][k] + dist[k][j]) {
                    dist[i][j] = dist[i][k] + dist[k][j];
                }
            }
        }

    }

    end = omp_get_wtime();
    printf("Time: %lf\n", end - start);
    //print results
    for(int i = 0; i < V; ++i) {
        for(int j = 0; j <  V; ++j) {
            //printf("%d ", dist[i][j]);
            fprintf(outfile, "%d ", dist[i][j]);
        }
        //printf("\n");
        fprintf(outfile,"\n");
    }
}

void main(int argc, char *argv[]){
    char* infile = "FWinp.txt";
    char* outfile = "out.txt";
    int V = 4;
    if (argc > 1) {
        infile = argv[1];
        outfile = argv[2];
        V = atoi(argv[3]);
    }
    findShortestPath(infile, outfile, V);

}

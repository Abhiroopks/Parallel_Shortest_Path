//
// Created by mohai on 4/21/2020.
//

#include <stdio.h>
#include <omp.h>
#include <stdlib.h>
#include <string.h>

int basecase;

void FWSequential(int** dist,int Arow,int Acol,int Brow,int Bcol, int Crow, int Ccol, int n) {
    for (int a_k = Acol, b_k = Brow; a_k < Acol + n; a_k++, b_k++) {
//#pragma omp parallel for
        for (int c_j = Ccol, b_j = Bcol; b_j < Bcol+n; c_j++, b_j++) {
            for (int c_i = Crow, a_i = Arow; a_i < Arow + n; c_i++, a_i++) {
                if (dist[c_i][c_j] > dist[a_i][a_k] + dist[b_k][b_j])
                    dist[c_i][c_j] = dist[a_i][a_k] + dist[b_k][b_j];
            }
        }
    }
}
//A11 = Arow+0,Acol+0,
//A12 = Arow+0,Acol+1/2
//A21 = Arow+1/2,Acol+0,
//A22 = Arow+1/2,Acol+1/2,

void FWRecursive(int** dist, int Arow,int Acol,int Brow,int Bcol, int Crow, int Ccol, int n) {
    if (n <= basecase) {
        FWSequential(dist, Arow, Acol, Brow, Bcol, Crow, Ccol, n);
    } else {
//From: Parallelizing the Floyd-Warshall Algorithm on Modern Multicore Platforms: Lessons Learned  paper
        //DIDN'T WORK
//        FWRecursive(dist, Arow + 0,     Acol + 0,     Brow + 0,     Bcol + 0, Crow + 0, Ccol + 0, n / 2); // (A11, B11, C11);
//        FWRecursive(dist, Arow + 0,     Acol + n / 2, Brow + 0,     Bcol + 0, Crow + 0, Ccol + n / 2, n / 2); //(A12, B11, C12);
//        FWRecursive(dist, Arow + n / 2, Acol + 0,     Brow + n / 2, Bcol + 0, Crow + 0, Ccol + 0, n / 2);//(A21, B21, C11);
//        FWRecursive(dist, Arow + n / 2, Acol + n / 2, Brow + n / 2, Bcol + 0, Crow + 0, Ccol + n / 2, n / 2);//(A22, B21, C12);
//        FWRecursive(dist, Arow + n / 2, Acol + n / 2, Brow + n / 2, Bcol + 0, Crow + 0, Ccol + n / 2, n / 2);//(A22, B21, C12);
//        FWRecursive(dist, Arow + n / 2, Acol + 0,     Brow + n / 2, Bcol + 0, Crow + 0, Ccol + 0, n / 2);//(A21, B21, C11);
//        FWRecursive(dist, Arow + 0,     Acol + n / 2, Brow + 0,     Bcol + 0, Crow + 0, Ccol + n / 2, n / 2);//(A12, B11, C12);
//        FWRecursive(dist, Arow + 0,     Acol + 0,     Brow + 0,     Bcol + 0, Crow + 0, Ccol + 0, n / 2);//(A11, B11, C11);
//
//Inspired by: Auto-Blocking Matrix-Multiplication or Tracking BLAS3 Performance from Source Code paper
#pragma omp task
        FWRecursive(dist, Arow + 0,   Acol + 0,   Brow + 0,   Bcol + 0,   Crow + 0,   Ccol + 0,   n / 2);//A11, B11, C11
#pragma omp task
        FWRecursive(dist, Arow + n/2, Acol + 0,   Brow + 0,   Bcol + n/2, Crow + n/2, Ccol + n/2, n / 2);//A21, B12, C22
#pragma omp task
        FWRecursive(dist, Arow + 0,   Acol + n/2, Brow + n/2, Bcol + 0,   Crow + 0,   Ccol + 0,   n / 2);//A12, B21, C11
#pragma omp task
        FWRecursive(dist, Arow + 0,   Acol + 0,   Brow + 0,   Bcol + n/2, Crow + 0,   Ccol + n/2, n / 2);//A11, B12, C12
#pragma omp task
        FWRecursive(dist, Arow + n/2, Acol + n/2, Brow + n/2, Bcol + n/2, Crow + n/2, Ccol + n/2, n / 2);//A22, B22, C22
#pragma omp task
        FWRecursive(dist, Arow + n/2, Acol + 0,   Brow + 0,   Bcol + 0,   Crow + n/2, Ccol + 0,   n / 2);//A21, B11, C21
#pragma omp task
        FWRecursive(dist, Arow + 0,   Acol + n/2, Brow + n/2, Bcol + n/2, Crow + 0,   Ccol + n/2, n / 2);//A12, B22, C12
#pragma omp task
        FWRecursive(dist, Arow + n/2, Acol + n/2, Brow + n/2, Bcol + 0,   Crow + n/2, Ccol + 0,   n / 2);//A22, B21, C21
    }
}

void findShortestPath(char ifile[], char ofile[], int n, int cores){
    FILE* infile = NULL;
    FILE* outfile = NULL;

    infile = fopen(ifile, "r");
    if (!infile) {
        printf("Error: Cannot open file %s\n", ifile);
        exit(1);
    }
    outfile = fopen(ofile, "w");

    double start, end;
    omp_set_num_threads(cores);
    start = omp_get_wtime();
    //set up the graph
    int V = n;
    //fscanf(infile, "%d %d", &row, &col); //get the dimensions
    int graph[V][V];
    for(int i = 0; i < V; i++) {
        for(int j = 0; j < V; j++)
            fscanf(infile, "%d", &graph[i][j]);
    }


    //    //initialize distances
    int** dist = (int**) malloc(V* sizeof(int*));

    for (int i = 0; i < V; i++){
        dist[i] = (int*) malloc(V* sizeof(int));
    }

#pragma omp parallel for
    for (int i = 0; i < V; i++) {
        for (int j = 0; j < V; j++) {
            if (graph[i][j] != 0) {
                dist[i][j] = graph[i][j];
            } else {
                dist[i][j] = 10000000; //let's just say this is large enough
            }
        }
    }

#pragma omp parallel for
    for (int i = 0; i < V; i++) {
        dist[i][i] = 0;
    }

    //FWRecursive(V, dist, dist, dist);
    FWRecursive(dist,0,0,0,0,0,0,V);
    end = omp_get_wtime();
    printf("%lf\n", (end - start)*1000);
    //print results
    for(int i = 0; i < V; ++i) {
        for(int j = 0; j <  V; ++j) {
            //printf("%d ", dist[i][j]);
            fprintf(outfile, "%d ", dist[i][j]);
        }
        //printf("\n");
        fprintf(outfile,"\n");
    }

    fclose(infile);
    fclose(outfile);
}

void main(int argc, char *argv[]){
    char* infile = "FWinp.txt";
    char* outfile = "out.txt";
    int V = 4;
    int cores = 2;
    if (argc > 1) {
        infile = argv[1];
        outfile = argv[2];
        V = atoi(argv[3]);
	cores = atoi(argv[4]);
	basecase = atoi(argv[5]);
    }
    findShortestPath(infile, outfile, V,cores);

}

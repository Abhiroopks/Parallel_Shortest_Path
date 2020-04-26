#include <stdio.h>
#include <omp.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX 10000000 // very big number - dont expect an edge weight to exceed this

struct edge{
	int src;
	int dest;
	int weight;
};

void findShortestPath(char ifile[], char ofile[], int n){
	FILE* infile = NULL;
	FILE* outfile = NULL;

	infile = fopen(ifile, "r");
	if (!infile) {
		printf("Error: Cannot open file %s\n", ifile);
		exit(1);
	}
	outfile = fopen(ofile, "w");

	//set up the graph
	int V = n;
	int numedges = 0;
	//fscanf(infile, "%d %d", &row, &col); //get the dimensions
	int graph[V][V];

	for(int i = 0; i < V; i++) {
		for(int j = 0; j < V; j++){
		    fscanf(infile, "%d", &graph[i][j]);
		    if(graph[i][j] != 0){
			numedges++; //count number of edges
	   	    }
		}
	}

	//initialize distances
	int dist[V];
	//if during a step, no vertex changed, terminate early.
	bool change;
	bool visited[V];
	for (int i = 0; i < V; i++) {
		dist[i] = MAX;
		visited[i] = false;
	}
	dist[0] = 0; //source is init to 0

	//create edges
	struct edge edges[numedges];
	int edgescount = 0;
	//go through the graph - all weights != 0 is a valid edge
	for(int i = 0; i < V; i++){
		for(int j = 0; j < V; j++){
			if(graph[i][j] != 0){
				edges[edgescount].src = i;
				edges[edgescount].dest = j;
				edges[edgescount].weight = graph[i][j];
				edgescount++;
			}
		}
	}

	double start, end;
	start = omp_get_wtime();

	//find shortest paths
	int newDist;
	//repeat V-1 times
	for(int i = 0; i < V - 1; i++){
		change = false;
		//go over all edges
		for(int j = 0; j < numedges; j++){
			newDist = dist[edges[j].src] + edges[j].weight;
			if(newDist < dist[edges[j].dest]){
				dist[edges[j].dest] = newDist;
				change = true;
			}
		}
		if(!change){break;} //terminate early if no vertices were updated
	}
	
	end = omp_get_wtime();
	printf("%lf\n", (end - start)*1000);
	//print results
	for(int i = 0; i < V; i++){
		fprintf(outfile, "Vertex: %d Path weight: %d\n",i,dist[i]);	
	}

	fclose(infile);
	fclose(outfile);
}



void main(int argc, char *argv[]){
	char* infile = "bfcinput.txt";
	char* outfile = "bfcout.txt";
	int V = 4;
	if (argc > 1) {
	infile = argv[1];
	outfile = argv[2];
	V = atoi(argv[3]);
	}
	findShortestPath(infile, outfile, V);

}

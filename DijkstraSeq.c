#include <stdio.h>
#include <omp.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX 10000000 // very big number - dont expect an edge weight to exceed this

int getMinIndex(int d[], bool vis[],int V);

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
	//fscanf(infile, "%d %d", &row, &col); //get the dimensions
	int graph[V][V];

	for(int i = 0; i < V; i++) {
		for(int j = 0; j < V; j++)
		    fscanf(infile, "%d", &graph[i][j]);
	}

	//initialize distances
	int dist[V];
	bool visited[V];
	for (int i = 0; i < V; i++) {
		dist[i] = MAX;
		visited[i] = false;
	}
	dist[0] = 0; //source is init to 0

	//find shortest paths
	int visitedNum = 0; //track how many nodes we visited in total
	int currIndex; //index of current node
	int newDist;
	double start, end;
	start = omp_get_wtime();
	while(visitedNum != V){
		currIndex = getMinIndex(dist,visited,V);
		visited[currIndex] = true; //mark as visited
		visitedNum ++;
		for(int i = 0; i < V; i++){ //go over every neighbor
			if(graph[currIndex][i] != 0 && !visited[i]){ //ensure unvisited neighbor and edge exists
				newDist = dist[currIndex] + graph[currIndex][i]; //new distance is dist to currNode + edge to neighbor
				if(newDist < dist[i]){ //update dist to neighbor if needed
					dist[i] = newDist;
				}
			}
		}
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


//return index of min distance node,that is not visited
//O(V) time
int getMinIndex(int d[], bool vis[],int V){
	int minVal = MAX;
	int minInd = -1;
	for(int i = 0; i < V; i++){
		if(d[i] < minVal && !vis[i]){
			minVal = d[i];
			minInd = i;
		}
	}

	return minInd;
}

void main(int argc, char *argv[]){
	char* infile = "dijinput.txt";
	char* outfile = "dijout.txt";
	int V = 4;
	if (argc > 1) {
	infile = argv[1];
	outfile = argv[2];
	V = atoi(argv[3]);
	}
	findShortestPath(infile, outfile, V);

}

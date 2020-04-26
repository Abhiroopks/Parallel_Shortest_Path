#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdlib.h>  // rand(), srand()
#include <time.h>    // time()

void main(int argc, char *argv[]) {
	FILE *outfile = NULL;
	//default values if no inputs
	char* filename = "matrix.txt";
	int numV = 5; 

	//read filename
	if(argc > 2){
		filename = argv[2];
	}

	//read dimension
	if (argc > 1){ 
		numV = atoi(argv[1]);
	}

	outfile = fopen(filename, "w");
	int** matrix = (int**) malloc(sizeof(int*) * numV);
	for (int i = 0; i < numV; i++){
		matrix[i] = (int*) malloc(sizeof(int) * numV);
	}

	//seed with time so it's truly randomized each run
	srand(time(0));

	for (int i = 0; i < numV; ++i) {
		for (int j = i; j < numV; ++j) {
			int num = rand() % 5;
			matrix[i][j] = num;
			matrix[j][i] = num;
		}
	}


	for (int i = 0; i < numV; ++i) {
		for (int j = i; j < numV; ++j) {
			int num = rand()%10;
			if (num < 5 || i == j) {
				matrix[i][j] = 0;
				matrix[j][i] = 0;
			}
		}
	}

	for (int i = 0; i < numV; ++i) {
		for (int j = 0; j < numV; ++j) {
			fprintf(outfile, "%d ", matrix[i][j]);
		}
		fprintf(outfile, "\n");
	}

	for (int i = 0; i < numV; i++){
		free(matrix[i]);
	}
	free(matrix);
	fclose(outfile);


}

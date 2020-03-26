#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void main(int argc, char *argv[]) {
	FILE *outfile = NULL;
	char* filename = "output.txt";
	int numRows = 5;
	int numCols = 5;
	if (argc > 1) filename = argv[1];
	if (argc > 2) numRows = atoi(argv[2]);
	if (argc > 3) numCols = atoi(argv[3]);

	outfile = fopen(filename, "w");
	int** matrix = (int**) malloc(sizeof(int*) * numRows);
	for (int i = 0; i < numRows; i++){
		matrix[i] = (int*) malloc(sizeof(int) * numCols);
	}

	for (int i = 0; i < numRows; ++i) {
		for (int j = i; j < numCols; ++j) {
			int num = rand() % 5;
			matrix[i][j] = num;
			matrix[j][i] = num;
		}
	}
	for (int i = 0; i < numRows; ++i) {
		for (int j = i; j < numCols; ++j) {
			int num = rand()%2;
			if (num == 0) {
				matrix[i][j] = num;
				matrix[j][i] = num;
			}
		}
	}

	for (int i = 0; i < numRows; ++i) {
		for (int j = 0; j < numCols; ++j) {
			fprintf(outfile, "%d ", matrix[i][j]);
		}
		fprintf(outfile, "\n");
	}

	for (int i = 0; i < numRows; i++){
		free(matrix[i]);
	}
	free(matrix);
	fclose(outfile);


}
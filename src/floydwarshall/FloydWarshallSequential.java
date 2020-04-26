import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FloydWarshallSequential {
	int[][] dist;
	int[][] graph;
	int V; //number of vertices 
	
	public FloydWarshallSequential(String filename) throws FileNotFoundException {
		createGraph(filename); //V and graph initialized in here
	}
	
	public void initializeDist() {
		dist = new int[V][V];
		for (int i = 0; i < V; i++) {
			for (int j = 0; j < V; j++) {
				if (graph[i][j] != 0) {
					dist[i][j] = graph[i][j];
				} else {
					dist[i][j] = 10000000; //let's just say this is large enough
				}
			}
		}
		
		for (int i = 0; i < V; i++) {
			dist[i][i] = 0;
		}
	}
	
	public void findShortestPaths() {
		for (int k = 0; k < V; k++) {
			for (int i = 0; i < V; i++) {
				for (int j = 0; j < V; j++) {
					if (dist[i][j] > dist[i][k] + dist[k][j]) {
						dist[i][j] = dist[i][k] + dist[k][j];
					}
				}
			}
		}
	}
	
	private void createGraph(String filename) throws FileNotFoundException {
		File inFile = new File(filename);
		Scanner in = new Scanner(inFile);
		
		//Find total number of vertices in the graph
		int numVertices = 0;
		String[] length = in.nextLine().trim().split("\\s+");
		  for (int i = 0; i < length.length; i++) {
		    numVertices++;
		  }
		in.close();
		V = numVertices;
		graph = new int[V][V];

		in = new Scanner(inFile);
		
		//convert the adjacency matrix from the input file into an adjacency list
		int vertex = 0;

		while (in.hasNextLine()) {
		  String[] currentLine = in.nextLine().trim().split("\\s+"); 
		     for (int i = 0; i < currentLine.length; i++) {
		    	int weight = Integer.parseInt(currentLine[i]);
		    	if (weight != 0) {
		    		graph[vertex][i] = weight;
		    	}
		     }
		     vertex++;
		 } 
	}
	
	public void printDistances() {
		for (int i = 0; i < V; i++) { 
			for (int j = 0; j < V; j++) {
				if (Math.abs(dist[i][j]) < 1000000) {
					System.out.print(dist[i][j] + " ");
				}else {
					System.out.print("I ");
				}
			}
			System.out.println();
		}
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		FloydWarshallSequential shortestPathFinder = new FloydWarshallSequential("FWinp.txt");
		long startTime = System.nanoTime();
		shortestPathFinder.initializeDist();
		shortestPathFinder.findShortestPaths();
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		System.out.println("Execution time in nanoseconds  : " + timeElapsed);
		shortestPathFinder.printDistances();
	}
	
	
	
}

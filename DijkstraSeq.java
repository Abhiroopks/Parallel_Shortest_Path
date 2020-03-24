import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class DijkstraSeq {
	static int n; //# of nodes
	static List<HashMap<Integer,Integer>> adjList; //index of list is vertex number, mapping is <adjacent vertex, weight>
	
	static int[] dist; //distance of each node from node 0
	static int[] prev; //prev node from destination (used to find path from node 0 to every other node)
	static List<Integer> visited; //keep track of visited nodes
	
	
	//prints min distances from node 0 to all other nodes
	private static void printDistances() {
		for(int i = 0; i < n; i++) {
			if(dist[i] >= 0) {
				System.out.println("Vertex: " + i +  " Path weight: " + dist[i]);
			}
		}
	}
	
	//will show the paths from node0 to all other nodes, if reachable
	private static void printPaths() {
		
		String sequence;
		Integer dest;
		Integer curr;
		
		for(dest = 1; dest < n; dest++) {
			sequence = "";
			curr = dest; //save copy
			if(prev[dest] >= 0) { //if reachable
				while(curr > 0) {
					sequence = " -> " + curr.toString() + sequence;
					curr = prev[curr]; //backtrack one node
				}
				sequence = "0" + sequence;
				System.out.println(sequence);	
			}
		}
	}
	
	private static int findMinIndex() { //returns index of node with min dist from node0 that hasn't been visited
		int min = Integer.MAX_VALUE;
		int minInd = 0;
		for(int i = 0; i < n; i++) {
			if(!visited.contains(i) && dist[i] < min) {
				min = dist[i];
				minInd = i;
			}
		}
		return minInd;
	}

	private static void findPaths() {
		
		HashMap<Integer,Integer> curr;
		int currIndex;
		int newDist;
		
		while(visited.size() != n) {
			//find curr node with shortest distance
			currIndex = findMinIndex();
			curr = adjList.get(currIndex); //returns the map<neighbor, weight> for this node
			visited.add(currIndex);
			
			for(Integer neighbor : curr.keySet()) { //each neighbor of curr
				if(!visited.contains(neighbor)) { //not yet visited
					newDist = dist[currIndex] + curr.get(neighbor); //dist to curr node + edge distance to neighbor
					if(newDist < dist[neighbor]) { //shorter path found to this neighbor
						dist[neighbor] = newDist;
						prev[neighbor] = currIndex;
					}
				}
			}
		}
	}
	
	
	
	
	//from mohaimin code
	private static void createGraph(String filename) throws FileNotFoundException {
		
		File inFile = new File( System.getProperty("user.dir")+ filename);
		Scanner in = new Scanner(inFile);
		
		//Find total number of vertices in the graph
		int numVertices = 0;
		String[] length = in.nextLine().trim().split("\\s+");
		  for (int i = 0; i < length.length; i++) {
		    numVertices++;
		  }
		in.close();
		n = numVertices;
		adjList = new ArrayList<>(n);
		//Initialize adjacency list
		for (int i = 0; i < numVertices; i++) {
			adjList.add(new HashMap<Integer, Integer>());
		}

		in = new Scanner(inFile);
		
		//convert the adjacency matrix from the input file into an adjacency list
		int vertex = 0;
		//int max = 0;
		while (in.hasNextLine()) {
		  String[] currentLine = in.nextLine().trim().split("\\s+"); 
		     for (int i = 0; i < currentLine.length; i++) {
		    	int weight = Integer.parseInt(currentLine[i]);
		    	if (weight > 0) {
		    		//if (weight > max) max = weight;
		    		adjList.get(vertex).put(i, weight);    
		    	}
		     }
		     vertex++;
		 } 
		//L = max*(n-1);
		
		dist = new int[n];
		prev = new int[n];
		//init the other arrays
		for(int i = 0 ; i < n; i ++) {
			dist[i] = Integer.MAX_VALUE;
			prev[i] = -1;
		}
		
		dist[0] = 0;	
		visited = new ArrayList<Integer>(n);
		
		in.close();
	
	}

	public static void main(String[] args)throws FileNotFoundException {
		
		//I had to put the \\src\\ to make it work on eclipse, idk
		createGraph("\\src\\inp.txt");
		findPaths();
		//printPaths(); //print actual paths
		printDistances(); //print distances only

	}

}

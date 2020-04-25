import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DijkstraParallel {
	
	//For path finding
	int nodes; //# of nodes
	int cores;
	//For path finding ^
	
	List<HashMap<Integer,Integer>> adjList; //index of list is vertex number, mapping is <adjacent vertex, weight>
	
	int[] dist; //distance of each node from node 0
	int[] prev; //prev node from destination (used to find path from node 0 to every other node)
	List<Integer> visited; //keep track of visited nodes
	HashMap<Integer,Integer> currNode; //used by threads 
	int currIndex; //used by threads
	
	//For min finding
	int nodes_per_core;
	int[] minArray;
	List<minThread> minThreads = new ArrayList<minThread>();
	//for min finding ^ 



	
	//will run the threads
	ExecutorService executorService; 


	private class minThread implements Callable<Void>{
		int myId;
		int startIndex;
		int endIndex;

		minThread(int id){
			myId = id;
			startIndex = id*nodes_per_core;
			endIndex = startIndex + nodes_per_core - 1;
		}
		
		@Override
		public Void call() throws Exception {
			//run O(n) algo for our segment
			int min = Integer.MAX_VALUE;
			int minInd = 0;
			for(int i = startIndex; i <= endIndex && i < nodes; i++){
				if(!visited.contains(i) && dist[i] < min){
					min = dist[i];
					minInd = i;
				}
			}

			//store local min in global array
			minArray[myId] = minInd;
			return null;


			
		}
		
	}

	private class dijkstraThread implements Callable<Void>{
		int myIndex;
		
		
		//constructor - assign the nodes for our thread
		dijkstraThread(int ind){
			myIndex = ind;
		}


		@Override
		public Void call() throws Exception {
			int newDist;
			//go thru all nodes I am responsible for, within bounds
			if(currNode.containsKey(myIndex)) { //if the currNode has this neighbor
				if(!visited.contains(myIndex)) { //not yet visited
					newDist = dist[currIndex] + currNode.get(myIndex);
					if(newDist < dist[myIndex]) {
						dist[myIndex] = newDist;
						prev[myIndex] = currIndex;
					}
				}
			}
		
		
		return null;
		}
	}
	
	
	//prints min distances from node 0 to all other nodes
	public String printDistances() {
		StringBuilder str = new StringBuilder();
		
		for(int i = 0; i < nodes; i++) {
			str.append("Vertex: " + i +  " Path weight: " + dist[i] + "\n");			
		}
		
		return str.toString();
		
	}
	
	//will show the paths from node0 to all other nodes, if reachable
	public String printPaths() {
		
		String sequence;
		Integer dest;
		Integer curr;
		StringBuilder str = new StringBuilder();
		
		for(dest = 1; dest < nodes; dest++) {
			sequence = "";
			curr = dest; //save copy
			if(prev[dest] >= 0) { //if reachable
				while(curr > 0) {
					sequence = " -> " + curr.toString() + sequence;
					curr = prev[curr]; //backtrack one node
				}
				sequence = "0" + sequence;
				str.append(sequence + "\n");	
			}
		}
		
		return str.toString();
	}
	
	
	//returns index of node with min dist from node0 that hasn't been visited

	private int findMinIndex() throws InterruptedException { 
		executorService.invokeAll(minThreads);
		int min = Integer.MAX_VALUE;
		int minInd = 0;
		for(Integer i : minArray) {
			if(!visited.contains(i) && dist[i] < min) {
				min = dist[i];
				minInd = i;
			}
		}
		return minInd;
	}

	private void processNeighbors() throws InterruptedException{
		int neighbors = currNode.size();
		List<dijkstraThread> collection = new ArrayList<dijkstraThread>(neighbors);
		for(Integer i : currNode.keySet()){
			collection.add(new dijkstraThread(i));
		}
		executorService.invokeAll(collection);
		
		
		
	}

	public void findShortestPaths() throws InterruptedException {
		
		while(visited.size() != nodes) {
			//find curr node with shortest distance
			currIndex = findMinIndex(); 
			currNode = adjList.get(currIndex); //returns the map<neighbor, weight> for this node
			visited.add(currIndex); //mark as visited
			
			//run all threads
			processNeighbors();
		}
		
		//shut it down for good
		executorService.shutdown();
	}
	
	
	//from mohaimin code
	public List<HashMap<Integer,Integer>> createGraph(String filename) throws FileNotFoundException {
		
		File inFile = new File(filename);
		Scanner in = new Scanner(inFile);
		
		//Find total number of vertices in the graph
		int numVertices = 0;
		String[] length = in.nextLine().trim().split("\\s+");
		  for (int i = 0; i < length.length; i++) {
		    numVertices++;
		  }
		in.close();
		nodes = numVertices;
		List<HashMap<Integer,Integer>> adjListLocal = new ArrayList<HashMap<Integer,Integer>>(nodes);
		//Initialize adjacency list
		for (int i = 0; i < numVertices; i++) {
			adjListLocal.add(new HashMap<Integer, Integer>());
		}

		in = new Scanner(inFile);
		
		//convert the adjacency matrix from the input file into an adjacency list
		int vertex = 0;
		while (in.hasNextLine()) {
		  String[] currentLine = in.nextLine().trim().split("\\s+"); 
		     for (int i = 0; i < currentLine.length; i++) {
		    	int weight = Integer.parseInt(currentLine[i]);
		    	if (weight > 0) {
		    		adjListLocal.get(vertex).put(i, weight);    
		    	}
		     }
		     vertex++;
		} 
		
		return adjListLocal;
	}
	
	public void initVariables(List<HashMap<Integer,Integer>> adjListLocal, int ncores) throws FileNotFoundException {
		//createGraph(filename);
		adjList = adjListLocal;
		nodes = adjList.size();
		
		dist = new int[nodes];
		prev = new int[nodes];
		//init the other arrays
		for(int i = 0 ; i < nodes; i ++) {
			dist[i] = Integer.MAX_VALUE;
			prev[i] = -1;
		}
		
		dist[0] = 0;	
		visited = new ArrayList<Integer>(nodes);
		
		cores = ncores;
		nodes_per_core = nodes / cores;
		//make sure all nodes are covered
		if(nodes % cores > 0){
			nodes_per_core ++;
		}
		for(int i = 0; i < cores; i++){
			minThreads.add(new minThread(i));
		}

		minArray = new int[cores];

		executorService = Executors.newFixedThreadPool(cores);
	}
	
	public static void main(String[] args)throws FileNotFoundException, InterruptedException {
		
		//I had to put the \\src\\ to make it work on eclipse, idk
//		createGraph("\\src\\inp.txt");
//		initVariables();
//		findPaths();
//		//printPaths(); //print actual paths
//		System.out.println(printDistances()); //print distances only

	}
	

	
	
	

}

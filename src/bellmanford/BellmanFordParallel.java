import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class BellmanFordParallel {
	
	int nodes; //# of nodes
	int edges; // # of edges
	List<myEdge> edgesList; //list of edges
	Integer[] dist; //distance of each node from node 0
	int[] prev;
	int maxweight; //ensure no overflow
	
	//For path findi
	int cores;
	int edges_per_core;
	
	List<BFThread> pathThreads = new ArrayList<BFThread>();
	//will run the threads
	ExecutorService executorService; 
	// locks for concurrent writes
	ReentrantLock[] locks;
	

	
	private class BFThread implements Callable<Void>{
		//each thread is responsible for a certain range of nodes (inclusive)
		int startEdge;
		int endEdge;
		
		
		//constructor - assign the edges for our thread
		BFThread(int id){
			startEdge = id*edges_per_core;
			endEdge = startEdge + edges_per_core - 1;
			if(endEdge > edges-1) { //if end is out of bounds
				endEdge = edges-1;
			}
		}


		@Override
		public Void call() throws Exception {
			int newDist;
		
			//go thru all edges I am responsible for, within bounds
			for(int i = startEdge; i <= endEdge && i < edges; i++) { 
				
				newDist = dist[edgesList.get(i).src] + edgesList.get(i).weight;
				//no need to update, go to next edge
				if(newDist >= dist[edgesList.get(i).dest]){
					continue;
				}
				//need to update, so lock the destination vertex
			//	locks[edgesList.get(i).dest].lock();
				dist[edgesList.get(i).dest] = newDist;
				prev[edgesList.get(i).dest] = edgesList.get(i).src;
			//	locks[edgesList.get(i).dest].unlock();
			}
				
			
			return null;
		}
	}
	
	
	//prints min distances from node 0 to all other nodes
	public String printDistances() {
		StringBuilder str = new StringBuilder();
		int d;
		
		for(int i = 0; i < nodes; i++) {
			if(dist[i] == Integer.MAX_VALUE - maxweight){dist[i] += maxweight;} //remove offset
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
	
	public void findShortestPaths() throws InterruptedException {
		for(int i = 0; i < nodes-1; i++){
			executorService.invokeAll(pathThreads);
		}
		executorService.shutdown();

	}
		
	//returns the created graph
	@SuppressWarnings("resource")
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
		nodes = numVertices;
		edgesList = new ArrayList<myEdge>();
	

		in = new Scanner(inFile);
		
		//convert the adjacency matrix from the input file into an adjacency list
		int vertex = 0;
		edges = 0;
		maxweight = 0; //keep track of max weight
		while (in.hasNextLine()) {
		  String[] currentLine = in.nextLine().trim().split("\\s+"); 
		     for (int i = 0; i < currentLine.length; i++) {
		    	int weight = Integer.parseInt(currentLine[i]);
		    	if (weight > 0) {
		    		if(weight > maxweight) {maxweight = weight;}
		    		edgesList.add(new myEdge(vertex,i,weight));    //add edge to list 
		    		edges ++;
		    	}
		     }
		     vertex++;
		 } 
		
	}

	
	//from mohaimin code
	public void initVariables(String filename, int cores) throws FileNotFoundException {
		createGraph(filename);
		dist = new Integer[nodes];
		prev = new int[nodes];
		//locks = new ReentrantLock[nodes];
		for(int i = 0 ; i < nodes; i ++) {
			dist[i] = new Integer(Integer.MAX_VALUE - maxweight);
			prev[i] = -1;
		//	locks[i] = new ReentrantLock();
		}
		
		dist[0] = 0;	
		
		//init the threads
		this.cores = cores;
		if(cores > edges) {
			cores = edges; //don't need more cores than edges
		}
		
		//
		edges_per_core = edges/cores; 
		//ensure enough threads are created
		if(edges % cores > 0) {
			edges_per_core ++;
		}
		
		executorService = Executors.newFixedThreadPool(cores);
		//allocate space for path finding threads
		for(int i = 0; i < cores; i ++) {
			pathThreads.add(new BFThread(i));
		}
		

		
		

	}
	
	class myEdge{
		public int src;
		public int dest;
		public int weight;
		
		myEdge(int s, int d, int w){
			this.src = s;
			this.dest = d;
			this.weight = w;
		}
		
	}
	
	

}

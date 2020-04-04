import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BellmanFordSequential {
	
	int nodes; //# of nodes
	int edges; // # of edges
	List<myEdge> edgesList; //list of edges
	int[] dist; //distance of each node from node 0
	int maxweight; //ensure no overflow
	
	
	//prints min distances from node 0 to all other nodes
	public String printDistances() {
		StringBuilder str = new StringBuilder();
		
		for(int i = 0; i < nodes; i++) {
			str.append("Vertex: " + i +  " Path weight: " + dist[i] + "\n");			
		}
		
		return str.toString();
		
	}
	

	
	public void findShortestPaths() {
		int newDist;
		
		//for(int i = 0; i < nodes - 1; i++) { //repeat V - 1 times
			
			for(int j = 0; j < edges; j++) { //go over each edge
				//if shorter path found to this node from src, replace it with new dist
				
				newDist = dist[edgesList.get(j).src] + edgesList.get(j).weight;

				if(newDist < dist[edgesList.get(j).dest]) {
					dist[edgesList.get(j).dest] = newDist;
				}
			}
		//} 

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
	public void initVariables(String filename) throws FileNotFoundException {
		createGraph(filename);
		dist = new int[nodes];
	//	prev = new int[nodes];
		//init the other arrays
		for(int i = 0 ; i < nodes; i ++) {
			dist[i] = Integer.MAX_VALUE - maxweight;
			//prev[i] = -1;
		}
		
		dist[0] = 0;	
		//visited = new ArrayList<Integer>(nodes);

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

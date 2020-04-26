import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class DeltaSteppingSequential {
	int delta = 1;
	int L; //the maximum shortest path weight, let's just call it largest weight*(number of vertices-1)
	int n;
	List<Integer> tent; //array of tentative distances, index is vertex
	List<Set<Integer>> buckets; //array of buckets i.e. sets of vertices
	List<HashMap<Integer,Integer>> adjList; //index of list is vertex number, mapping is <adjacent vertex, weight>
	int[] prev;

	
	//used to keep track of source,dest of reqs
	private class edge{
		int src;
		int dest;

		edge(int s, int d){
			this.src = s;
			this.dest = d;
		}
	}
	
	private List<Set<Integer>>initBuckets(){
		List<Set<Integer>> buckets = new ArrayList<Set<Integer>>();
		for (int i = 0; i < L/delta; i++) {
			buckets.add(new HashSet<Integer>());
		}
		buckets.get(0).add(0); //add source vertex to B[0]
		return buckets;		
	}
	
	private static List<Integer> initTent(int n){
		List<Integer> tent = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) {
			tent.add(Integer.MAX_VALUE);
		}
		tent.set(0, 0);
		return tent;
	}
	
	public void initVariables(String filename) throws FileNotFoundException {
		createGraph(filename);
		tent = initTent(n);
		buckets = initBuckets();
		prev = new int[n];
		for(int i = 0; i < n ; i++){
			prev[i] = -1;
		}
	}
	
	public void findShortestPaths() {
		int i = 0;
		while ((i = NotEmpty(buckets)) >= 0) {
			Set<Integer> R = new HashSet<>();
			while (!buckets.get(i).isEmpty()) {
				//Map<Integer, Integer> reqs = findRequests(buckets.get(i), true);
				Map<edge, Integer> reqs = findRequests(buckets.get(i), true);
				R.addAll(buckets.get(i));
				buckets.get(i).clear();
				relaxRequests(reqs);
			}
			Map<edge, Integer> reqs = findRequests(R, false);
			relaxRequests(reqs);
		}
	}
	
	private Integer NotEmpty(List<Set<Integer>> buckets) {
		for (int i = 0; i < buckets.size(); i++) {
			if (!buckets.get(i).isEmpty()) {
				return i;
			}
		}
		return -1;
	}
	
	private Map<edge, Integer> findRequests(Set<Integer> V_prime, boolean light){
		Map<edge, Integer> reqs = new HashMap<edge,Integer>();
		for (Integer v: V_prime) { //for each vertex in the set V_prime
			HashMap<Integer, Integer> v_edges = adjList.get(v); //get edges incident to v from adjacency list
			for (Entry<Integer, Integer> entry : v_edges.entrySet()) {//for each edge incident to v
				Integer w = entry.getKey(); //get vertex w adjacent to v
				Integer d = entry.getValue(); //weight for edge (v,w)
				if (light) {
					if (d <= delta) {
						reqs.put(new edge(v,w), tent.get(v) + d);
					}
				} else { //heavy
					if (d > delta) {
						reqs.put(new edge(v,w), tent.get(v) + d);
					}
				}
				
			}			
		}
		return reqs;
	}
	
	private void relaxRequests(Map<edge, Integer> reqs) {
		for (Entry<edge, Integer> entry : reqs.entrySet()) {
			relax(entry.getKey(), entry.getValue());
		}
	}
	
	private void relax(edge e, int x) {
		if (x < tent.get(e.dest)) {
			if (tent.get(e.dest) != Integer.MAX_VALUE) {
				buckets.get(tent.get(e.dest)/delta).remove(new Integer(e.dest));
			} 
			buckets.get(x/delta).add(new Integer(e.dest));
			tent.set(e.dest, x);
			prev[e.dest] = e.src;
 		}
	}
	
	//returns the adjList created
	public void createGraph(String filename) throws FileNotFoundException {
		File inFile = new File(filename);
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
		int max_weight = 0;
		while (in.hasNextLine()) {
		  String[] currentLine = in.nextLine().trim().split("\\s+"); 
		     for (int i = 0; i < currentLine.length; i++) {
		    	int weight = Integer.parseInt(currentLine[i]);
		    	if (weight > 0) {
		    		if (weight > max_weight) max_weight = weight;
		    		adjList.get(vertex).put(i, weight);    
		    	}
		     }
		     vertex++;
		 } 
		L = max_weight*(n-1);
	}
	
	
	//will show the paths from node0 to all other nodes, if reachable
	public String printPaths() {
		
		String sequence;
		Integer dest;
		Integer curr;
		StringBuilder str = new StringBuilder();
		
		for(dest = 1; dest < n; dest++) {
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

	public String printDistances() {
		StringBuilder str = new StringBuilder();
		
		for(int i = 0; i < n; i++) {
			str.append("Vertex: " + i +  " Path weight: " + tent.get(i) + "\n");			
		}
		
		return str.toString();
		
	}

	public static void main(String args[]) throws FileNotFoundException {
//		initVariables("\\src\\inp.txt");
//		long startTime = System.nanoTime();
//		findShortestPaths();
//		long endTime = System.nanoTime();
//		long timeElapsed = endTime - startTime;
//		System.out.println("Execution time in nanoseconds  : " + timeElapsed);
//		System.out.println(printDistances());
		
	}
}

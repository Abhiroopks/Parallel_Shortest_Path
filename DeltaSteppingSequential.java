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
	}
	
	public void findShortestPaths() {
		int i = 0;
		while ((i = NotEmpty(buckets)) >= 0) {
			Set<Integer> R = new HashSet<>();
			while (!buckets.get(i).isEmpty()) {
				Map<Integer, Integer> reqs = findRequests(buckets.get(i), true);
				R.addAll(buckets.get(i));
				buckets.get(i).clear();
				relaxRequests(reqs);
			}
			Map<Integer, Integer> reqs = findRequests(R, false);
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
	
	private Map<Integer, Integer> findRequests(Set<Integer> V_prime, boolean light){
		Map<Integer, Integer> reqs = new HashMap<Integer,Integer>();
		for (Integer v: V_prime) { //for each vertex in the set V_prime
			HashMap<Integer, Integer> v_edges = adjList.get(v); //get edges incident to v from adjacency list
			for (Entry<Integer, Integer> entry : v_edges.entrySet()) {//for each edge incident to v
				Integer w = entry.getKey(); //get vertex w adjacent to v
				Integer d = entry.getValue(); //weight for edge (v,w)
				if (light) {
					if (d <= delta) {
						reqs.put(w, tent.get(v) + d);
					}
				} else { //heavy
					if (d > delta) {
						reqs.put(w, tent.get(v) + d);
					}
				}
				
			}			
		}
		return reqs;
	}
	
	private void relaxRequests(Map<Integer, Integer> reqs) {
		for (Entry<Integer, Integer> entry : reqs.entrySet()) {
			relax(entry.getKey(), entry.getValue());
		}
	}
	
	private void relax(int w, int x) {
		if (x < tent.get(w)) {
			if (tent.get(w) != Integer.MAX_VALUE) {
				buckets.get(tent.get(w)/delta).remove(new Integer(w));
			} 
			buckets.get(x/delta).add(new Integer(w));
			tent.set(w, x);
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

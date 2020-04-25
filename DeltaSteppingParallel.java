import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DeltaSteppingParallel {
	int delta;
	int L; //the maximum shortest path weight, let's just call it largest weight*(number of vertices-1)
	int n;
	Map<Integer, Integer> tent; //mapping of vertices to tentative distances
	Set[] buckets; //array of buckets i.e. sets of vertices
	int numBuckets;
	Queue<Integer> currentVertices;
	ExecutorService threadPool;
	ExecutorCompletionService<Void> execCompServ;
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
	public DeltaSteppingParallel(int ncores) {
		delta = 1;
		L = 10; 
		
		//threadPool = Executors.newCachedThreadPool();
		threadPool = Executors.newFixedThreadPool(ncores);
		execCompServ = new ExecutorCompletionService<>(threadPool);
		currentVertices = new ConcurrentLinkedQueue<>();
		numBuckets = L/delta;
		
	}
	
	public void initVariables(String filename) throws FileNotFoundException {
		createGraph(filename);
		
		tent = new ConcurrentHashMap<>();
		prev = new int[n];
		for (int i = 0; i < n; i++) {
			tent.put(i, Integer.MAX_VALUE);
			prev[i] = -1;
		}
		tent.replace(0, 0);
		buckets = new Set[numBuckets];
		for (int i = 0; i < numBuckets; i++) {
			buckets[i] = new ConcurrentSkipListSet<Integer>();
		}
		buckets[0].add(0);

	}
	public void findShortestPaths() throws InterruptedException {
		int i = 0;
		while ((i = NotEmpty(buckets)) >= 0) {
			Set<Integer> R = new ConcurrentSkipListSet<>();
			while (!buckets[i].isEmpty()) {
				Map<edge, Integer> reqs = findRequests(buckets[i], true);
				R = (Set<Integer>) buckets[i].parallelStream().collect(Collectors.toSet());
				//R.addAll(buckets[i]);
				buckets[i].clear();
				relaxRequests(reqs);
			}
			Map<edge, Integer> reqs = findRequests(R, false);
			relaxRequests(reqs);
		}
        threadPool.shutdown();

        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {}
	}
	
	
	
	private Integer NotEmpty(Set[] buckets) {
		for (int i = 0; i < numBuckets; i++) {
			if (!buckets[i].isEmpty()) {
				return i;
			}
		}
		return -1;
	}
	
	private Map<edge, Integer> findRequests(Set<Integer> V_prime, boolean light){
		Map<edge, Integer> reqs = new ConcurrentHashMap<edge,Integer>();
		if (light) {
			V_prime.parallelStream().forEach(v->{reqs.putAll(adjList.get(v).entrySet().parallelStream().filter(entry->entry.getValue() <= delta)
			.collect(Collectors.toMap(
				entry -> new edge(v,entry.getKey()),
				entry -> entry.getValue() + tent.get(v))));});
//				V_prime.parallelStream().flatMap(v->adjList.get(v).entrySet().stream()).filter(entry->entry.getValue() <= delta)
//				.collect(Collectors.mapping(
//					entry -> entry.getKey(),
//					entry -> entry.getValue()+ tent.get(v)));
		} else {
			V_prime.parallelStream().forEach(v->{reqs.putAll(adjList.get(v).entrySet().parallelStream().filter(entry->entry.getValue() > delta)
			.collect(Collectors.toMap(
				entry -> new edge(v,entry.getKey()),
				entry -> entry.getValue() + tent.get(v))));});
		}
		return reqs;
	}
	
	private void relaxRequests(Map<edge, Integer> reqs) throws InterruptedException {
		/*
		int numTasks = 0;
		for (Entry<Integer, Integer> entry : reqs.entrySet()) {
			execCompServ.submit(new Relax(entry.getKey(), entry.getValue()), null);
			numTasks++;
		}
		for (int i = 0; i < numTasks; i++) {
			try {
				execCompServ.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		*/
		int numTasks = reqs.size();
		List<Callable<Void>> collection = new ArrayList<>(numTasks);
		for(Entry<edge,Integer> entry : reqs.entrySet()){
			collection.add(toCallable(new Relax(entry.getKey(),entry.getValue())));
		}
		threadPool.invokeAll(collection);
		
	}
	
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
	
	private class Relax implements Runnable{
		edge e;
		int x;
		
		Relax(edge e, int x) {
			this.e = e;
			this.x = x;
		}
		
		public void run() {
			if (x < tent.get(e.dest)) {
				if (tent.get(e.dest) != Integer.MAX_VALUE) {
					buckets[tent.get(e.dest)/delta].remove(new Integer(e.dest));
				} 
				buckets[x/delta].add(new Integer(e.dest));
				tent.replace(e.dest, x);
				prev[e.dest] = e.src;
	 		}
		}
	}

	private Callable<Void> toCallable(final Runnable runnable) {
	    return new Callable<Void>() {
		@Override
		public Void call() throws Exception {
		    runnable.run();
		    return null;
		}
	    };
	}

	/*
	class Relax implements Runnable{
		int w;
		int x;
		
		Relax(int w, int x) {
			this.w = w;
			this.x = x;
		}
		
		@Override
		public void run() {
			if (x < tent.get(w)) {
				if (tent.get(w) != Integer.MAX_VALUE) {
					buckets[tent.get(w)/delta].remove(new Integer(w));
				} 
				buckets[x/delta].add(new Integer(w));
				tent.replace(w, x);
	 		}
		}
	}
	*/
	public String printDistances() {
		StringBuilder str = new StringBuilder();
		
		for(int i = 0; i < n; i++) {
			str.append("Vertex: " + i +  " Path weight: " + tent.get(i) + "\n");			
		}
		
		return str.toString();
		
	}
	
	
	
	public static void main(String args[]) throws FileNotFoundException {
		//DeltaSteppingParallel shortestPathFinder = new DeltaSteppingParallel();
		//shortestPathFinder.createGraph("\\src\\inp.txt");
		//shortestPathFinder.initVariables("\\src\\inp.txt");
		//long startTime = System.nanoTime();
		//shortestPathFinder.findShortestPaths();
		//long endTime = System.nanoTime();
		//long timeElapsed = endTime - startTime;
		//System.out.println("Execution time in nanoseconds  : " + timeElapsed);
		
		//System.out.println(shortestPathFinder.printDistances());
		
		//int numberOfProcessors = Runtime.getRuntime().availableProcessors();
		//System.out.println("Number of processors available to this JVM: " + numberOfProcessors);
	}
	
}

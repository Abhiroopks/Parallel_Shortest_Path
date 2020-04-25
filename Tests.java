import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;

public class Tests {

	String testFile = "matrix0.txt";

	@Test
	public void testDijkstra() throws FileNotFoundException, InterruptedException {
		DijkstraSeq dijS = new DijkstraSeq();
		List<HashMap<Integer,Integer>> adjList = dijS.createGraph(testFile);
		dijS.initVariables(adjList);
		dijS.findShortestPaths();
		
		DijkstraParallel dijP = new DijkstraParallel();
		dijP.initVariables(adjList,100);
		dijP.findShortestPaths();
		
		assertEquals(dijS.printDistances(),dijP.printDistances());
		
		//For Demo
		
		//System.out.println("Dijkstra Sequential Paths:\n" + dijS.printPaths());
		//System.out.println("Dijkstra Parallel Paths:\n" + dijP.printPaths());
		//System.out.println("Dijkstra Sequential Distances:\n" + dijP.printDistances());
		//System.out.println("Dijkstra Parallel Distances:\n" + dijP.printDistances());
		
		
		if(!(dijS.printPaths().equals(dijP.printPaths()))){
			System.out.println("Note: Dijkstra Seq , Parallel paths differ");
			System.out.println("Dijkstra Sequential Paths:\n" + dijS.printPaths());
			System.out.println("Dijkstra Parallel Paths:\n" + dijP.printPaths());

		}
	}
	@Test
	public void testDeltaStepping() throws FileNotFoundException, InterruptedException {
		DeltaSteppingSequential deltaS = new DeltaSteppingSequential();
		deltaS.initVariables(testFile);
		deltaS.findShortestPaths();
		
		DeltaSteppingParallel deltaP = new DeltaSteppingParallel(100);
		deltaP.initVariables(testFile);
		deltaP.findShortestPaths();
		
		assertEquals(deltaS.printDistances(),deltaP.printDistances());
		
		
		if(!(deltaS.printPaths().equals(deltaP.printPaths()))){
			System.out.println("Note: Delta-Stepping Seq, Parallel paths differ");
			System.out.println("Delta-Stepping Sequential Paths:\n" + deltaS.printPaths());
			System.out.println("Delta-Stepping Parallel Paths:\n" + deltaP.printPaths());
		}
		
		
	}

	@Test
	public void testBellmanFord() throws FileNotFoundException, InterruptedException {
		BellmanFordSequential bfs = new BellmanFordSequential();
		bfs.initVariables(testFile);
		bfs.findShortestPaths();
		
		BellmanFordParallel bfp = new BellmanFordParallel();
		bfp.initVariables(testFile,100);
		bfp.findShortestPaths();
		
		assertEquals(bfs.printDistances(),bfp.printDistances());
		if(!(bfs.printPaths().equals(bfp.printPaths()))){
			System.out.println("Note: Bellman-Ford Seq, Parallel paths differ");
			System.out.println("Bellman-Ford Sequential Paths:\n" + bfs.printPaths());
			System.out.println("Bellman-Ford Parallel Paths:\n" + bfp.printPaths());
		}
		
	}

	@Test
	public void testDijDelta() throws FileNotFoundException, InterruptedException {
		DijkstraParallel dijP = new DijkstraParallel();
		List<HashMap<Integer,Integer>> adjList = dijP.createGraph(testFile);
		dijP.initVariables(adjList,100);
		dijP.findShortestPaths();

		DeltaSteppingParallel deltaP = new DeltaSteppingParallel(100);
		deltaP.initVariables(testFile);
		deltaP.findShortestPaths();

		assertEquals(dijP.printDistances(),deltaP.printDistances());
		if(!(dijP.printPaths().equals(deltaP.printPaths()))){
			System.out.println("Note: Dijkstra, Delta-Stepping paths differ");
			System.out.println("Delta-Stepping Parallel Paths:\n" + deltaP.printPaths());
			System.out.println("Dijkstra Parallel Paths:\n" + dijP.printPaths());
		}
	}

	@Test
	public void testDeltaBF() throws FileNotFoundException, InterruptedException {
		DeltaSteppingParallel deltaP = new DeltaSteppingParallel(100);
		deltaP.initVariables(testFile);
		deltaP.findShortestPaths();

		BellmanFordParallel bfp = new BellmanFordParallel();
		bfp.initVariables(testFile,100);
		bfp.findShortestPaths();

		assertEquals(deltaP.printDistances(),bfp.printDistances());

		if(!(bfp.printPaths().equals(deltaP.printPaths()))){
			System.out.println("Note: Bellman-Ford, Delta-Stepping paths differ");
			System.out.println("Delta-Stepping Parallel Paths:\n" + deltaP.printPaths());
			System.out.println("Bellman-Ford Parallel Paths:\n" + bfp.printPaths());
		}
	}
}

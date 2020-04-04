import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;

public class Tests {

	@Test
	public void testDijkstra() throws FileNotFoundException, InterruptedException {
		DijkstraSeq dijS = new DijkstraSeq();
		List<HashMap<Integer,Integer>> adjList = dijS.createGraph("matrix1.txt");
		dijS.initVariables(adjList);
		dijS.findShortestPaths();
		
		DijkstraParallel dijP = new DijkstraParallel();
		dijP.initVariables(adjList,100);
		dijP.findShortestPaths();
		
		assertEquals(dijS.printDistances(),dijP.printDistances());
	}
	@Test
	public void testDeltaStepping() throws FileNotFoundException, InterruptedException {
		DeltaSteppingSequential deltaS = new DeltaSteppingSequential();
		deltaS.initVariables("matrix1.txt");
		deltaS.findShortestPaths();
		
		DeltaSteppingParallel deltaP = new DeltaSteppingParallel(100);
		deltaP.initVariables("matrix1.txt");
		deltaP.findShortestPaths();
		
		assertEquals(deltaS.printDistances(),deltaP.printDistances());
	}

	@Test
	public void testBellmanFord() throws FileNotFoundException, InterruptedException {
		BellmanFordSequential bfs = new BellmanFordSequential();
		bfs.initVariables("matrix1.txt");
		bfs.findShortestPaths();
		
		BellmanFordParallel bfp = new BellmanFordParallel();
		bfp.initVariables("matrix1.txt",100);
		bfp.findShortestPaths();
		
		assertEquals(bfs.printDistances(),bfp.printDistances());
	}

	@Test
	public void testDijDelta() throws FileNotFoundException, InterruptedException {
		DijkstraParallel dijP = new DijkstraParallel();
		List<HashMap<Integer,Integer>> adjList = dijP.createGraph("matrix1.txt");
		dijP.initVariables(adjList,100);
		dijP.findShortestPaths();

		DeltaSteppingParallel deltaP = new DeltaSteppingParallel(100);
		deltaP.initVariables("matrix1.txt");
		deltaP.findShortestPaths();

		assertEquals(dijP.printDistances(),deltaP.printDistances());
	}

	@Test
	public void testDeltaBF() throws FileNotFoundException, InterruptedException {
		DeltaSteppingParallel deltaP = new DeltaSteppingParallel(100);
		deltaP.initVariables("matrix1.txt");
		deltaP.findShortestPaths();

		BellmanFordParallel bfp = new BellmanFordParallel();
		bfp.initVariables("matrix1.txt",100);
		bfp.findShortestPaths();


		assertEquals(deltaP.printDistances(),bfp.printDistances());
	}
}

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;

public class Timings {
	@Test
	public void testDijkstraTime() throws FileNotFoundException, InterruptedException {
		long seqAvg;
		long parallelAvg;
		long start;
		long end;
		long diff;
		DijkstraSeq dijS;
		DijkstraParallel dijP;
		List<HashMap<Integer,Integer>> adjList;
		int iterations = 5;
		System.out.println("Dijkstra");
		System.out.println("Vertices    Cores    SequentialTime    ParallelTime");
		int V;
		//each matrix size
		for(int i=1; i <= 10; i++){
			//create the graph once and use for each iteration below
			dijS = new DijkstraSeq();
			dijP = new DijkstraParallel();
			adjList = dijS.createGraph("matrix" + Integer.toString(i) + ".txt");
			V = (i-1)*100 + 100;
			//# cores
			for(int j = 2; j <= 256; j*=2){
				seqAvg = 0;
				parallelAvg = 0;
				for(int k = 0; k < iterations; k++){
					dijS.initVariables(adjList);
					dijP.initVariables(adjList,j);
		
					start = System.nanoTime();
					dijS.findShortestPaths();
					end = System.nanoTime();
					diff = end - start;
					seqAvg += diff;
					
					start = System.nanoTime();
					dijP.findShortestPaths();
					end = System.nanoTime();
					diff = end - start;
					parallelAvg += diff;
				}
				//report timing data formatted
				seqAvg /= iterations;
				parallelAvg /= iterations;
				System.out.printf("%8d%9d%18d%16d\n",V,j,seqAvg,parallelAvg);		
			}
		}

		assertTrue(true);
	}

	@Test
	public void testDeltaTime() throws FileNotFoundException, InterruptedException {
		long seqAvg;
		long parallelAvg;
		long start;
		long end;
		long diff;
		DeltaSteppingSequential deltaS;
		DeltaSteppingParallel deltaP;
		int iterations = 5;
		System.out.println("Delta Stepping");
		System.out.println("Vertices    Cores    SequentialTime    ParallelTime");
		int V;
		//each matrix size
		for(int i=1; i <= 10; i++){
			//create the graph once and use for each iteration below
			V = (i-1)*100 + 100;
			//# cores
			for(int j = 2; j <= 256; j*=2){
				seqAvg = 0;
				parallelAvg = 0;
				for(int k = 0; k < iterations; k++){
					deltaS = new DeltaSteppingSequential();
					deltaP = new DeltaSteppingParallel(j);
					deltaS.initVariables("matrix" + Integer.toString(i) + ".txt");
					deltaP.initVariables("matrix" + Integer.toString(i) + ".txt");
		
					start = System.nanoTime();
					deltaS.findShortestPaths();
					end = System.nanoTime();
					diff = end - start;
					seqAvg += diff;
					
					start = System.nanoTime();
					deltaP.findShortestPaths();
					end = System.nanoTime();
					diff = end - start;
					parallelAvg += diff;
				}
				//report timing data formatted
				seqAvg /= iterations;
				parallelAvg /= iterations;
				System.out.printf("%8d%9d%18d%16d\n",V,j,seqAvg,parallelAvg);		
			}
		}

		assertTrue(true);
	}
}

package org.itmo;

import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Outcome(id = "1", expect = Expect.ACCEPTABLE, desc = "Only one thread marked the vertex")
@Outcome(id = "2", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Race condition: both threads marked vertex as visited")
@State
public class GraphBFSSimpleRaceTest {

    private final Graph graph;
    private final VisitCounter counter;
    private final boolean[] visited;

    public GraphBFSSimpleRaceTest() {
        graph = new Graph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        
        visited = new boolean[3];
        counter = new VisitCounter();
    }

    static class VisitCounter {
        private int markCount = 0;
        
        public void markVisited(int vertex, boolean[] visited) {
            if (!visited[vertex]) {
                visited[vertex] = true;
                markCount++;
            }
        }
        
        public int getCount() {
            return markCount;
        }
    }

    @Actor
    public void actor1() {
        counter.markVisited(1, visited);
    }

    @Actor
    public void actor2() {
        counter.markVisited(1, visited);
    }

    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = counter.getCount();
    }
}

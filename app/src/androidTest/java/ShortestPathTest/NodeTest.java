package ShortestPathTest;

import static org.junit.Assert.*;

import org.junit.Test;

import ShortestPath.Node;


public class NodeTest {
    /**
     * Constant used for comparison
     */
    private final static double EPS = 1e-9;

    /**
     * Testing distance
     */
    @Test
    public void distanceTest() {
        Node nodeOne = new Node(0, 0, "one");
        Node nodeTwo = new Node(1, 0, "two");

        assertEquals(nodeOne.distanceTo(nodeTwo), 1.0, EPS);
        assertEquals(nodeTwo.distanceTo(nodeOne), 1.0, EPS);
    }
}

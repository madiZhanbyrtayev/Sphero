package ShortestPathTest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ShortestPath.Graph;
import ShortestPath.Node;


public class SPFATest {
    /**
     * Constant used in comparison of doubles
     */
    private final static double EPS = 1e-9;

    /**
     * Test runner
     * Accepts information about graph and runs test
     * @param nodeList is list of nodes of graph
     * @param from is one side of edge
     * @param to is second side of edge
     * @param source is the label of source of a path
     * @param destination is the label of destination
     * @param shortestDistance distance between source and destination
     */
    private void runTest(List <Node> nodeList,
                         List <String> from,
                         List <String> to,
                         String source,
                         String destination,
                         double shortestDistance) {

        Graph graph = new Graph();

        for (Node node: nodeList) {
            graph.addNode(node);
        }

        for (int i = 0; i < from.size(); i++) {
            graph.addEdge(from.get(i), to.get(i));
        }

        List <Node> shortestPath = graph.getShortestPath(source, destination);

        double algoDistance = 0;
        for (int i = 1; i < shortestPath.size(); i++) {
            algoDistance += shortestPath.get(i - 1).distanceTo(shortestPath.get(i));
        }

        System.out.println("Expected: " + shortestDistance + ", found: " + algoDistance);
        System.out.println("Path:");
        for (Node node: shortestPath) {
            System.out.print(node.getLabel() + " ");
        }

        System.out.println();

        assertEquals(shortestDistance, algoDistance, EPS);
    }

    /**
     * Simple test with two nodes at coordinates (0, 1) and (0, 0)
     */
    @Test
    public void test1() {
        List <Node> nodeList = new ArrayList <>();

        nodeList.add(new Node(0.0, 1.0, "A"));
        nodeList.add(new Node(0.0, 0.0, "B"));

        List <String> from = new ArrayList <>();
        List <String> to = new ArrayList <>();

        from.add("A");
        to.add("B");

        String source = "A";
        String destination = "B";

        double result = 1.0;

        runTest(nodeList, from, to, source, destination, result);
    }

    /**
     * Simple test with three nodes and two shortest paths
     */
    @Test
    public void test2() {
        List <Node> nodeList = new ArrayList <>();

        nodeList.add(new Node(0.0, 0.0, "A"));
        nodeList.add(new Node(0.0, 1.0, "B"));
        nodeList.add(new Node(0.0, 2.0, "C"));

        List <String> from = new ArrayList <>();
        List <String> to = new ArrayList <>();

        from.add("A");
        to.add("B");

        from.add("B");
        to.add("C");

        from.add("A");
        to.add("C");

        String source = "A";
        String destination = "C";

        double result = 2.0;

        runTest(nodeList, from, to, source, destination, result);
    }

    /**
     * Simple test with five nodes and exactly one path
     */
    @Test
    public void test3() {
        List <Node> nodeList = new ArrayList <>();

        nodeList.add(new Node(2.0, 2.0, "node1"));
        nodeList.add(new Node(6.0, 2.0, "node2"));
        nodeList.add(new Node(6.0, 3.0, "node3"));
        nodeList.add(new Node(9.0, 3.0, "node4"));

        List <String> from = new ArrayList <>();
        List <String> to = new ArrayList <>();

        from.add("node1");
        to.add("node2");

        from.add("node1");
        to.add("node3");

        from.add("node3");
        to.add("node4");

        from.add("node2");
        to.add("node4");

        String source = "node1";
        String destination = "node4";

        double result = 7.123105625617661;

        runTest(nodeList, from, to, source, destination, result);
    }

    /**
     * Test with 5 nodes and one shortest path
     */
    @Test
    public void test4() {
        List <Node> nodeList = new ArrayList <>();

        nodeList.add(new Node(4.0, 2.0, "node1"));
        nodeList.add(new Node(6.0, 3.0, "node2"));
        nodeList.add(new Node(7.0, 5.0, "node3"));
        nodeList.add(new Node(7.0, 7.0, "node4"));
        nodeList.add(new Node(1.0, 6.0, "node5"));

        List <String> from = new ArrayList <>();
        List <String> to = new ArrayList <>();

        from.add("node1");
        to.add("node2");

        from.add("node2");
        to.add("node3");

        from.add("node3");
        to.add("node4");

        from.add("node4");
        to.add("node5");

        from.add("node5");
        to.add("node1");

        String source = "node1";
        String destination = "node4";

        double result = 6.47213595499958;

        runTest(nodeList, from, to, source, destination, result);
    }

    /**
     * Similar to previous test but there is direct path
     * between source and destination
     */
    @Test
    public void test5() {
        List <Node> nodeList = new ArrayList <>();

        nodeList.add(new Node(4.0, 2.0, "node1"));
        nodeList.add(new Node(6.0, 3.0, "node2"));
        nodeList.add(new Node(7.0, 5.0, "node3"));
        nodeList.add(new Node(7.0, 7.0, "node4"));
        nodeList.add(new Node(1.0, 6.0, "node5"));

        List <String> from = new ArrayList <>();
        List <String> to = new ArrayList <>();

        from.add("node1");
        to.add("node2");

        from.add("node2");
        to.add("node3");

        from.add("node3");
        to.add("node4");

        from.add("node4");
        to.add("node5");

        from.add("node5");
        to.add("node1");

        from.add("node1");
        to.add("node4");

        String source = "node1";
        String destination = "node4";

        double result = 5.830951894845301;

        runTest(nodeList, from, to, source, destination, result);
    }

    /**
     * Seven nodes on parabola with some additional edges
     */
    @Test
    public void test6() {
        List <Node> nodeList = new ArrayList <>();
        List <String> from = new ArrayList <>();
        List <String> to = new ArrayList <>();

        final int N = 7;

        for (int i = 1; i <= N; i++) {
            nodeList.add(new Node((double) i, (double) i * i, "node" + Integer.toString(i)));
        }

        for (int i = 1; i <= N; i++) {
            from.add("node" + Integer.toString(i));
            to.add("node" + Integer.toString(i % N + 1));
        }

        String source = "node1";
        String destination = "node4";

        double result = 15.33236498562664;

        runTest(nodeList, from, to, source, destination, result);
    }
}

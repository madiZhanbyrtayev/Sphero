package ShortestPathTest;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


import org.junit.Test;

import ShortestPath.Graph;


public class GraphReaderTest {

    /**
     * Prints test to the specified file
     * @param fileName is file where test will be printed
     * @param N is number of nodes in test
     * @param nodes set of nodes of graph
     * @param edges set of edge of graph
     */
    private void printTest(String fileName, String N, String nodes[], String edges[]) {
        PrintWriter printWriter = null;

        try {
            printWriter = new PrintWriter(new File(fileName));
        } catch (IOException IOex) {
            fail(IOex.getMessage());
        }

        printWriter.println(N);

        if (nodes != null) {
            for (int i = 0; i < nodes.length; i++) {
                printWriter.println(nodes[i]);
            }
        }

        if (edges != null) {
            for (int i = 0; i < edges.length; i++) {
                printWriter.println(edges[i]);
            }
        }

        printWriter.close();
    }

    /**
     * Check for file not found exception
     * Uses random file name, hoping that it really does not exist
     */
    @Test
    public void fileNotFoundTest() {
        String fileName = "9G994MF2Q2.GGw"; // Random filename, low probability of existence

        try {
            Graph graph = Graph.readFromFile(fileName);
            fail("Expected exception opening file, resulting nodeset is: " + graph.getNodeList());
        } catch (Exception ex) {
            assertNotNull(ex.getMessage());
        }
    }

    /**
     * This test prints negative number to the first line
     */
    @Test
    public void wrongNodeNumberTest1() {
        String fileName = "data.txt";
        String N = Integer.toString(-1);

        printTest(fileName, N, null, null);

        Graph graph;
        try {
            graph = Graph.readFromFile(fileName);
            fail("Expected exception in parsing of first line, resulting nodeset is: " + graph.getNodeList());
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Wrong input format, line: -1");
        }
    }

    /**
     * Wrong number of nodes - first line contains letters
     */
    @Test
    public void wrongNumberTest2() {
        String fileName = "data.txt";
        String N = "Unexpected text";

        printTest(fileName, N, null, null);

        Graph graph;
        try {
            graph = Graph.readFromFile(fileName);
            fail("Expected exception in parsing of first line, resulting nodeset is: " + graph.getNodeList());
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Wrong input format, line: Unexpected text");
        }
    }

    /**
     * Common part of badNodeSetTests
     * @param fileName is name of file with data
     */
    private void badNodeSetTest(String fileName) {
        Graph graph;

        try {
            graph = Graph.readFromFile(fileName);
            fail("Expected exception in parsing of nodeset, resulting nodeset is: " + graph.getNodeList());
        } catch (Exception ex) {
            assertTrue(ex.getMessage().startsWith("Wrong input format, line: "));
        }
    }

    /**
     * This test contains the same node labels
     */
    @Test
    public void badNodeSetTest1() {
        String fileName = "data.txt";
        int N = 5;
        String[] nodes = {"0 0 A", "1 2 B", "2.34 5 C", "1.2 3 A", "5 6 D"};

        printTest(fileName, Integer.toString(N), nodes, null);

        badNodeSetTest(fileName);
    }

    /**
     * This test contains bad specifications of nodes
     */
    @Test
    public void badNodeSetTest2() {
        String fileName = "data.txt";
        int N = 5;
        String[] nodes = {"0 0 A", "B 1 2", "2.34 5 C", "1.2 3 A", "5 6 D"};

        printTest(fileName, Integer.toString(N), nodes, null);

        badNodeSetTest(fileName);
    }

    /**
     * This test contains bad specifications of nodes
     * (more than 3 tokens in one line)
     */
    @Test
    public void badNodeSetTest3() {
        String fileName = "data.txt";
        int N = 1;
        String[] nodes = {"A wewq 1 qwe qw"};

        printTest(fileName, Integer.toString(N), nodes, null);

        badNodeSetTest(fileName);
    }

    /**
     * First line in file does not match number of nodes
     */
    @Test
    public void badNodeSetTest4() {
        String fileName = "data.txt";
        int N = 1;
        String[] nodes = {"0 0 A", "1 1 B"};

        printTest(fileName, Integer.toString(N), nodes, null);

        badNodeSetTest(fileName);
    }

    /**
     * Common part of badEdgeSetTests
     * @param fileName if name of file with data
     */
    public void badEdgeSetTest(String fileName) {
        Graph graph;

        try {
            graph = Graph.readFromFile(fileName);
            fail("Expected exception in parsing of edgeset, resulting nodeset is: " + graph.getNodeList());
        } catch (Exception ex) {
            assertTrue(ex.getMessage().startsWith("Wrong input format, line: "));
        }
    }

    /**
     * This test contains bad label in edge set
     */
    @Test
    public void badEdgeSetTest1() {
        String fileName = "data.txt";
        int N = 5;
        String[] nodes = {"0 0 A", "1 1 B", "2 2 C", "3 3 D", "4 4 E"};
        String[] edges = {"A B", "B Q"};

        printTest(fileName, Integer.toString(N), nodes, edges);

        badEdgeSetTest(fileName);
    }

    /**
     * This test contains repeated edge in edge set
     */
    @Test
    public void badEdgeSetTest2() {
        String fileName = "data.txt";
        int N = 5;
        String[] nodes = {"0 0 A", "1 1 B", "2 2 C", "3 3 D", "4 4 E"};
        String[] edges = {"A B", "A B"};

        printTest(fileName, Integer.toString(N), nodes, edges);

        badEdgeSetTest(fileName);
    }

    /**
     * This test contains two same labels in description of an edge
     */
    @Test
    public void badEdgeSetTest3() {
        String fileName = "data.txt";
        int N = 5;
        String[] nodes = {"0 0 A", "1 1 B", "2 2 C", "3 3 D", "4 4 E"};
        String[] edges = {"A B", "A A"};

        printTest(fileName, Integer.toString(N), nodes, edges);

        badEdgeSetTest(fileName);
    }

    @Test
    public void okTest() {
        String fileName = "data.txt";
        int N = 10;
        String[] nodes = {
                "0 0 A",
                "3.14 5.15 B",
                "11.0 12.3   C",
                "13.4 1.8 Qwe",
                "20 30 asd",
                "15 14 the",
                "100 18 Uttq",
                "18 17 IKN",
                "15 15 NMB",
                "  10    222 TTT"
        };

        String[] edges = {
                "A B",
                "B C",
                "C Qwe",
                "Qwe asd",
                "asd the",
                "Uttq the",
                "Uttq IKN",
                "IKN NMB",
                "NMB TTT",
                "TTT A",
                "B the",
                "Uttq TTT",
                "Qwe C",
                "asd C",
                "IKN TTT"
        };

        printTest(fileName, Integer.toString(N), nodes, edges);

        badEdgeSetTest(fileName);
    }
}

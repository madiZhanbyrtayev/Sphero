package ShortestPathTest;

import static org.junit.Assert.*;

import org.junit.Test;


public class NodeTest {
    /**
     * Constant used for comparison
     */
    private final static double EPS = 1e-9;

    /**
     * Expected exception in this test (thrown by parseNode)
     * @param testString is string to be parsed
     */
    private void exceptionTest(String testString) {
        Node node = null;

        try {
            node = Node.parseNode(testString);
            fail("Expected exception in parseNode " + node);
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Wrong input format, line: " + testString);
        }
    }

    /**
     * Common part of all simple tests
     * @param x is x-coordinate of node
     * @param y is y-coordinate of node
     * @param label is label of node
     */
    private void simpleTest(double x, double y, String label) {
        String testString = x + " " + y + " " + label;
        Node node = null;

        try {
            node = Node.parseNode(testString);
        } catch (Exception ex) {
            fail("Unexpected exception: " + ex.getMessage());
        }

        assertNotNull(node);
        assertEquals(node.getX(), x, EPS);
        assertEquals(node.getY(), y, EPS);
        assertEquals(node.getLabel(), label);
        assertNotNull(node.getNeighbors());
        assertTrue(node.getNeighbors().isEmpty());
    }

    /**
     * Should be OK
     */
    @Test
    public void simpleTest1() {
        double x = 1;
        double y = 1;
        String label = "key";

        simpleTest(x, y, label);
    }

    /**
     * Should be OK
     */
    @Test
    public void simpleTest2() {
        double x = 1.53;
        double y = 2.48;
        String label = "nodeLabel";

        simpleTest(x, y, label);
    }

    /**
     * Should throw exception because given string is null
     */
    @Test
    public void exceptionTest1() {
        String testString = null;
        Node node = null;

        try {
            node = Node.parseNode(testString);
            fail("Expected exception in parseNode: " + node);
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), "Wrong input format, empty line");
        }
    }

    /**
     * Wrong format: too many tokens in line
     */
    @Test
    public void exceptionTest2() {
        String testString = "1 1 1 key";
        exceptionTest(testString);
    }

    /**
     * Wrong format: string does not start with coordinates
     */
    @Test
    public void exceptionTest3() {
        String testString = "asdasd";
        exceptionTest(testString);
    }

    /**
     * Wrong format: coordinates should go first
     */
    @Test
    public void exceptionTest4() {
        String testString = "key 1 1";
        exceptionTest(testString);
    }

    /**
     * Wrong format: need more tokens in line
     */
    @Test
    public void exceptionTest5() {
        String testString = "1 1     ";
        exceptionTest(testString);
    }

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

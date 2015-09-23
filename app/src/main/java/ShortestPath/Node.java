package ShortestPath;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bekzhan Kassenov
 * Two nodes are equal iff their labels are equal
 */
public class Node {
    private double x;
    private double y;
    private String label;
    private List <Node> neighbors;

    /**
     * Default constructor
     */
    public Node() {
        this.x = 0;
        this.y = 0;
        this.label = "";
        this.neighbors = new ArrayList <>();
    }

    /**
     * One more constructor
     * @param x
     * @param y
     * @param label
     */
    public Node(double x, double y, String label) {
        this.x = x;
        this.y = y;
        this.label = label;
        this.neighbors = new ArrayList <>();
    }

    /**
     * Adds neighbor if it was not in neighbor list
     * It walks through neighbor list in order to check
     * existence of given neighbor.
     * Starts working slow (because of quadratic asymptotic)
     * as size of list grows.
     * @param neighbor is node to be added to neighbor list
     */
    public void addNeighbor(Node neighbor) {
        if (!this.neighbors.contains(neighbor)) {
            this.neighbors.add(neighbor);
        }
    }

    /**
     * Removes given Node from neighbor list
     * @param neighbor to be removed
     */
    public void removeNeighbor(Node neighbor) {
        if (this.neighbors.contains(neighbor)) {
            this.neighbors.remove(neighbor);
        }
    }

    /**
     * Calculates distance to specified node in 2-D
     * @param node
     * @return distance between two nodes
     */
    public double distanceTo(Node node) {
        double diffX = this.x - node.x;
        double diffY = this.y - node.y;

        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * Setter for x
     * @param x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Getter for X
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Setter for y
     * @param y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Getter for Y
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * Setter for label
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Getter for label
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * Setter for neighbors
     * @param neighbors
     */
    public void setNeighbors(List<Node> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Getter for neighbors
     * @return
     */
    public List <Node> getNeighbors() {
        return neighbors;
    }

    /**
     * Parses given string and return Node object.
     * String must contain x, y and label separated
     * with any number of spaces.
     * @param string to parse
     * @throws Exception if string is not in specified format
     * @return parsed node
     */
    public static Node parseNode(String string) throws Exception {
        if (string == null) {
            throw new Exception("Wrong input format, empty line");
        }

        String[] stringParts = string.split(" ");
        if (stringParts.length != 3) {
            throw new Exception("Wrong input format, line: " + string);
        }

        double x, y;
        String label;

        try {
            x = Double.parseDouble(stringParts[0]);
            y = Double.parseDouble(stringParts[1]);
            label = stringParts[2];
        } catch (Exception ex) {
            throw new Exception("Wrong input format, line: " + string);
        }

        return new Node(x, y, label);
    }

    /**
     * hashCode for Node.
     * Includes only label into calculation of hashCode.
     */
    @Override
    public int hashCode() {
        if (label == null) {
            return 0;
        }

        return label.hashCode();
    }

    /**
     * Equality checker for Node.
     * Checks label for equality of two Nodes.
     * @param obj is object to compare
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;

        return true;
    }

    /**
     * @return string representation of node
     */
    @Override
    public String toString() {
        return "{x=" + this.x + ", y=" + this.y + ", label=" + this.label;
    }
}


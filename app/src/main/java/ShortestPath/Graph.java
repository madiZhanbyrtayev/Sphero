package ShortestPath;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class for storing a graph.
 * Graph stores list of all nodes and
 * mapping from label to node.
 *
 * Implementation of this class provides
 * methods for finding the shortest path
 * between two nodes.
 *
 * @author Bekzhan Kassenov
 *
 */
public class Graph {
    /**
     * Map string label to node
     */
    private Map <String, Node> labelToNode;

    /**
     * List of all node of the graph
     */
    private List <Node> nodeList;

    /**
     * Default constructor, creates empty graph
     */
    public Graph() {
        labelToNode = new HashMap <>();
        nodeList = new ArrayList <>();
    }

    /**
     * Getter for node list
     * @return list of nodes of graph
     */
    public List <Node> getNodeList() {
        return nodeList;
    }

    /**
     * Adds node to the graph
     * @param node to be added
     * @return true if node was added successfully (there was
     * no node with the same label) and false otherwise
     */
    public boolean addNode(Node node) {
        if (node == null || labelToNode.containsKey(node.getLabel())) {
            return false;
        }

        labelToNode.put(node.getLabel(), node);
        nodeList.add(node);
        return true;
    }

    /**
     * Adds edge between nodes with labels nodeOne and nodeTwo
     *
     * @param labelOne is one node of edge
     * @param labelTwo is second node of edge
     * @return true was added successfully and false otherwise
     */
    public boolean addEdge(String labelOne, String labelTwo) {
        Node nodeOne = labelToNode.get(labelOne);
        Node nodeTwo = labelToNode.get(labelTwo);

        // If nodes with such labels does not exist or we are trying
        // to connect a node with itself
        if (nodeOne == null || nodeTwo == null || nodeOne.equals(nodeTwo)) {
            return false;
        }

        // If such edge already exists
        if (nodeOne.getNeighbors().contains(nodeTwo) ||
                nodeTwo.getNeighbors().contains(nodeOne)) {
            return false;
        }

        // Add links between two nodes
        nodeOne.addNeighbor(nodeTwo);
        nodeTwo.addNeighbor(nodeOne);

        return true;
    }

    /**
     * SPFA - shortest path finder algorithm
     * Simple BFS-like algorithm for creating
     * source tree from given node.
     *
     * Average complexity is not proven, but it is close
     * to O(N logN).
     *
     * Worst-time complexity is exponential.
     *
     * @param node is root of source tree
     * @return Map <Node, Node> where keys are nodes and
     * values are parents of keys in source tree.
     */
    private Map <Node, Node> runSPFA(Node node) {
        // Distances from root to nodes
        Map <Node, Double> distance = new HashMap <>();

        // Result of running algorithm - parents of nodes in
        // source tree
        Map <Node, Node> parent = new HashMap <>();

        // For each key value is true if node is in queue and
        // false otherwise
        Map <Node, Boolean> inQueue = new HashMap <>();

        // Unbounded queue for storing nodes
        Queue <Node> queue = new LinkedBlockingQueue <>();

        // Initialize all data structures
        distance.put(node, 0.0);
        parent.put(node, null);
        inQueue.put(node, true);
        queue.add(node);

        while (!queue.isEmpty()) {
            // Take element from queue
            Node currentNode = queue.remove();

            // Set inQueue to false
            inQueue.put(currentNode, false);

            // Iterate over all neighbors of node
            for (Node neighbor: currentNode.getNeighbors()) {

                // Try to update distance from current node to neighbor
                double neighborDistance = distance.get(currentNode) + currentNode.distanceTo(neighbor);

                // If we can update distance
                if (!distance.containsKey(neighbor) || distance.get(neighbor) > neighborDistance) {

                    // Update distance
                    distance.put(neighbor, neighborDistance);

                    // Set parent for neighbor
                    parent.put(neighbor, currentNode);

                    // Try to add neighbor to queue
                    if (!inQueue.containsKey(neighbor) || !inQueue.get(neighbor)) {
                        inQueue.put(neighbor, true);
                        queue.add(neighbor);
                    }
                }
            }
        }

        return parent;
    }

    /**
     * Method that accepts two labels of nodes and returns
     * shortest path from node with label labelFrom to node
     * with label labelTo.
     *
     * @param labelFrom is first node in path
     * @param labelTo is the last node in path
     * @return null it is impossible to find node with given labels
     * or nodes with given labels are in different connected components.
     * Otherwise returns list of nodes on the shortest path between
     * given nodes.
     */
    public List <Node> getShortestPath(String labelFrom, String labelTo) {
        // If graph does not have nodes with given labels
        if (!labelToNode.containsKey(labelFrom) || !labelToNode.containsKey(labelTo)) {
            return null;
        }

        Node from = labelToNode.get(labelFrom);
        Node to = labelToNode.get(labelTo);

        // Cannot calculate distances between nulls
        if (from == null || to == null) {
            return null;
        }

        // Invocation of runSPFA!!!
        Map <Node, Node> parent = runSPFA(from);

        // If nodes are in different connected components
        if (!parent.containsKey(to)) {
            return null;
        }

        // Create shortest path between two nodes
        List <Node> result = new ArrayList <>();
        for (Node currentNode = to; currentNode != null; currentNode = parent.get(currentNode)) {
            result.add(currentNode);
        }

        // Since previous loop started from the end we have to reverse result
        Collections.reverse(result);
        return result;
    }
}

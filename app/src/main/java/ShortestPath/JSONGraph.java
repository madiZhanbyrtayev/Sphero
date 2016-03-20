package ShortestPath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bekzhan on 3/21/16.
 *
 * Handles all the JSON job with the graph
 * Graph is encoded into JSON in the following format:
 * {
 *     nodes : [
 *          <node_1>,
 *          <node_2>,
 *          ...
 *          <node_n>
 *     ],
 *     edges [
 *          {labelOne : <labelOne_1>, labelTwo : <labelTwo_1>},
 *          {labelOne : <labelOne_2>, labelTwo : <labelTwo_2>},
 *          ...
 *          {labelOne : <labelOne_m>, labelTwo : <labelTwo_m>}
 *     ]
 * }
 *
 * Each node is encoded in the following format:
 * {label : <label>, x : <x>, y : <y>},
 */
public class JSONGraph {
    /**
     * Names of the keys in JSON objects
     */
    private static final String JSON_NAME_NODE_LIST = "nodes";
    private static final String JSON_NAME_EDGE_LIST = "edges";
    private static final String JSON_NAME_LABEL_ONE = "labelOne"; // these two
    private static final String JSON_NAME_LABEL_TWO = "labelTwo"; // are for edges
    private static final String JSON_NAME_LABEL     = "label";    // this one is for node
    private static final String JSON_NAME_X         = "x";
    private static final String JSON_NAME_Y         = "y";

    /**
     * Decodes JSONObject into Node.
     * JSONObject must have the following format
     *
     * {label : <label>, x : <x>, y : <y>},
     *
     * @param nodeJSON is JSONObject of a node
     * @return Node object of given JSONObject
     * @throws Exception if JSON object does not satisfy format of the node
     */
    public static Node decodeNode(JSONObject nodeJSON) throws Exception {
        String label = nodeJSON.getString(JSON_NAME_LABEL);
        double x = nodeJSON.getDouble(JSON_NAME_X);
        double y = nodeJSON.getDouble(JSON_NAME_Y);

        return new Node(x, y, label);
    }

    /**
     * Encodes node in JSONObject according to specified format
     *
     * @param node is node to be encoded
     * @return JSONObject for given node
     * @throws JSONException if label of the node is null
     */
    public JSONObject encodeNode(Node node) throws JSONException {
        JSONObject nodeJSON = new JSONObject();
        nodeJSON.put(JSON_NAME_LABEL, node.getLabel());
        nodeJSON.put(JSON_NAME_X, node.getX());
        nodeJSON.put(JSON_NAME_Y, node.getY());

        return nodeJSON;
    }

    /**
     * Static method which creates Graph object and fills it according to
     * data given in graphJSON.
     *
     * @param graphJSON JSON object of a graph
     * @return constructed graph object
     * @throws Exception if graphJSON does not satisfy format of a graph
     */
    public static Graph decodeGraph(JSONObject graphJSON) throws Exception {
        JSONArray nodes = graphJSON.getJSONArray(JSON_NAME_NODE_LIST);
        JSONArray edges = graphJSON.getJSONArray(JSON_NAME_EDGE_LIST);

        Graph graph = new Graph();
        for (int i = 0; i < nodes.length(); i++) {
            Object nodeObj = nodes.get(i);

            if (!(nodeObj instanceof JSONObject)) {
                throw new Exception("Wrong format of JSON");
            }

            JSONObject nodeJSON = (JSONObject)nodeObj;
            Node node = decodeNode(nodeJSON);
            if (!graph.addNode(node)) {
                throw new Exception("Wrong format of JSON in the list of nodes");
            }
        }

        for (int i = 0; i < edges.length(); i++) {
            Object edgeObj = edges.get(i);

            if (!(edgeObj instanceof JSONObject)) {
                throw new Exception("Wrong format of JSON");
            }

            JSONObject edgeJSON = (JSONObject) edgeObj;
            String labelOne = edgeJSON.getString(JSON_NAME_LABEL_ONE);
            String labelTwo = edgeJSON.getString(JSON_NAME_LABEL_TWO);

            if (!graph.addEdge(labelOne, labelTwo)) {
                throw new Exception("Wrong format of JSON in the list of edges");
            }
        }

        return graph;
    }
}

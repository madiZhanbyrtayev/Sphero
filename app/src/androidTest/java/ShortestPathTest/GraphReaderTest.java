package ShortestPathTest;

import org.json.JSONObject;
import org.junit.Test;

import ShortestPath.Graph;
import ShortestPath.JSONGraph;

import static org.junit.Assert.fail;

public class GraphReaderTest {
    @Test
    public void decodeTest() {
        String data = "{\n" +
                "    \"nodes\" : [\n" +
                "        {\"label\" : \"Madi\",   \"x\" : 0.0,    \"y\": 0.0},\n" +
                "        {\"label\" : \"Keks\",   \"x\" : 0.0,    \"y\": 1600.0},\n" +
                "        {\"label\" : \"Label1\", \"x\" : 1600.0, \"y\": 1600.0},\n" +
                "        {\"label\" : \"Label2\", \"x\" : 1600.0, \"y\": 0.0},\n" +
                "        {\"label\" : \"Bekz\",   \"x\" : 0.0,    \"y\": 0.0}\n" +
                "    ],\n" +
                "\n" +
                "    \"edges\" : [\n" +
                "        {\"labelOne\" : \"Madi\", \"labelTwo\" : \"Keks\"},\n" +
                "        {\"labelOne\" : \"Keks\", \"labelTwo\" : \"Label1\"},\n" +
                "        {\"labelOne\" : \"Label1\", \"labelTwo\" : \"Label2\"},\n" +
                "        {\"labelOne\" : \"Label2\", \"labelTwo\" : \"Bekz\"}\n" +
                "    ]\n" +
                "}";

        try {
            Graph graph = JSONGraph.decodeGraph(new JSONObject(data));
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void distanceTest() {

    }
}

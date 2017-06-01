package Test;

import Graph.Connection;
import Graph.Device;
import Parser.Parser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by GustavMadslund on 01/06/2017.
 */
public class ParseConnectionsTest {
    @Test
    public void ParseDevice() throws Exception {
        Parser parser = new Parser();
        Map<String, Device> devices = parser.parse("xml/case1.xml");

        List<Connection> connections = new ArrayList<>();
        for (Map.Entry<String, Device> entry : devices.entrySet()) {
            for (Connection c : entry.getValue().getConnections()) {
                if (!connections.contains(c)){
                    connections.add(c);
                }
            }
        }

        assertEquals(2, connections.size());
    }
}


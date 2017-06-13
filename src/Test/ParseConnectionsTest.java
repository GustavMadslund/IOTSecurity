package Test;

import Analysis.Dimension;
import Graph.Connection;
import Graph.Device;
import Parser.Parser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class ParseConnectionsTest {
    @Test
    public void ParseDevice() throws Exception {
        Parser parser = new Parser();
        Map<String, Dimension> env = parser.parseEnvironment("xml/environment1.xml");
        Map<String, Device> devices = parser.parseSystem("xml/case2.xml", env);

        List<Connection> connections = new ArrayList<>();
        for (Map.Entry<String, Device> entry : devices.entrySet()) {
            for (Connection c : entry.getValue().getConnections()) {
                if (!connections.contains(c)){
                    connections.add(c);
                }
            }
        }

        assertEquals(8, connections.size());
    }
}


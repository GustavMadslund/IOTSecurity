package Test;


import Analysis.Dimension;
import Graph.Device;
import Parser.Parser;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class ParseDevicesTest {

    @Test
    public void ParseDevice() throws Exception {
        Parser parser = new Parser();
        Map<String, Dimension> env = parser.parseEnvironment("xml/environment1.xml");
        Map<String, Device> devices = parser.parseSystem("xml/case2.xml", env);

        assertEquals(7, devices.size());
    }
}
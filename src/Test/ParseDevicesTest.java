package Test; /**
 * Created by GustavMadslund on 01/06/2017.
 */


import Graph.Connection;
import Graph.Device;
import Parser.Parser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

public class ParseDevicesTest {

    @Test
    public void ParseDevice() throws Exception {
        Parser parser = new Parser();
        Map<String, Device> devices = parser.parse("xml/case1.xml");

        assertEquals(3, devices.size());
    }
}
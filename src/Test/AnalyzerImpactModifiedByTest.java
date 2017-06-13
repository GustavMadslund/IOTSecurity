package Test;

import Analysis.Analyzer;
import Analysis.Dimension;
import Graph.Device;
import Parser.Parser;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;


public class AnalyzerImpactModifiedByTest {
    @Test
    public void ParseDevice() throws Exception {
        Parser parser = new Parser();
        Map<String, Dimension> env = parser.parseEnvironment("xml/environment1.xml");
        Map<String, Device> devices = parser.parseSystem("xml/case2.xml", env);
        Analyzer analyzer = new Analyzer(devices);
        analyzer.computeRisk();
        for (Map.Entry<String, Device> entry : devices.entrySet()) {
            Device device = entry.getValue();
            String name = device.getName();
            Device modifiedBy = device.getImpactModifiedBy();
            switch (name) {
                case "0":
                    assertEquals("3", modifiedBy.getName());
                    break;
                case "1":
                    assertEquals("0", modifiedBy.getName());
                    break;
                case "2":
                    assertEquals("5", modifiedBy.getName());
                    break;
                case "3":
                    assertNull(device.getImpactModifiedBy());
                    break;
                case "4":
                    assertEquals("0", modifiedBy.getName());
                    break;
                case "5":
                    assertNull(device.getImpactModifiedBy());
                    break;
                case "6":
                    assertEquals("0", modifiedBy.getName());
                    break;
                default: assertEquals(0,1);
            }
        }
    }
}

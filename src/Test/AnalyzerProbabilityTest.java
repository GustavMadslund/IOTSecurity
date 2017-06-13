package Test;

import Analysis.Analyzer;
import Analysis.Dimension;
import Graph.Device;
import Parser.Parser;
import org.junit.Test;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;


public class AnalyzerProbabilityTest {
    @Test
    public void ParseDevice() throws Exception {
        Parser parser = new Parser();
        Map<String, Dimension> env = parser.parseEnvironment("xml/environment1.xml");
        Map<String, Device> devices = parser.parseSystem("xml/case2.xml", env);
        Analyzer analyzer = new Analyzer(devices);
        analyzer.computeRisk();
        for (Map.Entry<String, Device> entry : devices.entrySet()) {
            Device device = entry.getValue();
            Double probability = device.getNewProbability();
            String name = entry.getValue().getName();
            switch (name) {
                case "0":
                    assertEquals(0.675, probability);
                    break;
                case "1":
                    assertEquals(1.2, probability);
                    break;
                case "2":
                    assertEquals(0.3875, probability);
                    break;
                case "3":
                    assertEquals(2.5, probability);
                    break;
                case "4":
                    assertEquals(1.35, probability);
                    break;
                case "5":
                    assertEquals(3.0, probability);
                    break;
                case "6":
                    assertEquals(0.1, probability);
                    break;
                default: assertEquals(0,1);
            }
        }
    }
}

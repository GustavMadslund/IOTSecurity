package Analysis;

import Graph.Device;
import Parser.Parser;

import java.util.HashMap;
import java.util.Map;

public class Analyser {
    private Map<String, Device> devices;
    private Map<String, Dimension> dimensions;

    public Analyser(Map<String, Device> devices, Map<String, Dimension> dimensions) {
        this.devices = devices;
        this.dimensions = dimensions;
    }

    public double computeRisk() {
        return computeNodeRisk(devices.entrySet().iterator().next().getValue());
    }

    private double computeNodeRisk(Device device) {
        if (device.isVisited()) {
            return 0.0;
        }
        device.visit();

        double risk = device.getDimensions().stream()
                .map(s -> dimensions.get(s.toUpperCase()).getBaseRating())
                .reduce(0.0, Double::sum);
        risk += device.getConnections().stream()
                .map(c -> c.getFrom() != device ? c.getFrom() : c.getTo())
                .map(this::computeNodeRisk)
                .reduce(0.0, Double::sum);

        return risk;
    }

    public static void main(String[] args) throws Exception {
        Parser parser = new Parser();
        Map<String, Device> devices = parser.parse("xml/case1.xml");

        for(Map.Entry<String, Device> entry : devices.entrySet()){
            System.out.println(entry.getValue());
            System.out.println("CONNECTIONS:");
            entry.getValue().getConnections().forEach(System.out::println);
            System.out.println("----------");
        }

        Map<String, Dimension> dimensions = new HashMap<>();
        dimensions.put("MISSION CRITICAL", new Dimension("MISSION CRITICAL", 1.0));
        dimensions.put("PATCHABLE", new Dimension("PATCHABLE", 1.0));
        dimensions.put("UNPATCHABLE", new Dimension("UNPATCHABLE", 1.0));
        dimensions.put("STANDARD BASED", new Dimension("STANDARD BASED", 1.0));
        dimensions.put("MANAGED", new Dimension("STANDARD BASED", 1.0));

        Analyser analyser = new Analyser(devices, dimensions);
        System.out.println("RISK: " + analyser.computeRisk());
    }
}

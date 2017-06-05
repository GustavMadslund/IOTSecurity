package Analysis;

import Graph.Device;
import Parser.Parser;

import java.util.Map;

public class DataProcessor {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Parameters: ENVIRONMENT_PATH SYSTEM_PATH DEBUG_OUTPUT");
            return;
        }
        boolean debug = args.length > 2 && Boolean.parseBoolean(args[2]);

        // Parse
        Parser parser = new Parser();
        Map<String, Dimension> dimensions = parser.parseEnvironment(args[0]);
        Map<String, Device> devices = parser.parseSystem(args[1], dimensions);
        if (devices.isEmpty()) {
            return;
        }

        if (debug) {
            for(Map.Entry<String, Device> entry : devices.entrySet()){
                System.out.println(entry.getValue());
                System.out.println("CONNECTIONS:");
                entry.getValue().getConnections().forEach(System.out::println);
                System.out.println("----------");
            }
        }

        // Analyze
        Analyser analyser = new Analyser(devices);
        analyser.computeRisk();

        if (debug) {
            System.out.println("Probability:");
            devices.forEach((key, value) -> System.out.println(value.getName() + ": " + value.getNewProbability()));
            System.out.println("Impact:");
            devices.forEach((key, value) -> System.out.println(value.getName() + ": " + value.getNewImpact()));
        }
    }
}

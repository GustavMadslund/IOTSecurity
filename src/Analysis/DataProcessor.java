package Analysis;

import Graph.Device;
import Parser.Parser;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class DataProcessor {
    private Map<String, Device> devices;

    public DataProcessor(Map<String, Device> devices) {
        this.devices = devices;
    }

    public double getAverageBaseImpact() {
        return devices.entrySet().stream()
                .mapToDouble(entry -> entry.getValue().getBaseImpact())
                .average()
                .getAsDouble();
    }

    public double getAverageBaseProbability() {
        return devices.entrySet().stream()
                .mapToDouble(entry -> entry.getValue().getBaseProbability())
                .average()
                .getAsDouble();
    }

    public double getAverageNewImpact() {
        return devices.entrySet().stream()
                .mapToDouble(entry -> entry.getValue().getNewImpact())
                .average()
                .getAsDouble();
    }

    public double getAverageNewProbability() {
        return devices.entrySet().stream()
                .mapToDouble(entry -> entry.getValue().getNewProbability())
                .average()
                .getAsDouble();
    }

    public void exportToFile(String file, String content) throws IOException {
        Files.write(Paths.get(file), content.getBytes());
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Parameters: ENVIRONMENT_PATH SYSTEM_PATH DEBUG_OUTPUT");
            return;
        }
        String exportFile = args.length > 2 ? args[2] : null;

        // Parse
        Parser parser = new Parser();
        Map<String, Dimension> dimensions = parser.parseEnvironment(args[0]);
        Map<String, Device> devices = parser.parseSystem(args[1], dimensions);
        if (devices.isEmpty()) {
            return;
        }

        // Analyze
        Analyzer analyzer = new Analyzer(devices);
        analyzer.computeRisk();

        // Process data
        DataProcessor processor = new DataProcessor(devices);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

        System.out.println("Impact increase per device:");
        devices.forEach((key, value) -> System.out.println(value.getName() + ": " + decimalFormat.format((value.getNewImpact() - value.getBaseImpact()))));
        System.out.println("Probability increase per device:");
        devices.forEach((key, value) -> System.out.println(value.getName() + ": " + decimalFormat.format((value.getNewProbability() - value.getBaseProbability()))));

        System.out.println("Impact modified per device:");
        devices.forEach((key, value) -> System.out.println(value.getName() + ": " + value.getImpactModifiedString()));
        System.out.println("Probability modified per device:");
        devices.forEach((key, value) -> System.out.println(value.getName() + ": " + value.getProbabilityModifiedString()));

        double baseImpact = processor.getAverageBaseImpact();
        double baseProbability = processor.getAverageBaseProbability();
        double newImpact = processor.getAverageNewImpact();
        double newProbability = processor.getAverageNewProbability();
        System.out.println("Average base impact: " + decimalFormat.format(baseImpact));
        System.out.println("Average base probability: " + decimalFormat.format(baseProbability));
        System.out.println("Average new impact: " + decimalFormat.format(newImpact));
        System.out.println("Average new probability: " + decimalFormat.format(newProbability));

        double impactIncrease = newImpact - baseImpact;
        double probabilityIncrease = newProbability - baseProbability;
        System.out.println("Increase in impact rating: " + decimalFormat.format(impactIncrease));
        System.out.println("Increase in probability rating: " + decimalFormat.format(probabilityIncrease));

        double impactRelativeIncrease = impactIncrease / baseImpact;
        double probabilityRelativeIncrease = probabilityIncrease / baseProbability;
        System.out.println("Relative increase in impact rating: " + ((int) (impactRelativeIncrease * 100)) + "%");
        System.out.println("Relative increase in probability rating: " + ((int) (probabilityRelativeIncrease * 100)) + "%");

        if (exportFile != null) {
            String output = "device,impact_base,probability_base,impact_new,probability_new,impact_increase,probability_increase,impact_relative_increase,probability_relative_increase\n" +
                    devices.entrySet().stream()
                            .map(Map.Entry::getValue)
                            .map(Device::getExportString)
                            .collect(Collectors.joining("\n", "", "\n"));
            processor.exportToFile(exportFile, output);
        }
    }
}

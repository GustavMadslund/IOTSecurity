package Test;

import Analysis.Analyzer;
import Analysis.DataProcessor;
import Analysis.Dimension;
import Graph.Device;
import Parser.Parser;
import org.junit.Test;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class ProcessorAverageImpactTest {
    @Test
    public void ParseDevice() throws Exception {
        Parser parser = new Parser();
        Map<String, Dimension> env = parser.parseEnvironment("xml/environment1.xml");
        Map<String, Device> devices = parser.parseSystem("xml/case2.xml", env);
        Analyzer analyzer = new Analyzer(devices);
        analyzer.computeRisk();
        DataProcessor processor = new DataProcessor(devices);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

        double baseImpact = processor.getAverageBaseImpact();
        double newImpact = processor.getAverageNewImpact();

        assertEquals(decimalFormat.format(1.01), decimalFormat.format(baseImpact));
        assertEquals(decimalFormat.format(1.56), decimalFormat.format(newImpact));
    }
}
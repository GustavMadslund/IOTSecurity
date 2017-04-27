package Analysis;

import Graph.Device;
import Parser.Parser;

import java.util.Map;

public class Analyser {
    public static void main(String[] args) throws Exception {
        Parser parser = new Parser();

        for(Map.Entry<String, Device> entry : parser.parse("xml/case1.xml").entrySet()){
            System.out.println(entry.getValue());
            System.out.println("CONNECTIONS:");
            entry.getValue().getConnections().forEach(System.out::println);
            System.out.println("----------");
        }
    }
}

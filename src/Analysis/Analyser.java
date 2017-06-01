package Analysis;

import Graph.Connection;
import Graph.Device;
import Parser.Parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Analyser {
    private Map<String, Device> devices;
    private Map<String, Dimension> dimensions;

    public Analyser(Map<String, Device> devices, Map<String, Dimension> dimensions) {
        this.devices = devices;
        this.dimensions = dimensions;
    }

    private List<Device> getExpandedNodes(Device device, Set<Device> frontierSet, Set<Device> exploredNodes) {
        return device.getConnections().stream()
                .map(c -> c.getFrom() != device ? c.getFrom() : c.getTo())
                .filter(d -> !exploredNodes.contains(d))
                .filter(d -> !frontierSet.contains(d))
                .collect(Collectors.toList());
    }

    private List<Device> getExpandedProbabilityUpdateNodes(Device device, Set<Device> frontierSet, Set<Device> exploredNodes, Set<Device> exploredExploredNodes) {
        return device.getConnections().stream()
                .filter(c -> c.getFrom() == device)
                .filter(Connection::getAccess)
                .map(Connection::getTo)
                .filter(exploredNodes::contains)
                .filter(d -> !exploredExploredNodes.contains(d))
                .filter(d -> !frontierSet.contains(d))
                .collect(Collectors.toList());
    }

    private void updateRisk(Device device, Set<Device> exploredNodes) {
        LinkedList<Device> frontier = new LinkedList<>();
        Set<Device> frontierSet = new HashSet<>();
        Set<Device> exploredExploredNodes = new HashSet<>();

        frontier.addAll(getExpandedProbabilityUpdateNodes(device, frontierSet, exploredNodes, exploredExploredNodes));
        frontierSet.addAll(frontier);
        while (!frontier.isEmpty()) {
            Device currentDevice = frontier.pop();
            frontierSet.remove(currentDevice);

            double highestProbability = -1.0;
            Device otherDevice = null;
            for (Connection c : currentDevice.getConnections()) {
                if (c.getTo() == currentDevice && c.getAccess()) {
                    if (c.getFrom().getNewProbability() > highestProbability) {
                        otherDevice = c.getFrom();
                        highestProbability = otherDevice.getNewProbability();
                    }
                }
            }
            if (highestProbability > (2 * currentDevice.getNewProbability() - currentDevice.getBaseProbability())) {
                currentDevice.setNewProbability((currentDevice.getBaseProbability() + otherDevice.getNewProbability()) / 2);

                exploredExploredNodes.add(currentDevice);
                List<Device> expandedNodes = getExpandedProbabilityUpdateNodes(currentDevice, frontierSet, exploredNodes, exploredExploredNodes);
                frontier.addAll(expandedNodes);
                frontierSet.addAll(expandedNodes);
            }
            else {
                exploredExploredNodes.add(currentDevice);
            }
        }
    }

    public void computeRisk() {
        LinkedList<Device> frontier = new LinkedList<>();
        Set<Device> frontierSet = new HashSet<>();
        Set<Device> exploredNodes = new HashSet<>();

        Device startDevice = devices.entrySet().iterator().next().getValue();
        frontier.add(startDevice);
        frontierSet.add(startDevice);
        while (!frontier.isEmpty()) {
            Device currentDevice = frontier.pop();
            frontierSet.remove(currentDevice);

            double highestProbability = -1.0;
            Device otherDevice = null;
            for (Connection c : currentDevice.getConnections()) {
                if (c.getTo() == currentDevice && c.getAccess()) {
                    if (c.getFrom().getNewProbability() > highestProbability) {
                        otherDevice = c.getFrom();
                        highestProbability = otherDevice.getNewProbability();
                    }
                }
            }
            if (highestProbability > (2 * currentDevice.getNewProbability() - currentDevice.getBaseProbability())) {
                currentDevice.setNewProbability((currentDevice.getBaseProbability() + otherDevice.getNewProbability()) / 2);
                updateRisk(currentDevice, exploredNodes);
            }

            exploredNodes.add(currentDevice);
            List<Device> expandedNodes = getExpandedNodes(currentDevice, frontierSet, exploredNodes);
            frontier.addAll(expandedNodes);
            frontierSet.addAll(expandedNodes);
        }
    }

    public static void main(String[] args) throws Exception {
        Map<String, Dimension> dimensions = new HashMap<>();
        dimensions.put("MISSION CRITICAL", new Dimension("MISSION CRITICAL", 3.0, 1.5));
        dimensions.put("PATCHABLE", new Dimension("PATCHABLE", 0.0, 0.0));
        dimensions.put("UNPATCHABLE", new Dimension("UNPATCHABLE", 2.0, 3.0));
        dimensions.put("STANDARD BASED", new Dimension("STANDARD BASED", 1.0, 1.0));
        dimensions.put("MANAGED", new Dimension("STANDARD BASED", 1.0, 1.0));

        Parser parser = new Parser();
        Map<String, Device> devices = parser.parse("xml/case1.xml", dimensions);

        for(Map.Entry<String, Device> entry : devices.entrySet()){
            System.out.println(entry.getValue());
            System.out.println("CONNECTIONS:");
            entry.getValue().getConnections().forEach(System.out::println);
            System.out.println("----------");
        }

        Analyser analyser = new Analyser(devices, dimensions);
        analyser.computeRisk();
    }
}

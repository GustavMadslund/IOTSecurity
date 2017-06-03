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

    public Analyser(Map<String, Device> devices) {
        this.devices = devices;
    }

    // Find new devices connected to the current device
    private List<Device> getExpandedNodes(Device device, Set<Device> frontierSet, Set<Device> exploredNodes) {
        return device.getConnections().stream()
                .map(c -> c.getFrom() != device ? c.getFrom() : c.getTo())
                .filter(d -> !exploredNodes.contains(d))
                .filter(d -> !frontierSet.contains(d))
                .collect(Collectors.toList());
    }

    // Find new devices with a connection towards the current device
    private List<Device> getExpandedImpactUpdateNodes(Device device, Set<Device> frontierSet, Set<Device> relevantNodes, Set<Device> exploredNodes) {
        return device.getConnections().stream()
                .filter(c -> c.getTo() == device)
                .map(Connection::getFrom)
                .filter(relevantNodes::contains)
                .filter(d -> !exploredNodes.contains(d))
                .filter(d -> !frontierSet.contains(d))
                .collect(Collectors.toList());
    }

    // Find new devices with an access connection from the current device
    private List<Device> getExpandedProbabilityUpdateNodes(Device device, Set<Device> frontierSet, Set<Device> relevantNodes, Set<Device> exploredNodes) {
        return device.getConnections().stream()
                .filter(c -> c.getFrom() == device)
                .filter(Connection::getAccess)
                .map(Connection::getTo)
                .filter(relevantNodes::contains)
                .filter(d -> !exploredNodes.contains(d))
                .filter(d -> !frontierSet.contains(d))
                .collect(Collectors.toList());
    }

    private void updateImpact(Device device, Set<Device> exploredNodes) {
        LinkedList<Device> frontier = new LinkedList<>();
        Set<Device> frontierSet = new HashSet<>();
        Set<Device> exploredExploredNodes = new HashSet<>();

        frontier.addAll(getExpandedImpactUpdateNodes(device, frontierSet, exploredNodes, exploredExploredNodes));
        frontierSet.addAll(frontier);
        while (!frontier.isEmpty()) {
            Device currentDevice = frontier.pop();
            frontierSet.remove(currentDevice);

            double highestImpact = -1.0;
            Device otherDevice = null;
            for (Connection c : currentDevice.getConnections()) {
                if (c.getFrom() == currentDevice) {
                    if (c.getTo().getNewImpact() > highestImpact) {
                        otherDevice = c.getTo();
                        highestImpact = otherDevice.getNewImpact();
                    }
                }
            }
            if (highestImpact > (2 * currentDevice.getNewImpact() - currentDevice.getBaseImpact())) {
                currentDevice.setNewImpact((currentDevice.getBaseImpact() + otherDevice.getNewImpact()) / 2);

                exploredExploredNodes.add(currentDevice);
                List<Device> expandedNodes = getExpandedImpactUpdateNodes(currentDevice, frontierSet, exploredNodes, exploredExploredNodes);
                frontier.addAll(expandedNodes);
                frontierSet.addAll(expandedNodes);
            }
            else {
                exploredExploredNodes.add(currentDevice);
            }
        }
    }

    private void updateProbability(Device device, Set<Device> exploredNodes) {
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
            double highestImpact = -1.0;
            Device otherProbabilityDevice = null;
            Device otherImpactDevice = null;
            for (Connection c : currentDevice.getConnections()) {
                if (c.getTo() == currentDevice && c.getAccess()) {
                    if (c.getFrom().getNewProbability() > highestProbability) {
                        otherProbabilityDevice = c.getFrom();
                        highestProbability = otherProbabilityDevice.getNewProbability();
                    }
                }
                else if (c.getFrom() == currentDevice) {
                    if (c.getTo().getNewImpact() > highestImpact) {
                        otherImpactDevice = c.getTo();
                        highestImpact = otherImpactDevice.getNewImpact();
                    }
                }
            }

            if (highestProbability > (2 * currentDevice.getNewProbability() - currentDevice.getBaseProbability())) {
                currentDevice.setNewProbability((currentDevice.getBaseProbability() + otherProbabilityDevice.getNewProbability()) / 2);
                updateProbability(currentDevice, exploredNodes);
            }
            if (highestImpact > (2 * currentDevice.getNewImpact() - currentDevice.getBaseImpact())) {
                currentDevice.setNewImpact((currentDevice.getBaseImpact() + otherImpactDevice.getNewImpact()) / 2);
                updateImpact(currentDevice, exploredNodes);
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

        //===
        dimensions.put("0", new Dimension("0", 0.0, 0.0));
        dimensions.put("1", new Dimension("1", 1.2, 1.2));
        dimensions.put("2", new Dimension("2", 0.1, 0.1));
        dimensions.put("3", new Dimension("3", 2.5, 2.5));
        dimensions.put("4", new Dimension("4", 0.2, 0.2));
        dimensions.put("5", new Dimension("5", 3.0, 3.0));
        dimensions.put("6", new Dimension("6", 0.1, 0.1));
        //===

        Parser parser = new Parser();
        Map<String, Device> devices = parser.parse("xml/case2.xml", dimensions);

        for(Map.Entry<String, Device> entry : devices.entrySet()){
            System.out.println(entry.getValue());
            System.out.println("CONNECTIONS:");
            entry.getValue().getConnections().forEach(System.out::println);
            System.out.println("----------");
        }

        Analyser analyser = new Analyser(devices);
        analyser.computeRisk();

        System.out.println("Probability:");
        devices.forEach((key, value) -> System.out.println(value.getName() + ": " + value.getNewProbability()));
        System.out.println("Impact:");
        devices.forEach((key, value) -> System.out.println(value.getName() + ": " + value.getNewImpact()));
    }
}

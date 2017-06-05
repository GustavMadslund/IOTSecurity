package Analysis;

import Graph.Connection;
import Graph.Device;
import Parser.Parser;

import java.util.*;
import java.util.stream.Collectors;

public class Analyzer {
    private Map<String, Device> devices;

    public Analyzer(Map<String, Device> devices) {
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

    // Updates impact ratings backwards through the graph
    private void updateImpactRatings(Device device, Set<Device> relevantNodes) {
        // Find start devices and do BFS from them
        List<Device> startDevices = getExpandedImpactUpdateNodes(device, Collections.emptySet(), relevantNodes, Collections.emptySet());
        BFS.search(startDevices, relevantNodes, (currentDevice, frontierSet, frontier, relevantNodesBody, exploredNodes) -> {
            // Find the neighbouring device with the highest impact
            Device highestImpactDevice = null;
            for (Connection c : currentDevice.getConnections()) {
                if (c.getFrom() == currentDevice) {
                    if (highestImpactDevice == null || c.getTo().getNewImpact() > highestImpactDevice.getNewImpact()) {
                        highestImpactDevice = c.getTo();
                    }
                }
            }

            exploredNodes.add(currentDevice);

            //Check if the impact rating should be updated
            if (highestImpactDevice != null) {
                double newImpact = (currentDevice.getBaseImpact() + highestImpactDevice.getNewImpact()) / 2;
                if (newImpact > currentDevice.getNewImpact()) {
                    currentDevice.setNewImpact(newImpact);

                    List<Device> expandedNodes = getExpandedImpactUpdateNodes(currentDevice, frontierSet, relevantNodesBody, exploredNodes);
                    frontier.addAll(expandedNodes);
                    frontierSet.addAll(expandedNodes);
                }
            }
        });
    }

    // Updates probability ratings backwards through the graph
    private void updateProbabilityRatings(Device device, Set<Device> relevantNodes) {
        // Find start devices and do BFS from them
        List<Device> startDevices = getExpandedProbabilityUpdateNodes(device, Collections.emptySet(), relevantNodes, Collections.emptySet());
        BFS.search(startDevices, relevantNodes, (currentDevice, frontierSet, frontier, relevantNodesBody, exploredNodes) -> {
            // Find the neighbouring device with the highest probability
            Device highestProbabilityDevice = null;
            for (Connection c : currentDevice.getConnections()) {
                if (c.getTo() == currentDevice && c.getAccess()) {
                    if (highestProbabilityDevice == null || c.getFrom().getNewProbability() > highestProbabilityDevice.getNewProbability()) {
                        highestProbabilityDevice = c.getFrom();
                    }
                }
            }

            exploredNodes.add(currentDevice);

            //Check if the probability rating should be updated
            if (highestProbabilityDevice != null) {
                double newProbability = (currentDevice.getBaseProbability() + highestProbabilityDevice.getNewProbability()) / 2;
                if (newProbability > currentDevice.getNewProbability()) {
                    currentDevice.setNewProbability(newProbability);

                    List<Device> expandedNodes = getExpandedProbabilityUpdateNodes(currentDevice, frontierSet, relevantNodesBody, exploredNodes);
                    frontier.addAll(expandedNodes);
                    frontierSet.addAll(expandedNodes);
                }
            }
        });
    }

    // Computes the impact and probability ratings for every node using BFS
    public void computeRisk() {
        // Pick a start device and do BFS from there
        Device startDevice = devices.entrySet().iterator().next().getValue();
        BFS.search(Collections.singletonList(startDevice), null, (currentDevice, frontierSet, frontier, relevantNodes, exploredNodes) -> {
            // Find the neighbouring device with the highest impact and probability
            Device highestImpactDevice = null;
            Device highestProbabilityDevice = null;
            for (Connection c : currentDevice.getConnections()) {
                if (c.getFrom() == currentDevice) {
                    if (highestImpactDevice == null || c.getTo().getNewImpact() > highestImpactDevice.getNewImpact()) {
                        highestImpactDevice = c.getTo();
                    }
                }
                else if (c.getTo() == currentDevice && c.getAccess()) {
                    if (highestProbabilityDevice == null || c.getFrom().getNewProbability() > highestProbabilityDevice.getNewProbability()) {
                        highestProbabilityDevice = c.getFrom();
                    }
                }
            }

            // Check if the impact rating should be updated
            if (highestImpactDevice != null) {
                double newImpact = (currentDevice.getBaseImpact() + highestImpactDevice.getNewImpact()) / 2;
                if (newImpact > currentDevice.getNewImpact()) {
                    currentDevice.setNewImpact(newImpact);
                    updateImpactRatings(currentDevice, exploredNodes);
                }
            }
            //Check if the probability rating should be updated
            if (highestProbabilityDevice != null) {
                double newProbability = (currentDevice.getBaseProbability() + highestProbabilityDevice.getNewProbability()) / 2;
                if (newProbability > currentDevice.getNewProbability()) {
                    currentDevice.setNewProbability(newProbability);
                    updateProbabilityRatings(currentDevice, exploredNodes);
                }
            }

            exploredNodes.add(currentDevice);
            List<Device> expandedNodes = getExpandedNodes(currentDevice, frontierSet, exploredNodes);
            frontier.addAll(expandedNodes);
            frontierSet.addAll(expandedNodes);
        });
    }
}

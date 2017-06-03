package Analysis;

import Graph.Device;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BFS {
    public static void search(List<Device> startDevices, Set<Device> relevantNodes, Body searchBody) {
        LinkedList<Device> frontier = new LinkedList<>();
        Set<Device> frontierSet = new HashSet<>();
        Set<Device> exploredNodes = new HashSet<>();

        frontier.addAll(startDevices);
        frontierSet.addAll(startDevices);
        while (!frontier.isEmpty()) {
            Device currentDevice = frontier.pop();
            frontierSet.remove(currentDevice);

            searchBody.step(currentDevice, frontierSet, frontier, relevantNodes, exploredNodes);
        }
    }

    public interface Body {
        void step(Device currentDevice, Set<Device> frontierSet, List<Device> frontier, Set<Device> relevantNodes, Set<Device> exploredNodes);
    }
}

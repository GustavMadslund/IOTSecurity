package Graph;

import Analysis.Dimension;
import Enum.DeviceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Device{
    public static final double SCALE = 3;

    private String name;
    private DeviceType deviceType;
    private List<String> sensors;
    private List<String> actuators;
    private List<String> dimensions;
    private List<Connection> connections;

    private double baseImpact;
    private double baseProbability;

    private double newImpact;
    private double newProbability;

    public Device(String name, DeviceType deviceType, List<String> sensors, List<String> actuators, List<String> dimensions) {
        this.name = name;
        this.deviceType = deviceType;
        this.sensors = sensors;
        this.actuators = actuators;
        this.dimensions = dimensions;
        connections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public List<String> getSensors() {
        return sensors;
    }

    public List<String> getActuators() {
        return actuators;
    }

    public List<String> getDimensions() {
        return dimensions;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public double getBaseImpact() {
        return baseImpact;
    }

    public double getBaseProbability() {
        return baseProbability;
    }

    public double getNewImpact() {
        return newImpact;
    }

    public void setNewImpact(double newImpact) {
        this.newImpact = newImpact;
    }

    public double getNewProbability() {
        return newProbability;
    }

    public void setNewProbability(double newProbability) {
        this.newProbability = newProbability;
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    public void computeRatings(Map<String, Dimension> dimensionRatings) {
        baseImpact = dimensions.stream()
                .mapToDouble(s -> dimensionRatings.get(s.toUpperCase()).getBaseImpact())
                .average()
                .orElse(SCALE / 2);

        baseProbability = dimensions.stream()
                .mapToDouble(s -> dimensionRatings.get(s.toUpperCase()).getBaseProbability())
                .average()
                .orElse(SCALE / 2);

        newImpact = baseImpact;
        newProbability = baseProbability;
    }

    @Override
    public String toString() {
        return "Name: " + name
                + "\n Type: " + deviceType
                + "\n Sensors: " + sensors
                + "\n Actuators: " + actuators
                + "\n Dimensions: " + dimensions;
    }
}

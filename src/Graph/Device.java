package Graph;

import Analysis.Dimension;
import Enum.DeviceType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Device{
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

    private Device impactModifiedBy;
    private Device probabilityModifiedBy;
    private Set<Device> impactModified;
    private Set<Device> probabilityModified;

    public Device(String name, DeviceType deviceType, List<String> sensors, List<String> actuators, List<String> dimensions) {
        this.name = name;
        this.deviceType = deviceType;
        this.sensors = sensors;
        this.actuators = actuators;
        this.dimensions = dimensions;
        connections = new ArrayList<>();

        impactModifiedBy = null;
        probabilityModifiedBy = null;
        impactModified = new HashSet<>();
        probabilityModified = new HashSet<>();
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

    public Device getImpactModifiedBy() {
        return impactModifiedBy;
    }

    public void setImpactModifiedBy(Device impactModifiedBy) {
        this.impactModifiedBy = impactModifiedBy;
    }

    public Device getProbabilityModifiedBy() {
        return probabilityModifiedBy;
    }

    public void setProbabilityModifiedBy(Device probabilityModifiedBy) {
        this.probabilityModifiedBy = probabilityModifiedBy;
    }

    public Set<Device> getImpactModified() {
        return impactModified;
    }

    public String getImpactModifiedString() {
        String result =  impactModified.stream()
                .map(Device::getName)
                .collect(Collectors.joining(", "));
        return result.isEmpty() ? "-" : result;
    }

    public Set<Device> getProbabilityModified() {
        return probabilityModified;
    }

    public String getProbabilityModifiedString() {
        String result =  probabilityModified.stream()
                .map(Device::getName)
                .collect(Collectors.joining(", "));
        return result.isEmpty() ? "-" : result;
    }

    public void computeRatings(Map<String, Dimension> dimensionRatings) {
        // Compute base impact from dimensions (with no dimensions present, use SCALE / 2)
        baseImpact = dimensions.stream()
                .mapToDouble(s -> dimensionRatings.get(s.toUpperCase()).getBaseImpact())
                .average()
                .orElse(Dimension.SCALE / 2);

        // Compute base probability from dimensions (with no dimensions present, use SCALE / 2)
        baseProbability = dimensions.stream()
                .mapToDouble(s -> dimensionRatings.get(s.toUpperCase()).getBaseProbability())
                .average()
                .orElse(Dimension.SCALE / 2);

        newImpact = baseImpact;
        newProbability = baseProbability;
    }

    public String getExportString() {
        return name +
                "," +
                baseImpact +
                "," +
                baseProbability +
                "," +
                newImpact +
                "," +
                newProbability +
                "," +
                (newImpact - baseImpact) +
                "," +
                (newProbability - baseProbability) +
                "," +
                (newImpact - baseImpact) / baseImpact +
                "," +
                (newProbability - baseProbability) / baseProbability;
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

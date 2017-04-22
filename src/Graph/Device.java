package Graph;

import Enum.DeviceType;

import java.util.ArrayList;
import java.util.List;

public class Device {
    private String name;
    private DeviceType deviceType;
    private List<String> sensors;
    private List<String> actuators;
    private List<String> dimensions;
    private List<Connection> connections;

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

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    @Override
    public String toString() {
        return "Name: " + name
                + "\n Type: " + deviceType
                + "\n Sensors: " + sensors
                + "\n Actuators: " + actuators
                + "\n Analysis: " + dimensions;
    }
}

import Enums.DeviceType;

import java.util.ArrayList;
import java.util.List;

public class Device {
    private String name;
    private DeviceType deviceType;
    private List<Dimension> dimensions;
    private List<Connection> connections;

    public Device(String name, DeviceType deviceType, List<Dimension> dimensions) {
        this.name = name;
        this.deviceType = deviceType;
        this.dimensions = dimensions;
        connections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public List<Dimension> getDimensions() {
        return dimensions;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void addConnection(Connection connection){
        connections.add(connection);
    }
}

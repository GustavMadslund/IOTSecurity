package Graph;

import Enum.ConnectionFormat;
import Enum.ConnectionType;

public class Connection {
    private Device firstDevice;
    private Device secondDevice;
    private ConnectionFormat format;
    private ConnectionType connectionType;

    public Connection(Device firstDevice, Device secondDevice, ConnectionFormat format, ConnectionType connectionType) {
        this.firstDevice = firstDevice;
        this.secondDevice = secondDevice;
        this.format = format;
        this.connectionType = connectionType;
    }

    public void updateDeviceConnections(){
        firstDevice.addConnection(this);
        secondDevice.addConnection(this);
    }

    public Device getFirstDevice() {
        return firstDevice;
    }

    public Device getSecondDevice() {
        return secondDevice;
    }

    public ConnectionFormat getFormat() {
        return format;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    @Override
    public String toString() {
        return "Device_1: " + firstDevice.getName()
                + "\nDevice_2: " + secondDevice.getName()
                + "\n ConnectionFormat: " + format
                + "\n Connection type: " + connectionType;
    }
}

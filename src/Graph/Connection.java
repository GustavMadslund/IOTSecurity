package Graph;

import Enum.ConnectionFormat;
import Enum.ConnectionType;

public class Connection {
    private Device from;
    private Device to;
    private ConnectionFormat format;
    private ConnectionType connectionType;
    private boolean access;

    public Connection(Device from, Device to, ConnectionFormat format, ConnectionType connectionType, boolean access) {
        this.from = from;
        this.to = to;
        this.format = format;
        this.connectionType = connectionType;
        this.access = access;
    }

    public void updateDeviceConnections(){
        from.addConnection(this);
        to.addConnection(this);
    }

    public Device getFrom() {
        return from;
    }

    public Device getTo() {
        return to;
    }

    public ConnectionFormat getFormat() {
        return format;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public boolean getAccess() {
        return access;
    }

    @Override
    public String toString() {
        return "From: " + from.getName()
                + "\nTo: " + to.getName()
                + "\n ConnectionFormat: " + format
                + "\n Connection type: " + connectionType
                + "\n Access: " + access;
    }
}

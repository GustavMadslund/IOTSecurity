import Enums.ConnectionType;
import Enums.Formats;

public class Connection {
    private Device firstDevice;
    private Device secondDevice;
    private Formats format;
    private ConnectionType connectionType;

    public Connection(Device firstDevice, Device secondDevice, Formats format, ConnectionType connectionType) {
        this.firstDevice = firstDevice;
        this.secondDevice = secondDevice;
        this.format = format;
        this.connectionType = connectionType;
    }

    public Device getFirstDevice() {
        return firstDevice;
    }

    public Device getSecondDevice() {
        return secondDevice;
    }

    public Formats getFormat() {
        return format;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }




}

package Parser;

import Enum.ConnectionFormat;
import Enum.ConnectionType;
import Enum.DeviceType;
import Graph.Connection;
import Graph.Device;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private DocumentBuilder db;

    public Parser() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Device> parse(String file) throws IOException, SAXException, ParserException {
        //Load XML
        Document doc = db.parse(new File(file));

        //Find all device and connection tags
        NodeList devices = doc.getElementsByTagName("device");
        NodeList connections = doc.getElementsByTagName("connection");

        //Parse devices
        Map<String, Device> deviceMap = parseDevices(devices);

        //Parse and encode the connections
        parseConnections(connections, deviceMap);

        //Done
        return deviceMap;
    }

    private Map<String, Device> parseDevices(NodeList devices) throws ParserException {
        Map<String, Device> deviceMap = new HashMap<>();

        for (int i = 0; i < devices.getLength(); i++){
            Node deviceNode = devices.item(i);
            NamedNodeMap deviceAttributes = deviceNode.getAttributes();

            String deviceName;
            try {
                deviceName = deviceAttributes.getNamedItem("name").getNodeValue();
            }
            catch (NullPointerException e) {
                throw new ParserException("Missing name tag for device.");
            }

            DeviceType deviceType;
            try {
                deviceType = DeviceType.valueOf(deviceAttributes.getNamedItem("type").getNodeValue().toUpperCase());
            }
            catch (NullPointerException e) {
                throw new ParserException("Missing type tag for device '" + deviceName + "'.");
            }
            catch (IllegalArgumentException e) {
                throw new ParserException("Unknown device type '" + deviceAttributes.getNamedItem("type").getNodeValue() + "'.");
            }

            List<String> sensorList = new ArrayList<>();
            List<String> actuatorList = new ArrayList<>();
            List<String> dimensionList = new ArrayList<>();
            NodeList deviceChildren = deviceNode.getChildNodes();
            for (int j = 0; j < deviceChildren.getLength(); j++){
                switch (deviceChildren.item(j).getNodeName().toUpperCase()) {
                    case "SENSOR":
                        sensorList.add(deviceChildren.item(j).getTextContent());
                        break;
                    case "ACTUATOR":
                        actuatorList.add(deviceChildren.item(j).getTextContent());
                        break;
                    case "DIMENSION":
                        dimensionList.add(deviceChildren.item(j).getTextContent());
                        break;
                }
            }

            Device device = new Device(deviceName, deviceType, sensorList, actuatorList, dimensionList);
            deviceMap.put(deviceName, device);
        }

        return deviceMap;
    }

    private void parseConnections(NodeList connections, Map<String, Device> deviceMap) throws ParserException {
        for (int i = 0; i < connections.getLength(); i++){
            Node connectionNode = connections.item(i);
            NamedNodeMap connectionAttributes = connectionNode.getAttributes();

            ConnectionFormat connectionFormat;
            try {
                connectionFormat = ConnectionFormat.valueOf(connectionAttributes.getNamedItem("format").getNodeValue().toUpperCase());
            }
            catch (NullPointerException e) {
                throw new ParserException("Missing format tag for connection.");
            }
            catch (IllegalArgumentException e) {
                throw new ParserException("Unknown connection format '" + connectionAttributes.getNamedItem("format").getNodeValue() + "'.");
            }

            ConnectionType connectionType;
            try {
                connectionType = ConnectionType.valueOf(connectionAttributes.getNamedItem("type").getNodeValue().toUpperCase());
            }
            catch (NullPointerException e) {
                throw new ParserException("Missing type tag for connection.");
            }
            catch (IllegalArgumentException e) {
                throw new ParserException("Unknown connection type '" + connectionAttributes.getNamedItem("type").getNodeValue() + "'.");
            }

            NodeList connectionChildren = connectionNode.getChildNodes();
            List<String> devicenameList = new ArrayList<>();
            for (int j = 0; j < connectionChildren.getLength(); j++){
                if (connectionChildren.item(j).getNodeName().equalsIgnoreCase("DEVICENAME")) {
                    devicenameList.add(connectionChildren.item(j).getTextContent());
                }
            }

            if (devicenameList.size() != 2) {
                throw new ParserException("Connections should have two and only two 'devicename' tags.");
            }

            Device firstDevice = deviceMap.get(devicenameList.get(0));
            Device secondDevice = deviceMap.get(devicenameList.get(1));

            if (firstDevice == null || secondDevice == null) {
                throw new ParserException("'devicename' tags can only reference defined devices.");
            }

            Connection connection = new Connection(firstDevice, secondDevice, connectionFormat, connectionType);
            connection.updateDeviceConnections();
        }
    }
}

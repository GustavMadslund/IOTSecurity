package Parser;

import Analysis.Dimension;
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
import sun.security.pkcs.ParsingException;

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

    public Map<String, Dimension> parseEnvironment(String file) throws IOException, SAXException, ParserException {
        // Load XML
        Document doc = db.parse(new File(file));

        // Find all dimension tags
        NodeList dimensions = doc.getElementsByTagName("dimension");

        Map<String, Dimension> dimensionMap = new HashMap<>();

        // Parse dimensions
        for (int i = 0; i < dimensions.getLength(); i++) {
            Node dimensionNode = dimensions.item(i);
            NamedNodeMap dimensionAttributes = dimensionNode.getAttributes();

            String dimensionName;
            try {
                dimensionName = dimensionAttributes.getNamedItem("name").getNodeValue();
            }
            catch (NullPointerException e) {
                throw new ParsingException("Missing 'name' attribute for dimension.");
            }

            double impact;
            try {
                impact = Double.parseDouble(dimensionAttributes.getNamedItem("impact").getNodeValue());
            }
            catch (NullPointerException e) {
                throw new ParserException("Missing 'impact' attribute for dimension.");
            }
            catch (NumberFormatException e) {
                throw new ParserException("Invalid 'impact' attribute for dimension.");
            }
            if (impact < 0.0 || impact > Dimension.SCALE) {
                throw new ParsingException("Impact rating out of range for dimension.");
            }

            double probability;
            try {
                probability = Double.parseDouble(dimensionAttributes.getNamedItem("probability").getNodeValue());
            }
            catch (NullPointerException e) {
                throw new ParserException("Missing 'probability' attribute for dimension.");
            }
            catch (NumberFormatException e) {
                throw new ParserException("Invalid 'probability' attribute for dimension.");
            }
            if (probability < 0.0 || probability > Dimension.SCALE) {
                throw new ParsingException("Probability rating out of range for dimension.");
            }

            dimensionMap.put(dimensionName, new Dimension(dimensionName, impact, probability));
        }

        return dimensionMap;
    }

    public Map<String, Device> parseSystem(String file, Map<String, Dimension> dimensions) throws IOException, SAXException, ParserException {
        // Load XML
        Document doc = db.parse(new File(file));

        // Find all device and connection tags
        NodeList devices = doc.getElementsByTagName("device");
        NodeList connections = doc.getElementsByTagName("connection");

        // Parse devices
        Map<String, Device> deviceMap = parseDevices(devices, dimensions);

        // Parse and encode the connections
        parseConnections(connections, deviceMap);

        return deviceMap;
    }

    private Map<String, Device> parseDevices(NodeList devices, Map<String, Dimension> dimensions) throws ParserException {
        Map<String, Device> deviceMap = new HashMap<>();

        for (int i = 0; i < devices.getLength(); i++){
            Node deviceNode = devices.item(i);
            NamedNodeMap deviceAttributes = deviceNode.getAttributes();

            String deviceName;
            try {
                deviceName = deviceAttributes.getNamedItem("name").getNodeValue();
            }
            catch (NullPointerException e) {
                throw new ParserException("Missing 'name' attribute for device.");
            }

            DeviceType deviceType;
            try {
                deviceType = DeviceType.valueOf(deviceAttributes.getNamedItem("type").getNodeValue().toUpperCase());
            }
            catch (NullPointerException e) {
                throw new ParserException("Missing 'type' attribute for device '" + deviceName + "'.");
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
            device.computeRatings(dimensions);
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
                throw new ParserException("Missing 'format' attribute for connection.");
            }
            catch (IllegalArgumentException e) {
                throw new ParserException("Unknown connection format '" + connectionAttributes.getNamedItem("format").getNodeValue() + "'.");
            }

            ConnectionType connectionType;
            try {
                connectionType = ConnectionType.valueOf(connectionAttributes.getNamedItem("type").getNodeValue().toUpperCase());
            }
            catch (NullPointerException e) {
                throw new ParserException("Missing 'type' attribute for connection.");
            }
            catch (IllegalArgumentException e) {
                throw new ParserException("Unknown connection type '" + connectionAttributes.getNamedItem("type").getNodeValue() + "'.");
            }

            boolean access;
            try {
                access = Boolean.parseBoolean(connectionAttributes.getNamedItem("access").getNodeValue());
            }
            catch (NullPointerException e) {
                throw new ParserException("Missing 'access' attribute for connection.");
            }

            Device fromDevice = null;
            Device toDevice = null;
            NodeList connectionChildren = connectionNode.getChildNodes();
            for (int j = 0; j < connectionChildren.getLength(); j++){
                switch (connectionChildren.item(j).getNodeName().toUpperCase()) {
                    case "FROM":
                        fromDevice = deviceMap.get(connectionChildren.item(j).getTextContent());
                        break;
                    case "TO":
                        toDevice = deviceMap.get(connectionChildren.item(j).getTextContent());
                        break;
                }
            }

            if (fromDevice == null) {
                throw new ParserException("Missing 'from' tag for connection.");
            }

            if (toDevice == null) {
                throw new ParserException("Missing 'to' tag for connection.");
            }

            Connection connection = new Connection(fromDevice, toDevice, connectionFormat, connectionType, access);
            connection.updateDeviceConnections();
        }
    }
}

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    public static void main(String[] args) throws Exception {
        //Load XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File("xml/case1.xml"));

        //Find all device and connection tags
        NodeList devices = doc.getElementsByTagName("device");
        NodeList connections = doc.getElementsByTagName("connection");

        //Parse all devices
        Map<String, Device> deviceMap = new HashMap<>();
        for (int i = 0; i < devices.getLength(); i++){
            Node deviceNode = devices.item(i);
            NamedNodeMap deviceAttributes = deviceNode.getAttributes();

            String deviceName = deviceAttributes.getNamedItem("name").getNodeValue();
            DeviceType deviceType = DeviceType.valueOf(deviceAttributes.getNamedItem("type").getNodeValue().toUpperCase());

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

        //Parse all connections
        for (int i = 0; i < connections.getLength(); i++){
            Node connectionNode = connections.item(i);
            NamedNodeMap connectionAttributes = connectionNode.getAttributes();

            ConnectionFormat connectionFormat = ConnectionFormat.valueOf(connectionAttributes.getNamedItem("format").getNodeValue().toUpperCase());
            ConnectionType connectionType = ConnectionType.valueOf(connectionAttributes.getNamedItem("type").getNodeValue().toUpperCase());

            NodeList connectionChildren = connectionNode.getChildNodes();
            List<String> devicenameList = new ArrayList<>();
            for (int j = 0; j < connectionChildren.getLength(); j++){
                if (connectionChildren.item(j).getNodeName().equalsIgnoreCase("DEVICENAME")) {
                    devicenameList.add(connectionChildren.item(j).getTextContent());
                }
            }
            Device firstDevice = deviceMap.get(devicenameList.get(0));
            Device secondDevice = deviceMap.get(devicenameList.get(1));

            Connection connection = new Connection(firstDevice, secondDevice, connectionFormat, connectionType);
            connection.updateDeviceConnections();
        }

        //Print
        for(Map.Entry<String, Device> entry : deviceMap.entrySet()){
            System.out.println(entry.getValue());
            System.out.println("CONNECTIONS:");
            entry.getValue().getConnections().forEach(System.out::println);
            System.out.println("----------");
        }
    }
}

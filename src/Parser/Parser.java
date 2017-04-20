package Parser;

import Enums.DeviceType;
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
    public static void main(String[] args) throws Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File("xml/case1.xml"));

        NodeList devices = doc.getElementsByTagName("device");
        NodeList connections = doc.getElementsByTagName("connection");

        Map<String, Device> deviceMap = new HashMap<>();
        for(int i = 0; i < devices.getLength(); i++){
            Node deviceNode = devices.item(i);
            NamedNodeMap attributes = deviceNode.getAttributes();

            String deviceName = attributes.getNamedItem("name").getNodeValue();
            DeviceType deviceType;
            switch (attributes.getNamedItem("type").getNodeValue().toUpperCase()) {
                case "THERMOSTAT":
                    deviceType = DeviceType.THERMOSTAT;
                    break;
                case "TV":
                    deviceType = DeviceType.TV;
                    break;
                case "BULB":
                    deviceType = DeviceType.BULB;
                    break;
                case "LOCK":
                    deviceType = DeviceType.LOCK;
                    break;
                case "WINDOW":
                    deviceType = DeviceType.WINDOW;
                    break;
                case "ALARM":
                    deviceType = DeviceType.ALARM;
                    break;
                case "COFFEE_MACHINE":
                    deviceType = DeviceType.COFFEE_MACHINE;
                    break;
                case "SMOKE_ALARM":
                    deviceType = DeviceType.ALARM;
                    break;
                case "PLUG":
                    deviceType = DeviceType.PLUG;
                    break;
                case "CCTV":
                    deviceType = DeviceType.CCTV;
                    break;
                case "FRIDGE":
                    deviceType = DeviceType.FRIDGE;
                    break;
                case "HUB":
                    deviceType = DeviceType.HUB;
                    break;
                case "PHONE":
                    deviceType = DeviceType.PHONE;
                    break;
                default:
                    deviceType = DeviceType.OTHER;
                    break;
            }
            List<String> sensorList = new ArrayList<>();
            List<String> actuatorList = new ArrayList<>();
            List<String> dimensionList = new ArrayList<>();
            NodeList deviceChildren = deviceNode.getChildNodes();
            for(int j = 0; j < deviceChildren.getLength(); j++){
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
            Device dev = new Device(deviceName, deviceType, sensorList, actuatorList, dimensionList);
            deviceMap.put(deviceName, dev);
        }
        for(Map.Entry<String, Device> entry : deviceMap.entrySet()){
            System.out.println(entry.getValue());
        }
    }
}

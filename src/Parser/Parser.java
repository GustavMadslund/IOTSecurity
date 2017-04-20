package Parser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Parser {
    public static void main(String[] args) throws Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File("xml/case1.xml"));

        NodeList devices = doc.getElementsByTagName("device");
        NodeList connections = doc.getElementsByTagName("connection");

        System.out.println("Devices:");
        for(int i = 0; i < devices.getLength(); i++){
            System.out.println(devices.item(i).getAttributes().getNamedItem("name").getNodeValue());
        }
    }
}

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Parser {
    public static void main(String[] args) throws Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File("xml/case1.xml"));

        System.out.println(doc.getElementsByTagName("device").item(0).getAttributes().getNamedItem("name"));
    }
}

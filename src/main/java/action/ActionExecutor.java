package action;

import com.sun.tools.jconsole.JConsoleContext;
import entity.characters.NPC;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ActionExecutor
{
    /*
    public static void main(String[] args) throws ParserConfigurationException, SAXException, NoSuchMethodException
    {
        try
        {
            // carica file
            File file = new File("src/main/resources/scenari/scenario1.xml");

            // parser per xml
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // per ottenere documento
            DocumentBuilder db = dbf.newDocumentBuilder();

            // documento
            Document document = db.parse(file);

            // elabora documento
            document.getDocumentElement().normalize();

            System.out.println("Root Element :" + document.getDocumentElement().getNodeName());

            // ottieni lista di azioni
            NodeList actionList = document.getElementsByTagName("azione");

            // cicla sulle azioni
            for (int i = 0; i < actionList.getLength(); i++)
            {
                //
                Node actionToExecute = actionList.item(i);

                // prendi nome metodo
                String methodName = ((Element) actionToExecute).getElementsByTagName("method").item(0).getTextContent();
                Method method = NPC.class.getMethod(methodName);
                Class[] parameterTypes = method.getParameterTypes();

                for(int j = 0; j < parameterTypes.length; j++)
                {
                    if (!parameterTypes[j].isPrimitive())
                    {
                        Constructor c = parameterTypes[j].getConstructor();
                    }
                        parameterTypes[j].newInstance()
                }

                if (actionToExecute.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) actionToExecute;
                    System.out.println("numAction" + eElement.getAttribute("num"));
                    System.out.println("param1" + eElement.getElementsByTagName("param").item(0).getTextContent());
                    System.out.println("param2" + eElement.getElementsByTagName("param").item(1).getTextContent());
                }
            }
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

     */
}

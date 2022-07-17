package action;

import com.sun.tools.jconsole.JConsoleContext;
import entity.GamePiece;
import entity.characters.NPC;
import entity.rooms.BlockPosition;
import general.GameManager;
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
import java.util.Objects;

@Deprecated
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
                Node actionToExecute = actionList.item(i);
                Element eAction = (Element) actionToExecute;
                // prendi nome metodo
                String methodName = eAction
                                    .getElementsByTagName("method")
                                    .item(0)
                                    .getTextContent();

                if (methodName.equals("move"))
                    parseMove(eAction);

            }
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

     */


}

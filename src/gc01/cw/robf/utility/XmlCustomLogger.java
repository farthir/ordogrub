package gc01.cw.robf.utility;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import gc01.cw.robf.model.LogModel;
import gc01.cw.robf.model.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Utility class for working with XML files for LogModel items. Defines
 * functions to save and load log XML data files.
 * <p>
 * Working with XML adapted from
 * https://www.tutorialspoint.com/java_xml/java_dom_create_document.htm
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class XmlCustomLogger {

	/**
	 * Static method to append a passed LogModel object to the specified XML log
	 * file.
	 * 
	 * @param filePath
	 *            Path of the XML file
	 * @param logObject
	 *            LogModel object to log
	 */
	public static void writeLogLine(String filePath, LogModel logObject) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			Element rootElement;

			// check whether a log already exists so we can append to it
			File logFile = new File(filePath);

			if (logFile.exists()) {
				doc = dBuilder.parse(filePath);
				rootElement = doc.getDocumentElement();
			} else {
				doc = dBuilder.newDocument();

				// root log element
				rootElement = doc.createElement("logLines");
				doc.appendChild(rootElement);
			}

			Element logLineElement = doc.createElement("logLine");
			rootElement.appendChild(logLineElement);

			Element username = doc.createElement("username");
			username.appendChild(doc.createTextNode(logObject.getUsername()));
			logLineElement.appendChild(username);

			Element dateTime = doc.createElement("dateTime");
			dateTime.appendChild(doc.createTextNode(logObject.getDateTime()));
			logLineElement.appendChild(dateTime);

			Element message = doc.createElement("message");
			message.appendChild(doc.createTextNode(logObject.getMessage()));
			logLineElement.appendChild(message);

			// save the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath));
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Static method to retrieve LogModel objects for the specified UserModel
	 * user from a specified file.
	 * 
	 * @param filePath
	 *            Path of the XML file
	 * @param user
	 *            UserModel object of the user to search for
	 * @return ObservableList of LogModel objects which were generated by the
	 *         specified user
	 */
	public static ObservableList<LogModel> readLogLinesByUser(String filePath, UserModel user) {
		ObservableList<LogModel> logLines = FXCollections.observableArrayList();

		try {
			File inputFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;

			dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("logLine");

			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					if (eElement.getElementsByTagName("username").item(0).getTextContent().equals(user.getUsername())) {
						String username = eElement.getElementsByTagName("username").item(0).getTextContent();
						String dateTime = eElement.getElementsByTagName("dateTime").item(0).getTextContent();
						String message = eElement.getElementsByTagName("message").item(0).getTextContent();

						LogModel logObject = new LogModel(username, message);
						logObject.setDateTime(dateTime);

						logLines.add(logObject);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return logLines;
	}
}

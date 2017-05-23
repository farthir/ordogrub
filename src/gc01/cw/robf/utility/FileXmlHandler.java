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

import gc01.cw.robf.model.MenuItemModel;
import gc01.cw.robf.model.OrderModel;
import gc01.cw.robf.model.UserModel;
import javafx.collections.ObservableList;

/**
 * Utility class for working with XML files for OrderModel, UserModel and
 * MenuItemModel items. Defines functions to save and load respective XML data
 * file for each type.
 * <p>
 * Working with XML adapted from
 * https://www.tutorialspoint.com/java_xml/java_dom_create_document.htm
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class FileXmlHandler {

	/**
	 * Static method which parses the specified XML file and loads the stored
	 * nodes as UserModel objects. Loaded file requires structure created by
	 * saveUsers method.
	 * 
	 * @param filePath
	 *            Path of the XML file
	 * @param overwrite
	 *            Specifies whether loaded users should overwrite current users
	 *            in memory or append only.
	 */
	public static void loadUsers(String filePath, boolean overwrite) {

		// allows the loader to completely clear or just append to the list
		if (overwrite) {
			// clear the current list of objects
			ObservableList<UserModel> userList = UserModel.getUserList();
			userList.clear();
		}

		try {
			File inputFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("user");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					String fullName = eElement.getElementsByTagName("fullName").item(0).getTextContent();
					String username = eElement.getElementsByTagName("username").item(0).getTextContent();
					String password = eElement.getElementsByTagName("password").item(0).getTextContent();
					String userType = eElement.getElementsByTagName("userType").item(0).getTextContent();

					new UserModel(fullName, username, password, UserModel.UserType.valueOf(userType));
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Static method for taking list of UserModel objects and saving to
	 * specified XML file.
	 * 
	 * @param filePath
	 *            Path of the XML file
	 * @param userList
	 *            ObservableList of UserModel objects to save.
	 */
	public static void saveUsers(String filePath, ObservableList<UserModel> userList) {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// root users element
			Element rootElement = doc.createElement("users");
			doc.appendChild(rootElement);

			for (UserModel user : userList) {
				// user element
				Element userElement = doc.createElement("user");
				rootElement.appendChild(userElement);

				// full name element
				Element fullName = doc.createElement("fullName");
				fullName.appendChild(doc.createTextNode(user.getFullName()));
				userElement.appendChild(fullName);

				// username element
				Element username = doc.createElement("username");
				username.appendChild(doc.createTextNode(user.getUsername()));
				userElement.appendChild(username);

				// password element
				Element password = doc.createElement("password");
				password.appendChild(doc.createTextNode(user.getPassword()));
				userElement.appendChild(password);

				// user type element
				Element userType = doc.createElement("userType");
				userType.appendChild(doc.createTextNode(user.getType()));
				userElement.appendChild(userType);
			}

			// write the content into xml file
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
		}

	}

	/**
	 * Static method which parses the specified XML file and loads the stored
	 * nodes as OrderModel objects. Loaded file requires structure created by
	 * saveOrders method. Uses name of menu items contained within orders to
	 * lookup MenuItemModel objects. This requires that a MenuItemObject exists
	 * for such orders.
	 * 
	 * @param filePath
	 *            Path of the XML file
	 * @param overwrite
	 *            Specifies whether loaded orders should overwrite current
	 *            orders in memory or append only.
	 */
	public static void loadOrders(String filePath, boolean overwrite) {
		ObservableList<MenuItemModel> menuItemList = MenuItemModel.getMenu();

		// allows the loader to completely clear or just append to the list
		if (overwrite) {
			ObservableList<OrderModel> orderList = OrderModel.getOrderList();
			orderList.clear();
		}

		try {
			File inputFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("order");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					Integer tableNumber = Integer
							.valueOf(eElement.getElementsByTagName("tableNumber").item(0).getTextContent());
					String creatingUser = eElement.getElementsByTagName("creatingUser").item(0).getTextContent();
					String status = eElement.getElementsByTagName("status").item(0).getTextContent();
					String dateOpened = eElement.getElementsByTagName("dateOpened").item(0).getTextContent();
					String dateClosed = eElement.getElementsByTagName("dateClosed").item(0).getTextContent();
					Double totalCost = Double
							.valueOf(eElement.getElementsByTagName("totalCost").item(0).getTextContent());
					String comments = eElement.getElementsByTagName("comments").item(0).getTextContent();

					OrderModel order = new OrderModel(tableNumber);
					// override defaults
					order.setCreatingUser(creatingUser);
					order.setStatus(OrderModel.OrderStatus.valueOf(status));
					order.setDateOpened(dateOpened);
					order.setDateClosed(dateClosed);
					order.setTotalCost(totalCost);
					order.setComments(comments);

					// look up menu item objects from saved name and add to
					// order
					ObservableList<MenuItemModel> orderMenuItems = order.getItemList();

					NodeList menuItem = eElement.getElementsByTagName("menuItem");
					for (int j = 0; j < menuItem.getLength(); j++) {
						Node menuItemNode = menuItem.item(j);
						if (menuItemNode.getNodeType() == Node.ELEMENT_NODE) {
							Element menuItemElement = (Element) menuItemNode;
							String itemName = menuItemElement.getElementsByTagName("name").item(0).getTextContent();

							for (MenuItemModel mItem : menuItemList) {
								if (mItem.getName().equals(itemName)) {
									orderMenuItems.add(mItem);
									break;
								}
							}
						}
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

	}

	/**
	 * Static method for taking list of OrderModel objects and saving to
	 * specified XML file.
	 * 
	 * @param filePath
	 *            Path of the XML file
	 * @param orderList
	 *            ObservableList of OrderModel objects to save.
	 */
	public static void saveOrders(String filePath, ObservableList<OrderModel> orderList) {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// root users element
			Element rootElement = doc.createElement("orders");
			doc.appendChild(rootElement);

			for (OrderModel order : orderList) {
				// order element
				Element orderElement = doc.createElement("order");
				rootElement.appendChild(orderElement);

				Element tableNumber = doc.createElement("tableNumber");
				tableNumber.appendChild(doc.createTextNode(order.getTableNumber().toString()));
				orderElement.appendChild(tableNumber);

				Element creatingUser = doc.createElement("creatingUser");
				creatingUser.appendChild(doc.createTextNode(order.getCreatingUser()));
				orderElement.appendChild(creatingUser);

				Element status = doc.createElement("status");
				status.appendChild(doc.createTextNode(order.getStatus()));
				orderElement.appendChild(status);

				Element dateOpened = doc.createElement("dateOpened");
				dateOpened.appendChild(doc.createTextNode(order.getDateOpened()));
				orderElement.appendChild(dateOpened);

				Element dateClosed = doc.createElement("dateClosed");
				dateClosed.appendChild(doc.createTextNode(order.getDateClosed()));
				orderElement.appendChild(dateClosed);

				Element totalCost = doc.createElement("totalCost");
				totalCost.appendChild(doc.createTextNode(order.getTotalCost().toString()));
				orderElement.appendChild(totalCost);

				Element comments = doc.createElement("comments");
				comments.appendChild(doc.createTextNode(order.getComments()));
				orderElement.appendChild(comments);

				// write the items within the order using the name. Name field
				// is assumed unique
				for (MenuItemModel menuItem : order.getItemList()) {
					Element menuItemElement = doc.createElement("menuItem");
					orderElement.appendChild(menuItemElement);

					Element name = doc.createElement("name");
					name.appendChild(doc.createTextNode(menuItem.getName()));
					menuItemElement.appendChild(name);
				}

			}

			// write the content into xml file
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
		}

	}

	/**
	 * Static method which parses the specified XML file and loads the stored
	 * nodes as MenuItemModel objects. Loaded file requires structure created by
	 * saveMenuItems method.
	 * 
	 * @param filePath
	 *            Path of the XML file
	 * @param overwrite
	 *            Specifies whether loaded menu items should overwrite current
	 *            menu items in memory or append only.
	 */
	public static void loadMenuItems(String filePath, boolean overwrite) {

		// allows the loader to completely clear or just append to the list
		if (overwrite) {
			ObservableList<MenuItemModel> menuItemList = MenuItemModel.getMenu();
			menuItemList.clear();
		}

		try {
			File inputFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("menuItem");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String name = eElement.getElementsByTagName("name").item(0).getTextContent();
					String description = eElement.getElementsByTagName("description").item(0).getTextContent();
					Double price = Double.valueOf(eElement.getElementsByTagName("price").item(0).getTextContent());
					String type = eElement.getElementsByTagName("type").item(0).getTextContent();

					new MenuItemModel(name, description, price, MenuItemModel.ItemType.valueOf(type));
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Static method for taking list of MenuItemModel objects and saving to
	 * specified XML file.
	 * 
	 * @param filePath
	 *            Path of the XML file
	 * @param menuItemList
	 *            ObservableList of MenuItemModel objects to save.
	 */
	public static void saveMenuItems(String filePath, ObservableList<MenuItemModel> menuItemList) {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// root users element
			Element rootElement = doc.createElement("menuItems");
			doc.appendChild(rootElement);

			for (MenuItemModel menuItem : menuItemList) {
				Element menuItemElement = doc.createElement("menuItem");
				rootElement.appendChild(menuItemElement);

				Element name = doc.createElement("name");
				name.appendChild(doc.createTextNode(menuItem.getName()));
				menuItemElement.appendChild(name);

				Element description = doc.createElement("description");
				description.appendChild(doc.createTextNode(menuItem.getDescription()));
				menuItemElement.appendChild(description);

				Element price = doc.createElement("price");
				price.appendChild(doc.createTextNode(menuItem.getPrice().toString()));
				menuItemElement.appendChild(price);

				Element type = doc.createElement("type");
				type.appendChild(doc.createTextNode(menuItem.getType()));
				menuItemElement.appendChild(type);

			}

			// write the content into xml file
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
		}

	}

	/*
	 * // hashing passwords to protect them during save // Adapted from
	 * https://adambard.com/blog/3-wrong-ways-to-store-a-password/ public static
	 * String hashPassword(final String password, final String salt) { final int
	 * ITERATIONS = 1000; final int KEY_LENGTH = 192; // bits
	 * 
	 * char[] passwordChars = password.toCharArray(); byte[] saltBytes =
	 * salt.getBytes(); byte[] hashedPassword = {};
	 * 
	 * PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, ITERATIONS,
	 * KEY_LENGTH); SecretKeyFactory key; try { key =
	 * SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1"); hashedPassword =
	 * key.generateSecret(spec).getEncoded(); } catch (NoSuchAlgorithmException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (InvalidKeySpecException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * return String.format("%x", new BigInteger(hashedPassword)); }
	 */

}

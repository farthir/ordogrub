package gc01.cw.robf.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model for menu item objects. Uses Property and Observable types for most
 * variables to allow JavaFX tableView refresh. Contains enum ItemType to
 * describe menu item type.
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class MenuItemModel {

	/**
	 * Simple enum for type of MenuItemModel
	 * 
	 * @author Rob Farthing
	 * @version 1.0.0
	 *
	 */
	public enum ItemType {
		STARTER, MAIN, DESSERT, SIDE, DRINK;
	}

	private SimpleStringProperty name;
	private SimpleStringProperty description;
	private SimpleDoubleProperty price;
	private SimpleStringProperty type;
	private static ObservableList<MenuItemModel> menuItemList = FXCollections.observableArrayList();

	/**
	 * Constructor for Menu Item objects. Adds new menu items to the statically
	 * accessible menuItemList list.
	 * 
	 * @param name
	 *            String of short name for menu item.
	 * @param description
	 *            String of item description.
	 * @param price
	 *            Double of item price.
	 * @param type
	 *            MenuModelItem.ItemType enum of item type.
	 */
	public MenuItemModel(String name, String description, Double price, ItemType type) {
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.price = new SimpleDoubleProperty(price);
		this.type = new SimpleStringProperty(type.toString());
		menuItemList.add(this);
	}

	/**
	 * Static method for returning the list of all menu items.
	 * 
	 * @return ObservableList containing all MenuItemModel objects.
	 */
	public static ObservableList<MenuItemModel> getMenu() {
		return menuItemList;
	}

	/**
	 * @return String value of object name.
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @param name
	 *            String value of name to set.
	 */
	public void setName(String name) {
		this.name.set(name);
	}

	/**
	 * @return String value of object description.
	 */
	public String getDescription() {
		return description.get();
	}

	/**
	 * @param description
	 *            String value of description to set.
	 */
	public void setDescription(String description) {
		this.description.set(description);
	}

	/**
	 * @return Double value of object price.
	 */
	public Double getPrice() {
		return price.get();
	}

	/**
	 * @param price
	 *            Double value of price to set.
	 */
	public void setPrice(Double price) {
		this.price.set(price);
	}

	/**
	 * @return String value of object type.
	 */
	public String getType() {
		return type.get();
	}

	/**
	 * @param type
	 *            MenuItemModel.ItemType enum of type to set.
	 */
	public void setType(ItemType type) {
		this.type.set(type.toString());
	}

	/*
	 * Overridden toString method to return String value of object name.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name.get();
	}

}

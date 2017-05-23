package gc01.cw.robf.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model for Order objects. Uses Property and Observable types for most
 * variables to allow JavaFX tableView refresh.
 * <p>
 * Returning current date-time adapted from
 * https://www.mkyong.com/java/java-how-to-get-current-date-time-date-and-calender/
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class OrderModel {

	/**
	 * Simple enum for OrderModel status
	 * 
	 * @author Rob Farthing
	 * @version 1.0.0
	 *
	 */
	public enum OrderStatus {
		OPEN, CLOSED;
	}

	private SimpleIntegerProperty tableNumber;
	private SimpleStringProperty creatingUser;
	private SimpleStringProperty status;
	private SimpleStringProperty dateOpened;
	private SimpleStringProperty dateClosed;
	private SimpleDoubleProperty totalCost;
	private SimpleStringProperty comments;
	private ObservableList<MenuItemModel> itemList = FXCollections.observableArrayList();

	private static ObservableList<OrderModel> orderList = FXCollections.observableArrayList();
	private static OrderModel currentOrder;

	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	/**
	 * Constructor for OrderModel objects. Automatically sets status to OPEN and
	 * dateOpened to the current date-time. Adds new orders to the statically
	 * accessible orderList list.
	 * 
	 * @param tableNumber
	 *            Integer value of table number to create order against.
	 */
	public OrderModel(Integer tableNumber) {
		this.tableNumber = new SimpleIntegerProperty(tableNumber);
		this.creatingUser = new SimpleStringProperty(UserModel.getCurrentUser().getFullName());
		this.status = new SimpleStringProperty(OrderModel.OrderStatus.OPEN.toString());
		LocalDateTime now = LocalDateTime.now();
		this.dateOpened = new SimpleStringProperty(dtf.format(now));
		this.dateClosed = new SimpleStringProperty("N/A");
		this.totalCost = new SimpleDoubleProperty(0.0);
		this.comments = new SimpleStringProperty("N/A");

		orderList.add(this);
	}

	/**
	 * Static method for returning the list of all orders.
	 * 
	 * @return ObservableList containing all OrderModel objects.
	 */
	public static ObservableList<OrderModel> getOrderList() {
		return orderList;
	}

	/**
	 * Static method for returning the current order as assigned with
	 * setCurrentOrder
	 * 
	 * @return OrderModel of current order
	 */
	public static OrderModel getCurrentOrder() {
		return currentOrder;
	}

	/**
	 * Static method for setting the current order
	 * 
	 * @param currentOrder
	 *            OrderModel object to set to static reference
	 */
	public static void setCurrentOrder(OrderModel currentOrder) {
		OrderModel.currentOrder = currentOrder;
	}

	/**
	 * @return String value of object status.
	 */
	public String getStatus() {
		return status.get();
	}

	/**
	 * If input status is CLOSED, also sets the dateClosed variable to the
	 * current datetime.
	 * 
	 * @param status
	 *            OrderModel.OrderStatus enum of status to set.
	 */
	public void setStatus(OrderStatus status) {
		this.status.set(status.toString());

		if (this.status.get().equals(OrderStatus.CLOSED.toString())) {
			closeOrder();
		}

	}

	/**
	 * @return String value of object creating user username.
	 */
	public String getCreatingUser() {
		return creatingUser.get();
	}

	/**
	 * @param creatingUser
	 *            String of creating user username.
	 */
	public void setCreatingUser(String creatingUser) {
		this.creatingUser.set(creatingUser);
	}

	/**
	 * @return Integer value of object table number.
	 */
	public Integer getTableNumber() {
		return tableNumber.get();
	}

	/**
	 * @param tableNumber
	 *            Integer of table number for object.
	 */
	public void setTableNumber(Integer tableNumber) {
		this.tableNumber.set(tableNumber);
	}

	/**
	 * Calculates total order cost based on prices of items in order item list.
	 * 
	 * @return Double value of object total cost.
	 */
	public Double getTotalCost() {
		Double tempCost = 0.0;

		for (MenuItemModel item : this.getItemList()) {
			tempCost += (Double) item.getPrice();
		}

		totalCost.set(tempCost);
		return totalCost.get();
	}

	/**
	 * @param totalCost
	 *            Double of total order cost. Required as menu items may change
	 *            in price so we want a way to set this from file
	 */
	public void setTotalCost(Double totalCost) {
		this.totalCost.set(totalCost);
	}

	/**
	 * @return String value of object comments.
	 */
	public String getComments() {
		return comments.get();
	}

	/**
	 * @param comments
	 *            String of order comments to set.
	 */
	public void setComments(String comments) {
		this.comments.set(comments);
	}

	/**
	 * @return String value of object opened datetime.
	 */
	public String getDateOpened() {
		return dateOpened.get();
	}

	/**
	 * @param dateOpened
	 *            String of dateOpened for object. Expected format yyyy/MM/dd
	 *            HH:mm:ss
	 */
	public void setDateOpened(String dateOpened) {
		this.dateOpened.set(dateOpened);
	}

	/**
	 * @return String value of object closed datetime.
	 */
	public String getDateClosed() {
		return dateClosed.get();
	}

	/**
	 * @param dateClosed
	 *            String of dateClosed for object. Expected format yyyy/MM/dd
	 *            HH:mm:ss
	 */
	public void setDateClosed(String dateClosed) {
		this.dateClosed.set(dateClosed);
	}

	/**
	 * @return ObservableList of MenuItemModel objects of order items.
	 */
	public ObservableList<MenuItemModel> getItemList() {
		return itemList;
	}

	/**
	 * Must pass full menu item list as method overwrites existing items.
	 * 
	 * @param itemList
	 *            ObservableList of MenuItemModel containing list of menu items
	 *            in order.
	 */
	public void setItemList(ObservableList<MenuItemModel> itemList) {
		this.itemList.clear();
		this.itemList.addAll(itemList);
	}

	/**
	 * @return int of total number of items in order.
	 */
	public int getNumberItems() {
		return this.itemList.size();
	}

	/*
	 * Private method for setting the dateClosed value when setting order status
	 * to CLOSED.
	 */
	private void closeOrder() {
		LocalDateTime now = LocalDateTime.now();
		this.dateClosed = new SimpleStringProperty(dtf.format(now));
	}
}

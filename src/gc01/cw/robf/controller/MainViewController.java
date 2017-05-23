package gc01.cw.robf.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import gc01.cw.robf.model.LogModel;
import gc01.cw.robf.model.MenuItemModel;
import gc01.cw.robf.model.OrderModel;
import gc01.cw.robf.model.UserModel;
import gc01.cw.robf.utility.FileXmlHandler;
import gc01.cw.robf.utility.XmlCustomLogger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Controller for the main application interface. Includes click-able view of
 * table layout and control over basic order information. Uses FXML template.
 * Access to restricted menus (User and Menu editor) dependent on type of logged
 * in user.
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class MainViewController {

	private String orderFilePath = "./data/orders.xml";
	private String logFilePath = "./data/activityLog.xml";

	private Stage loginViewStage;
	private Stage mainViewStage;
	private Stage manageUsersViewStage;
	private Stage manageOrdersViewStage;
	private Stage manageMenuItemsViewStage;
	private Stage editOrderItemsView;
	private ObservableList<OrderModel> orderList;
	private Shape currentTable;
	private Integer intTableNumber;

	@FXML
	private Button logoutButton;

	@FXML
	private Button userManagerButton;

	@FXML
	private Button menuManagerButton;

	@FXML
	private Button openOrderButton;

	@FXML
	private Button closeOrderButton;

	@FXML
	private Button editOrderItemsButton;

	@FXML
	private Button clearTableButton;

	@FXML
	private Button saveCommentsButton;

	@FXML
	private Label systemStatusLabel;

	@FXML
	private Label tableNumberLabel;

	@FXML
	private Label statusLabel;

	@FXML
	private Label creatingUserLabel;

	@FXML
	private Label timeOpenedLabel;

	@FXML
	private ListView<MenuItemModel> itemsListView;

	@FXML
	private Label totalCostLabel;

	@FXML
	private TextArea commentsTextArea;

	/**
	 * Method triggered when user presses Logout button. Closes the Main view
	 * and reloads the Login view.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void logout(ActionEvent event) {
		XmlCustomLogger.writeLogLine(logFilePath,
				new LogModel(UserModel.getCurrentUser().getUsername(), "User successfully logged out."));

		// close Main View and restore Login View
		mainViewStage = (Stage) logoutButton.getScene().getWindow();
		mainViewStage.close();

		loginViewStage = new Stage();
		loginViewStage.setTitle("Login");

		// Open FXML and load
		FXMLLoader loginViewLoader = new FXMLLoader(getClass().getResource("/gc01/cw/robf/view/LoginView.fxml"));
		Scene loginViewScene;
		try {
			loginViewScene = new Scene(loginViewLoader.load());
			String baseCss = this.getClass().getResource("/gc01/cw/robf/view/base.css").toExternalForm();
			loginViewScene.getStylesheets().addAll(baseCss);
			loginViewStage.setScene(loginViewScene);
			loginViewStage.getIcons().add(new Image("file:./static/OrdoGrubLogo.png"));
			loginViewStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method triggered when user presses Manage Users button. Loads the Manage
	 * Users view in modal and undecorated mode to force user to complete
	 * interaction with Manage Users view before returning to Main view. Applies
	 * overlay CSS formatting to window as well as basic formatting. This button
	 * is only available if the user is of type Manager.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void manageUsers(ActionEvent event) {
		// clear system status
		systemStatusLabel.setText(null);

		// Load Manage Users View
		mainViewStage = (Stage) logoutButton.getScene().getWindow();

		manageUsersViewStage = new Stage();
		manageUsersViewStage.setTitle("Manage Users");
		manageUsersViewStage.initOwner(mainViewStage);
		manageUsersViewStage.initModality(Modality.WINDOW_MODAL);
		manageUsersViewStage.initStyle(StageStyle.UNDECORATED);

		// Open FXML and load
		FXMLLoader manageUsersViewLoader = new FXMLLoader(
				getClass().getResource("/gc01/cw/robf/view/ManageUsersView.fxml"));
		Scene manageUsersViewScene;
		try {
			manageUsersViewScene = new Scene(manageUsersViewLoader.load());
			String baseCss = this.getClass().getResource("/gc01/cw/robf/view/base.css").toExternalForm();
			String overlayCss = this.getClass().getResource("/gc01/cw/robf/view/overlay.css").toExternalForm();
			manageUsersViewScene.getStylesheets().addAll(baseCss, overlayCss);
			manageUsersViewStage.setScene(manageUsersViewScene);
			manageUsersViewStage.getIcons().add(new Image("file:./static/OrdoGrubLogo.png"));
			manageUsersViewStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method triggered when user presses Manage Orders button. Loads the Manage
	 * Orders view in modal and undecorated mode to force user to complete
	 * interaction with Manage Orders view before returning to Main view.
	 * Applies overlay CSS formatting to window as well as basic formatting.
	 * Overrides EventHandler for when Orders view is closed so that the table
	 * layout in Main view is cleared as it could have changed.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void manageOrders(ActionEvent event) {
		// clear system status
		systemStatusLabel.setText(null);

		// Load Manage Orders View
		mainViewStage = (Stage) logoutButton.getScene().getWindow();

		manageOrdersViewStage = new Stage();
		manageOrdersViewStage.setTitle("Manage Orders");
		manageOrdersViewStage.initOwner(mainViewStage);
		manageOrdersViewStage.initModality(Modality.WINDOW_MODAL);
		manageOrdersViewStage.initStyle(StageStyle.UNDECORATED);

		// Open FXML and load
		FXMLLoader manageOrdersViewLoader = new FXMLLoader(
				getClass().getResource("/gc01/cw/robf/view/ManageOrdersView.fxml"));
		Scene manageOrdersViewScene;
		try {
			manageOrdersViewScene = new Scene(manageOrdersViewLoader.load());
			String baseCss = this.getClass().getResource("/gc01/cw/robf/view/base.css").toExternalForm();
			String overlayCss = this.getClass().getResource("/gc01/cw/robf/view/overlay.css").toExternalForm();
			manageOrdersViewScene.getStylesheets().addAll(baseCss, overlayCss);
			manageOrdersViewStage.setScene(manageOrdersViewScene);
			manageOrdersViewStage.getIcons().add(new Image("file:./static/OrdoGrubLogo.png"));
			manageOrdersViewStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// clear the table as changes could have been made which impact the
		// order
		manageOrdersViewStage.setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				clearTable(event);
			}
		});

	}

	/**
	 * Method triggered when user presses Manage Menu button. Loads the Manage
	 * Menu view in modal and undecorated mode to force user to complete
	 * interaction with Manage Menu view before returning to Main view. Applies
	 * overlay CSS formatting to window as well as basic formatting. This button
	 * is only available if the user is of type Manager.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void manageMenuItems(ActionEvent event) {
		// clear system status
		systemStatusLabel.setText(null);

		// Load Manage Menu View
		mainViewStage = (Stage) logoutButton.getScene().getWindow();

		manageMenuItemsViewStage = new Stage();
		manageMenuItemsViewStage.setTitle("Manage Menu Items");
		manageMenuItemsViewStage.initOwner(mainViewStage);
		manageMenuItemsViewStage.initModality(Modality.WINDOW_MODAL);
		manageMenuItemsViewStage.initStyle(StageStyle.UNDECORATED);

		// Open FXML and load
		FXMLLoader manageMenuItemsViewLoader = new FXMLLoader(
				getClass().getResource("/gc01/cw/robf/view/ManageMenuItemsView.fxml"));
		Scene manageMenuItemsViewScene;
		try {
			manageMenuItemsViewScene = new Scene(manageMenuItemsViewLoader.load());
			String baseCss = this.getClass().getResource("/gc01/cw/robf/view/base.css").toExternalForm();
			String overlayCss = this.getClass().getResource("/gc01/cw/robf/view/overlay.css").toExternalForm();
			manageMenuItemsViewScene.getStylesheets().addAll(baseCss, overlayCss);
			manageMenuItemsViewStage.setScene(manageMenuItemsViewScene);
			manageMenuItemsViewStage.getIcons().add(new Image("file:./static/OrdoGrubLogo.png"));
			manageMenuItemsViewStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method triggered when user presses Clear Table button. Resets the layout
	 * of the tables, clears the order pane and flushes the current order. Can
	 * be called at any time to reset the view.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void clearTable(Event event) {
		clearTableButton.setDisable(true);
		systemStatusLabel.setText(null);

		if (currentTable != null) {
			// clear the current order
			OrderModel.setCurrentOrder(null);

			// clear selected table
			currentTable.setStyle(null);
			currentTable = null;

			// clear order pane
			openOrderButton.setDisable(true);
			closeOrderButton.setDisable(true);
			editOrderItemsButton.setDisable(true);
			saveCommentsButton.setDisable(true);
			tableNumberLabel.setText("no table selected");
			statusLabel.setText(null);
			creatingUserLabel.setText(null);
			timeOpenedLabel.setText(null);
			itemsListView.setItems(null);
			totalCostLabel.setText(null);
			commentsTextArea.setEditable(false);
			commentsTextArea.setText(null);
		}
	}

	/**
	 * Method triggered when user clicks any table Shape defined in the Main
	 * FXML. Clears any existing selection and highlights clicked table. Sets
	 * the selected table number and displays any existing orders in the order
	 * pane.
	 * 
	 * @param event
	 *            Mouse event object passed during trigger.
	 */
	@FXML
	public void selectTable(MouseEvent event) {

		// clear the currently selected table (if there is one)
		clearTable(event);

		clearTableButton.setDisable(false);

		// set the table formatting to show it highlighted
		Shape clickedTable = (Shape) event.getSource();
		currentTable = clickedTable;
		currentTable.setStyle("-fx-fill: firebrick");

		// get the table number clicked
		intTableNumber = Integer.valueOf(currentTable.getId().split("table")[1]);

		// set table number text field
		tableNumberLabel.setText(intTableNumber.toString());

		displayOrder();
	}

	/**
	 * Method triggered when user presses Open Order button. Open order button
	 * is only available if a table has been selected and there is no current
	 * Open order for that table. When triggered, creates a new order object on
	 * the highlighted table and immediately saves it to file. Refreshes the
	 * order pane.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void openOrder(ActionEvent event) {
		// create new order and save to file immediately (so not lost if user
		// closes window)
		OrderModel order = new OrderModel(intTableNumber);
		OrderModel.setCurrentOrder(order);

		XmlCustomLogger.writeLogLine(logFilePath, new LogModel(UserModel.getCurrentUser().getUsername(),
				"Order opened on Table " + OrderModel.getCurrentOrder().getTableNumber() + "."));

		FileXmlHandler.saveOrders(orderFilePath, OrderModel.getOrderList());

		openOrderButton.setDisable(true);
		displayOrder();

	}

	/**
	 * Method triggered when user presses Close Order button. Close order button
	 * is only available if a table has been selected and there is an Open order
	 * for that table. When triggered, activates an Alert prompt and if user
	 * accepts this, closes the order on the highlighted table and immediately
	 * saves to file. Clears the table selection as this order can no longer be
	 * modified.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void closeOrder(ActionEvent event) {
		// create a confirmation dialog
		Alert confirmOrderClose = new Alert(AlertType.CONFIRMATION);
		confirmOrderClose.setTitle("Close Order Confirmation");
		confirmOrderClose.setHeaderText(
				"You have chosen to close the order on Table " + OrderModel.getCurrentOrder().getTableNumber() + ".");
		confirmOrderClose.setContentText("Proceed?");

		Optional<ButtonType> buttonPressed = confirmOrderClose.showAndWait();
		if (buttonPressed.get() == ButtonType.OK) {
			XmlCustomLogger.writeLogLine(logFilePath, new LogModel(UserModel.getCurrentUser().getUsername(),
					"Order closed on Table " + OrderModel.getCurrentOrder().getTableNumber() + "."));
			OrderModel.getCurrentOrder().setStatus(OrderModel.OrderStatus.CLOSED);

			FileXmlHandler.saveOrders(orderFilePath, OrderModel.getOrderList());
			clearTable(event);
		}
	}

	/**
	 * Method triggered when user presses Edit button under Order pane's Items
	 * list box. Button only available if a table has been selected and there is
	 * an Open order for that table. Loads the Edit Order Items view in modal
	 * and undecorated mode to force user to complete interaction before
	 * returning to Main view. Applies overlay CSS formatting to window as well
	 * as basic formatting. Overrides EventHandler for when view is closed to
	 * refresh Order pane as it could have changed.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void editOrderItems(ActionEvent event) {
		// clear system status
		systemStatusLabel.setText(null);

		// Load Orders View
		mainViewStage = (Stage) logoutButton.getScene().getWindow();

		editOrderItemsView = new Stage();
		editOrderItemsView.setTitle("Edit Order Items");
		editOrderItemsView.initOwner(mainViewStage);
		editOrderItemsView.initModality(Modality.WINDOW_MODAL);
		editOrderItemsView.initStyle(StageStyle.UNDECORATED);

		// Open FXML and load
		FXMLLoader editOrderItemsViewLoader = new FXMLLoader(
				getClass().getResource("/gc01/cw/robf/view/EditOrderItemsView.fxml"));
		Scene editOrderItemsViewScene;
		try {
			editOrderItemsViewScene = new Scene(editOrderItemsViewLoader.load());
			String baseCss = this.getClass().getResource("/gc01/cw/robf/view/base.css").toExternalForm();
			String overlayCss = this.getClass().getResource("/gc01/cw/robf/view/overlay.css").toExternalForm();
			editOrderItemsViewScene.getStylesheets().addAll(baseCss, overlayCss);
			editOrderItemsView.setScene(editOrderItemsViewScene);
			editOrderItemsView.getIcons().add(new Image("file:./static/OrdoGrubLogo.png"));
			editOrderItemsView.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// call displayOrder when closing edit window so that all values refresh
		editOrderItemsView.setOnHidden(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				displayOrder();
			}

		});
	}

	/**
	 * Method triggered when user presses Save button under comments text box.
	 * Save button is only available if a table has been selected and there is
	 * an Open order for that table. When triggered, sets the comments in the
	 * current Order and immediately saves to file.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void saveComments(ActionEvent event) {
		XmlCustomLogger.writeLogLine(logFilePath,
				new LogModel(UserModel.getCurrentUser().getUsername(),
						"Comments saved for order opened on " + OrderModel.getCurrentOrder().getDateOpened()
								+ " on Table " + OrderModel.getCurrentOrder().getTableNumber() + "."));

		// set the comments in the current order object
		OrderModel.getCurrentOrder().setComments(commentsTextArea.getText());

		// save the orders
		FileXmlHandler.saveOrders(orderFilePath, orderList);

		// tell the user
		systemStatusLabel.setText("Comments saved");
	}

	/**
	 * Method automatically called by JavaFX Application class when loading a
	 * view. Loads existing list of orders and enables User and Menu Manager
	 * buttons if the currentUser type is MANAGER.
	 */
	public void initialize() {

		// initialise lists for use in creating orders
		orderList = OrderModel.getOrderList();

		// allow access to user and menu managers for Manager users
		if (UserModel.getCurrentUser().getType().equals(UserModel.UserType.MANAGER.toString())) {
			userManagerButton.setDisable(false);
			menuManagerButton.setDisable(false);
		}
	}

	/*
	 * Private method which opens an order in the Order pane in the Main view.
	 * Handles multiple open orders per table: Sorts list of orders by latest
	 * opened date first. Checks whether there is >1 open order on current table
	 * and warns user if so. Opens latest order if there is one. If no order is
	 * found, enables use of Open order button.
	 */
	private void displayOrder() {
		// sort the orders in ascending opened date order to allow us to get
		// the latest open order if there is more than one
		Collections.sort(orderList, new Comparator<OrderModel>() {
			@Override
			public int compare(OrderModel oM1, OrderModel oM2) {
				return oM1.getDateOpened().compareTo(oM2.getDateOpened());
			}

		});

		int openOrderCount = 0;
		boolean existingOrder = false;

		// check whether there is one or more open orders on the table
		// take the last open order for the table in any case which is the
		// latest based on sort above
		for (OrderModel order : orderList) {
			if (order.getTableNumber().equals(intTableNumber) && order.getStatus().equals("OPEN")) {
				OrderModel.setCurrentOrder(order);
				existingOrder = true;
				openOrderCount++;
			}
		}

		// set the order view for the selected table if one exists
		if (existingOrder) {
			// print a warning if the open order count is greater than one for
			// the table
			if (openOrderCount > 1) {
				systemStatusLabel.setText(
						"Warning: more than one open order for table " + OrderModel.getCurrentOrder().getTableNumber()
								+ ". Most recent opened. Close other open orders in Order Manager.");

				XmlCustomLogger.writeLogLine(logFilePath,
						new LogModel(UserModel.getCurrentUser().getUsername(), "WARNING: Multiple orders open on Table "
								+ OrderModel.getCurrentOrder().getTableNumber() + "."));
			}

			closeOrderButton.setDisable(false);
			editOrderItemsButton.setDisable(false);
			saveCommentsButton.setDisable(false);
			statusLabel.setText(OrderModel.getCurrentOrder().getStatus());
			creatingUserLabel.setText(OrderModel.getCurrentOrder().getCreatingUser());
			timeOpenedLabel.setText(OrderModel.getCurrentOrder().getDateOpened());
			itemsListView.setItems(OrderModel.getCurrentOrder().getItemList());
			totalCostLabel.setText(String.format("£%1$.2f", OrderModel.getCurrentOrder().getTotalCost()));
			commentsTextArea.setEditable(true);
			commentsTextArea.setText(OrderModel.getCurrentOrder().getComments());
		} else {
			OrderModel.setCurrentOrder(null);
			statusLabel.setText("no order present");
			openOrderButton.setDisable(false);
		}

	}

}

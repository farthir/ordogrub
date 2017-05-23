package gc01.cw.robf.controller;

import java.util.ArrayList;

import gc01.cw.robf.model.LogModel;
import gc01.cw.robf.model.MenuItemModel;
import gc01.cw.robf.model.OrderModel;
import gc01.cw.robf.model.UserModel;
import gc01.cw.robf.utility.FileXmlHandler;
import gc01.cw.robf.utility.XmlCustomLogger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

/**
 * Controller for the edit order items interface. Allows user to choose a Menu
 * category and displays click-able items from that category to be added to the
 * current order. Displays order items in an un-editable tableView. Uses FXML
 * template. Also allows user to delete existing items from order.
 * <p>
 * TableView adapted from
 * http://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class EditOrderItemsViewController {

	private boolean modTag;
	private TableView<MenuItemModel> editOrdersTable = new TableView<>();
	private Stage ordersViewStage;
	private ArrayList<Button> starterMenuButtons;
	private ArrayList<Button> mainMenuButtons;
	private ArrayList<Button> dessertMenuButtons;
	private ArrayList<Button> sideMenuButtons;
	private ArrayList<Button> drinkMenuButtons;
	private String orderFilePath = "./data/orders.xml";
	private String logFilePath = "./data/activityLog.xml";

	private ObservableList<MenuItemModel> orderMenuItems;
	private ObservableList<MenuItemModel> existingMenuItems;

	private static ObservableList<MenuItemModel> menuItemList = FXCollections.observableArrayList();

	@FXML
	private Button cancelButton;

	@FXML
	private Button deleteButton;

	@FXML
	private Button starterButton;

	@FXML
	private Button mainButton;

	@FXML
	private Button dessertButton;

	@FXML
	private Button sideButton;

	@FXML
	private Button drinkButton;

	@FXML
	private TilePane menuTilePane;

	@FXML
	private Label statusLabel;

	/**
	 * Method triggered when user presses Save button. If items have been
	 * modified, saves orders to file and closes view.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void save(ActionEvent event) {

		if (modTag) {
			// save order to file
			FileXmlHandler.saveOrders(orderFilePath, OrderModel.getOrderList());
			XmlCustomLogger.writeLogLine(logFilePath,
					new LogModel(UserModel.getCurrentUser().getUsername(),
							"Order menu items modified for order opened on "
									+ OrderModel.getCurrentOrder().getDateOpened() + " on Table "
									+ OrderModel.getCurrentOrder().getTableNumber() + "."));
		}

		// close Orders View
		ordersViewStage = (Stage) cancelButton.getScene().getWindow();
		ordersViewStage.close();
	}

	/**
	 * Method triggered when user presses Cancel button. Closes view. Main view
	 * will reload orders from file.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void cancel(ActionEvent event) {

		// restore menu items as they were
		orderMenuItems.setAll(existingMenuItems);

		// close Orders View
		ordersViewStage = (Stage) cancelButton.getScene().getWindow();
		ordersViewStage.close();
	}

	/**
	 * Method triggered when user presses Delete button. Removes item selected
	 * in tableView.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void delete(ActionEvent event) {
		statusLabel.setText("");
		ObservableList<MenuItemModel> menuItemsSelected = editOrdersTable.getSelectionModel().getSelectedItems();

		if (menuItemsSelected.isEmpty()) {
			statusLabel.setText("Select a menu item to delete");
		} else {
			for (MenuItemModel menuItemSelected : menuItemsSelected) {
				modTag = true;
				orderMenuItems.remove(menuItemSelected);
			}
		}

	}

	/**
	 * Method triggered when user presses Starter button. Populates tile pane
	 * with buttons for each menu item.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void selectStarter(ActionEvent event) {
		statusLabel.setText("");
		menuTilePane.getChildren().clear();
		menuTilePane.getChildren().addAll(starterMenuButtons);
	}

	/**
	 * Method triggered when user presses Main button. Populates tile pane with
	 * buttons for each menu item.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void selectMain(ActionEvent event) {
		statusLabel.setText("");
		menuTilePane.getChildren().clear();
		menuTilePane.getChildren().addAll(mainMenuButtons);
	}

	/**
	 * Method triggered when user presses Dessert button. Populates tile pane
	 * with buttons for each menu item.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void selectDessert(ActionEvent event) {
		statusLabel.setText("");
		menuTilePane.getChildren().clear();
		menuTilePane.getChildren().addAll(dessertMenuButtons);
	}

	/**
	 * Method triggered when user presses Side button. Populates tile pane with
	 * buttons for each menu item.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void selectSide(ActionEvent event) {
		statusLabel.setText("");
		menuTilePane.getChildren().clear();
		menuTilePane.getChildren().addAll(sideMenuButtons);
	}

	/**
	 * Method triggered when user presses Drink button. Populates tile pane with
	 * buttons for each menu item.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void selectDrink(ActionEvent event) {
		statusLabel.setText("");
		menuTilePane.getChildren().clear();
		menuTilePane.getChildren().addAll(drinkMenuButtons);
	}

	/**
	 * Method automatically called by JavaFX Application class when loading a
	 * view. Initialises buttons for each menu item and their EventHandlers.
	 * Also initialises the tableView of items in the current order and adds it
	 * to the view.
	 */
	public void initialize() {

		// get the current order
		orderMenuItems = OrderModel.getCurrentOrder().getItemList();

		// add to a new ObservableList so it can be restored if we close the
		// window
		existingMenuItems = FXCollections.observableArrayList();
		existingMenuItems.setAll(orderMenuItems);

		// initialise tile pane containing menu selector
		menuTilePane.setHgap(30);
		menuTilePane.setVgap(30);

		// create arraylist of buttons for each menu item enum type
		// STARTER, MAIN, DESSERT, SIDE, DRINK;
		starterMenuButtons = new ArrayList<>();
		mainMenuButtons = new ArrayList<>();
		dessertMenuButtons = new ArrayList<>();
		sideMenuButtons = new ArrayList<>();
		drinkMenuButtons = new ArrayList<>();

		menuItemList = MenuItemModel.getMenu();

		// create menu item buttons
		for (MenuItemModel item : menuItemList) {
			Button button = new Button(item.getName());
			button.setPrefWidth(100);
			button.setPrefHeight(100);

			// add event handler that adds menu item to current order when
			// clicked
			button.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					statusLabel.setText("");
					Button clickedButton = (Button) e.getSource();
					String clickedItemName = clickedButton.getText();

					for (MenuItemModel menuItem : menuItemList) {
						if (menuItem.getName().equals(clickedItemName)) {
							orderMenuItems.add(menuItem);
							modTag = true;
							break;
						}
					}
				}
			});

			switch (item.getType()) {
			case "STARTER":
				starterMenuButtons.add(button);
				break;
			case "MAIN":
				mainMenuButtons.add(button);
				break;
			case "DESSERT":
				dessertMenuButtons.add(button);
				break;
			case "SIDE":
				sideMenuButtons.add(button);
				break;
			case "DRINK":
				drinkMenuButtons.add(button);
				break;
			}
		}

		// initialise table view
		// sort out table
		Parent root = cancelButton.getParent();
		Pane tablePane = (Pane) ((AnchorPane) root).lookup("#tablePane");

		editOrdersTable.setEditable(false);
		editOrdersTable.setPrefWidth(tablePane.getPrefWidth());
		editOrdersTable.setPrefHeight(tablePane.getPrefHeight());

		TableColumn<MenuItemModel, MenuItemModel.ItemType> menuTypeCol = new TableColumn<>("Type");
		menuTypeCol.setCellValueFactory(new PropertyValueFactory<MenuItemModel, MenuItemModel.ItemType>("type"));

		TableColumn<MenuItemModel, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<MenuItemModel, String>("name"));

		TableColumn<MenuItemModel, String> descriptionCol = new TableColumn<>("Description");
		descriptionCol.setCellValueFactory(new PropertyValueFactory<MenuItemModel, String>("description"));

		TableColumn<MenuItemModel, Double> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<MenuItemModel, Double>("price"));

		editOrdersTable.getColumns().addAll(menuTypeCol, nameCol, descriptionCol, priceCol);

		// add table to existing pane
		tablePane.getChildren().addAll(editOrdersTable);

		editOrdersTable.setItems(orderMenuItems);

	}

}

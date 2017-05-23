package gc01.cw.robf.controller;

import java.io.File;
import java.util.Optional;

import gc01.cw.robf.model.LogModel;
import gc01.cw.robf.model.OrderModel;
import gc01.cw.robf.model.UserModel;
import gc01.cw.robf.utility.FileXmlHandler;
import gc01.cw.robf.utility.XmlCustomLogger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Controller for the Manage Orders interface. Allows all users to change order
 * Status (in tableView) and permits managers to delete orders (selected
 * tableView item), import orders from an XML file and export order to an XML
 * file. Includes filter on tableView for search. Uses FXML template.
 * <p>
 * TableView and editing adapted from
 * http://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 * <p>
 * TableView filtering and sorting adapted from
 * http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
 * <p>
 * TableView format override adapted from
 * http://stackoverflow.com/questions/11412360/javafx-table-cell-formatting
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class ManageOrdersViewController {

	private boolean modTag;
	private Stage manageOrdersViewStage;
	// table views for nested windows
	private TableView<OrderModel> manageOrdersTable = new TableView<>();
	private ObservableList<OrderModel> orderList;
	private String orderFilePath = "./data/orders.xml";
	private String logFilePath = "./data/activityLog.xml";

	// Return values from OrderStatus enum for combo box in orders table
	private ObservableList<OrderModel.OrderStatus> orderTypeCombo = FXCollections
			.observableArrayList(OrderModel.OrderStatus.values());

	@FXML
	private TextField filterTextField;

	@FXML
	private Button cancelButton;

	@FXML
	private Button importButton;

	@FXML
	private Button exportButton;

	@FXML
	private Button deleteButton;

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

		// if modified, save and log
		if (modTag) {
			FileXmlHandler.saveOrders(orderFilePath, orderList);
			XmlCustomLogger.writeLogLine(logFilePath,
					new LogModel(UserModel.getCurrentUser().getUsername(), "Orders modified."));
		}

		// close Manage Orders Views
		manageOrdersViewStage = (Stage) cancelButton.getScene().getWindow();
		manageOrdersViewStage.close();
	}

	/**
	 * Method triggered when user presses Cancel button. Closes view.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void cancel(ActionEvent event) {

		// reload orders from file
		FileXmlHandler.loadOrders(orderFilePath, true);

		// close Manage Orders Views
		manageOrdersViewStage = (Stage) cancelButton.getScene().getWindow();
		manageOrdersViewStage.close();
	}

	/**
	 * Method triggered when user presses Delete button. If user has selected an
	 * item in the table, displays a confirmation dialog and if accepted,
	 * deletes the selected item.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void delete(ActionEvent event) {
		statusLabel.setText("");

		// multiple selector not implemented
		ObservableList<OrderModel> ordersSelected = manageOrdersTable.getSelectionModel().getSelectedItems();

		// create a confirmation dialog
		Alert confirmOrderDelete = new Alert(AlertType.CONFIRMATION);
		confirmOrderDelete.setTitle("Delete Order Confirmation");
		confirmOrderDelete.setHeaderText(
				"You have selected an order on Table " + ordersSelected.get(0).getTableNumber() + " for deletion.");
		confirmOrderDelete.setContentText("Proceed?");

		if (ordersSelected.isEmpty()) {
			statusLabel.setText("Select an order to delete");
		} else {
			Optional<ButtonType> buttonPressed = confirmOrderDelete.showAndWait();
			if (buttonPressed.get() == ButtonType.OK) {
				for (OrderModel orderSelected : ordersSelected) {
					modTag = true;
					orderList.remove(orderSelected);
				}
			}
		}

	}

	/**
	 * Method triggered when user presses Import button. Opens an Open File
	 * dialog and passes selected file path to FileXmlHandler to append file
	 * orders to currently loaded orders.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void importOrders(ActionEvent event) {
		statusLabel.setText("");

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import Orders XML File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML Files", "*.xml"),
				new ExtensionFilter("All Files", "*.*"));

		// get the current stage so the dialog can be opened in its context
		manageOrdersViewStage = (Stage) cancelButton.getScene().getWindow();
		File selectedFile = fileChooser.showOpenDialog(manageOrdersViewStage);
		if (selectedFile != null) {
			FileXmlHandler.loadOrders(selectedFile.getAbsolutePath(), false);
			modTag = true;
		}
	}

	/**
	 * Method triggered when user presses Export button. Opens a Save File
	 * dialog and passes selected file path to FileXmlHandler to save current
	 * order list.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void exportOrders(ActionEvent event) {
		statusLabel.setText("");

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export Orders XML File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("XML Files", "*.xml"),
				new ExtensionFilter("All Files", "*.*"));

		// get the current stage so the dialog can be opened in its context
		manageOrdersViewStage = (Stage) cancelButton.getScene().getWindow();
		// save dialog for choosing location to save file
		File selectedFile = fileChooser.showSaveDialog(manageOrdersViewStage);
		if (selectedFile != null) {
			// save whatever is in memory directly to the file
			FileXmlHandler.saveOrders(selectedFile.getAbsolutePath(), OrderModel.getOrderList());
			statusLabel.setText("Orders saved to " + selectedFile.getAbsolutePath());
			XmlCustomLogger.writeLogLine(logFilePath, new LogModel(UserModel.getCurrentUser().getUsername(),
					"Orders exported to " + selectedFile.getAbsolutePath() + "."));
		}

	}

	/**
	 * Method automatically called by JavaFX Application class when loading a
	 * view. Initialises the tableView of orders. Sets up filtered view for
	 * interactive searching on table columns. Also adds custom edit handlers to
	 * permit modifying order Status directly in table. Enables delete, import
	 * and export buttons if currentUser is a MANAGER.
	 */
	public void initialize() {
		// initialise lists for use in creating orders
		orderList = OrderModel.getOrderList();

		// allow access to user and menu managers for Manager users
		if (UserModel.getCurrentUser().getType().equals(UserModel.UserType.MANAGER.toString())) {
			importButton.setDisable(false);
			exportButton.setDisable(false);
			deleteButton.setDisable(false);
		}

		// sort out table
		Parent root = cancelButton.getParent();
		Pane tablePane = (Pane) ((AnchorPane) root).lookup("#tablePane");

		manageOrdersTable.setEditable(true);
		manageOrdersTable.setPrefWidth(tablePane.getPrefWidth());
		manageOrdersTable.setPrefHeight(tablePane.getPrefHeight());

		TableColumn<OrderModel, Integer> tableNumberCol = new TableColumn<>("Table Number");
		tableNumberCol.setCellValueFactory(new PropertyValueFactory<OrderModel, Integer>("tableNumber"));

		TableColumn<OrderModel, OrderModel.OrderStatus> orderStatusCol = new TableColumn<>("Status");
		orderStatusCol.setCellValueFactory(new PropertyValueFactory<OrderModel, OrderModel.OrderStatus>("status"));
		orderStatusCol.setCellFactory(ComboBoxTableCell.forTableColumn(orderTypeCombo));
		orderStatusCol.setOnEditCommit(new EventHandler<CellEditEvent<OrderModel, OrderModel.OrderStatus>>() {
			@Override
			public void handle(CellEditEvent<OrderModel, OrderModel.OrderStatus> t) {
				statusLabel.setText("");
				((OrderModel) t.getTableView().getItems().get(t.getTablePosition().getRow()))
						.setStatus(t.getNewValue());
				modTag = true;
			}
		});

		TableColumn<OrderModel, String> creatingUserCol = new TableColumn<>("Creating User");
		creatingUserCol.setCellValueFactory(new PropertyValueFactory<OrderModel, String>("creatingUser"));

		TableColumn<OrderModel, String> dateOpenedCol = new TableColumn<>("Opened");
		dateOpenedCol.setCellValueFactory(new PropertyValueFactory<OrderModel, String>("dateOpened"));

		TableColumn<OrderModel, String> dateClosedCol = new TableColumn<>("Closed");
		dateClosedCol.setCellValueFactory(new PropertyValueFactory<OrderModel, String>("dateClosed"));

		TableColumn<OrderModel, Integer> numberItemsCol = new TableColumn<>("Items");
		numberItemsCol.setCellValueFactory(new PropertyValueFactory<OrderModel, Integer>("numberItems"));

		// override to format total cost column
		TableColumn<OrderModel, String> totalCostCol = new TableColumn<>("Total Cost");
		totalCostCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<OrderModel, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(TableColumn.CellDataFeatures<OrderModel, String> totalCost) {
						SimpleStringProperty totalCostProperty = new SimpleStringProperty();
						totalCostProperty.setValue(String.format("£%1$.2f", totalCost.getValue().getTotalCost()));
						return totalCostProperty;
					}
				});

		TableColumn<OrderModel, String> commentsCol = new TableColumn<>("Comments");
		commentsCol.setCellValueFactory(new PropertyValueFactory<OrderModel, String>("comments"));

		orderStatusCol.prefWidthProperty().bind(manageOrdersTable.widthProperty().multiply(0.15));

		manageOrdersTable.getColumns().addAll(tableNumberCol, orderStatusCol, creatingUserCol, dateOpenedCol,
				dateClosedCol, numberItemsCol, totalCostCol, commentsCol);

		// add table to existing pane
		tablePane.getChildren().addAll(manageOrdersTable);

		// Wrap the ObservableList in a FilteredList and initially show all data
		FilteredList<OrderModel> filteredOrderList = new FilteredList<>(orderList, null);

		filterTextField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				// using Java 8 lambda notation
				filteredOrderList.setPredicate(order -> {
					// If filter text is empty, display all values
					if (newValue == null || newValue.isEmpty()) {
						return true;
					}

					// filter on multiple columns
					String lowerCaseFilter = newValue.toLowerCase();

					if (order.getTableNumber().toString().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (order.getStatus().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (order.getCreatingUser().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (order.getDateOpened().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (order.getDateClosed().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (order.getTotalCost().toString().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (order.getComments().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					}
					return false; // Does not match
				});

			}
		});

		// Wrap the FilteredList in a SortedList.
		SortedList<OrderModel> sortedOrderList = new SortedList<>(filteredOrderList);

		// Bind the SortedList comparator to the TableView comparator.
		sortedOrderList.comparatorProperty().bind(manageOrdersTable.comparatorProperty());

		// Add sorted (and filtered) data to the table.
		manageOrdersTable.setItems(sortedOrderList);

	}

}

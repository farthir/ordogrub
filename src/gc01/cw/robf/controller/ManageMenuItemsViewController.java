package gc01.cw.robf.controller;

import java.util.Optional;

import gc01.cw.robf.model.LogModel;
import gc01.cw.robf.model.MenuItemModel;
import gc01.cw.robf.model.UserModel;
import gc01.cw.robf.utility.FileXmlHandler;
import gc01.cw.robf.utility.XmlCustomLogger;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

/**
 * Controller for the Manage Menu Items interface. Allows Managers to add (text entry
 * fields), edit (in tableView) and delete menu items (selected tableView item).
 * Includes filter for search. Uses FXML template.
 * <p>
 * TableView and editing adapted from
 * http://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 * <p>
 * TableView filtering and sorting adapted from
 * http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class ManageMenuItemsViewController {

	private boolean modTag;
	private Stage manageMenuItemsViewStage;
	private TableView<MenuItemModel> manageMenuItemsTable = new TableView<>();
	private ObservableList<MenuItemModel> menuItemsList = MenuItemModel.getMenu();
	private String menuItemFilePath = "./data/menuItems.xml";
	private String logFilePath = "./data/activityLog.xml";

	// Return values from ItemType enum for combo box in table
	private ObservableList<MenuItemModel.ItemType> itemTypeCombo = FXCollections
			.observableArrayList(MenuItemModel.ItemType.values());

	@FXML
	private TextField filterTextField;

	@FXML
	private Button cancelButton;

	@FXML
	private ComboBox<MenuItemModel.ItemType> itemTypeComboBox;

	@FXML
	private TextArea nameTextArea;

	@FXML
	private TextArea descriptionTextArea;

	@FXML
	private TextField priceTextField;

	@FXML
	private Label statusLabel;

	/**
	 * Method triggered when user presses Save button. If items have been
	 * modified, saves menu items to file and closes view.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void save(ActionEvent event) {

		// if modified, save and log
		if (modTag) {
			FileXmlHandler.saveMenuItems(menuItemFilePath, menuItemsList);
			XmlCustomLogger.writeLogLine(logFilePath,
					new LogModel(UserModel.getCurrentUser().getUsername(), "Menu items modified."));
		}

		// close menu editor stage
		manageMenuItemsViewStage = (Stage) cancelButton.getScene().getWindow();
		manageMenuItemsViewStage.close();
	}

	/**
	 * Method triggered when user presses Cancel button. Closes view.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void cancel(ActionEvent event) {

		// reload menu items from file
		FileXmlHandler.loadMenuItems(menuItemFilePath, true);

		// close menu editor stage
		manageMenuItemsViewStage = (Stage) cancelButton.getScene().getWindow();
		manageMenuItemsViewStage.close();
	}

	/**
	 * Method triggered when user presses Add button. Takes values user has
	 * entered into text fields and creates a new menu item object if input is
	 * valid.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void add(ActionEvent event) {
		statusLabel.setText("");

		// regex expression adapted from my compgc04 compilers coursework
		boolean priceValid = priceTextField.getText().matches("^(0|[1-9][0-9]*)(.[0-9]+)?$");

		if (!priceValid) {
			statusLabel.setText(statusLabel.getText() + "Price must be entered in numerical format (e.g. 21.76). ");
		}

		if (itemTypeComboBox.getValue() == null) {
			statusLabel.setText(statusLabel.getText() + "A menu item type must be selected. ");
		}

		if (priceValid && (itemTypeComboBox.getValue() != null)) {
			new MenuItemModel(nameTextArea.getText(), descriptionTextArea.getText(),
					Double.valueOf(priceTextField.getText()), itemTypeComboBox.getValue());
			nameTextArea.clear();
			descriptionTextArea.clear();
			priceTextField.clear();
			modTag = true;
		}

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
		ObservableList<MenuItemModel> menuItemsSelected = manageMenuItemsTable.getSelectionModel().getSelectedItems();

		// create a confirmation dialog
		Alert confirmMenuItemDelete = new Alert(AlertType.CONFIRMATION);
		confirmMenuItemDelete.setTitle("Delete Menu Item Confirmation");
		confirmMenuItemDelete
				.setHeaderText("You have selected \"" + menuItemsSelected.get(0).getName() + "\" for deletion.");
		confirmMenuItemDelete.setContentText("Proceed?");

		if (menuItemsSelected.isEmpty()) {
			statusLabel.setText("Select a menu item to delete");
		} else {
			Optional<ButtonType> buttonPressed = confirmMenuItemDelete.showAndWait();
			if (buttonPressed.get() == ButtonType.OK) {
				for (MenuItemModel menuItemSelected : menuItemsSelected) {
					modTag = true;
					menuItemsList.remove(menuItemSelected);
				}
			}
		}

	}

	/**
	 * Method automatically called by JavaFX Application class when loading a
	 * view. Initialises the tableView of menu items. Sets up filtered view for
	 * interactive searching on table columns. Also adds custom edit handlers to
	 * permit modifying menu items directly in table.
	 */
	public void initialize() {

		// modification tag
		modTag = false;

		// sort out table
		Parent root = cancelButton.getParent();
		Pane tablePane = (Pane) ((AnchorPane) root).lookup("#tablePane");

		manageMenuItemsTable.setEditable(true);
		manageMenuItemsTable.setPrefWidth(tablePane.getPrefWidth());
		manageMenuItemsTable.setPrefHeight(tablePane.getPrefHeight());

		TableColumn<MenuItemModel, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<MenuItemModel, String>("name"));
		nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		nameCol.setOnEditCommit(new EventHandler<CellEditEvent<MenuItemModel, String>>() {
			@Override
			public void handle(CellEditEvent<MenuItemModel, String> t) {
				statusLabel.setText("");
				((MenuItemModel) t.getTableView().getItems().get(t.getTablePosition().getRow()))
						.setName(t.getNewValue());
				modTag = true;
			}
		});

		TableColumn<MenuItemModel, String> descriptionCol = new TableColumn<>("Description");
		descriptionCol.setCellValueFactory(new PropertyValueFactory<MenuItemModel, String>("description"));
		descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
		descriptionCol.setOnEditCommit(new EventHandler<CellEditEvent<MenuItemModel, String>>() {
			@Override
			public void handle(CellEditEvent<MenuItemModel, String> t) {
				statusLabel.setText("");
				((MenuItemModel) t.getTableView().getItems().get(t.getTablePosition().getRow()))
						.setDescription(t.getNewValue());
				modTag = true;
			}
		});

		TableColumn<MenuItemModel, Number> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<MenuItemModel, Number>("price"));
		priceCol.setCellFactory(TextFieldTableCell.<MenuItemModel, Number>forTableColumn(new NumberStringConverter()));
		priceCol.setOnEditCommit(new EventHandler<CellEditEvent<MenuItemModel, Number>>() {
			@Override
			public void handle(CellEditEvent<MenuItemModel, Number> t) {
				statusLabel.setText("");

				((MenuItemModel) t.getTableView().getItems().get(t.getTablePosition().getRow()))
						.setPrice(t.getNewValue().doubleValue());
				modTag = true;
			}
		});

		// uses Enum from MenuItem in ComboBox
		TableColumn<MenuItemModel, MenuItemModel.ItemType> itemTypeCol = new TableColumn<>("Item Type");
		itemTypeCol.setCellValueFactory(new PropertyValueFactory<MenuItemModel, MenuItemModel.ItemType>("type"));
		itemTypeCol.setCellFactory(ComboBoxTableCell.forTableColumn(itemTypeCombo));
		itemTypeCol.setOnEditCommit(new EventHandler<CellEditEvent<MenuItemModel, MenuItemModel.ItemType>>() {
			@Override
			public void handle(CellEditEvent<MenuItemModel, MenuItemModel.ItemType> t) {
				statusLabel.setText("");
				((MenuItemModel) t.getTableView().getItems().get(t.getTablePosition().getRow()))
						.setType(t.getNewValue());
				modTag = true;
			}
		});

		nameCol.prefWidthProperty().bind(manageMenuItemsTable.widthProperty().multiply(0.25));
		descriptionCol.prefWidthProperty().bind(manageMenuItemsTable.widthProperty().multiply(0.25));
		itemTypeCol.prefWidthProperty().bind(manageMenuItemsTable.widthProperty().multiply(0.15));

		manageMenuItemsTable.getColumns().addAll(nameCol, descriptionCol, priceCol, itemTypeCol);

		// add table to existing pane
		tablePane.getChildren().addAll(manageMenuItemsTable);

		// Wrap the ObservableList in a FilteredList and initially show all data
		FilteredList<MenuItemModel> filteredMenuItemList = new FilteredList<>(menuItemsList, null);

		filterTextField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				// using Java 8 lambda notation
				filteredMenuItemList.setPredicate(menuItem -> {
					// If filter text is empty, display all values
					if (newValue == null || newValue.isEmpty()) {
						return true;
					}

					// filter on name, description and type columns
					String lowerCaseFilter = newValue.toLowerCase();

					if (menuItem.getName().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (menuItem.getDescription().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (menuItem.getType().toString().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					}
					return false; // Does not match
				});

			}
		});

		// Wrap the FilteredList in a SortedList.
		SortedList<MenuItemModel> sortedMenuItemList = new SortedList<>(filteredMenuItemList);

		// Bind the SortedList comparator to the TableView comparator.
		sortedMenuItemList.comparatorProperty().bind(manageMenuItemsTable.comparatorProperty());

		// Add sorted (and filtered) data to the table.
		manageMenuItemsTable.setItems(sortedMenuItemList);

		// sort out other stuff
		itemTypeComboBox.setItems(itemTypeCombo);

	}

}

package gc01.cw.robf.controller;

import gc01.cw.robf.model.LogModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller for the log view interface. Displays list of log items for
 * selected user. Includes filter for search. Uses FXML template.
 * <p>
 * TableView adapted from
 * http://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 * <p>
 * TableView filtering and sorting adapted from
 * http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class LogViewController {

	private Stage logViewStage;
	private TableView<LogModel> logTable = new TableView<>();
	private ObservableList<LogModel> logList;

	@FXML
	private TextField filterTextField;

	@FXML
	private Button closeButton;

	@FXML
	private Label statusLabel;

	/**
	 * Method triggered when user presses Close button. Closes view.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void close(ActionEvent event) {
		// close menu editor stage
		logViewStage = (Stage) closeButton.getScene().getWindow();
		logViewStage.close();
	}

	/**
	 * Method automatically called by JavaFX Application class when loading a
	 * view. Initialises the tableView of log items for the selected user. Sets
	 * up filtered view for interactive searching on table columns.
	 */
	public void initialize() {

		logList = LogModel.getSelectedLogList();

		// sort out table
		Parent root = closeButton.getParent();
		Pane tablePane = (Pane) ((AnchorPane) root).lookup("#tablePane");

		logTable.setEditable(false);
		logTable.setPrefWidth(tablePane.getPrefWidth());
		logTable.setPrefHeight(tablePane.getPrefHeight());

		TableColumn<LogModel, String> usernameCol = new TableColumn<>("Username");
		usernameCol.setCellValueFactory(new PropertyValueFactory<LogModel, String>("username"));

		TableColumn<LogModel, String> dateTimeCol = new TableColumn<>("Date/Time");
		dateTimeCol.setCellValueFactory(new PropertyValueFactory<LogModel, String>("dateTime"));

		TableColumn<LogModel, String> messageCol = new TableColumn<>("Message");
		messageCol.setCellValueFactory(new PropertyValueFactory<LogModel, String>("message"));

		logTable.getColumns().addAll(usernameCol, dateTimeCol, messageCol);

		// add table to existing pane
		tablePane.getChildren().addAll(logTable);

		// Wrap the ObservableList in a FilteredList and initially show all data
		FilteredList<LogModel> filteredLogList = new FilteredList<>(logList, null);

		filterTextField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				// using Java 8 lambda notation
				filteredLogList.setPredicate(logItem -> {
					// If filter text is empty, display all values
					if (newValue == null || newValue.isEmpty()) {
						return true;
					}

					// filter on username, description and message columns
					String lowerCaseFilter = newValue.toLowerCase();

					if (logItem.getUsername().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (logItem.getDateTime().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (logItem.getMessage().toString().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					}
					return false; // Does not match
				});

			}
		});

		// Wrap the FilteredList in a SortedList.
		SortedList<LogModel> sortedLogList = new SortedList<>(filteredLogList);

		// Bind the SortedList comparator to the TableView comparator.
		sortedLogList.comparatorProperty().bind(logTable.comparatorProperty());

		// Add sorted (and filtered) data to the table.
		logTable.setItems(sortedLogList);

	}

}

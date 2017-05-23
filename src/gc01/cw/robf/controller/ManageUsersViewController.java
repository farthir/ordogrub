package gc01.cw.robf.controller;

import java.io.IOException;
import java.util.Optional;

import gc01.cw.robf.model.LogModel;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * Controller for the Manage Users interface. Allows Managers to add (text entry
 * fields), edit (in tableView) and delete users (selected tableView item).
 * Includes filter for search and obscures passwords. Uses FXML template.
 * <p>
 * TableView and editing adapted from
 * http://docs.oracle.com/javafx/2/ui_controls/table-view.htm
 * <p>
 * TableView filtering and sorting adapted from
 * http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
 * <p>
 * Custom password change dialog implementation adapted from
 * http://code.makery.ch/blog/javafx-dialogs-official/
 * <p>
 * Workaround for JavaFX not refreshing tableView after modifying a field
 * adapted from
 * http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class ManageUsersViewController {

	private boolean modTag;
	private Stage manageUsersViewStage;
	private Stage logViewStage;
	// table views for nested windows
	private TableView<UserModel> manageUsersTable = new TableView<>();
	private ObservableList<UserModel> userList = UserModel.getUserList();
	private String userFilePath = "./data/users.xml";
	private String logFilePath = "./data/activityLog.xml";

	// Return values from UserType enum for combo box in users table
	private ObservableList<UserModel.UserType> userTypeCombo = FXCollections
			.observableArrayList(UserModel.UserType.values());

	@FXML
	private TextField filterTextField;

	@FXML
	private Button cancelButton;

	@FXML
	private ComboBox<UserModel.UserType> userTypeComboBox;

	@FXML
	private TextField fullNameTextField;

	@FXML
	private TextField usernameTextField;

	@FXML
	private TextField passwordTextField;

	@FXML
	private Label statusLabel;

	/**
	 * Method triggered when user presses Save button. If items have been
	 * modified, saves users to file and closes view.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void save(ActionEvent event) {

		// if modified, save and log
		if (modTag) {
			FileXmlHandler.saveUsers(userFilePath, userList);
			XmlCustomLogger.writeLogLine(logFilePath,
					new LogModel(UserModel.getCurrentUser().getUsername(), "Users modified."));
		}

		// close Manage Users Views
		manageUsersViewStage = (Stage) cancelButton.getScene().getWindow();
		manageUsersViewStage.close();
	}

	/**
	 * Method triggered when user presses Cancel button. Closes view.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void cancel(ActionEvent event) {

		// reload users from file
		FileXmlHandler.loadUsers(userFilePath, true);

		// close Manage Users Views
		manageUsersViewStage = (Stage) cancelButton.getScene().getWindow();
		manageUsersViewStage.close();
	}

	/**
	 * Method triggered when user presses View Log button. Loads the Log view in
	 * modal and undecorated mode to force user to complete interaction with
	 * Manage Menu view before returning to Main view. Applies overlay CSS
	 * formatting to window as well as basic formatting.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void viewLog(ActionEvent event) {
		statusLabel.setText("");

		// multiple selector not implemented
		ObservableList<UserModel> usersSelected = manageUsersTable.getSelectionModel().getSelectedItems();

		if (usersSelected.isEmpty()) {
			statusLabel.setText("Select a user to view their logs.");
		} else {
			// generate the selected user's list of logs
			LogModel.setSelectedLogList(XmlCustomLogger.readLogLinesByUser(logFilePath, usersSelected.get(0)));

			// Load log view
			manageUsersViewStage = (Stage) cancelButton.getScene().getWindow();

			logViewStage = new Stage();
			logViewStage.setTitle("Log Viewer - " + usersSelected.get(0).getUsername());
			logViewStage.initOwner(manageUsersViewStage);
			logViewStage.initModality(Modality.WINDOW_MODAL);
			logViewStage.initStyle(StageStyle.UNDECORATED);

			// Open FXML and load
			FXMLLoader logViewLoader = new FXMLLoader(getClass().getResource("/gc01/cw/robf/view/LogView.fxml"));
			Scene logViewScene;
			try {
				logViewScene = new Scene(logViewLoader.load());
				String baseCss = this.getClass().getResource("/gc01/cw/robf/view/base.css").toExternalForm();
				String overlayCss = this.getClass().getResource("/gc01/cw/robf/view/overlay.css").toExternalForm();
				logViewScene.getStylesheets().addAll(baseCss, overlayCss);
				logViewStage.getIcons().add(new Image("file:./static/OrdoGrubLogo.png"));
				logViewStage.setScene(logViewScene);
			} catch (IOException e) {
				e.printStackTrace();
			}

			logViewStage.show();
		}
	}

	/**
	 * Method triggered when user presses Add button. Takes values user has
	 * entered into text fields and creates a new user object if input is valid.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void add(ActionEvent event) {
		statusLabel.setText("");

		boolean fullNameValid = fullNameTextField.getText().matches("^([a-zA-Z -])*$");
		boolean usernameValid = usernameTextField.getText().matches("^([a-z0-9])*$");

		if (!fullNameValid) {
			statusLabel.setText(statusLabel.getText() + "Name can only contain letters, spaces or dashes. ");
		}

		if (!usernameValid) {
			statusLabel.setText(statusLabel.getText() + "Username can only contain lowercase letters or numbers. ");
		}

		if (userTypeComboBox.getValue() == null) {
			statusLabel.setText(statusLabel.getText() + "A user type must be selected. ");
		}

		if (fullNameValid && usernameValid && (userTypeComboBox.getValue() != null)) {
			new UserModel(fullNameTextField.getText(), usernameTextField.getText(), passwordTextField.getText(),
					userTypeComboBox.getValue());
			fullNameTextField.clear();
			usernameTextField.clear();
			passwordTextField.clear();
			userTypeComboBox.valueProperty().set(null);
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
		ObservableList<UserModel> usersSelected = manageUsersTable.getSelectionModel().getSelectedItems();

		// create a confirmation dialog
		Alert confirmUserDelete = new Alert(AlertType.CONFIRMATION);

		if (usersSelected.isEmpty()) {
			statusLabel.setText("Select a user to delete");
		} else {
			confirmUserDelete.setTitle("Delete User Confirmation");
			confirmUserDelete
					.setHeaderText("You have selected \"" + usersSelected.get(0).getFullName() + "\" for deletion.");
			confirmUserDelete.setContentText("Proceed?");
			Optional<ButtonType> buttonPressed = confirmUserDelete.showAndWait();
			if (buttonPressed.get() == ButtonType.OK) {
				for (UserModel userSelected : usersSelected) {

					if (userSelected.equals(UserModel.getCurrentUser())) {
						statusLabel.setText("You cannot delete yourself!");
					} else {
						userList.remove(userSelected);
						modTag = true;
					}
				}
			}
		}

	}

	/**
	 * Method triggered when user presses Change button. If user has selected a
	 * user in the table, displays a custom dialog to set a new password for the
	 * selected user. Obfuscates the entry.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void resetPassword(ActionEvent event) {
		statusLabel.setText("");

		// get the selected user
		UserModel userSelected = manageUsersTable.getSelectionModel().getSelectedItems().get(0);

		// Create the custom dialog.
		Dialog<String> dialog = new Dialog<>();

		if (userSelected == null) {
			statusLabel.setText("Select a user to set a new password.");
		} else {
			dialog.setTitle("Set Password");
			dialog.setHeaderText("Enter a new password for \"" + userSelected.getUsername() + "\".");

			// Set the button type
			ButtonType applyButtonType = new ButtonType("Apply", ButtonData.APPLY);
			dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);

			// Create gridpane to add password field
			GridPane grid = new GridPane();
			grid.setHgap(20);
			grid.setVgap(20);

			PasswordField password = new PasswordField();
			password.setPromptText("New Password");

			grid.add(new Label("Password:"), 0, 0);
			grid.add(password, 1, 0);

			dialog.getDialogPane().setContent(grid);

			// Get the new password
			dialog.setResultConverter(new Callback<ButtonType, String>() {
				@Override
				public String call(ButtonType dialogButton) {
					if (dialogButton == applyButtonType) {
						return password.getText();
					}
					return null;
				}
			});

			// use the result to set a new password
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				userSelected.setPassword(result.get());
				modTag = true;
			}
		}
	}

	/**
	 * Method automatically called by JavaFX Application class when loading a
	 * view. Initialises the tableView of users. Sets up filtered view for
	 * interactive searching on table columns. Also adds custom edit handlers to
	 * permit modifying users directly in table.
	 */
	public void initialize() {

		// sort out table
		Parent root = cancelButton.getParent();
		Pane tablePane = (Pane) ((AnchorPane) root).lookup("#tablePane");

		manageUsersTable.setEditable(true);
		manageUsersTable.setPrefWidth(tablePane.getPrefWidth());
		manageUsersTable.setPrefHeight(tablePane.getPrefHeight());

		TableColumn<UserModel, String> fullNameCol = new TableColumn<>("Full Name");
		fullNameCol.setCellValueFactory(new PropertyValueFactory<UserModel, String>("fullName"));
		fullNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		fullNameCol.setOnEditCommit(new EventHandler<CellEditEvent<UserModel, String>>() {
			@Override
			public void handle(CellEditEvent<UserModel, String> t) {
				statusLabel.setText("");

				// check if input is valid
				boolean valueValid = t.getNewValue().matches("^([a-zA-Z -])*$");

				if (valueValid) {
					((UserModel) t.getTableView().getItems().get(t.getTablePosition().getRow()))
							.setFullName(t.getNewValue());
					modTag = true;
				} else {
					statusLabel.setText(statusLabel.getText() + "Name can only contain letters, spaces or dashes. ");
				}

				// workaround
				manageUsersTable.getColumns().get(0).setVisible(false);
				manageUsersTable.getColumns().get(0).setVisible(true);
			}
		});

		TableColumn<UserModel, String> usernameCol = new TableColumn<>("Username");
		usernameCol.setCellValueFactory(new PropertyValueFactory<UserModel, String>("username"));
		usernameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		usernameCol.setOnEditCommit(new EventHandler<CellEditEvent<UserModel, String>>() {
			@Override
			public void handle(CellEditEvent<UserModel, String> t) {
				statusLabel.setText("");

				// check if input is valid
				boolean valueValid = t.getNewValue().matches("^([a-z0-9])*$");

				if (valueValid) {
					((UserModel) t.getTableView().getItems().get(t.getTablePosition().getRow()))
							.setUsername(t.getNewValue());
					modTag = true;
				} else {
					statusLabel.setText(
							statusLabel.getText() + "Username can only contain lowercase letters or numbers. ");
				}

				// workaround
				manageUsersTable.getColumns().get(0).setVisible(false);
				manageUsersTable.getColumns().get(0).setVisible(true);
			}
		});

		// uses Enum from User in ComboBox
		TableColumn<UserModel, UserModel.UserType> userTypeCol = new TableColumn<>("User Type");
		userTypeCol.setCellValueFactory(new PropertyValueFactory<UserModel, UserModel.UserType>("type"));
		userTypeCol.setCellFactory(ComboBoxTableCell.forTableColumn(userTypeCombo));
		userTypeCol.setOnEditCommit(new EventHandler<CellEditEvent<UserModel, UserModel.UserType>>() {
			@Override
			public void handle(CellEditEvent<UserModel, UserModel.UserType> t) {
				statusLabel.setText("");
				((UserModel) t.getTableView().getItems().get(t.getTablePosition().getRow())).setType(t.getNewValue());
				modTag = true;
			}
		});

		userTypeCol.prefWidthProperty().bind(manageUsersTable.widthProperty().multiply(0.15));

		manageUsersTable.getColumns().addAll(fullNameCol, usernameCol, userTypeCol);

		// add table to existing pane
		tablePane.getChildren().addAll(manageUsersTable);

		// Wrap the ObservableList in a FilteredList and initially show all data
		FilteredList<UserModel> filteredUserList = new FilteredList<>(userList, null);

		filterTextField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				// using Java 8 lambda notation
				filteredUserList.setPredicate(user -> {
					// If filter text is empty, display all values
					if (newValue == null || newValue.isEmpty()) {
						return true;
					}

					// filter on fullName, username and type columns
					String lowerCaseFilter = newValue.toLowerCase();

					if (user.getFullName().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					} else if (user.getType().toString().toLowerCase().contains(lowerCaseFilter)) {
						return true;
					}
					return false; // Does not match
				});

			}
		});

		// Wrap the FilteredList in a SortedList.
		SortedList<UserModel> sortedUserList = new SortedList<>(filteredUserList);

		// Bind the SortedList comparator to the TableView comparator.
		sortedUserList.comparatorProperty().bind(manageUsersTable.comparatorProperty());

		// Add sorted (and filtered) data to the table.
		manageUsersTable.setItems(sortedUserList);

		// sort out other stuff
		userTypeComboBox.setItems(userTypeCombo);

	}

}

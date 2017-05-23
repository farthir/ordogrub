package gc01.cw.robf.controller;

import java.io.IOException;

import gc01.cw.robf.model.LogModel;
import gc01.cw.robf.model.UserModel;
import gc01.cw.robf.utility.XmlCustomLogger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Controller for the login application interface. Prompts user for user-name
 * and password credentials and loads Main view if authenticated. Uses FXML
 * template.
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class LoginViewController {

	private Stage loginViewStage;
	private Stage mainViewStage;
	private ObservableList<UserModel> userList;
	private String logFilePath = "./data/activityLog.xml";

	@FXML
	private Label lblStatus;

	@FXML
	private TextField txtUsername;

	@FXML
	private TextField txtPassword;

	@FXML
	private Button loginButton;

	/**
	 * Method triggered when user presses Login button. Checks entered user
	 * credentials. If authenticated and not a BLOCKED user, sets currentUser
	 * and loads the Main view. Otherwise displays error.
	 * 
	 * @param event
	 *            Event object passed during trigger.
	 */
	@FXML
	public void login(ActionEvent event) {
		userList = UserModel.getUserList();

		boolean validLogin = false;

		// check whether the user is authenticated and prevent blocked users
		// from logging in
		for (UserModel user : userList) {
			if (!user.getType().equals(UserModel.UserType.BLOCKED.toString())
					&& user.getUsername().equals(txtUsername.getText())
					&& user.getPassword().equals(txtPassword.getText())) {
				validLogin = true;
				UserModel.setCurrentUser(user);
			}
		}

		if (validLogin == true) {
			XmlCustomLogger.writeLogLine(logFilePath,
					new LogModel(UserModel.getCurrentUser().getUsername(), "User successfully logged in."));

			// close the login window
			loginViewStage = (Stage) loginButton.getScene().getWindow();
			loginViewStage.close();

			// start the main window
			mainViewStage = new Stage();
			mainViewStage.setTitle("OrdoGrub");

			// Open FXML and load
			FXMLLoader mainViewLoader = new FXMLLoader(getClass().getResource("/gc01/cw/robf/view/MainView.fxml"));
			Scene mainViewScene;
			try {
				mainViewScene = new Scene(mainViewLoader.load());
				String baseCss = this.getClass().getResource("/gc01/cw/robf/view/base.css").toExternalForm();
				mainViewScene.getStylesheets().addAll(baseCss);
				mainViewStage.setScene(mainViewScene);
				mainViewStage.getIcons().add(new Image("file:./static/OrdoGrubLogo.png"));
				mainViewStage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			XmlCustomLogger.writeLogLine(logFilePath, new LogModel("unknown",
					"User unsuccessfully attempted to log in with username \"" + txtUsername.getText() + "\"."));

			lblStatus.setText("Login failed, try again.");
		}
	}

}

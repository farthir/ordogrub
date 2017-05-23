package gc01.cw.robf.controller;

import java.io.IOException;

import gc01.cw.robf.model.UserModel;
import gc01.cw.robf.utility.FileXmlHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * AppDriver serves as the driver class containing the main method for OrdoGrub.
 * Its functionality is limited to loading menus, users and prior orders from
 * file and displaying the login view. Extends JavaFX Application class to
 * instantiate first stage.
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class AppDriver extends Application {

	// paths to data files
	private static String userFilePath = "./data/users.xml";
	private static String orderFilePath = "./data/orders.xml";
	private static String menuFilePath = "./data/menuItems.xml";

	/*
	 * Overridden JavaFX Application start method which loads LoginView FXML and
	 * applies basic formatting.
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage loginViewStage) {

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
	 * Main method. Sets temporary pre-login user and loads menu, orders, and
	 * users from XML files using the XML handler utility. Calls launch method
	 * on JavaFX Application class which loads overridden start method.
	 * 
	 * @param args
	 *            Unused
	 */
	public static void main(String[] args) {
		// set a fake user to prevent null pointer exceptions when order file
		// loader looks for users
		UserModel preLoginUser = new UserModel("Pre-login User", "preLoginUser", "", UserModel.UserType.BLOCKED);
		UserModel.setCurrentUser(preLoginUser);

		// Load data from files with overwriting enabled
		FileXmlHandler.loadMenuItems(menuFilePath, true);
		FileXmlHandler.loadOrders(orderFilePath, true);

		// load users from file with overwriting and clear preLoginUser
		FileXmlHandler.loadUsers(userFilePath, true);
		UserModel.setCurrentUser(null);

		launch(args);
	}
}

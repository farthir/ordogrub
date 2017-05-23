package gc01.cw.robf.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model for User objects. Uses Property and Observable types for most variables
 * to allow JavaFX tableView refresh.
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class UserModel {

	/**
	 * Simple enum for type of UserModel
	 * 
	 * @author Rob Farthing
	 * @version 1.0.0
	 *
	 */
	public enum UserType {
		WAITING, MANAGER, BLOCKED;
	}

	private SimpleStringProperty fullName;
	private SimpleStringProperty username;
	private SimpleStringProperty password;
	private SimpleStringProperty type;
	private static ObservableList<UserModel> userList = FXCollections.observableArrayList();
	private static UserModel currentUser;

	/**
	 * Constructor for User objects. Adds new users to the statically accessible
	 * userList list.
	 * 
	 * @param fullName
	 *            String of user full name
	 * @param username
	 *            String of user username
	 * @param password
	 *            String of user password
	 * @param type
	 *            UserModel.UserType of user type
	 */
	public UserModel(String fullName, String username, String password, UserType type) {
		this.fullName = new SimpleStringProperty(fullName);
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
		this.type = new SimpleStringProperty(type.toString());
		userList.add(this);
	}

	/**
	 * Static method for returning the list of all users.
	 * 
	 * @return ObservableList containing all UserModel objects.
	 */
	public static ObservableList<UserModel> getUserList() {
		return userList;
	}

	/**
	 * Static method for returning the current user as assigned with
	 * setCurrentUser
	 * 
	 * @return UserModel of current user
	 */
	public static UserModel getCurrentUser() {
		return currentUser;
	}

	/**
	 * Static method for setting the current user
	 * 
	 * @param currentUser
	 *            UserModel object to set to static reference
	 */
	public static void setCurrentUser(UserModel currentUser) {
		UserModel.currentUser = currentUser;
	}

	/**
	 * @return String value of user full name.
	 */
	public String getFullName() {
		return fullName.get();
	}

	/**
	 * @param fullName
	 *            String value of full name to set.
	 */
	public void setFullName(String fullName) {
		this.fullName.set(fullName);
	}

	/**
	 * @return String value of user username.
	 */
	public String getUsername() {
		return username.get();
	}

	/**
	 * @param username
	 *            String value of username to set.
	 */
	public void setUsername(String username) {
		this.username.set(username);
	}

	/**
	 * @return String value of user password.
	 */
	public String getPassword() {
		return password.get();
	}

	/**
	 * @param password
	 *            String value of password to set.
	 */
	public void setPassword(String password) {
		this.password.set(password);
	}

	/**
	 * @return String value of user type.
	 */
	public String getType() {
		return type.get();
	}

	/**
	 * @param type
	 *            UserModel.UserType enum of type to set.
	 */
	public void setType(UserType type) {
		this.type.set(type.toString());
	}

	/*
	 * Overridden toString method to return String value of user full name.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return fullName.get();
	}
}

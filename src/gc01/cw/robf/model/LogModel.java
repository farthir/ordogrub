package gc01.cw.robf.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model for log objects. Uses Property and Observable types for most variables
 * to allow JavaFX tableView refresh.
 * <p>
 * Returning current date-time adapted from
 * https://www.mkyong.com/java/java-how-to-get-current-date-time-date-and-calender/
 * 
 * @author Rob Farthing
 * @version 1.0.0
 *
 */
public class LogModel {

	private SimpleStringProperty username;
	private SimpleStringProperty dateTime;
	private SimpleStringProperty message;
	private static ObservableList<LogModel> selectedLogList = FXCollections.observableArrayList();
	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	/**
	 * Constructor for Log objects. Takes user-name and message and sets
	 * currentDateTime to the current date-time.
	 * 
	 * @param username
	 *            String value of username to log.
	 * @param message
	 *            String value of message to log.
	 */
	public LogModel(String username, String message) {
		this.username = new SimpleStringProperty(username);
		LocalDateTime now = LocalDateTime.now();
		String currentDateTime = dtf.format(now);
		this.dateTime = new SimpleStringProperty(currentDateTime);
		this.message = new SimpleStringProperty(message);
	}

	/**
	 * Static method for returning the list of log objects set with
	 * setSelectedLogList.
	 * 
	 * @return ObservableList containing LogModel objects of selected log items.
	 */
	public static ObservableList<LogModel> getSelectedLogList() {
		return selectedLogList;
	}

	/**
	 * Static method for storing a list of LogModel objects.
	 * 
	 * @param selectedLogList
	 *            ObservableList of LogModel objects to store.
	 */
	public static void setSelectedLogList(ObservableList<LogModel> selectedLogList) {
		LogModel.selectedLogList.setAll(selectedLogList);
	}

	/**
	 * @return String value of object username.
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
	 * @return String value of object dateTime.
	 */
	public String getDateTime() {
		return dateTime.get();
	}

	/**
	 * @param dateTime
	 *            String value of dateTime to set.
	 */
	public void setDateTime(String dateTime) {
		this.dateTime.set(dateTime);
	}

	/**
	 * @return String value of object message.
	 */
	public String getMessage() {
		return message.get();
	}

	/**
	 * @param message
	 *            String value of message to set.
	 */
	public void setMessage(String message) {
		this.message.set(message);
	}
}
